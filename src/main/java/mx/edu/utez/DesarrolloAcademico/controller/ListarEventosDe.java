package mx.edu.utez.DesarrolloAcademico.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.edu.utez.DesarrolloAcademico.model.agregarEvento_co;
import mx.edu.utez.DesarrolloAcademico.model.dao.UsuarioDao;
import mx.edu.utez.DesarrolloAcademico.model.dao.UsuarioListaDao;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Controlador (servlet) que gestiona la lógica de 'ListarEventosDe' en la capa de presentación del patrón MVC.
 * @author Carlos Apreza Gutierrez
 * @since 2026-08-16
 */
@WebServlet(name = "ListarEventosDe", value = "/ListarEventosDe")
public class ListarEventosDe extends HttpServlet {

    private final UsuarioListaDao dao = new UsuarioListaDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        UsuarioDao newUser=new UsuarioDao();
        try {
            List<agregarEvento_co> eventos = newUser.listarTodosEventos();

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < eventos.size(); i++) {
                agregarEvento_co ev = eventos.get(i);
                if (i > 0) json.append(",");
                json.append("{")
                        .append("\"id\":").append(ev.getId()).append(",")
                        .append("\"nombre\":\"").append(esc(ev.getNombre())).append("\",")
                        .append("\"tipo\":\"").append(esc(ev.getTipo())).append("\",")
                        .append("\"institucion\":\"").append(esc(ev.getInstitucion())).append("\",")
                        .append("\"lugar\":\"").append(esc(ev.getLugar())).append("\",")
                        .append("\"descripcion\":\"").append(esc(ev.getDescripcion())).append("\",")
                        .append("\"fechaInicio\":\"").append(esc(ev.getFechaInicio())).append("\",")
                        .append("\"fechaFin\":\"").append(esc(ev.getFechaFin())).append("\",")
                        .append("\"modalidad\":\"").append(esc(ev.getModalidad())).append("\"")
                        .append("}");
            }
            json.append("]");

            out.write(json.toString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\":\"" + esc(e.getMessage()) + "\"}");
        } finally {
            out.flush();
        }
    }

    /**
     * Método auxiliar de la clase.
     * @param v Parámetro `v`.
     * @return Cadena de texto resultante.
     */
    private String esc(String v) {
        if (v == null) return "";
        return v.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}