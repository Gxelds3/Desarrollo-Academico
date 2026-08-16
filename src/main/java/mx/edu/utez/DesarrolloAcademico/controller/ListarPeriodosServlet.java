package mx.edu.utez.DesarrolloAcademico.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mx.edu.utez.DesarrolloAcademico.model.Periodo;
import mx.edu.utez.DesarrolloAcademico.model.dao.UsuarioListaDao;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/ListarPeriodosServlet")
public class ListarPeriodosServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            UsuarioListaDao dao = new UsuarioListaDao();
            dao.desactivarPeriodosVencidos();
            List<Periodo> listaPeriodos = dao.obtenerTodosLosPeriodos();

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < listaPeriodos.size(); i++) {
                Periodo p = listaPeriodos.get(i);
                String fechaIni = p.getFechaInicio() != null ? p.getFechaInicio().toString() : "";
                String fechaFin = p.getFechaFin()    != null ? p.getFechaFin().toString()    : "";
                json.append("{")
                        .append("\"idPeriodo\":").append(p.getId()).append(",")
                        .append("\"division\":\"").append(p.getDivision() != null ? p.getDivision().replace("\"","\\\"") : "").append("\",")
                        .append("\"fechaInicio\":\"").append(fechaIni).append("\",")
                        .append("\"fechaFin\":\"").append(fechaFin).append("\",")
                        .append("\"activo\":").append(p.isActivo())
                        .append("}");

                if (i < listaPeriodos.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");

            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            out.print(json.toString());
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");
        }
    }
}