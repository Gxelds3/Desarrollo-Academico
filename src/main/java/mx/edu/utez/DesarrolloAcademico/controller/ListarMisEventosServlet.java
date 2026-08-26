package mx.edu.utez.DesarrolloAcademico.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mx.edu.utez.DesarrolloAcademico.model.Usuario;
import mx.edu.utez.DesarrolloAcademico.model.agregarEvento_co;
import mx.edu.utez.DesarrolloAcademico.model.dao.UsuarioListaDao;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Servlet controlador que atiende las peticiones HTTP relacionadas con 'ListarMisEventos' dentro de la arquitectura MVC del proyecto.
 * @author Gael Itzaya Velez Reyez
 * @since 2026-08-02
 */
@WebServlet(name = "ListarMisEventosServlet", value = "/ListarMisEventosServlet")
public class ListarMisEventosServlet extends HttpServlet {

    private final UsuarioListaDao dao = new UsuarioListaDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"error\":\"No autenticado\"}");
            out.flush();
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String rol = usuario.getRol() != null ? usuario.getRol().toLowerCase() : "";

        List<agregarEvento_co> eventos;
        if ("desarrollo".equals(rol)) {
            // Desarrollador: todos los eventos sin excepción
            eventos = new mx.edu.utez.DesarrolloAcademico.model.dao.UsuarioDao().listarTodosEventos();
        } else if ("docente".equals(rol)) {
            // Docente: solo los eventos en los que está asignado
            eventos = dao.listarEventosAsignados(usuario.getIdUsuario());
        } else {
            // Coordinador: todos los de su división
            eventos = dao.listarEventosPorDivision1(usuario.getIdDivision());
        }

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
        out.flush();
    }

    /**
     * Método auxiliar de la clase.
     * @param v Parámetro `v`.
     * @return Cadena de texto resultante.
     */
    private String esc(String v) {
        if (v == null) return "";
        return v
                .replace("\\", "\\\\") // Escapa barras invertidas
                .replace("\"", "\\\"") // Escapa comillas dobles
                .replace("\n", "\\n")  // Escapa saltos de línea (AQUÍ ESTÁ EL FIX)
                .replace("\r", "\\r")  // Escapa retornos de carro
                .replace("\t", "\\t"); // Escapa tabulaciones
    }
}
