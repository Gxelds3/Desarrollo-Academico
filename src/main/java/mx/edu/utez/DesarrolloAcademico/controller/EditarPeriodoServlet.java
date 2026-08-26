package mx.edu.utez.DesarrolloAcademico.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.edu.utez.DesarrolloAcademico.model.dao.UsuarioListaDao;

import java.io.IOException;

/**
 * Servlet controlador que atiende las peticiones HTTP relacionadas con 'EditarPeriodo' dentro de la arquitectura MVC del proyecto.
 * @author Carlos Apreza Gutierrez
 * @since 2026-08-07
 */
@WebServlet("/EditarPeriodoServlet")
public class EditarPeriodoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String idStr = request.getParameter("id");
            String division = request.getParameter("division");
            String fechaInicio = request.getParameter("fechaInicio");
            String fechaFin = request.getParameter("fechaFin");

            if (idStr == null || division == null || fechaInicio == null || fechaFin == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"status\":\"error\", \"message\":\"Datos incompletos para actualizar.\"}");
                return;
            }

            int id = Integer.parseInt(idStr);
            UsuarioListaDao dao = new UsuarioListaDao();

            // VALIDACIÓN: si General está activo, solo se puede editar el periodo de General
            boolean generalActivo = dao.esGeneralActivo();
            if (generalActivo) {
                // Obtener la división a la que pertenece este periodo
                String divisionDelPeriodo = dao.obtenerDivisionDePeriodo(id);
                if (divisionDelPeriodo != null && !divisionDelPeriodo.equalsIgnoreCase("General")) {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    response.getWriter().write("{\"status\":\"error\", \"message\":\"El periodo General está activo. Primero apaga la división General antes de editar otra división individualmente.\"}");
                    return;
                }
            }

            // Validar Duplicado en Editar (Excluyendo ID actual)
            if (dao.existeDivision(division, id)) {
                String nombreDivision = dao.obtenerNombreDivision(division);
                response.setStatus(HttpServletResponse.SC_CONFLICT); // Código HTTP 409
                response.getWriter().write("{\"status\":\"duplicate\", \"message\":\"La división " + nombreDivision + " ya tiene un periodo de carga asignado.\"}");
                return;
            }

            String errorMsg = dao.actualizarPeriodoError(id, division, fechaInicio, fechaFin);

            if (errorMsg == null) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"status\":\"success\", \"message\":\"Periodo actualizado correctamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"status\":\"error\", \"message\":\"" + errorMsg + "\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String errorClean = e.getMessage() != null ? e.getMessage().replace("\"", "'").replace("\r", "").replace("\n", " ") : "Error interno del servidor";
            response.getWriter().write("{\"status\":\"error\", \"message\":\"" + errorClean + "\"}");
        }
    }
}