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
public class SubirConstanciaServlet1 extends HttpServlet {

    // ID correspondiente a la división "General" en tu base de datos
    private static final int ID_DIVISION_GENERAL = 5;

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
            // 2. VALIDAR DOBLE PERIODO (GENERAL Y DIVISIÓN CORRESPONDIENTE)
            // ------------------------------------------------------------------
            boolean periodoGeneralActivo = dao.esPeriodoActivo(ID_DIVISION_GENERAL);

            if (!periodoGeneralActivo) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"success\": false, \"message\": \"El periodo General de carga se encuentra deshabilitado. No es posible subir constancias.\"}");
                out.flush();
                return;
            }

            if (targetUsuario != null && targetUsuario.getIdDivision() != null) {
                int idDivisionTarget = targetUsuario.getIdDivision();
                System.out.println("ID Division del Usuario Destino: " + idDivisionTarget);

                // Si la división del usuario no es la general, validamos su división específica
                if (idDivisionTarget != ID_DIVISION_GENERAL) {
                    boolean periodoDivisionActivo = dao.esPeriodoActivo(idDivisionTarget);

                    if (!periodoDivisionActivo) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"success\": false, \"message\": \"El periodo de carga para la división del docente no está activo.\"}");
                        out.flush();
                        return;
                    }
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

            // 5. PROCESAR Y VALIDAR EL ARCHIVO SUBIDO
            Part filePart = request.getPart("archivo");
            if (filePart == null || filePart.getSize() == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"success\": false, \"message\": \"Debes seleccionar un archivo PDF, PNG o JPG.\"}");
                out.flush();
                return;
            }

            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String fileNameLower = fileName.toLowerCase();
            if (!fileNameLower.endsWith(".pdf") && !fileNameLower.endsWith(".png")
                    && !fileNameLower.endsWith(".jpg") && !fileNameLower.endsWith(".jpeg")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"success\": false, \"message\": \"El archivo debe ser PDF, PNG o JPG.\"}");
                out.flush();
                return;
            }

            // Determinar Content-Type
            String contentType = filePart.getContentType();
            if (contentType == null) {
                if (fileNameLower.endsWith(".pdf")) contentType = "application/pdf";
                else if (fileNameLower.endsWith(".png")) contentType = "image/png";
                else contentType = "image/jpeg";
            }

            // Leer bytes para BLOB
            byte[] contenidoArchivo;
            try (InputStream is = filePart.getInputStream()) {
                contenidoArchivo = is.readAllBytes();
            }

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