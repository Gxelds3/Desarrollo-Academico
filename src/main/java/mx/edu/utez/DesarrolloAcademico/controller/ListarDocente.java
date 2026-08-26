package mx.edu.utez.DesarrolloAcademico.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.edu.utez.DesarrolloAcademico.model.Usuario;
import mx.edu.utez.DesarrolloAcademico.model.dao.UsuarioListaDao;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

// Regresa en JSON a todos los usuarios con rol 'docente', para la tabla
// de "Gestión de Docentes" (gestion_docentes.jsp).
/**
 * Controlador (servlet) que gestiona la lógica de 'ListarDocente'.
 * @author Carlos Apreza Gutierrez
 * @since 2026-07-31
 */
@WebServlet(name = "ListarDocente", value = "/ListarDocente")
public class ListarDocente extends HttpServlet {

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

        UsuarioListaDao dao = new UsuarioListaDao();
        List<Usuario> docentes = dao.listarPorRoles("docente", "coordinador");

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