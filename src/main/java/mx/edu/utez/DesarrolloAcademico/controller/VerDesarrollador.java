package mx.edu.utez.DesarrolloAcademico.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.edu.utez.DesarrolloAcademico.model.Usuario;
import mx.edu.utez.DesarrolloAcademico.model.dao.AgregarDesarrollador_Dao;

import java.io.IOException;


/**
 * Controlador (servlet) que gestiona la lógica de 'VerDesarrollador.
 * @author Carlos Apreza Gutierrez
 * @since 2026-08-07
 */
@WebServlet(name = "VerDesarrollador", value = "/VerDesarrollador")
public class VerDesarrollador extends HttpServlet {

    /**
     * Maneja las peticiones HTTP GET recibidas por este servlet.
     * @param request Objeto de la petición HTTP entrante.
     * @param response Objeto de la respuesta HTTP a generar.
     * @throws ServletException Si ocurre un error al procesar la petición dentro del servlet.
     * @throws IOException Si ocurre un error de entrada/salida al leer o escribir la petición/respuesta.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");

        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                AgregarDesarrollador_Dao dao = new AgregarDesarrollador_Dao();
                Usuario dev = dao.obtenerPorId(id);

                if (dev != null) {
                    request.setAttribute("dev", dev);
                    // Reenvía directamente a la vista de detalles
                    request.getRequestDispatcher("detalles_docente_coordinadores_de.jsp").forward(request, response);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        response.sendRedirect("gestion_desarrolladores_de.jsp");
    }
}