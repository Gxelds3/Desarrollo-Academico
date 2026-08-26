package mx.edu.utez.DesarrolloAcademico.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mx.edu.utez.DesarrolloAcademico.model.Usuario;
import mx.edu.utez.DesarrolloAcademico.model.dao.UsuarioListaDao;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

// Regresa en JSON los docentes pertenecientes a la división del coordinador autenticado
/**
 * Controlador (servlet) que gestiona la lógica de 'ListarDocente_Co' en la capa de presentación del patrón MVC.
 * @author Carlos Apreza Gutierrez
 * @since 2026-07-31
 */
@WebServlet(name = "ListarDocente_Co", value = "/ListarDocente_Co")
public class ListarDocente_Co extends HttpServlet {

    /**
     * Método auxiliar de la clase.
     * @param valor Parámetro `valor`.
     * @return Cadena de texto resultante.
     */
    private String escapar(String valor) {
        if (valor == null) return "";
        return valor.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /**
     * Maneja las peticiones HTTP GET recibidas por este servlet.
     * @param request Objeto de la petición HTTP entrante.
     * @param response Objeto de la respuesta HTTP a generar.
     * @throws ServletException Si ocurre un error al procesar la petición dentro del servlet.
     * @throws IOException Si ocurre un error de entrada/salida al leer o escribir la petición/respuesta.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // 1. Obtener la sesión actual y validar al usuario
        HttpSession session = request.getSession(false);
        Usuario coordinador = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        List<Usuario> docentes = new ArrayList<>();

        // 2. Si hay sesión y el coordinador tiene una división asignada
        if (coordinador != null && coordinador.getIdDivision() != null) {
            int idDivisionCoordinador = coordinador.getIdDivision();
            UsuarioListaDao dao = new UsuarioListaDao();

            // Consultar filtrando por la división del coordinador
            docentes = dao.listarPorRolesYDivision(idDivisionCoordinador, "docente", "coordinador");
        } else {
            // Si no hay sesión válida o no tiene división, responder no autorizado o lista vacía
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        // 3. Construcción del JSON
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < docentes.size(); i++) {
            Usuario u = docentes.get(i);
            if (i > 0) json.append(",");
            json.append("{")
                    .append("\"id\":").append(u.getIdUsuario()).append(",")
                    .append("\"nombre\":\"").append(escapar(u.getNombre())).append("\",")
                    .append("\"apellidoPaterno\":\"").append(escapar(u.getApellidoPaterno())).append("\",")
                    .append("\"apellidoMaterno\":\"").append(escapar(u.getApellidoMaterno())).append("\",")
                    .append("\"idDivision\":").append(u.getIdDivision() != null ? u.getIdDivision() : "null").append(",")
                    .append("\"numeroEmpleado\":\"").append(escapar(u.getNumeroEmpleado())).append("\",")
                    .append("\"telefono\":\"").append(escapar(u.getTelefono())).append("\",")
                    .append("\"correo\":\"").append(escapar(u.getCorreoInstitucional())).append("\",")
                    .append("\"rol\":\"").append(escapar(u.getRol())).append("\",")
                    .append("\"activo\":").append(u.getActivo())
                    .append("}");
        }
        json.append("]");

        out.write(json.toString());
        out.flush();
    }
}