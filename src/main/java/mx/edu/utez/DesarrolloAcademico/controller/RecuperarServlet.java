package mx.edu.utez.DesarrolloAcademico.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mx.edu.utez.DesarrolloAcademico.model.Usuario;
import mx.edu.utez.DesarrolloAcademico.utils.EmailService;
import mx.edu.utez.DesarrolloAcademico.model.dao.UsuarioDao; // Tu DAO para la BD

import java.io.IOException;
import java.util.Random;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import mx.edu.utez.DesarrolloAcademico.model.dao.UsuarioDao;
import mx.edu.utez.DesarrolloAcademico.utils.EmailService;

import java.io.IOException;
import java.util.Random;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import mx.edu.utez.DesarrolloAcademico.model.Usuario;
import mx.edu.utez.DesarrolloAcademico.model.dao.UsuarioDao;
import mx.edu.utez.DesarrolloAcademico.utils.EmailService;

import java.io.IOException;
import java.util.Random;

@WebServlet(name = "RecuperarServlet", value = "/recuperar")
public class RecuperarServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Al entrar por primera vez (GET), mostramos el Paso 1 (solicitar)
        request.setAttribute("step", "solicitar");
        request.getRequestDispatcher("recuperar-contra.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        UsuarioDao dao = new UsuarioDao();

        // ==========================================
        // PASO 1: SOLICITAR CÓDIGO
        // ==========================================
        if ("solicitar".equals(action)) {
            String dato = request.getParameter("dato"); // Correo o número de empleado

            if (dato == null || dato.trim().isEmpty()) {
                request.setAttribute("step", "solicitar");
                request.setAttribute("mensajeError", "Ingresa tu correo o usuario.");
                request.getRequestDispatcher("recuperar-contra.jsp").forward(request, response);
                return;
            }

            Usuario usuario = dao.buscarPorEmailOEmpleado(dato.trim());

            if (usuario != null) {
                // Generar código aleatorio de 6 dígitos
                String codigo = String.format("%06d", new Random().nextInt(1000000));

                // Guardar token en BD
                boolean guardadoBD = dao.guardarCodigoRecuperacion(usuario.getIdUsuario(), codigo);

                if (guardadoBD) {
                    EmailService emailService = new EmailService();
                    boolean correoEnviado = emailService.enviarCodigoRecuperacion(usuario.getCorreoInstitucional(), codigo);

                    if (correoEnviado) {
                        HttpSession session = request.getSession();
                        session.setAttribute("idUsuarioRecuperacion", usuario.getIdUsuario());
                        session.setAttribute("correoRecuperacion", usuario.getCorreoInstitucional());
                        session.setAttribute("tiempoEnvioCodigo", System.currentTimeMillis());

                        request.setAttribute("step", "verificar");
                        request.setAttribute("mensajeInfo", "Código enviado con éxito a tu correo.");
                    } else {
                        request.setAttribute("step", "solicitar");
                        request.setAttribute("mensajeError", "Error al enviar el correo. Verifica tus credenciales de servicio.");
                    }
                } else {
                    request.setAttribute("step", "solicitar");
                    request.setAttribute("mensajeError", "Error al generar el token en la base de datos.");
                }
            } else {
                request.setAttribute("step", "solicitar");
                request.setAttribute("mensajeError", "El correo o usuario ingresado no existe.");
            }
            request.getRequestDispatcher("recuperar-contra.jsp").forward(request, response);
        }

        // ==========================================
        // PASO 2: VERIFICAR CÓDIGO
        // ==========================================
        else if ("verificar".equals(action)) {
            String codigoInput = request.getParameter("codigo");

            if (codigoInput == null || codigoInput.trim().isEmpty()) {
                request.setAttribute("step", "verificar");
                request.setAttribute("mensajeError", "Ingresa el código de seguridad.");
                request.getRequestDispatcher("recuperar-contra.jsp").forward(request, response);
                return;
            }

            // Verificar si el código coincide y no ha expirado
            Usuario usuarioValido = dao.verificarCodigo(codigoInput.trim().toUpperCase());

            if (usuarioValido != null) {
                HttpSession session = request.getSession();
                session.setAttribute("idUsuarioVerificado", usuarioValido.getIdUsuario());

                request.setAttribute("step", "cambiar");
                request.setAttribute("mensajeInfo", "Código verificado correctamente. Ingresa tu nueva contraseña.");
            } else {
                request.setAttribute("step", "verificar");
                request.setAttribute("mensajeError", "El código ingresado es incorrecto o ya ha expirado.");
            }
            request.getRequestDispatcher("recuperar-contra.jsp").forward(request, response);
        }

        // ==========================================
        // PASO 3: CAMBIAR CONTRASEÑA
        // ==========================================
        else if ("cambiar".equals(action)) {
            String pass1 = request.getParameter("pass1");
            String pass2 = request.getParameter("pass2");

            HttpSession session = request.getSession(false);
            Integer idUsuario = (session != null) ? (Integer) session.getAttribute("idUsuarioVerificado") : null;

            if (idUsuario == null) {
                request.setAttribute("step", "solicitar");
                request.setAttribute("mensajeError", "Sesión de recuperación inválida o expirada. Inicia el proceso de nuevo.");
                request.getRequestDispatcher("recuperar-contra.jsp").forward(request, response);
                return;
            }

            if (pass1 == null || pass2 == null || pass1.trim().isEmpty() || pass2.trim().isEmpty()) {
                request.setAttribute("step", "cambiar");
                request.setAttribute("mensajeError", "Las contraseñas no pueden estar vacías.");
                request.getRequestDispatcher("recuperar-contra.jsp").forward(request, response);
                return;
            }

            if (!pass1.equals(pass2)) {
                request.setAttribute("step", "cambiar");
                request.setAttribute("mensajeError", "Las contraseñas no coinciden.");
                request.getRequestDispatcher("recuperar-contra.jsp").forward(request, response);
                return;
            }

            // Actualizar la contraseña y marcar token como utilizado
            boolean actualizada = dao.actualizarPasswordLimpiaCodigo(idUsuario, pass1.trim());

            if (actualizada) {
                // Limpiar la sesión
                session.removeAttribute("idUsuarioRecuperacion");
                session.removeAttribute("correoRecuperacion");
                session.removeAttribute("tiempoEnvioCodigo");
                session.removeAttribute("idUsuarioVerificado");

                response.sendRedirect("login.jsp?exito=password_actualizada");
            } else {
                request.setAttribute("step", "cambiar");
                request.setAttribute("mensajeError", "No se pudo actualizar la contraseña. Inténtalo de nuevo.");
                request.getRequestDispatcher("recuperar-contra.jsp").forward(request, response);
            }
        }
    }
}