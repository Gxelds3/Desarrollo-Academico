package mx.edu.utez.DesarrolloAcademico.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mx.edu.utez.DesarrolloAcademico.model.Usuario;
import mx.edu.utez.DesarrolloAcademico.model.dao.UsuarioDao;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "ActualizarMiCuentaServlet", value = "/ActualizarMiCuentaServlet")
@MultipartConfig
public class ActualizarMiCuentaServlet extends HttpServlet {

    private String escapar(String valor) {
        if (valor == null) return "";
        return valor.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // 1. Validar que la sesión exista y que el usuario esté autenticado
            HttpSession session = request.getSession(false);
            Usuario usuarioSesion = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

            if (usuarioSesion == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"success\": false, \"message\": \"Sesión expirada o no válida.\"}");
                out.flush();
                return;
            }

            // 2. Obtener parámetros del formulario
            String telefono = request.getParameter("telefono");
            String passActual = request.getParameter("passActual");
            String passNueva = request.getParameter("passNueva");

            // Sanitización básica
            telefono = (telefono != null) ? telefono.trim() : "";
            passActual = (passActual != null) ? passActual.trim() : "";
            passNueva = (passNueva != null) ? passNueva.trim() : "";

            // 3. REGLA CLAVE: La contraseña actual es OBLIGATORIA siempre
            if (passActual.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"success\": false, \"message\": \"Debes ingresar tu contraseña actual para confirmar cualquier cambio.\"}");
                out.flush();
                return;
            }

            // 4. Validar formato de Teléfono (10 dígitos)
            if (!telefono.matches("^\\d{10}$")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"success\": false, \"message\": \"El teléfono debe contener exactamente 10 dígitos numéricos.\"}");
                out.flush();
                return;
            }

            UsuarioDao usuarioDao = new UsuarioDao();

            // 5. Verificar que la contraseña actual sea correcta (comparando contra sesión)
            if (!usuarioSesion.getContrasena().equals(mx.edu.utez.DesarrolloAcademico.utils.HashUtils.hashSHA256(passActual))) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"success\": false, \"message\": \"La contraseña actual es incorrecta.\"}");
                out.flush();
                return;
            }

            boolean cambioExitoso = false;
            String mensajeError = "No se pudieron actualizar los datos.";

            // 6. Lógica de Actualización
            if (!passNueva.isEmpty()) {
                // A) Intenta cambiar teléfono Y contraseña
                if (passNueva.length() < 12 || passNueva.length() > 15) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.write("{\"success\": false, \"message\": \"La nueva contraseña debe contener entre 12 y 15 caracteres.\"}");
                    out.flush();
                    return;
                }

                cambioExitoso = usuarioDao.actualizarPerfil(usuarioSesion.getIdUsuario(), telefono, passNueva);

                if (cambioExitoso) {
                    usuarioSesion.setTelefono(telefono);
                    usuarioSesion.setContrasena(mx.edu.utez.DesarrolloAcademico.utils.HashUtils.hashSHA256(passNueva));
                    session.setAttribute("usuario", usuarioSesion);
                } else {
                    mensajeError = "Ocurrió un error en la base de datos al actualizar la contraseña y teléfono.";
                }

            } else {
                // B) Solo actualiza su teléfono (previa confirmación de passActual)
                cambioExitoso = usuarioDao.actualizarPerfil(usuarioSesion.getIdUsuario(), telefono, null);

                if (cambioExitoso) {
                    usuarioSesion.setTelefono(telefono);
                    session.setAttribute("usuario", usuarioSesion);
                } else {
                    mensajeError = "Ocurrió un error en la base de datos al actualizar el teléfono.";
                }
            }

            // 7. Respuesta JSON final
            if (cambioExitoso) {
                out.write("{\"success\": true, \"message\": \"Perfil actualizado correctamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write("{\"success\": false, \"message\": \"" + escapar(mensajeError) + "\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"success\": false, \"message\": \"Error inesperado: " + escapar(e.getMessage()) + "\"}");
        }
        out.flush();
    }
}