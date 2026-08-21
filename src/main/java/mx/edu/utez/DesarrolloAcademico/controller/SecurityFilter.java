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

@WebFilter(urlPatterns = {"*.jsp"})
public class SecurityFilter implements Filter {

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

    @Override
    public void destroy() {
    }
}
