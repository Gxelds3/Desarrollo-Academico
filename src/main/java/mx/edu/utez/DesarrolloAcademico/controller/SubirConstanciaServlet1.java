package mx.edu.utez.DesarrolloAcademico.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import mx.edu.utez.DesarrolloAcademico.model.Usuario;
import mx.edu.utez.DesarrolloAcademico.model.dao.ConstanciaDao;
import mx.edu.utez.DesarrolloAcademico.model.dao.UsuarioDao;
import org.apache.tika.Tika;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Map;

@WebServlet(name = "SubirConstanciaServlet1", value = "/SubirConstanciaServlet1")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 25,      // 25MB
        maxRequestSize = 1024 * 1024 * 30    // 30MB
)
public class SubirConstanciaServlet1 extends HttpServlet {

    private static final int ID_DIVISION_GENERAL = 5;

    // Reutilización de la instancia Tika (Thread-safe)
    private final Tika tika = new Tika();

    // Mapeo estático de tipos MIME permitidos hacia sus extensiones correspondientes
    private static final Map<String, String> MIME_EXTENSION_MAP = Map.of(
            "application/pdf", ".pdf",
            "image/png", ".png",
            "image/jpeg", ".jpg"
    );

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"success\": false, \"message\": \"Sesión no válida.\"}");
            out.flush();
            return;
        }

        Usuario usuarioEnSesion = (Usuario) session.getAttribute("usuario");

        try {
            String idEventoStr = request.getParameter("idEvento");
            String vigencia = request.getParameter("vigencia");
            String fechaVencimiento = request.getParameter("fechaVencimiento");

            if (idEventoStr == null || idEventoStr.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"success\": false, \"message\": \"Falta el ID del evento.\"}");
                out.flush();
                return;
            }

            int idEvento = Integer.parseInt(idEventoStr);
            boolean tieneVigencia = "si".equalsIgnoreCase(vigencia);

            ConstanciaDao dao = new ConstanciaDao();
            UsuarioDao usuarioDao = new UsuarioDao();

            // 1. DETERMINAR PARA QUÉ USUARIO ES LA CONSTANCIA (TARGET)
            String targetUserStr = request.getParameter("idUsuarioTarget");
            int idUsuarioSubir = usuarioEnSesion.getIdUsuario();

            if (targetUserStr != null && !targetUserStr.trim().isEmpty()) {
                String rol = (usuarioEnSesion.getRol() != null) ? usuarioEnSesion.getRol().toLowerCase().trim() : "";
                boolean esAutorizado = rol.contains("coordinador") || rol.contains("desarroll") || rol.contains("dev");

                if (esAutorizado) {
                    try {
                        idUsuarioSubir = Integer.parseInt(targetUserStr);
                    } catch (NumberFormatException e) {
                        System.err.println("Error al parsear idUsuarioTarget: " + targetUserStr);
                    }
                }
            }

            Usuario targetUsuario = usuarioDao.buscarPorId(idUsuarioSubir);

            System.out.println("=== DEPURACION PERIODO ===");
            System.out.println("ID Usuario Destino: " + idUsuarioSubir);
            System.out.println("Usuario Encontrado: " + (targetUsuario != null));

            // 2. VALIDAR PERIODO DE CARGA
            boolean periodoGeneralActivo = dao.esPeriodoActivo(ID_DIVISION_GENERAL);

            if (periodoGeneralActivo) {
                System.out.println("Modo SINCRONIZADO (General activo): subida permitida.");
            } else {
                if (targetUsuario != null && targetUsuario.getIdDivision() != null) {
                    int idDivisionTarget = targetUsuario.getIdDivision();
                    System.out.println("Modo AUTÓNOMO: revisando division " + idDivisionTarget);

                    if (idDivisionTarget != ID_DIVISION_GENERAL) {
                        boolean periodoDivisionActivo = dao.esPeriodoActivo(idDivisionTarget);
                        if (!periodoDivisionActivo) {
                            response.setStatus(HttpServletResponse.SC_OK);
                            out.write("{\"success\": false, \"message\": \"El periodo de carga para tu división está cerrado o ha vencido. No es posible subir constancias.\"}");
                            out.flush();
                            return;
                        }
                    } else {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"success\": false, \"message\": \"El periodo General de carga está deshabilitado.\"}");
                        out.flush();
                        return;
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.write("{\"success\": false, \"message\": \"No se pudo determinar tu división para validar el periodo de carga.\"}");
                    out.flush();
                    return;
                }
            }

            // 3. VINCULAR AL DOCENTE/DESTINATARIO CON EL EVENTO
            int idParticipante = usuarioDao.obtenerOCrearParticipante(idEvento, idUsuarioSubir);

            if (idParticipante == -1) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"success\": false, \"message\": \"El docente o destinatario seleccionado no está registrado en este evento.\"}");
                out.flush();
                return;
            }

            // 4. VERIFICAR CONSTANCIA EXISTENTE
            if (dao.verificarConstanciaExistente(idParticipante)) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"success\": false, \"message\": \"Ya se subió una constancia para este docente en este evento.\"}");
                out.flush();
                return;
            }

            // ------------------------------------------------------------------
            // 5. VALIDACIÓN DEL ARCHIVO USANDO APACHE TIKA
            // ------------------------------------------------------------------
            Part filePart = request.getPart("archivo");
            if (filePart == null) {
                filePart = request.getPart("archivoPdf");
            }

            if (filePart == null || filePart.getSize() == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"success\": false, \"message\": \"Debes seleccionar un archivo PDF, PNG o JPG.\"}");
                out.flush();
                return;
            }

            // A) Leer bytes del archivo en memoria
            byte[] contenidoArchivo;
            try (InputStream is = filePart.getInputStream()) {
                contenidoArchivo = is.readAllBytes();
            }

            // B) Detección MIME real usando la firma interna (Magic Bytes) analizada por Tika
            String contentType = tika.detect(contenidoArchivo);
            String extensionCorrecta = MIME_EXTENSION_MAP.get(contentType);

            // C) Verificar si el MIME detectado está permitido
            if (extensionCorrecta == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"success\": false, \"message\": \"El contenido real del archivo no es un PDF, PNG o JPG válido.\"}");
                out.flush();
                return;
            }

            // D) Construir un nombre de archivo seguro con la extensión corregida por Tika
            String originalName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String fileNameOnly = originalName.contains(".")
                    ? originalName.substring(0, originalName.lastIndexOf('.'))
                    : originalName;

            String fileName = fileNameOnly + extensionCorrecta;

            // 6. GUARDAR EN BASE DE DATOS
            boolean exito = dao.guardarConstancia(idParticipante, fileName, contenidoArchivo, contentType,
                    tieneVigencia, fechaVencimiento, usuarioEnSesion.getIdUsuario());

            if (exito) {
                out.write("{\"success\": true, \"message\": \"Constancia subida correctamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write("{\"success\": false, \"message\": \"Error al guardar en base de datos.\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"success\": false, \"message\": \"Error en el servidor: " + e.getMessage() + "\"}");
        } finally {
            out.flush();
        }
    }
}