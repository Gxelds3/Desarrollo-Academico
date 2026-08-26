package mx.edu.utez.DesarrolloAcademico.model.dao;

import mx.edu.utez.DesarrolloAcademico.model.Usuario;
import mx.edu.utez.DesarrolloAcademico.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de acceso a datos (DAO) encargada de las operaciones CRUD sobre la base de datos relacionadas con la entidad correspondiente a 'AgregarDesarrollador_Dao'.
 * @author Carlos Apreza Gutierrez
 * @since 2026-07-31
 */
public class AgregarDesarrollador_Dao {

    /**
     * Registra un nuevo registro en la base de datos con los datos recibidos.
     * @param usuario Objeto Usuario con los datos a utilizar.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean registrarDesarrollador(Usuario usuario) {
        boolean estado = false;
        String query = "INSERT INTO usuario " +
                "(nombre, apellido_paterno, apellido_materno, rol, id_division, numero_empleado, telefono, correo_institucional, contrasena, fecha_registro, activo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE, 1)";

        // 💡 CORREGIDO: try-with-resources para liberar la conexión al instante
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) return false;

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellidoPaterno());
            ps.setString(3, usuario.getApellidoMaterno());
            ps.setString(4, usuario.getRol());

            if (usuario.getIdDivision() != null) {
                ps.setInt(5, usuario.getIdDivision());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }

            ps.setString(6, usuario.getNumeroEmpleado());
            ps.setString(7, usuario.getTelefono());
            ps.setString(8, usuario.getCorreoInstitucional());
            ps.setString(9, mx.edu.utez.DesarrolloAcademico.utils.HashUtils.hashSHA256(usuario.getContrasena()));

            int filas = ps.executeUpdate();
            estado = filas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return estado;
    }

    /**
     * Verifica si el registro indicado ya existe en la base de datos.
     * @param correo Correo electrónico.
     * @param numeroEmpleado Número de empleado institucional.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean existeCorreoOEmpleado(String correo, String numeroEmpleado) {
        boolean existe = false;
        String query = "SELECT COUNT(*) FROM usuario WHERE correo_institucional = ? OR numero_empleado = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) return false;

            ps.setString(1, correo);
            ps.setString(2, numeroEmpleado);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    existe = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return existe;
    }

    /**
     * Verifica si el registro indicado ya existe en la base de datos.
     * @param correo Correo electrónico.
     * @param numeroEmpleado Número de empleado institucional.
     * @param idUsuario Identificador único del usuario.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean existeCorreoOEmpleadoExcluyendo(String correo, String numeroEmpleado, int idUsuario) {
        boolean existe = false;
        String query = "SELECT COUNT(*) FROM usuario WHERE (correo_institucional = ? OR numero_empleado = ?) AND id_usuario <> ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) return false;

            ps.setString(1, correo);
            ps.setString(2, numeroEmpleado);
            ps.setInt(3, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    existe = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return existe;
    }

    /**
     * Obtiene y devuelve el dato solicitado.
     * @param idUsuario Identificador único del usuario.
     * @return Objeto Usuario encontrado, o `null` si no existe.
     */
    public Usuario obtenerPorId(int idUsuario) {
        Usuario usuario = null;
        String query = "SELECT * FROM usuario WHERE id_usuario = ? AND rol = 'desarrollo'";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) return null;

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setApellidoPaterno(rs.getString("apellido_paterno"));
                    usuario.setApellidoMaterno(rs.getString("apellido_materno"));
                    usuario.setRol(rs.getString("rol"));

                    int idDivision = rs.getInt("id_division");
                    if (!rs.wasNull()) {
                        usuario.setIdDivision(idDivision);
                    }

                    usuario.setNumeroEmpleado(rs.getString("numero_empleado"));
                    usuario.setTelefono(rs.getString("telefono"));
                    usuario.setCorreoInstitucional(rs.getString("correo_institucional"));
                    usuario.setActivo(rs.getInt("activo"));
                    usuario.setContrasena(rs.getString("contrasena"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuario;
    }

    /**
     * Actualiza en la base de datos el registro indicado con los nuevos datos recibidos.
     * @param usuario Objeto Usuario con los datos a utilizar.
     * @param nuevaContrasena Parámetro `nuevaContrasena`.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     * @throws Exception Si ocurre un error durante la ejecución del método.
     */
    public boolean actualizarDesarrollador(Usuario usuario, String nuevaContrasena) throws Exception {
        boolean estado = false;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        boolean cambiaPassword = nuevaContrasena != null && !nuevaContrasena.trim().isEmpty();

        try {
            con = DatabaseConnection.getConnection();
            if (con == null) {
                throw new Exception("No se pudo obtener conexión con la base de datos.");
            }

            // 1. Validar número de empleado
            String checkNumEmp = "SELECT COUNT(*) FROM usuario WHERE numero_empleado = ? AND id_usuario != ?";
            ps = con.prepareStatement(checkNumEmp);
            ps.setString(1, usuario.getNumeroEmpleado());
            ps.setInt(2, usuario.getIdUsuario());
            rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new Exception("El número de empleado '" + usuario.getNumeroEmpleado() + "' ya se encuentra asignado a otro docente.");
            }
            rs.close();
            ps.close();

            // 2. Validar correo institucional
            String checkCorreo = "SELECT COUNT(*) FROM usuario WHERE correo_institucional = ? AND id_usuario != ?";
            ps = con.prepareStatement(checkCorreo);
            ps.setString(1, usuario.getCorreoInstitucional());
            ps.setInt(2, usuario.getIdUsuario());
            rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new Exception("El correo '" + usuario.getCorreoInstitucional() + "' ya se encuentra registrado por otro docente.");
            }
            rs.close();
            ps.close();

            // 3. Ejecutar Update
            String query = "UPDATE usuario SET " +
                    "nombre = ?, apellido_paterno = ?, apellido_materno = ?, id_division = ?, " +
                    "numero_empleado = ?, telefono = ?, correo_institucional = ?, rol = ?" +
                    (cambiaPassword ? ", contrasena = ?" : "") +
                    " WHERE id_usuario = ?";

            ps = con.prepareStatement(query);
            int i = 1;
            ps.setString(i++, usuario.getNombre());
            ps.setString(i++, usuario.getApellidoPaterno());
            ps.setString(i++, usuario.getApellidoMaterno());

            if (usuario.getIdDivision() != null) {
                ps.setInt(i++, usuario.getIdDivision());
            } else {
                ps.setNull(i++, java.sql.Types.INTEGER);
            }

            ps.setString(i++, usuario.getNumeroEmpleado());
            ps.setString(i++, usuario.getTelefono());
            ps.setString(i++, usuario.getCorreoInstitucional());
            ps.setString(i++, usuario.getRol());

            if (cambiaPassword) {
                ps.setString(i++, mx.edu.utez.DesarrolloAcademico.utils.HashUtils.hashSHA256(nuevaContrasena));
            }

            ps.setInt(i, usuario.getIdUsuario());

            int filas = ps.executeUpdate();
            estado = filas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error al consultar la base de datos: " + e.getMessage());
        } finally {
            // 💡 CORREGIDO: Cierre adecuado devolviendo al pool de HikariCP
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (ps != null) ps.close(); } catch (SQLException ignored) {}
            if (con != null) {
                DatabaseConnection.closeConnection(con);
            }
        }

        return estado;
    }

    /**
     * Consulta y devuelve la lista de registros solicitada.
     * @return Lista con los registros obtenidos (vacía si no hay resultados).
     */
    public List<Usuario> listarDesarrolladores() {
        List<Usuario> lista = new ArrayList<>();
        String query = "SELECT * FROM usuario WHERE rol = 'desarrollo' ORDER BY id_usuario DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) return lista;

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setApellidoPaterno(rs.getString("apellido_paterno"));
                    usuario.setApellidoMaterno(rs.getString("apellido_materno"));
                    usuario.setRol(rs.getString("rol"));

                    int idDivision = rs.getInt("id_division");
                    if (!rs.wasNull()) {
                        usuario.setIdDivision(idDivision);
                    }

                    usuario.setNumeroEmpleado(rs.getString("numero_empleado"));
                    usuario.setTelefono(rs.getString("telefono"));
                    usuario.setCorreoInstitucional(rs.getString("correo_institucional"));
                    usuario.setActivo(rs.getInt("activo"));

                    lista.add(usuario);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * Cambia el estado (activo/inactivo u otro) del registro indicado en la base de datos.
     * @param idUsuario Identificador único del usuario.
     * @param nuevoEstado Nuevo valor de estado a asignar.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean cambiarEstado(int idUsuario, int nuevoEstado) {
        String query = "UPDATE usuario SET activo = ? WHERE id_usuario = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) return false;

            ps.setInt(1, nuevoEstado);
            ps.setInt(2, idUsuario);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Valida los datos recibidos según las reglas de negocio definidas.
     * @param idUsuario Identificador único del usuario.
     * @param passActualIngresada Parámetro `passActualIngresada`.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean validarContrasenaActual(int idUsuario, String passActualIngresada) {
        String sql = "SELECT contrasena FROM usuario WHERE id_usuario = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (con == null || ps == null) return false;

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String passBD = rs.getString("contrasena");
                    String passHash = mx.edu.utez.DesarrolloAcademico.utils.HashUtils.hashSHA256(passActualIngresada);
                    return passBD != null && passBD.equals(passHash);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}