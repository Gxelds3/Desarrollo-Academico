package mx.edu.utez.DesarrolloAcademico.model.dao;

import mx.edu.utez.DesarrolloAcademico.model.Evento;
import mx.edu.utez.DesarrolloAcademico.model.Usuario;
import mx.edu.utez.DesarrolloAcademico.model.agregarEvento_co;
import mx.edu.utez.DesarrolloAcademico.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de acceso a datos (DAO) encargada de las operaciones CRUD sobre la base de datos relacionadas con la entidad correspondiente a 'UsuarioDao'.
 * @author Gael Itzaya Velez Reyez
 * @since 2026-07-16
 */
public class UsuarioDao {

    // Método auxiliar para mapear el ResultSet al objeto Usuario
    /**
     * Convierte una fila del ResultSet recibido en un objeto del modelo correspondiente.
     * @param rs Resultado (ResultSet) de la consulta SQL ejecutada.
     * @return Objeto Usuario encontrado, o `null` si no existe.
     * @throws SQLException Si ocurre un error al ejecutar la operación en la base de datos.
     */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
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
        usuario.setContrasena(rs.getString("contrasena"));
        usuario.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        usuario.setActivo(rs.getInt("activo"));

        int creadoPor = rs.getInt("creado_por");
        if (!rs.wasNull()) {
            usuario.setCreadoPor(creadoPor);
        }

        return usuario;
    }

    /**
     * Busca en la base de datos el registro cuyo identificador coincide con el recibido.
     * @param idUsuario Identificador único del usuario.
     * @return Objeto Usuario encontrado, o `null` si no existe.
     */
    public Usuario buscarPorId(int idUsuario) {
        Usuario usuario = null;
        String query = "SELECT * FROM usuario WHERE id_usuario = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) return null;

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en buscarPorId: " + e.getMessage());
            e.printStackTrace();
        }
        return usuario;
    }

    /**
     * Verifica las credenciales recibidas contra la base de datos y devuelve el usuario autenticado si son correctas.
     * @param credencial Correo institucional o número de empleado usado para autenticar.
     * @param contrasena Contraseña del usuario.
     * @return Objeto Usuario encontrado, o `null` si no existe.
     */
    public Usuario login(String credencial, String contrasena) {
        Usuario usuario = null;
        String query = "SELECT * FROM usuario WHERE (correo_institucional = ? OR numero_empleado = ?) AND contrasena = ? AND activo = 1";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) return null;

            ps.setString(1, credencial);
            ps.setString(2, credencial);
            ps.setString(3, contrasena);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    /**
     * Busca en la base de datos el/los registro(s) que cumplen el criterio indicado.
     * @param credencial Correo institucional o número de empleado usado para autenticar.
     * @return Objeto Usuario encontrado, o `null` si no existe.
     */
    public Usuario buscarPorEmailOEmpleado(String credencial) {
        Usuario usuario = null;
        String query = "SELECT * FROM usuario WHERE correo_institucional = ? OR numero_empleado = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) return null;

            ps.setString(1, credencial);
            ps.setString(2, credencial);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    /**
     * Método auxiliar de la clase.
     * @param idUsuario Identificador único del usuario.
     * @param codigo Parámetro `codigo`.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean guardarCodigoRecuperacion(int idUsuario, String codigo) {
        String query = "INSERT INTO token_recuperacion (id_usuario, codigo_token, utilizado, fecha_expiracion) " +
                "VALUES (?, ?, 0, CURRENT_TIMESTAMP + INTERVAL '15' MINUTE)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) return false;

            ps.setInt(1, idUsuario);
            ps.setString(2, codigo);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Verifica que la condición o dato indicado sea correcto.
     * @param codigo Parámetro `codigo`.
     * @return Objeto Usuario encontrado, o `null` si no existe.
     */
    public Usuario verificarCodigo(String codigo) {
        Usuario usuario = null;
        String query = "SELECT u.* FROM usuario u " +
                "JOIN token_recuperacion t ON u.id_usuario = t.id_usuario " +
                "WHERE t.codigo_token = ? AND t.utilizado = 0 AND t.fecha_expiracion > CURRENT_TIMESTAMP";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) return null;

            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    /**
     * Actualiza en la base de datos el registro indicado con los nuevos datos recibidos.
     * @param idUsuario Identificador único del usuario.
     * @param nuevaPassword Parámetro `nuevaPassword`.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean actualizarPasswordLimpiaCodigo(int idUsuario, String nuevaPassword) {
        Connection con = null;
        boolean exitoso = false;

        try {
            con = DatabaseConnection.getConnection();
            if (con == null) return false;

            con.setAutoCommit(false);

            String queryUpdatePass = "UPDATE usuario SET contrasena = ? WHERE id_usuario = ?";
            try (PreparedStatement ps1 = con.prepareStatement(queryUpdatePass)) {
                ps1.setString(1, nuevaPassword);
                ps1.setInt(2, idUsuario);
                ps1.executeUpdate();
            }

            String queryUpdateToken = "UPDATE token_recuperacion SET utilizado = 1 WHERE id_usuario = ?";
            try (PreparedStatement ps2 = con.prepareStatement(queryUpdateToken)) {
                ps2.setInt(1, idUsuario);
                ps2.executeUpdate();
            }

            con.commit();
            exitoso = true;
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                DatabaseConnection.closeConnection(con);
            }
        }
        return exitoso;
    }

    /**
     * Registra un nuevo registro en la base de datos con los datos recibidos.
     * @param usuario Objeto Usuario con los datos a utilizar.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean registrarUsuario(Usuario usuario) {
        boolean estado = false;
        String query = "INSERT INTO usuario (nombre, apellido_paterno, apellido_materno, rol, id_division, numero_empleado, telefono, correo_institucional, contrasena, fecha_registro, activo, creado_por) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, 1, ?)";

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

            if (usuario.getCreadoPor() != null) {
                ps.setInt(10, usuario.getCreadoPor());
            } else {
                ps.setNull(10, java.sql.Types.INTEGER);
            }

            estado = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return estado;
    }

    /**
     * Actualiza en la base de datos el registro indicado con los nuevos datos recibidos.
     * @param idUsuario Identificador único del usuario.
     * @param actualPassword Parámetro `actualPassword`.
     * @param nuevaPassword Parámetro `nuevaPassword`.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean actualizarPasswordEnCuenta(int idUsuario, String actualPassword, String nuevaPassword) {
        String query = "UPDATE usuario SET contrasena = ? WHERE id_usuario = ? AND contrasena = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) return false;

            ps.setString(1, mx.edu.utez.DesarrolloAcademico.utils.HashUtils.hashSHA256(nuevaPassword));
            ps.setInt(2, idUsuario);
            ps.setString(3, mx.edu.utez.DesarrolloAcademico.utils.HashUtils.hashSHA256(actualPassword));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Obtiene y devuelve el dato solicitado.
     * @param idDivision Identificador de la división académica.
     * @return Lista con los registros obtenidos (vacía si no hay resultados).
     */
    public List<Evento> obtenerProximosEventos(Integer idDivision) {
        List<Evento> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT ID_EVENTO, NOMBRE, FECHA_INICIO, FECHA_FIN FROM EVENTO ");
        sql.append("WHERE FECHA_INICIO > SYSDATE ");

        if (idDivision != null && idDivision > 0) {
            sql.append("AND ID_DIVISION = ? ");
        }
        sql.append("ORDER BY FECHA_INICIO ASC");

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql.toString()) : null) {

            if (con == null || ps == null) return lista;

            if (idDivision != null && idDivision > 0) {
                ps.setInt(1, idDivision);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Evento evento = new Evento();
                    evento.setID(rs.getInt("ID_EVENTO"));
                    evento.setNombre(rs.getString("NOMBRE"));

                    java.sql.Timestamp fInicio = rs.getTimestamp("FECHA_INICIO");
                    java.sql.Timestamp fFin = rs.getTimestamp("FECHA_FIN");

                    evento.setFecha_Inicio(fInicio);
                    evento.setFecha_Fin(fFin);

                    lista.add(evento);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar próximos eventos: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Elimina de forma permanente el registro indicado de la base de datos.
     * @param idUsuario Identificador único del usuario.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean eliminarUsuario(int idUsuario) {
        String sql = "DELETE FROM usuario WHERE ID_USUARIO = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (con == null || ps == null) return false;

            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene y devuelve el dato solicitado.
     * @param idUsuario Identificador único del usuario.
     * @return Lista con los registros obtenidos (vacía si no hay resultados).
     */
    public List<Evento> obtenerMisEventos(int idUsuario) {
        List<Evento> lista = new ArrayList<>();
        String sql = "SELECT ID_EVENTO, NOMBRE, FECHA_INICIO, FECHA_FIN, TIPO_EVENTO, INSTITUCION " +
                "FROM EVENTO WHERE CREADO_POR = ? ORDER BY FECHA_INICIO ASC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (con == null || ps == null) return lista;

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Evento e = new Evento();
                    e.setID(rs.getInt("ID_EVENTO"));
                    e.setNombre(rs.getString("NOMBRE"));
                    e.setFecha_Inicio(rs.getTimestamp("FECHA_INICIO"));
                    e.setFecha_Fin(rs.getTimestamp("FECHA_FIN"));
                    e.setTipo_Evento(rs.getString("TIPO_EVENTO"));
                    e.setInstitucion(rs.getString("INSTITUCION"));

                    lista.add(e);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error al listar mis eventos: " + ex.getMessage());
            ex.printStackTrace();
        }
        return lista;
    }

    /**
     * Método auxiliar de la clase.
     * @return Lista con los registros obtenidos (vacía si no hay resultados).
     */
    public List<Usuario> GestionDocentes() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT ID_USUARIO, NOMBRE, APELLIDO_PATERNO, APELLIDO_MATERNO, " +
                "CORREO_INSTITUCIONAL, ID_DIVISION, NUMERO_EMPLEADO, ACTIVO, CONTRASENA " +
                "FROM usuario WHERE LOWER(ROL) = 'docente' ORDER BY NOMBRE ASC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null;
             ResultSet rs = ps != null ? ps.executeQuery() : null) {

            if (con == null || ps == null || rs == null) return lista;

            while (rs.next()) {
                Usuario U = new Usuario();
                U.setIdUsuario(rs.getInt("ID_USUARIO"));
                U.setNombre(rs.getString("NOMBRE"));
                U.setApellidoPaterno(rs.getString("APELLIDO_PATERNO"));
                U.setApellidoMaterno(rs.getString("APELLIDO_MATERNO"));
                U.setCorreoInstitucional(rs.getString("CORREO_INSTITUCIONAL"));
                U.setIdDivision(rs.getInt("ID_DIVISION"));
                U.setNumeroEmpleado(rs.getString("NUMERO_EMPLEADO"));
                U.setActivo(rs.getInt("ACTIVO"));
                U.setContrasena(rs.getString("CONTRASENA"));

                lista.add(U);
            }
        } catch (SQLException ex) {
            System.err.println("Error al listar docentes: " + ex.getMessage());
            ex.printStackTrace();
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
            System.err.println("Error al cambiar estado de usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Obtiene y devuelve el dato solicitado.
     * @param idUsuario Identificador único del usuario.
     * @return Objeto Usuario encontrado, o `null` si no existe.
     */
    public Usuario obtenerDocentePorId(int idUsuario) {
        Usuario u = null;
        String sql = "SELECT ID_USUARIO, NOMBRE, APELLIDO_PATERNO, APELLIDO_MATERNO, " +
                "CORREO_INSTITUCIONAL, ID_DIVISION, NUMERO_EMPLEADO, TELEFONO, ACTIVO, CONTRASENA, ROL " +
                "FROM usuario WHERE ID_USUARIO = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (con == null || ps == null) return null;

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    u = new Usuario();
                    u.setIdUsuario(rs.getInt("ID_USUARIO"));
                    u.setNombre(rs.getString("NOMBRE"));
                    u.setApellidoPaterno(rs.getString("APELLIDO_PATERNO"));
                    u.setApellidoMaterno(rs.getString("APELLIDO_MATERNO") != null ? rs.getString("APELLIDO_MATERNO") : "");
                    u.setCorreoInstitucional(rs.getString("CORREO_INSTITUCIONAL"));
                    u.setIdDivision(rs.getInt("ID_DIVISION"));
                    u.setNumeroEmpleado(rs.getString("NUMERO_EMPLEADO"));
                    u.setTelefono(rs.getString("TELEFONO") != null ? rs.getString("TELEFONO") : "");
                    u.setActivo(rs.getInt("ACTIVO"));
                    u.setContrasena(rs.getString("CONTRASENA"));
                    u.setRol(rs.getString("ROL"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error SQL al obtener docente: " + ex.getMessage());
            ex.printStackTrace();
        }
        return u;
    }

    /**
     * Cambia el estado (activo/inactivo u otro) del registro indicado en la base de datos.
     * @param idUsuario Identificador único del usuario.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean cambiarEstado(int idUsuario) {
        String query = "UPDATE usuario SET activo = CASE WHEN activo = 1 THEN 0 ELSE 1 END WHERE id_usuario = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) return false;

            ps.setInt(1, idUsuario);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al alternar estado de usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cambia el estado (activo/inactivo u otro) del registro indicado en la base de datos.
     * @param idUsuario Identificador único del usuario.
     * @param nuevoEstado Nuevo valor de estado a asignar.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean cambiarEstadoUsuario(int idUsuario, int nuevoEstado) {
        return cambiarEstado(idUsuario, nuevoEstado);
    }

    /**
     * Cambia el estado (activo/inactivo u otro) del registro indicado en la base de datos.
     * @param idUsuario Identificador único del usuario.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean cambiarEstadoUsuario(int idUsuario) {
        return cambiarEstado(idUsuario);
    }

    /**
     * Actualiza en la base de datos el registro indicado con los nuevos datos recibidos.
     * @param idUsuario Identificador único del usuario.
     * @param telefono Número de teléfono de contacto.
     * @param nuevaContrasena Parámetro `nuevaContrasena`.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean actualizarPerfil(int idUsuario, String telefono, String nuevaContrasena) {
        StringBuilder sql = new StringBuilder("UPDATE usuario SET ");
        boolean tieneTelefono = (telefono != null && !telefono.trim().isEmpty());
        boolean tienePass = (nuevaContrasena != null && !nuevaContrasena.trim().isEmpty());

        if (!tieneTelefono && !tienePass) {
            return false;
        }

        if (tieneTelefono && tienePass) {
            sql.append("telefono = ?, contrasena = ? ");
        } else if (tieneTelefono) {
            sql.append("telefono = ? ");
        } else {
            sql.append("contrasena = ? ");
        }
        sql.append("WHERE id_usuario = ?");

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql.toString()) : null) {

            if (con == null || ps == null) return false;

            int paramIndex = 1;
            if (tieneTelefono) {
                ps.setString(paramIndex++, telefono.trim());
            }
            if (tienePass) {
                ps.setString(paramIndex++, mx.edu.utez.DesarrolloAcademico.utils.HashUtils.hashSHA256(nuevaContrasena.trim()));
            }
            ps.setInt(paramIndex, idUsuario);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar perfil: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Obtiene y devuelve el dato solicitado.
     * @param idEvento Identificador del evento.
     * @param idUsuario Identificador único del usuario.
     * @return Valor entero resultante.
     */
    public int obtenerIdParticipante(int idEvento, int idUsuario) {
        String sql = "SELECT id_participante FROM participante_evento WHERE id_evento = ? AND id_usuario = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (con == null || ps == null) return -1;

            ps.setInt(1, idEvento);
            ps.setInt(2, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_participante");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar id_participante: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Obtiene y devuelve el dato solicitado.
     * @param idEvento Identificador del evento.
     * @param idUsuario Identificador único del usuario.
     * @return Valor entero resultante.
     */
    public int obtenerOCrearParticipante(int idEvento, int idUsuario) {
        int idParticipante = obtenerIdParticipante(idEvento, idUsuario);
        if (idParticipante != -1) {
            return idParticipante;
        }

        String sql = "INSERT INTO participante_evento (id_evento, id_usuario, registrado_por, fecha_registro) " +
                "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql, new String[]{"ID_PARTICIPANTE"}) : null) {

            if (con != null && ps != null) {
                ps.setInt(1, idEvento);
                ps.setInt(2, idUsuario);
                ps.setInt(3, idUsuario); // REGISTRADO_POR
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Advertencia getGeneratedKeys Oracle: " + e.getMessage());
        }

        // Reconsulta de rescate por si la secuencia no devolvió el valor mediante el driver
        return obtenerIdParticipante(idEvento, idUsuario);
    }

    /**
     * Consulta y devuelve la lista completa de registros de la entidad correspondiente.
     * @return Lista con los registros obtenidos (vacía si no hay resultados).
     */
    public List<agregarEvento_co> listarTodosEventos() {
        List<agregarEvento_co> lista = new ArrayList<>();
        String sql = "SELECT id_evento, nombre, tipo_evento, institucion, lugar, descripcion, " +
                "fecha_inicio, fecha_fin, modalidad FROM evento ORDER BY id_evento DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null;
             ResultSet rs = ps != null ? ps.executeQuery() : null) {

            if (con == null || ps == null || rs == null) return lista;

            while (rs.next()) {
                agregarEvento_co ev = new agregarEvento_co();
                ev.setId(rs.getInt("id_evento"));
                ev.setNombre(rs.getString("nombre"));
                ev.setTipo(rs.getString("tipo_evento"));
                ev.setInstitucion(rs.getString("institucion"));
                ev.setLugar(rs.getString("lugar"));
                ev.setDescripcion(rs.getString("descripcion"));

                java.sql.Date dInicio = rs.getDate("fecha_inicio");
                java.sql.Date dFin = rs.getDate("fecha_fin");
                ev.setFechaInicio(dInicio != null ? dInicio.toString() : "");
                ev.setFechaFin(dFin != null ? dFin.toString() : "");

                ev.setModalidad(rs.getString("modalidad"));
                lista.add(ev);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar todos los eventos: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Obtiene y devuelve el dato solicitado.
     * @param idUsuario Identificador único del usuario.
     * @return Lista con los registros obtenidos (vacía si no hay resultados).
     */
    public List<Evento> obtenerProximosEventosAsignados(int idUsuario) {
        List<Evento> lista = new ArrayList<>();
        String sql = "SELECT e.ID_EVENTO, e.NOMBRE, e.FECHA_INICIO, e.FECHA_FIN " +
                     "FROM EVENTO e " +
                     "JOIN PARTICIPANTE_EVENTO pe ON e.ID_EVENTO = pe.ID_EVENTO " +
                     "WHERE pe.ID_USUARIO = ? AND e.FECHA_INICIO > SYSDATE " +
                     "ORDER BY e.FECHA_INICIO ASC";
                     
        try (java.sql.Connection con = mx.edu.utez.DesarrolloAcademico.utils.DatabaseConnection.getConnection();
             java.sql.PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {
             
            if (con == null || ps == null) return lista;
            
            ps.setInt(1, idUsuario);
            
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    mx.edu.utez.DesarrolloAcademico.model.Evento evento = new mx.edu.utez.DesarrolloAcademico.model.Evento();
                    evento.setID(rs.getInt("ID_EVENTO"));
                    evento.setNombre(rs.getString("NOMBRE"));
                    
                    java.sql.Timestamp fInicio = rs.getTimestamp("FECHA_INICIO");
                    if (fInicio != null) {
                        evento.setFecha_Inicio(new java.util.Date(fInicio.getTime()));
                    }
                    
                    java.sql.Timestamp fFin = rs.getTimestamp("FECHA_FIN");
                    if (fFin != null) {
                        evento.setFecha_Fin(new java.util.Date(fFin.getTime()));
                    }
                    
                    lista.add(evento);
                }
            }
        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }
}
