package mx.edu.utez.DesarrolloAcademico.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mx.edu.utez.DesarrolloAcademico.model.Usuario;
import mx.edu.utez.DesarrolloAcademico.model.dao.ConstanciaDao;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Servlet que sirve el archivo (BLOB) de una constancia directamente desde Oracle.
 * Reemplaza el acceso a archivos locales.
 * URL: /DescargarConstanciaServlet?idConstancia=X
 */
@WebServlet(name = "DescargarConstanciaServlet", value = "/DescargarConstanciaServlet")
public class DescargarConstanciaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\": false, \"message\": \"Sesión no válida.\"}");
            return;
        }

        String idConstanciaStr = request.getParameter("idConstancia");
        if (idConstanciaStr == null || idConstanciaStr.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\": false, \"message\": \"Falta idConstancia.\"}");
            return;
        }

        try {
            int idConstancia = Integer.parseInt(idConstanciaStr.trim());
            ConstanciaDao dao = new ConstanciaDao();

            // Obtener metadatos (nombre + content-type)
            Map<String, String> meta = dao.obtenerMetaDescarga(idConstancia);
            if (meta == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\": false, \"message\": \"Constancia no encontrada.\"}");
                return;
            }

            // Obtener el BLOB
            byte[] contenido = dao.obtenerContenidoArchivo(idConstancia);
            if (contenido == null || contenido.length == 0) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\": false, \"message\": \"Archivo no encontrado en la base de datos.\"}");
                return;
            }

            String nombreArchivo = meta.get("nombre");
            String contentType = meta.get("contentType");

            // Codificar el nombre del archivo para el header
            String nombreCodificado = URLEncoder.encode(nombreArchivo, StandardCharsets.UTF_8.name())
                    .replaceAll("\\+", "%20");

            // Determinar si mostrar inline (PDF, imágenes) o forzar descarga
            boolean esInline = contentType.startsWith("image/") || contentType.equals("application/pdf");
            String disposition = (esInline ? "inline" : "attachment") + "; filename=\"" + nombreCodificado + "\"; filename*=UTF-8''" + nombreCodificado;

            response.setContentType(contentType);
            response.setContentLength(contenido.length);
            response.setHeader("Content-Disposition", disposition);
            response.setHeader("Cache-Control", "private, max-age=3600");

            try (OutputStream out = response.getOutputStream()) {
                out.write(contenido);
                out.flush();
            }

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\": false, \"message\": \"ID inválido.\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\": false, \"message\": \"Error al descargar: " + e.getMessage() + "\"}");
        }
    }
}
