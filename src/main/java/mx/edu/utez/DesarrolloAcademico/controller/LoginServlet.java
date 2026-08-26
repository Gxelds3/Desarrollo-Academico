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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import mx.edu.utez.DesarrolloAcademico.utils.DatabaseConnection;

/**
 * Servlet controlador que atiende las peticiones HTTP relacionadas con 'Login' dentro de la arquitectura MVC del proyecto.
 * @author Gael Itzaya Velez Reyez
 * @since 2026-07-27
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private UsuarioDao usuarioDao = new UsuarioDao();

    /**
     * Maneja las peticiones HTTP GET recibidas por este servlet.
     * @param request Objeto de la petición HTTP entrante.
     * @param response Objeto de la respuesta HTTP a generar.
     * @throws ServletException Si ocurre un error al procesar la petición dentro del servlet.
     * @throws IOException Si ocurre un error de entrada/salida al leer o escribir la petición/respuesta.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Si intenta acceder a /login por GET, lo mandamos al jsp
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    /**
     * Maneja las peticiones HTTP POST recibidas por este servlet.
     * @param request Objeto de la petición HTTP entrante.
     * @param response Objeto de la respuesta HTTP a generar.
     * @throws ServletException Si ocurre un error al procesar la petición dentro del servlet.
     * @throws IOException Si ocurre un error de entrada/salida al leer o escribir la petición/respuesta.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String credencial = request.getParameter("email");
        String contraPlana = request.getParameter("contra");

        if (credencial == null || credencial.trim().isEmpty() || contraPlana == null || contraPlana.trim().isEmpty()) {
            request.setAttribute("error", "Por favor ingresa tus credenciales.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        String contra = mx.edu.utez.DesarrolloAcademico.utils.HashUtils.hashSHA256(contraPlana);

        Usuario usuario = usuarioDao.login(credencial, contra);

        if (usuario != null) {
            // Usuario autenticado correctamente
            HttpSession session = request.getSession();
            session.setAttribute("usuario", usuario); // Filtro requiere esto
            
            // Redirección según rol
            String rol = usuario.getRol();
            if ("desarrollo".equalsIgnoreCase(rol)) {
                response.sendRedirect(request.getContextPath() + "/vista_general_desarrollador_de.jsp");
            } else if ("coordinador".equalsIgnoreCase(rol)) {
                response.sendRedirect(request.getContextPath() + "/vista_general_coordinador_co.jsp");
            } else if ("docente".equalsIgnoreCase(rol)) {
                response.sendRedirect(request.getContextPath() + "/vista_general_docente_do.jsp");
            } else {
                // Rol desconocido
                request.setAttribute("error", "El rol de este usuario no es válido.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } else {
            // Verificar si el usuario existe pero está inactivo
            if (estaInactivo(credencial, contra)) {
                request.setAttribute("error", "Estado de cuenta desactivado. Contacta al administrador.");
            } else {
                request.setAttribute("error", "Correo/Número de empleado o contraseña incorrectos.");
            }
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    /**
     * Verifica si el usuario existe con esas credenciales pero su cuenta está inactiva.
     */
    private boolean estaInactivo(String credencial, String contrasena) {
        String query = "SELECT activo FROM usuario WHERE (correo_institucional = ? OR numero_empleado = ?) AND contrasena = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, credencial);
            ps.setString(2, credencial);
            ps.setString(3, contrasena);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("activo") == 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
