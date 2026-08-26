package mx.edu.utez.DesarrolloAcademico.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mx.edu.utez.DesarrolloAcademico.model.Usuario;
import mx.edu.utez.DesarrolloAcademico.model.dao.UsuarioDao;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet controlador que atiende las peticiones HTTP relacionadas con 'CambiarEstadoUsuario' .
 * @author Gael Itzaya Velez Reyez
 * @since 2026-08-05
 */
@WebServlet(name = "CambiarEstadoUsuarioServlet", value = "/CambiarEstadoUsuarioServlet")
public class CambiarEstadoUsuarioServlet extends HttpServlet {

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
            out.write("{\"success\": false, \"message\": \"Sesión inválida o expirada.\"}");
            return;
        }

        Usuario admin = (Usuario) session.getAttribute("usuario");
        if (!"desarrollo".equalsIgnoreCase(admin.getRol()) && !"coordinador".equalsIgnoreCase(admin.getRol())) {
            out.write("{\"success\": false, \"message\": \"No tienes permisos para esta acción.\"}");
            return;
        }

        try {
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                idStr = request.getParameter("idUsuario");
            }
            String estadoStr = request.getParameter("estado");
            
            if (idStr == null || estadoStr == null) {
                out.write("{\"success\": false, \"message\": \"Faltan parámetros (id o estado).\"}");
                return;
            }
            
            int idUsuario = Integer.parseInt(idStr.trim());
            int nuevoEstado = Integer.parseInt(estadoStr.trim());

            // Prevenir desactivarse a uno mismo
            if (idUsuario == admin.getIdUsuario() && nuevoEstado == 0) {
                out.write("{\"success\": false, \"message\": \"No puedes desactivar tu propia cuenta.\"}");
                return;
            }

            UsuarioDao dao = new UsuarioDao();
            boolean exito = dao.cambiarEstado(idUsuario, nuevoEstado);

            if (exito) {
                out.write("{\"success\": true, \"message\": \"Estado actualizado correctamente.\"}");
            } else {
                out.write("{\"success\": false, \"message\": \"No se encontró el usuario o no se pudo cambiar el estado.\"}");
            }

        } catch (NumberFormatException e) {
            out.write("{\"success\": false, \"message\": \"Parámetros inválidos. Asegúrate de enviar valores numéricos para ID y estado.\"}");
        } catch (Exception e) {
            e.printStackTrace();
            out.write("{\"success\": false, \"message\": \"Error del servidor: " + e.getMessage() + "\"}");
        } finally {
            out.flush();
        }
    }
}
