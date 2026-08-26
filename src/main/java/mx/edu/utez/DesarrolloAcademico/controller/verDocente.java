package mx.edu.utez.DesarrolloAcademico.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.edu.utez.DesarrolloAcademico.model.Usuario;
import mx.edu.utez.DesarrolloAcademico.model.dao.AgregarDesarrollador_Dao;
import mx.edu.utez.DesarrolloAcademico.model.dao.UsuarioDao;

import java.io.IOException;

/**
 * Controlador (servlet) que gestiona la lógica de 'verDocente'.
 * @author Carlos Apreza Gutierrez
 * @since 2026-08-07
 */
@WebServlet(name = "verDocente", value = "/verDocente")
public class verDocente extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");

        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                int idUsuario = Integer.parseInt(idStr.trim());

                UsuarioDao dao = new UsuarioDao();

                Usuario docente = dao.obtenerDocentePorId(idUsuario);

                if (docente != null) {
                    request.setAttribute("dev", docente);

                    jakarta.servlet.http.HttpSession session = request.getSession(false);
                    String redirectPage = "gestion_docente_de.jsp";
                    String forwardPage = "/detalles_docente_coordinadores_de.jsp";
                    
                    if (session != null && session.getAttribute("usuario") != null) {
                        Usuario current = (Usuario) session.getAttribute("usuario");
                        if ("coordinador".equalsIgnoreCase(current.getRol())) {
                            forwardPage = "/detalles_docente_coordinadores_co.jsp";
                            redirectPage = "gestion_docente_co.jsp";
                        }
                    }

                    // Renderizamos la vista de detalles
                    request.getRequestDispatcher(forwardPage).forward(request, response);
                    return;
                } else {
                    System.out.println("[verDocente] No se encontró ningún docente con el ID: " + idUsuario);
                }
            } catch (NumberFormatException e) {
                System.err.println("[verDocente] ID con formato inválido: " + idStr);
            }
        }

        // Si el docente no existe o el ID viene vacío, reorientamos a la lista
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        String redirectPage = "gestion_docente_de.jsp";
        if (session != null && session.getAttribute("usuario") != null) {
            Usuario current = (Usuario) session.getAttribute("usuario");
            if ("coordinador".equalsIgnoreCase(current.getRol())) {
                redirectPage = "gestion_docente_co.jsp";
            }
        }
        response.sendRedirect(request.getContextPath() + "/" + redirectPage);
    }
}