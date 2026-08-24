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
import org.apache.tika.Tika;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Map;

@WebServlet(name = "SubirConstanciaServlet", value = "/SubirConstanciaServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 25,      // 25MB
        maxRequestSize = 1024 * 1024 * 30    // 30MB
)
public class SubirConstanciaServlet extends HttpServlet {

    private static final int ID_DIVISION_GENERAL = 5;

    // Instancia reutilizable e inmutable de Tika
    private final Tika tika = new Tika();

    // Mapeo de tipos MIME soportados a sus extensiones correspondientes
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

        Usuario usuario = (Usuario) session.getAttribute("usuario");

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

            // 1. VALIDACIÓN DE DOBLE PERIODO
            boolean periodoGeneralActivo = dao.esPeriodoActivo(ID_DIVISION_GENERAL);
            if (!periodoGeneralActivo) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"success\": false, \"message\": \"El periodo General de carga se encuentra deshabilitado. No es posible subir constancias.\"}");
                out.flush();
                return;
            }

            if (usuario.getIdDivision() != null && usuario.getIdDivision() > 0) {
                int idDivisionUsuario = usuario.getIdDivision();
                if (idDivisionUsuario != ID_DIVISION_GENERAL) {
                    boolean periodoDivisionActivo = dao.esPeriodoActivo(idDivisionUsuario);
                    if (!periodoDivisionActivo) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"success\": false, \"message\": \"El periodo de carga para tu división no está activo.\"}");
                        out.flush();
                        return;
                    }
                }
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"success\": false, \"message\": \"No tienes una división asignada para validar el periodo de carga.\"}");
                out.flush();
                return;
            }

            // 2. VALIDAR ASIGNACIÓN AL EVENTO
            int idParticipante = dao.obtenerIdParticipante(idEvento, usuario.getIdUsuario());
            if (idParticipante == -1) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.write("{\"success\": false, \"message\": \"No estás asignado a este evento.\"}");
                out.flush();
                return;
            }

            // 3. VERIFICAR CONSTANCIA DUPLICADA
            if (dao.verificarConstanciaExistente(idParticipante)) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"success\": false, \"message\": \"Ya has subido una constancia para este evento.\"}");
                out.flush();
                return;
            }

            // ------------------------------------------------------------------
            // 4. VALIDACIÓN DE ARCHIVO CON APACHE TIKA
            // ------------------------------------------------------------------
            Part filePart = request.getPart("archivoPdf");
            if (filePart == null) {
                filePart = request.getPart("archivo");
            }

            if (filePart == null || filePart.getSize() == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"success\": false, \"message\": \"Debes seleccionar un archivo PDF, PNG o JPG.\"}");
                out.flush();
                return;
            }

            byte[] contenidoArchivo;
            try (InputStream is = filePart.getInputStream()) {
                contenidoArchivo = is.readAllBytes();
            }

            // Detección automática del MIME-type real mediante Apache Tika
            String contentType = tika.detect(contenidoArchivo);
            String extensionCorrecta = MIME_EXTENSION_MAP.get(contentType);

            // Si el formato detectado no está dentro de la lista de permitidos
            if (extensionCorrecta == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"success\": false, \"message\": \"El contenido real del archivo no es un PDF, PNG o JPG válido.\"}");
                out.flush();
                return;
            }

            // Sanitización y armado del nombre de archivo seguro
            String originalName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String fileNameOnly = originalName.contains(".")
                    ? originalName.substring(0, originalName.lastIndexOf('.'))
                    : originalName;

            String fileName = fileNameOnly + extensionCorrecta;

            // 5. GUARDAR EN BASE DE DATOS
            boolean exito = dao.guardarConstanciaCO(idParticipante, fileName, contenidoArchivo, contentType,
                    tieneVigencia, fechaVencimiento, usuario.getIdUsuario());

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