package mx.edu.utez.DesarrolloAcademico;

import java.io.*;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

/**
 * Servlet de prueba/plantilla generado por defecto, usado para verificar que el servidor de aplicaciones responde correctamente.
 * @author Ángel Gael Flores Ronces
 * @since 2026-06-29
 */
@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {
    private String message;

    /**
     * Inicializa el filtro/servlet antes de que comience a atender peticiones.
     */
    public void init() {
        message = "Hello World!";
    }

    /**
     * Maneja las peticiones HTTP GET recibidas por este servlet.
     * @param request Objeto de la petición HTTP entrante.
     * @param response Objeto de la respuesta HTTP a generar.
     * @throws IOException Si ocurre un error de entrada/salida al leer o escribir la petición/respuesta.
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        // Hello verificando que subi todos los cambios
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + message + "</h1>");
        out.println("</body></html>");
    }

    /**
     * Libera los recursos utilizados por el filtro/servlet al finalizar su ciclo de vida.
     */
    public void destroy() {
    }
}