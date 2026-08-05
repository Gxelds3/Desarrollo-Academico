package mx.edu.utez.DesarrolloAcademico.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.edu.utez.DesarrolloAcademico.model.Usuario;
import mx.edu.utez.DesarrolloAcademico.model.dao.AgregarDesarrollador_Dao;

import java.io.IOException;



@WebServlet(name = "verDocente", value = "/verDocente")
public class verDocente extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");

        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);

                // REVISA AQUÍ: Asegúrate de que este DAO busque docentes en la base de datos
                AgregarDesarrollador_Dao dao = new AgregarDesarrollador_Dao();
                Usuario dev = dao.obtenerPorId(id);

                if (dev != null) {
                    request.setAttribute("dev", dev);
                    // Nombre exacto del archivo JSP de detalles de docente
                    request.getRequestDispatcher("ver_detalles_docente_de.jsp").forward(request, response);
                    return;
                } else {
                    System.out.println("No se encontró ningún docente con el ID: " + id);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Si falló la consulta o dev fue null, vuelve a la gestión
        response.sendRedirect(request.getContextPath() + "/vista_general_desarrollador_de.jsp");
    }
}