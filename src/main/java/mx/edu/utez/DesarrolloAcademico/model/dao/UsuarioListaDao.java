package mx.edu.utez.DesarrolloAcademico.model.dao;

import mx.edu.utez.DesarrolloAcademico.model.Periodo;
import mx.edu.utez.DesarrolloAcademico.model.Usuario;
import mx.edu.utez.DesarrolloAcademico.model.agregarEvento_co;
import mx.edu.utez.DesarrolloAcademico.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de acceso a datos (DAO) encargada de las operaciones CRUD sobre la base de datos relacionadas con la entidad correspondiente a 'UsuarioListaDao'.
 * @author Gael Itzaya Velez Reyez
 * @since 2026-08-02
 */
public class UsuarioListaDao {

    private static final int ID_DIVISION_GENERAL = 5;

    /** Devuelve true si el periodo de la división General está ACTIVO (independiente de fechas) */
    public boolean esGeneralActivo() {
        String sql = "SELECT ACTIVO FROM periodo_carga WHERE ID_DIVISION = " + ID_DIVISION_GENERAL + " AND ROWNUM = 1";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null;
             ResultSet rs = ps != null ? ps.executeQuery() : null) {
            if (con == null || ps == null || rs == null) return false;
            if (rs.next()) return rs.getInt("ACTIVO") == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Devuelve el nombre de la división que tiene el periodo con ese id */
    public String obtenerDivisionDePeriodo(int idPeriodo) {
        String sql = "SELECT d.NOMBRE FROM periodo_carga p JOIN division d ON p.ID_DIVISION = d.ID_DIVISION WHERE p.ID_PERIODO = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {
            if (con == null || ps == null) return null;
            ps.setInt(1, idPeriodo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("NOMBRE");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Consulta y devuelve la lista de registros solicitada.
     * @param roles Parámetro `roles`.
     * @return Lista con los registros obtenidos (vacía si no hay resultados).
     */
    public List<Usuario> listarPorRoles(String... roles) {
        List<Usuario> lista = new ArrayList<>();
        if (roles == null || roles.length == 0) return lista;

        StringBuilder sb = new StringBuilder("SELECT id_usuario, nombre, apellido_paterno, apellido_materno, correo_institucional, numero_empleado, id_division, telefono, activo, rol FROM usuario WHERE LOWER(rol) IN (");
        for (int i = 0; i < roles.length; i++) {
            sb.append(i == 0 ? "?" : ",?");
        }
        sb.append(") ORDER BY id_usuario DESC");

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sb.toString()) : null) {

            if (con == null || ps == null) return lista;

            for (int i = 0; i < roles.length; i++) {
                ps.setString(i + 1, roles[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setNombre(rs.getString("nombre"));
                    u.setApellidoPaterno(rs.getString("apellido_paterno"));
                    u.setApellidoMaterno(rs.getString("apellido_materno"));
                    u.setCorreoInstitucional(rs.getString("correo_institucional"));
                    u.setNumeroEmpleado(rs.getString("numero_empleado"));
                    u.setTelefono(rs.getString("telefono"));
                    Object divObj = rs.getObject("id_division");
                    u.setIdDivision(divObj != null ? ((Number) divObj).intValue() : null);
                    u.setActivo(rs.getInt("activo"));
                    u.setRol(rs.getString("rol"));
                    lista.add(u);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Consulta y devuelve la lista de registros solicitada.
     * @param idDivision Identificador de la división académica.
     * @return Lista con los registros obtenidos (vacía si no hay resultados).
     */
    public List<agregarEvento_co> listarEventosPorDivision1(int idDivision) {
        List<agregarEvento_co> lista = new ArrayList<>();

        String query = "SELECT id_evento, nombre, lugar, institucion, tipo_evento, descripcion, fecha_inicio, fecha_fin, modalidad " +
                "FROM evento " +
                "WHERE id_division = ? " +
                "ORDER BY id_evento DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) return lista;

            ps.setInt(1, idDivision);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    agregarEvento_co ev = new agregarEvento_co();
                    ev.setId(rs.getInt("id_evento"));
                    ev.setNombre(rs.getString("nombre"));
                    ev.setLugar(rs.getString("lugar"));
                    ev.setInstitucion(rs.getString("institucion"));
                    ev.setTipo(rs.getString("tipo_evento"));
                    ev.setDescripcion(rs.getString("descripcion"));

                    java.sql.Date dInicio = rs.getDate("fecha_inicio");
                    java.sql.Date dFin    = rs.getDate("fecha_fin");
                    ev.setFechaInicio(dInicio != null ? dInicio.toString() : "");
                    ev.setFechaFin(dFin != null ? dFin.toString() : "");
                    ev.setModalidad(rs.getString("modalidad"));

                    lista.add(ev);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Consulta y devuelve la lista de registros solicitada.
     * @param idUsuario Identificador único del usuario.
     * @return Lista con los registros obtenidos (vacía si no hay resultados).
     */
    public List<agregarEvento_co> listarEventosAsignados(int idUsuario) {
        List<agregarEvento_co> lista = new ArrayList<>();

        String query = "SELECT e.id_evento, e.nombre, e.lugar, e.institucion, e.tipo_evento, " +
                "e.descripcion, e.fecha_inicio, e.fecha_fin, e.modalidad " +
                "FROM evento e " +
                "INNER JOIN participante_evento pe ON e.id_evento = pe.id_evento " +
                "WHERE pe.id_usuario = ? " +
                "ORDER BY e.id_evento DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) return lista;

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    agregarEvento_co ev = new agregarEvento_co();
                    ev.setId(rs.getInt("id_evento"));
                    ev.setNombre(rs.getString("nombre"));
                    ev.setLugar(rs.getString("lugar"));
                    ev.setInstitucion(rs.getString("institucion"));
                    ev.setTipo(rs.getString("tipo_evento"));
                    ev.setDescripcion(rs.getString("descripcion"));

                    java.sql.Date dInicio = rs.getDate("fecha_inicio");
                    java.sql.Date dFin    = rs.getDate("fecha_fin");
                    ev.setFechaInicio(dInicio != null ? dInicio.toString() : "");
                    ev.setFechaFin(dFin != null ? dFin.toString() : "");
                    ev.setModalidad(rs.getString("modalidad"));

                    lista.add(ev);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Próximos eventos para Docente: solo los eventos futuros/en curso a los que está asignado.
     */
    public List<agregarEvento_co> listarProximosEventosDocente(int idUsuario) {
        List<agregarEvento_co> lista = new ArrayList<>();
        String query = "SELECT e.id_evento, e.nombre, e.lugar, e.institucion, e.tipo_evento, " +
                "e.descripcion, e.fecha_inicio, e.fecha_fin, e.modalidad " +
                "FROM evento e " +
                "INNER JOIN participante_evento pe ON e.id_evento = pe.id_evento " +
                "WHERE pe.id_usuario = ? AND e.fecha_fin >= CURRENT_DATE " +
                "ORDER BY e.id_evento DESC";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {
            if (con == null || ps == null) return lista;
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    agregarEvento_co ev = new agregarEvento_co();
                    ev.setId(rs.getInt("id_evento"));
                    ev.setNombre(rs.getString("nombre"));
                    ev.setLugar(rs.getString("lugar"));
                    ev.setInstitucion(rs.getString("institucion"));
                    ev.setTipo(rs.getString("tipo_evento"));
                    ev.setDescripcion(rs.getString("descripcion"));
                    java.sql.Date dInicio = rs.getDate("fecha_inicio");
                    java.sql.Date dFin    = rs.getDate("fecha_fin");
                    ev.setFechaInicio(dInicio != null ? dInicio.toString() : "");
                    ev.setFechaFin(dFin != null ? dFin.toString() : "");
                    ev.setModalidad(rs.getString("modalidad"));
                    lista.add(ev);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Consulta y devuelve la lista de registros solicitada.
     * @param idUsuario Identificador único del usuario.
     * @return Lista con los registros obtenidos (vacía si no hay resultados).
     */
    public List<agregarEvento_co> listarEventosPorUsuario(int idUsuario) {
        List<agregarEvento_co> lista = new ArrayList<>();
        String query = "SELECT e.id_evento, e.nombre, e.lugar, e.institucion, e.tipo_evento, e.descripcion, e.fecha_inicio, e.fecha_fin, e.modalidad " +
                "FROM evento e " +
                "JOIN participante_evento pe ON e.id_evento = pe.id_evento " +
                "WHERE pe.id_usuario = ? ORDER BY e.id_evento DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) return lista;

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    agregarEvento_co ev = new agregarEvento_co();
                    ev.setId(rs.getInt("id_evento"));
                    ev.setNombre(rs.getString("nombre"));
                    ev.setLugar(rs.getString("lugar"));
                    ev.setInstitucion(rs.getString("institucion"));
                    ev.setTipo(rs.getString("tipo_evento"));
                    ev.setDescripcion(rs.getString("descripcion"));
                    java.sql.Date dInicio = rs.getDate("fecha_inicio");
                    java.sql.Date dFin    = rs.getDate("fecha_fin");
                    ev.setFechaInicio(dInicio != null ? dInicio.toString() : "");
                    ev.setFechaFin(dFin != null ? dFin.toString() : "");
                    ev.setModalidad(rs.getString("modalidad"));
                    lista.add(ev);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Consulta y devuelve la lista de registros solicitada.
     * @param idEvento Identificador del evento.
     * @return Lista con los registros obtenidos (vacía si no hay resultados).
     */
    public List<Usuario> listarParticipantesPorEvento(int idEvento) {
        List<Usuario> lista = new ArrayList<>();
        String query = "SELECT u.id_usuario, u.nombre, u.apellido_paterno, u.apellido_materno, " +
                "u.correo_institucional, u.numero_empleado, u.activo, u.rol, " +
                "CASE WHEN EXISTS (" +
                "    SELECT 1 FROM constancia c WHERE c.id_participante = pe.id_participante" +
                ") THEN 1 ELSE 0 END AS entregado " +
                "FROM usuario u " +
                "JOIN participante_evento pe ON u.id_usuario = pe.id_usuario " +
                "WHERE pe.id_evento = ? " +
                "ORDER BY u.id_usuario DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) return lista;

            ps.setInt(1, idEvento);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setNombre(rs.getString("nombre"));
                    u.setApellidoPaterno(rs.getString("apellido_paterno"));
                    u.setApellidoMaterno(rs.getString("apellido_materno"));
                    u.setCorreoInstitucional(rs.getString("correo_institucional"));
                    u.setNumeroEmpleado(rs.getString("numero_empleado"));
                    u.setActivo(rs.getInt("activo"));
                    u.setRol(rs.getString("rol"));
                    u.setEntregado(rs.getInt("entregado") == 1);
                    lista.add(u);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Asigna la relación/registro indicado dentro del sistema.
     * @param idEvento Identificador del evento.
     * @param idUsuario Identificador único del usuario.
     * @param idRegistrador Parámetro `idRegistrador`.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean asignarParticipante(int idEvento, int idUsuario, int idRegistrador) {
        String query = "INSERT INTO participante_evento (id_evento, id_usuario, registrado_por, fecha_registro) VALUES (?, ?, ?, SYSDATE)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) return false;

            ps.setInt(1, idEvento);
            ps.setInt(2, idUsuario);
            ps.setInt(3, idRegistrador);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Elimina la asociación/registro indicado.
     * @param idEvento Identificador del evento.
     * @param idUsuario Identificador único del usuario.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean removerParticipante(int idEvento, int idUsuario) {
        String query = "DELETE FROM participante_evento WHERE id_evento = ? AND id_usuario = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) return false;

            ps.setInt(1, idEvento);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Actualiza en la base de datos el registro indicado con los nuevos datos recibidos.
     * @param u Parámetro `u`.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean actualizarUsuario(Usuario u) {
        boolean estado = false;
        String query = "UPDATE usuario SET nombre = ?, apellido_paterno = ?, apellido_materno = ?, id_division = ?, numero_empleado = ?, telefono = ?, correo_institucional = ? WHERE id_usuario = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) return false;

            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellidoPaterno());
            ps.setString(3, u.getApellidoMaterno());
            if (u.getIdDivision() != null) {
                ps.setInt(4, u.getIdDivision());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.setString(5, u.getNumeroEmpleado());
            ps.setString(6, u.getTelefono());
            ps.setString(7, u.getCorreoInstitucional());
            ps.setInt(8, u.getIdUsuario());

            int filas = ps.executeUpdate();
            estado = (filas > 0);

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return estado;
    }

    /**
     * Elimina de forma permanente el registro indicado de la base de datos.
     * @param idUsuario Identificador único del usuario.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean eliminarUsuario(int idUsuario) {
        String deleteParticipante = "DELETE FROM participante_evento WHERE id_usuario = ?";
        String deleteUsuario = "DELETE FROM usuario WHERE id_usuario = ?";
        try (Connection con = DatabaseConnection.getConnection()) {
            if (con == null) return false;
            con.setAutoCommit(false);
            try (PreparedStatement ps1 = con.prepareStatement(deleteParticipante);
                 PreparedStatement ps2 = con.prepareStatement(deleteUsuario)) {
                ps1.setInt(1, idUsuario);
                ps1.executeUpdate();
                ps2.setInt(1, idUsuario);
                boolean ok = ps2.executeUpdate() > 0;
                con.commit();
                return ok;
            } catch (SQLException e) {
                con.rollback();
                System.err.println("Error al eliminar usuario: " + e.getMessage());
                e.printStackTrace();
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
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
    public boolean cambiarEstado(int idUsuario, int nuevoEstado) {
        String query = "UPDATE usuario SET activo = ? WHERE id_usuario = ?";
        try (Connection con = DatabaseConnection.getConnection()) {
            if (con == null) return false;
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, nuevoEstado);
                ps.setInt(2, idUsuario);
                boolean ok = ps.executeUpdate() > 0;
                // Si se desactiva (nuevoEstado=0), quitar de todos los eventos
                if (ok && nuevoEstado == 0) {
                    try (PreparedStatement psDel = con.prepareStatement(
                            "DELETE FROM participante_evento WHERE id_usuario = ?")) {
                        psDel.setInt(1, idUsuario);
                        psDel.executeUpdate();
                    }
                }
                con.commit();
                return ok;
            } catch (SQLException e) {
                con.rollback();
                System.err.println("Error al cambiar estado del usuario: " + e.getMessage());
                e.printStackTrace();
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error al cambiar estado del usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Método auxiliar de la clase.
     * @param idUsuario Identificador único del usuario.
     * @return Valor entero resultante.
     */
    public int contarEventosAsignados(int idUsuario) {
        String sql = "SELECT COUNT(e.ID_EVENTO) " +
                "FROM EVENTO e " +
                "INNER JOIN PARTICIPANTE_EVENTO pe ON e.ID_EVENTO = pe.ID_EVENTO " +
                "WHERE pe.ID_USUARIO = ? AND e.FECHA_FIN >= SYSDATE";

        int total = 0;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (con == null || ps == null) return total;

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al contar eventos asignados: " + e.getMessage());
            e.printStackTrace();
        }

        return total;
    }

    /**
     * Método auxiliar de la clase.
     * @return Valor entero resultante.
     */
    public int contarEventos() {
        int total = 0;
        String sql = "SELECT COUNT(ID_EVENTO) FROM EVENTO WHERE FECHA_FIN >= SYSDATE";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null;
             ResultSet rs = ps != null ? ps.executeQuery() : null) {

            if (con == null || ps == null || rs == null) return total;

            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al contar eventos no vencidos: " + e.getMessage());
            e.printStackTrace();
        }

        return total;
    }

    /**
     * Método auxiliar de la clase.
     * @param idDivision Identificador de la división académica.
     * @return Valor entero resultante.
     */
    public int contarEventosPorDivision(int idDivision) {
        String sql = "SELECT COUNT(ID_EVENTO) FROM EVENTO WHERE ID_DIVISION = ? AND FECHA_FIN >= SYSDATE";
        int total = 0;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (con == null || ps == null) return total;

            ps.setInt(1, idDivision);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al contar eventos por división: " + e.getMessage());
            e.printStackTrace();
        }

        return total;
    }

    /**
     * Método auxiliar de la clase.
     * @param idDivision Identificador de la división académica.
     * @return Valor entero resultante.
     */
    public int contarDocentesYCoordinadoresPorDivision(int idDivision) {
        String sql = "SELECT COUNT(*) FROM USUARIO WHERE LOWER(ROL) IN ('docente', 'coordinador') AND ID_DIVISION = ?";
        int total = 0;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (con == null || ps == null) return total;

            ps.setInt(1, idDivision);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al contar docentes y coordinadores por división: " + e.getMessage());
            e.printStackTrace();
        }

        return total;
    }

    /**
     * Método auxiliar de la clase.
     * @param idDivision Identificador de la división académica.
     * @return Valor entero resultante.
     */
    public int contarDocentesYCoordinadoresPorDivision1(int idDivision) {
        return contarDocentesYCoordinadoresPorDivision(idDivision);
    }

    /**
     * Método auxiliar de la clase.
     * @return Valor entero resultante.
     */
    public int contarDocentesYCoordinadores() {
        String sql = "SELECT COUNT(*) FROM USUARIO WHERE LOWER(ROL) IN ('docente', 'coordinador')";
        int total = 0;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null;
             ResultSet rs = ps != null ? ps.executeQuery() : null) {

            if (con == null || ps == null || rs == null) return total;

            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al contar docentes y coordinadores: " + e.getMessage());
            e.printStackTrace();
        }

        return total;
    }

    /**
     * Consulta y devuelve la lista de registros solicitada.
     * @param idDivision Identificador de la división académica.
     * @param roles Parámetro `roles`.
     * @return Lista con los registros obtenidos (vacía si no hay resultados).
     */
    public List<Usuario> listarPorRolesYDivision(int idDivision, String... roles) {
        List<Usuario> lista = new ArrayList<>();
        if (roles == null || roles.length == 0) return lista;

        StringBuilder sb = new StringBuilder(
                "SELECT id_usuario, nombre, apellido_paterno, apellido_materno, " +
                        "correo_institucional, numero_empleado, id_division, telefono, activo, rol " +
                        "FROM usuario " +
                        "WHERE id_division = ? AND LOWER(rol) IN ("
        );

        for (int i = 0; i < roles.length; i++) {
            sb.append(i == 0 ? "?" : ", ?");
        }
        sb.append(") ORDER BY id_usuario DESC");

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sb.toString()) : null) {

            if (con == null || ps == null) return lista;

            ps.setInt(1, idDivision);

            for (int i = 0; i < roles.length; i++) {
                ps.setString(i + 2, roles[i] != null ? roles[i].toLowerCase() : "");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setNombre(rs.getString("nombre"));
                    u.setApellidoPaterno(rs.getString("apellido_paterno"));
                    u.setApellidoMaterno(rs.getString("apellido_materno"));
                    u.setCorreoInstitucional(rs.getString("correo_institucional"));
                    u.setNumeroEmpleado(rs.getString("numero_empleado"));
                    u.setTelefono(rs.getString("telefono"));

                    Object divObj = rs.getObject("id_division");
                    u.setIdDivision(divObj != null ? ((Number) divObj).intValue() : null);

                    u.setActivo(rs.getInt("activo"));
                    u.setRol(rs.getString("rol"));

                    lista.add(u);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en listarPorRolesYDivision: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Registra un nuevo registro en la base de datos con los datos recibidos.
     * @param periodo Objeto Periodo con los datos a utilizar.
     * @param idUsuario Identificador único del usuario.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean registrarPeriodo(Periodo periodo, int idUsuario) {
        String sql = "INSERT INTO periodo_carga (ID_DIVISION, FECHA_INICIO, FECHA_FIN, ACTIVO, CREADO_POR) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (con == null || ps == null) return false;

            ps.setInt(1, Integer.parseInt(periodo.getDivision()));
            ps.setDate(2, periodo.getFechaInicio());
            ps.setDate(3, periodo.getFechaFin());
            ps.setInt(4, periodo.isActivo() ? 1 : 0);
            ps.setInt(5, idUsuario);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Consulta y devuelve todos los registros de la entidad correspondiente.
     * @return Lista con los registros obtenidos (vacía si no hay resultados).
     */
    public List<Periodo> obtenerTodosLosPeriodos() {
        List<Periodo> lista = new ArrayList<>();

        String sql = "SELECT p.ID_PERIODO, p.ID_DIVISION, d.NOMBRE AS NOMBRE_DIVISION, p.FECHA_INICIO, p.FECHA_FIN, p.ACTIVO " +
                "FROM periodo_carga p " +
                "JOIN division d ON p.ID_DIVISION = d.ID_DIVISION " +
                "ORDER BY p.ID_PERIODO DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null;
             ResultSet rs = ps != null ? ps.executeQuery() : null) {

            if (con == null || ps == null || rs == null) return lista;

            while (rs.next()) {
                Periodo p = new Periodo();
                p.setId(rs.getInt("ID_PERIODO"));
                p.setIdDivision(rs.getInt("ID_DIVISION"));
                p.setDivision(rs.getString("NOMBRE_DIVISION"));
                p.setFechaInicio(rs.getDate("FECHA_INICIO"));
                p.setFechaFin(rs.getDate("FECHA_FIN"));
                p.setActivo(rs.getInt("ACTIVO") == 1);

                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener periodos: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Elimina de forma permanente el registro indicado de la base de datos.
     * @param idPeriodo Identificador del periodo de carga.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean eliminarPeriodo(int idPeriodo) {
        String sql = "DELETE FROM periodo_carga WHERE ID_PERIODO = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (con == null || ps == null) return false;

            ps.setInt(1, idPeriodo);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar el periodo " + idPeriodo + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Cambia el estado (activo/inactivo u otro) del registro indicado en la base de datos.
     * @param idPeriodo Identificador del periodo de carga.
     * @param nuevoEstado Nuevo valor de estado a asignar.
     * @return Cadena de texto resultante.
     */
    public String cambiarEstadoPeriodoError(int idPeriodo, boolean nuevoEstado) {
        try (Connection con = DatabaseConnection.getConnection()) {
            if (con == null) return "Sin conexion";

            // Obtener division de este periodo
            int idDivision = 0;
            try (PreparedStatement psDiv = con.prepareStatement("SELECT ID_DIVISION, FECHA_INICIO, FECHA_FIN FROM periodo_carga WHERE ID_PERIODO = ?")) {
                psDiv.setInt(1, idPeriodo);
                try (ResultSet rs = psDiv.executeQuery()) {
                    if (rs.next()) {
                        idDivision = rs.getInt("ID_DIVISION");
                    } else {
                        return "Periodo no encontrado";
                    }
                }
            }

            if (!nuevoEstado && idDivision != 5) {
                // Check if General is active
                try (PreparedStatement psGen = con.prepareStatement("SELECT ACTIVO FROM periodo_carga WHERE ID_DIVISION = 5 AND ROWNUM = 1")) {
                    try (ResultSet rsGen = psGen.executeQuery()) {
                        if (rsGen.next() && rsGen.getInt("ACTIVO") == 1) {
                            return "Primero apaga la division General";
                        }
                    }
                }
            }

            // Actualizar este periodo
            try (PreparedStatement psUpd = con.prepareStatement("UPDATE periodo_carga SET ACTIVO = ? WHERE ID_PERIODO = ?")) {
                psUpd.setInt(1, nuevoEstado ? 1 : 0);
                psUpd.setInt(2, idPeriodo);
                psUpd.executeUpdate();
            }

            // Si es General y se enciende, encender y sincronizar fechas de todas las demas
            if (idDivision == 5 && nuevoEstado) {
                try (PreparedStatement psSync = con.prepareStatement(
                        "UPDATE periodo_carga SET ACTIVO = 1, FECHA_INICIO = (SELECT FECHA_INICIO FROM periodo_carga WHERE ID_PERIODO = ?), FECHA_FIN = (SELECT FECHA_FIN FROM periodo_carga WHERE ID_PERIODO = ?) WHERE ID_DIVISION != 5"
                )) {
                    psSync.setInt(1, idPeriodo);
                    psSync.setInt(2, idPeriodo);
                    psSync.executeUpdate();
                }
            }

            return null; // Exito
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * Cambia el estado (activo/inactivo u otro) del registro indicado en la base de datos.
     * @param idPeriodo Identificador del periodo de carga.
     * @param nuevoEstado Nuevo valor de estado a asignar.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean cambiarEstadoPeriodo(int idPeriodo, boolean nuevoEstado) {

        String sql = "UPDATE periodo_carga SET ACTIVO = ? WHERE ID_PERIODO = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (con == null || ps == null) return false;

            ps.setInt(1, nuevoEstado ? 1 : 0);
            ps.setInt(2, idPeriodo);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Actualiza en la base de datos el registro indicado con los nuevos datos recibidos.
     * @param idPeriodo Identificador del periodo de carga.
     * @param division Parámetro `division`.
     * @param fechaInicio Fecha de inicio.
     * @param fechaFin Fecha de fin.
     * @return Cadena de texto resultante.
     */
    public String actualizarPeriodoError(int idPeriodo, String division, String fechaInicio, String fechaFin) {
        try (Connection con = DatabaseConnection.getConnection()) {
            if (con == null) return "Sin conexion";

            // Obtener division destino
            int idDivDestino = 0;
            try (PreparedStatement psDiv = con.prepareStatement("SELECT ID_DIVISION FROM division WHERE (NOMBRE = ? OR TO_CHAR(ID_DIVISION) = ?) AND ROWNUM <= 1")) {
                psDiv.setString(1, division);
                psDiv.setString(2, division);
                try (ResultSet rs = psDiv.executeQuery()) {
                    if (rs.next()) idDivDestino = rs.getInt("ID_DIVISION");
                }
            }

            // Obtener estado actual (antes de actualizar) y si es general activo
            if (idDivDestino != 5) {
                try (PreparedStatement psGen = con.prepareStatement("SELECT ACTIVO FROM periodo_carga WHERE ID_DIVISION = 5 AND ROWNUM = 1")) {
                    try (ResultSet rsGen = psGen.executeQuery()) {
                        if (rsGen.next() && rsGen.getInt("ACTIVO") == 1) {
                            return "No puedes modificar una division individual si General esta encendido. Modifica General.";
                        }
                    }
                }
            }

            String sql = "UPDATE periodo_carga SET " +
                    "ID_DIVISION = ?, " +
                    "FECHA_INICIO = TO_DATE(?, 'YYYY-MM-DD'), " +
                    "FECHA_FIN = TO_DATE(?, 'YYYY-MM-DD'), " +
                    "ACTIVO = CASE WHEN TO_DATE(?, 'YYYY-MM-DD') >= TRUNC(SYSDATE) THEN 1 ELSE 0 END " +
                    "WHERE ID_PERIODO = ?";
                    
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idDivDestino);
                ps.setString(2, fechaInicio);
                ps.setString(3, fechaFin);
                ps.setString(4, fechaFin);
                ps.setInt(5, idPeriodo);
                ps.executeUpdate();
            }

            // Si se actualizo general, sincronizar a los demas
            if (idDivDestino == 5) {
                try (PreparedStatement psSync = con.prepareStatement(
                        "UPDATE periodo_carga SET ACTIVO = CASE WHEN TO_DATE(?, 'YYYY-MM-DD') >= TRUNC(SYSDATE) THEN 1 ELSE 0 END, FECHA_INICIO = TO_DATE(?, 'YYYY-MM-DD'), FECHA_FIN = TO_DATE(?, 'YYYY-MM-DD') WHERE ID_DIVISION != 5"
                )) {
                    psSync.setString(1, fechaFin);
                    psSync.setString(2, fechaInicio);
                    psSync.setString(3, fechaFin);
                    psSync.executeUpdate();
                }
            }

            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * Actualiza en la base de datos el registro indicado con los nuevos datos recibidos.
     * @param idPeriodo Identificador del periodo de carga.
     * @param division Parámetro `division`.
     * @param fechaInicio Fecha de inicio.
     * @param fechaFin Fecha de fin.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean actualizarPeriodo(int idPeriodo, String division, String fechaInicio, String fechaFin) {

        String sql = "UPDATE periodo_carga SET " +
                "ID_DIVISION = (SELECT ID_DIVISION FROM division WHERE (NOMBRE = ? OR TO_CHAR(ID_DIVISION) = ?) AND ROWNUM <= 1), " +
                "FECHA_INICIO = TO_DATE(?, 'YYYY-MM-DD'), " +
                "FECHA_FIN = TO_DATE(?, 'YYYY-MM-DD'), " +
                "ACTIVO = CASE WHEN TO_DATE(?, 'YYYY-MM-DD') >= TRUNC(SYSDATE) THEN 1 ELSE 0 END " +
                "WHERE ID_PERIODO = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (con == null || ps == null) return false;

            ps.setString(1, division);
            ps.setString(2, division);
            ps.setString(3, fechaInicio);
            ps.setString(4, fechaFin);
            ps.setString(5, fechaFin);
            ps.setInt(6, idPeriodo);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar periodo " + idPeriodo + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verifica si el registro indicado ya existe en la base de datos.
     * @param division Parámetro `division`.
     * @param idPeriodoExcluir Parámetro `idPeriodoExcluir`.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean existeDivision(String division, int idPeriodoExcluir) {
        String sql = "SELECT COUNT(*) FROM periodo_carga p " +
                "JOIN division d ON p.ID_DIVISION = d.ID_DIVISION " +
                "WHERE (d.NOMBRE = ? OR TO_CHAR(d.ID_DIVISION) = ?) " +
                "AND (? = 0 OR p.ID_PERIODO != ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (con == null || ps == null) return false;

            ps.setString(1, division);
            ps.setString(2, division);
            ps.setInt(3, idPeriodoExcluir);
            ps.setInt(4, idPeriodoExcluir);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar duplicado de división: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Obtiene y devuelve el dato solicitado.
     * @param divisionOrId Parámetro `divisionOrId`.
     * @return Cadena de texto resultante.
     */
    public String obtenerNombreDivision(String divisionOrId) {
        String sql = "SELECT NOMBRE FROM division WHERE NOMBRE = ? OR TO_CHAR(ID_DIVISION) = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (con == null || ps == null) return divisionOrId;

            ps.setString(1, divisionOrId);
            ps.setString(2, divisionOrId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("NOMBRE");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener nombre de división: " + e.getMessage());
        }
        return divisionOrId;
    }

    /**
     * Método auxiliar de la clase.
     */
    public void desactivarPeriodosVencidos() {
        String sql = "UPDATE periodo_carga SET activo = 0 WHERE fecha_fin < TRUNC(SYSDATE) AND activo = 1";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (con == null || ps == null) return;

            int filasActualizadas = ps.executeUpdate();
            if (filasActualizadas > 0) {
                System.out.println("Periodos desactivados automáticamente: " + filasActualizadas);
            }

        } catch (SQLException e) {
            System.err.println("Error al desactivar periodos vencidos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método auxiliar de la clase.
     * @param idPeriodo Identificador del periodo de carga.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean periodoYaVencio(int idPeriodo) {
        String sql = "SELECT CASE WHEN fecha_fin < TRUNC(SYSDATE) THEN 1 ELSE 0 END AS vencido " +
                "FROM periodo_carga WHERE id_periodo = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (con == null || ps == null) return false;

            ps.setInt(1, idPeriodo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("vencido") == 1;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar vencimiento del periodo: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}