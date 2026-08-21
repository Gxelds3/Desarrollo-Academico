package mx.edu.utez.DesarrolloAcademico.controller;

import mx.edu.utez.DesarrolloAcademico.model.agregarEvento_co;
import mx.edu.utez.DesarrolloAcademico.model.dao.AgregarEvento_Co;
import mx.edu.utez.DesarrolloAcademico.model.dao.UsuarioListaDao;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "ListarEventosServlet", value = "/ListarEventosServlet")
public class ListarEventosServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        mx.edu.utez.DesarrolloAcademico.model.Usuario u = null;
        if (session != null) {
            u = (mx.edu.utez.DesarrolloAcademico.model.Usuario) session.getAttribute("usuario");
        }

        if (u == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"error\":\"No autenticado\"}");
            out.flush();
            return;
        }

        String rol = u.getRol() != null ? u.getRol().toLowerCase() : "";
        AgregarEvento_Co dao = new AgregarEvento_Co();
        UsuarioListaDao listaDao = new UsuarioListaDao();

        List<agregarEvento_co> eventos;
        if ("docente".equals(rol)) {
            // Docente: solo próximos eventos en los que está asignado
            eventos = listaDao.listarProximosEventosDocente(u.getIdUsuario());
        } else if ("coordinador".equals(rol)) {
            // Coordinador: próximos de su división
            eventos = dao.listarEventos1(u.getIdDivision());
        } else {
            // Desarrollo: todos los próximos
            eventos = dao.listarEventos1(null);
        }

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < eventos.size(); i++) {
            agregarEvento_co ev = eventos.get(i);
            if (i > 0) json.append(",");
            json.append("{")
                    .append("\"id\":").append(ev.getId()).append(",")
                    .append("\"nombre\":\"").append(escapar(ev.getNombre())).append("\",")
                    .append("\"lugar\":\"").append(escapar(ev.getLugar())).append("\",")
                    .append("\"institucion\":\"").append(escapar(ev.getInstitucion())).append("\",")
                    .append("\"tipo\":\"").append(escapar(ev.getTipo())).append("\",")
                    .append("\"descripcion\":\"").append(escapar(ev.getDescripcion())).append("\",")
                    .append("\"fechaInicio\":\"").append(escapar(ev.getFechaInicio())).append("\",")
                    .append("\"fechaFin\":\"").append(escapar(ev.getFechaFin())).append("\",")
                    .append("\"modalidad\":\"").append(escapar(ev.getModalidad())).append("\",")
                    .append("\"nombreDivision\":\"").append(escapar(ev.getNombreDivision())).append("\"")
                    .append("}");
        }
        json.append("]");

        out.write(json.toString());
        out.flush();
    }

    private String escapar(String valor) {
        if (valor == null) return "";
        return valor.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
}