package mx.edu.utez.DesarrolloAcademico.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mx.edu.utez.DesarrolloAcademico.model.Usuario;
import mx.edu.utez.DesarrolloAcademico.utils.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Filtro de seguridad que protege las vistas JSP restringidas por rol (sufijos _de, _co, _do): valida que exista una sesión activa y que el usuario en sesión siga activo en la base de datos antes de permitir el acceso.
 * @author Gael Itzaya Velez Reyez
 * @since 2026-08-04
 */
@WebFilter(urlPatterns = {"*.jsp"})
public class SecurityFilter implements Filter {

    /**
     * Inicializa el filtro/servlet antes de que comience a atender peticiones.
     * @param filterConfig Configuración del filtro proporcionada por el contenedor de servlets.
     * @throws ServletException Si ocurre un error al procesar la petición dentro del servlet.
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String uri = req.getRequestURI();

        // Ignorar login, recursos y páginas públicas
        if (uri.endsWith("login.jsp") || uri.contains("/assets/")
                || uri.endsWith("recuperar_contrasena.jsp")
                || uri.endsWith("sidebar_co.jsp")
                || uri.endsWith("sidebar_de.jsp")
                || uri.endsWith("sidebar_do.jsp")) {
            chain.doFilter(request, response);
            return;
        }

        // Solo proteger vistas de rol específico (_de, _co, _do)
        if (uri.endsWith("_de.jsp") || uri.endsWith("_co.jsp") || uri.endsWith("_do.jsp")) {
            HttpSession session = req.getSession(false);
            Usuario u = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

            if (u == null) {
                res.sendRedirect(req.getContextPath() + "/login.jsp");
                return;
            }

            // Verificar en BD que el usuario siga activo y exista
            if (!usuarioSigueActivo(u.getIdUsuario())) {
                session.invalidate();
                res.sendRedirect(req.getContextPath() + "/login.jsp");
                return;
            }

            String rol = u.getRol() != null ? u.getRol().toLowerCase() : "";
            if (uri.endsWith("_de.jsp") && !rol.equals("desarrollo")) {
                res.sendRedirect(req.getContextPath() + "/login.jsp");
                return;
            }
            if (uri.endsWith("_co.jsp") && !rol.equals("coordinador")) {
                res.sendRedirect(req.getContextPath() + "/login.jsp");
                return;
            }
            if (uri.endsWith("_do.jsp") && !rol.equals("docente")) {
                res.sendRedirect(req.getContextPath() + "/login.jsp");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    /**
     * Método auxiliar de la clase.
     * @param idUsuario Identificador único del usuario.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    private boolean usuarioSigueActivo(int idUsuario) {
        String sql = "SELECT activo FROM usuario WHERE id_usuario = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {
            if (con == null || ps == null) return true;
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("activo") == 1;
                }
            }
        } catch (Exception e) {
            System.err.println("Error verificando estado de usuario: " + e.getMessage());
            return true;
        }
        return false;
    }

    /**
     * Libera los recursos utilizados por el filtro/servlet al finalizar su ciclo de vida.
     */
    @Override
    public void destroy() {
    }
}
