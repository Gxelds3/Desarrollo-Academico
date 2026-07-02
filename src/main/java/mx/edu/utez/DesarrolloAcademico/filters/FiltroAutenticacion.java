package mx.edu.utez.DesarrolloAcademico.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

// El filtro se aplicará a todas las URLs de tu app
@WebFilter("/*")
public class FiltroAutenticacion extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String requestURI = request.getRequestURI();
        HttpSession session = request.getSession(false); // false = no crea una nueva sesión si no existe

        // 1. Verificar si el usuario ya está logueado
        // cuando inicia sesión guardamos un atributo llamado "usuario", ¿Existe?
        boolean loggedIn = (session != null && session.getAttribute("usuario") != null);

        // 2. Definir las rutas que son públicas (login, registro, CSS, imágenes, etc.)
        // Las rutas publicas deben definirse porque si no serian bloqueadas
        boolean loginRequest =
                requestURI.endsWith("login.jsp") ||
                        requestURI.endsWith("/login") ||
                        requestURI.endsWith("registro.jsp") ||
                        requestURI.endsWith("/register") ||
                        requestURI.endsWith("recuperar-contra.jsp") ||
                        requestURI.endsWith("/reset")
                ;
        boolean isResource = requestURI.contains("/assets/") || requestURI.contains("/layout/");

        if (loggedIn) {
            // SI TIENE SESIÓN:
            if (loginRequest) {
                // Si ya está logueado e intenta ir al login, lo mandamos al index
                response.sendRedirect(request.getContextPath() + "/index.jsp");
            } else {
                // Si va a cualquier otra página, lo dejamos pasar
                chain.doFilter(request, response);
            }
        } else {
            // NO TIENE SESIÓN:
            if (loginRequest || isResource) {
                // Si no tiene sesión pero va al login o a recursos públicos, lo dejamos pasar
                chain.doFilter(request, response);
            } else {
                // Si intenta entrar a una página protegida sin sesión, va para el login
                response.sendRedirect(request.getContextPath() + "/login.jsp");
            }
        }
    }
}