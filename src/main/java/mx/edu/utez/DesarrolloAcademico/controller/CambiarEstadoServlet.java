package mx.edu.utez.DesarrolloAcademico.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mx.edu.utez.DesarrolloAcademico.model.dao.UsuarioListaDao;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/CambiarEstadoServlet")
public class CambiarEstadoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String idStr = request.getParameter("id");
        String estadoStr = request.getParameter("estado");

        if (idStr == null || estadoStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\":\"Parámetros faltantes\"}");
            out.flush();
            return;
        }

        try {
            int idPeriodo = Integer.parseInt(idStr);
            boolean nuevoEstado = Boolean.parseBoolean(estadoStr);

            UsuarioListaDao dao = new UsuarioListaDao();

            if (nuevoEstado && dao.periodoYaVencio(idPeriodo)) {
                response.setStatus(HttpServletResponse.SC_CONFLICT); // 409
                out.write("{\"error\":\"No se puede activar: la fecha de fin ya venció. Modifica la fecha antes de activarlo.\"}");
                out.flush();
                return;
            }

            dao.cambiarEstadoPeriodo(idPeriodo, nuevoEstado);
            response.setStatus(HttpServletResponse.SC_OK);
            out.write("{\"success\":true}");

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\":\"" + e.getMessage() + "\"}");
        } finally {
            out.flush();
        }
    }
}