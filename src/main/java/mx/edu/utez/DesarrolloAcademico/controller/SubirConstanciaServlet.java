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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;

@WebServlet(name = "SubirConstanciaServlet", value = "/SubirConstanciaServlet")

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB (Límite para guardar en memoria RAM antes de disco)
        maxFileSize = 1024 * 1024 * 25,      // 25MB (Tamaño máximo de un solo archivo)
        maxRequestSize = 1024 * 1024 * 30    // 30MB (Tamaño máximo total de la petición HTTP)
)
public class SubirConstanciaServlet extends HttpServlet {

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

        Usuario usuario = (Usuario) session.getAttribute("usuario");

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

            // ------------------------------------------------------------------
            // 1. VALIDACIÓN DE DOBLE PERIODO (GENERAL + DIVISIÓN DEL DOCENTE)
            // ------------------------------------------------------------------

            // A) Verificar periodo General (ID = 5)
            boolean periodoGeneralActivo = dao.esPeriodoActivo(ID_DIVISION_GENERAL);
            if (!periodoGeneralActivo) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"success\": false, \"message\": \"El periodo General de carga se encuentra deshabilitado. No es posible subir constancias.\"}");
                out.flush();
                return;
            }

            // B) Verificar periodo de la División específica del usuario
            if (usuario.getIdDivision() != null && usuario.getIdDivision() > 0) {
                int idDivisionUsuario = usuario.getIdDivision();

                // Si no es la misma ID general, validamos su división correspondiente
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

            // ------------------------------------------------------------------
            // 2. VALIDAR ASIGNACIÓN AL EVENTO
            // ------------------------------------------------------------------
            int idParticipante = dao.obtenerIdParticipante(idEvento, usuario.getIdUsuario());

            if (idParticipante == -1) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.write("{\"success\": false, \"message\": \"No estás asignado a este evento.\"}");
                out.flush();
                return;
            }

            // ------------------------------------------------------------------
            // 3. VERIFICAR CONSTANCIA DUPLICADA
            // ------------------------------------------------------------------
            if (dao.verificarConstanciaExistente(idParticipante)) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"success\": false, \"message\": \"Ya has subido una constancia para este evento.\"}");
                out.flush();
                return;
            }

            // ------------------------------------------------------------------
            Part filePart = request.getPart("archivoPdf");
            if (filePart == null) {
                filePart = request.getPart("archivo"); // Fallback por si el input se llama "archivo"
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

            // B) Verificar longitud mínima para leer la cabecera
            if (contenidoArchivo.length < 4) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"success\": false, \"message\": \"El archivo es inválido o está dañado.\"}");
                out.flush();
                return;
            }

            // C) Inspección directa de Magic Bytes
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
                // Si el archivo no es un PDF, PNG o JPG binariamente (ej. un script .txt o .exe)
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"success\": false, \"message\": \"El contenido real del archivo no es un PDF, PNG o JPG válido.\"}");
                out.flush();
                return;
            }

            // D) Generar nombre con la extensión REAL comprobada por firma
            String originalName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String fileNameOnly = originalName.contains(".")
                    ? originalName.substring(0, originalName.lastIndexOf('.'))
                    : originalName;

            String fileName = fileNameOnly + extensionCorrecta;

            // ------------------------------------------------------------------
            // 5. GUARDAR EN BASE DE DATOS
            // ------------------------------------------------------------------
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
