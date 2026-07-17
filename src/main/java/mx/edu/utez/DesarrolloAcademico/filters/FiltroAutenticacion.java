package mx.edu.utez.DesarrolloAcademico.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/*")
public class FiltroAutenticacion extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String requestURI = request.getRequestURI();
        HttpSession session = request.getSession(false);

        boolean loggedIn = (session != null && session.getAttribute("usuario") != null);

        boolean loginRequest =
                requestURI.endsWith("login.jsp") ||
                        requestURI.endsWith("/login") ||
                        requestURI.endsWith("registro.jsp") ||
                        requestURI.endsWith("/register") ||
                        requestURI.endsWith("recuperar-contra.jsp") ||
                        requestURI.endsWith("/recuperar") ||
                        requestURI.endsWith("/reset") ||
                        requestURI.endsWith("vista_general_coordinador.jsp") ||
                        requestURI.endsWith("gestion_evento.jsp") ||
                        requestURI.endsWith("agregar_docente.jsp") ||
                        requestURI.endsWith("agregar_evento.jsp") ||
                        requestURI.endsWith("archivo_subido.jsp") ||
                        requestURI.endsWith("cargar_archivo.jsp") ||
                        requestURI.endsWith("editar_docente.jsp") ||
                        requestURI.endsWith("editar_evento.jsp") ||
                        requestURI.endsWith("gestion_docente.jsp") ||
                        requestURI.endsWith("historial_evento.jsp") ||
                        requestURI.endsWith("mi_cuenta.jsp") ||
                        requestURI.endsWith("mi_evento.jsp") ||
                        requestURI.endsWith("ver_mas_evento.jsp")
                ;
        boolean isResource = requestURI.contains("/assets/") || requestURI.contains("/layout/");

        if (loggedIn) {
            if (loginRequest) {
                response.sendRedirect(request.getContextPath() + "/registro.jsp");
            } else {
                chain.doFilter(request, response);
            }
        } else {
            if (loginRequest || isResource) {
                chain.doFilter(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/registro.jsp");
            }
        }
    }
}