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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;

@WebServlet(name = "SubirConstanciaServlet1", value = "/SubirConstanciaServlet1")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB (Límite para guardar en memoria RAM antes de disco)
        maxFileSize = 1024 * 1024 * 25,      // 25MB (Tamaño máximo de un solo archivo)
        maxRequestSize = 1024 * 1024 * 30    // 30MB (Tamaño máximo total de la petición HTTP)

)
/**
 * Controlador (servlet) que gestiona la lógica de 'SubirConstanciaServlet1' en la capa de presentación del patrón MVC.
 * @author Gael Itzaya Velez Reyez
 * @since 2026-08-02
 */
public class SubirConstanciaServlet1 extends HttpServlet {

    // ID correspondiente a la división "General" en tu base de datos
    private static final int ID_DIVISION_GENERAL = 5;

    /**
     * Maneja las peticiones HTTP POST recibidas por este servlet.
     * @param request Objeto de la petición HTTP entrante.
     * @param response Objeto de la respuesta HTTP a generar.
     * @throws ServletException Si ocurre un error al procesar la petición dentro del servlet.
     * @throws IOException Si ocurre un error de entrada/salida al leer o escribir la petición/respuesta.
     */
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
            String vigencia = request.getParameter("vigencia"); // "si" o "no"
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
            int idUsuarioSubir = usuarioEnSesion.getIdUsuario(); // Por defecto, el usuario en sesión

            if (targetUserStr != null && !targetUserStr.trim().isEmpty()) {
                String rol = (usuarioEnSesion.getRol() != null) ? usuarioEnSesion.getRol().toLowerCase().trim() : "";

                // Flexibilidad en el rol
                boolean esAutorizado = rol.contains("coordinador") || rol.contains("desarroll") || rol.contains("dev");

                if (esAutorizado) {
                    try {
                        idUsuarioSubir = Integer.parseInt(targetUserStr);
                    } catch (NumberFormatException e) {
                        System.err.println("Error al parsear idUsuarioTarget: " + targetUserStr);
                    }
                }
            }

            // Busca datos del usuario destino
            Usuario targetUsuario = usuarioDao.buscarPorId(idUsuarioSubir);

            // LOGS PARA LA CONSOLA DE TOMCAT
            System.out.println("=== DEPURACION PERIODO ===");
            System.out.println("ID Usuario Destino: " + idUsuarioSubir);
            System.out.println("Usuario Encontrado: " + (targetUsuario != null));

            // ------------------------------------------------------------------
            // 2. VALIDAR PERIODO DE CARGA (LÓGICA CORRECTA)
            // ------------------------------------------------------------------
            boolean periodoGeneralActivo = dao.esPeriodoActivo(ID_DIVISION_GENERAL);

            if (periodoGeneralActivo) {
                // MODO SINCRONIZADO: General activo → todas las divisiones siguen a General.
                // esPeriodoActivo ya valida ACTIVO=1 AND SYSDATE BETWEEN fechas → si General pasa, OK.
                // No hace falta checar la división individual.
                System.out.println("Modo SINCRONIZADO (General activo): subida permitida.");
            } else {
                // MODO AUTÓNOMO: General apagado → cada división es independiente.
                // Hay que validar SÓLO la división del usuario destino.
                if (targetUsuario != null && targetUsuario.getIdDivision() != null) {
                    int idDivisionTarget = targetUsuario.getIdDivision();
                    System.out.println("Modo AUTÓNOMO: revisando division " + idDivisionTarget);

                    // No validar si el usuario es de la division "General" (id 5)
                    if (idDivisionTarget != ID_DIVISION_GENERAL) {
                        boolean periodoDivisionActivo = dao.esPeriodoActivo(idDivisionTarget);
                        if (!periodoDivisionActivo) {
                            response.setStatus(HttpServletResponse.SC_OK);
                            out.write("{\"success\": false, \"message\": \"El periodo de carga para tu división está cerrado o ha vencido. No es posible subir constancias.\"}");
                            out.flush();
                            return;
                        }
                    } else {
                        // Usuario de división General pero General está apagado → bloquear
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"success\": false, \"message\": \"El periodo General de carga está deshabilitado.\"}");
                        out.flush();
                        return;
                    }
                } else {
                    // No se puede determinar la división → bloquear por seguridad
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

            // 4. VERIFICAR SI YA EXISTE UNA CONSTANCIA PARA ESTE PARTICIPANTE
            if (dao.verificarConstanciaExistente(idParticipante)) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"success\": false, \"message\": \"Ya se subió una constancia para este docente en este evento.\"}");
                out.flush();
                return;
            }

            Part filePart = request.getPart("archivo");
            if (filePart == null) {
                filePart = request.getPart("archivoPdf"); // Fallback por si el formulario manda este parámetro
            }

            if (filePart == null || filePart.getSize() == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"success\": false, \"message\": \"Debes seleccionar un archivo PDF, PNG o JPG.\"}");
                out.flush();
                return;
            }

            // A) Leer bytes completos en memoria
            byte[] contenidoArchivo;
            try (InputStream is = filePart.getInputStream()) {
                contenidoArchivo = is.readAllBytes();
            }

            // B) Verificar que el archivo tenga al menos 4 bytes para leer los Magic Bytes
            if (contenidoArchivo.length < 4) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"success\": false, \"message\": \"El archivo es inválido o está dañado.\"}");
                out.flush();
                return;
            }

            // C) Comparación directa de Magic Bytes
            boolean esPdf = contenidoArchivo[0] == 0x25 && contenidoArchivo[1] == 0x50
                    && contenidoArchivo[2] == 0x44 && contenidoArchivo[3] == 0x46; // %PDF

            boolean esPng = (contenidoArchivo[0] & 0xFF) == 0x89 && contenidoArchivo[1] == 0x50
                    && contenidoArchivo[2] == 0x4E && contenidoArchivo[3] == 0x47; // PNG

            boolean esJpg = (contenidoArchivo[0] & 0xFF) == 0xFF && (contenidoArchivo[1] & 0xFF) == 0xD8
                    && (contenidoArchivo[2] & 0xFF) == 0xFF; // JPEG/JPG

            String contentType = null;
            String extensionCorrecta = null;

            if (esPdf) {
                contentType = "application/pdf";
                extensionCorrecta = ".pdf";
            } else if (esPng) {
                contentType = "image/png";
                extensionCorrecta = ".png";
            } else if (esJpg) {
                contentType = "image/jpeg";
                extensionCorrecta = ".jpg";
            } else {
                // Si el archivo no tiene la firma interna de PDF, PNG o JPG (ej. un .txt o .exe)
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"success\": false, \"message\": \"El contenido real del archivo no es un PDF, PNG o JPG válido.\"}");
                out.flush();
                return;
            }

            // D) Construir un nombre seguro con la extensión REAL detectada
            String originalName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String fileNameOnly = originalName.contains(".")
                    ? originalName.substring(0, originalName.lastIndexOf('.'))
                    : originalName;

            String fileName = fileNameOnly + extensionCorrecta;

            // ------------------------------------------------------------------
            // 6. GUARDAR EN BASE DE DATOS
            // ------------------------------------------------------------------
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
