package mx.edu.utez.DesarrolloAcademico.model.dao;

import mx.edu.utez.DesarrolloAcademico.model.agregarEvento_co;
import mx.edu.utez.DesarrolloAcademico.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AgregarEvento_Co {

    public String registrarEventoError(agregarEvento_co evento) {
        Connection con = null;
        PreparedStatement psEvento = null;
        PreparedStatement psDocentes = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnection.getConnection();
            if (con == null) return "No se pudo obtener conexion a la BD";

            con.setAutoCommit(false);

            String queryEvento = "INSERT INTO evento (nombre, lugar, institucion, tipo_evento, descripcion, fecha_inicio, fecha_fin, modalidad, id_division, creado_por, fecha_creacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE)";
            String[] returnId = { "ID_EVENTO" };
            psEvento = con.prepareStatement(queryEvento, returnId);

            psEvento.setString(1, evento.getNombre());
            psEvento.setString(2, evento.getLugar());
            psEvento.setString(3, evento.getInstitucion());
            psEvento.setString(4, evento.getTipo());
            psEvento.setString(5, evento.getDescripcion());
            psEvento.setDate(6, Date.valueOf(LocalDate.parse(evento.getFechaInicio())));
            psEvento.setDate(7, Date.valueOf(LocalDate.parse(evento.getFechaFin())));
            psEvento.setString(8, evento.getModalidad());
            psEvento.setInt(9, evento.getIdDivision());
            psEvento.setInt(10, evento.getCreadoPor());

            psEvento.executeUpdate();
            rs = psEvento.getGeneratedKeys();

            if (rs.next()) {
                int idEventoGenerado = rs.getInt(1);

                if (evento.getDocentesAsignados() != null && !evento.getDocentesAsignados().isEmpty()) {
                    String queryDocentes = "INSERT INTO participante_evento (id_evento, id_usuario, registrado_por, fecha_registro) VALUES (?, ?, ?, SYSDATE)";
                    psDocentes = con.prepareStatement(queryDocentes);

                    for (Integer idDocente : evento.getDocentesAsignados()) {
                        psDocentes.setInt(1, idEventoGenerado);
                        psDocentes.setInt(2, idDocente);
                        psDocentes.setInt(3, evento.getCreadoPor());
                        psDocentes.addBatch();
                    }
                    psDocentes.executeBatch();
                }

                con.commit();
                return null; // OK
            }
            return "No se generó el ID del evento";
        } catch (SQLException e) {
            System.err.println("Error al registrar el evento: " + e.getMessage());
            e.printStackTrace();
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return e.getMessage();
        } finally {
            try {
                if (rs != null) rs.close();
                if (psEvento != null) psEvento.close();
                if (psDocentes != null) psDocentes.close();
                if (con != null) {
                    con.setAutoCommit(true);
                    DatabaseConnection.closeConnection(con);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean registrarEvento(agregarEvento_co evento) {
        boolean estado = false;
        Connection con = null;
        PreparedStatement psEvento = null;
        PreparedStatement psDocentes = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnection.getConnection();
            if (con == null) return false;

            con.setAutoCommit(false);

            String queryEvento = "INSERT INTO evento (nombre, lugar, institucion, tipo_evento, descripcion, fecha_inicio, fecha_fin, modalidad, id_division, creado_por, fecha_creacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE)";
            String[] returnId = { "ID_EVENTO" };
            psEvento = con.prepareStatement(queryEvento, returnId);

            psEvento.setString(1, evento.getNombre());
            psEvento.setString(2, evento.getLugar());
            psEvento.setString(3, evento.getInstitucion());
            psEvento.setString(4, evento.getTipo());
            psEvento.setString(5, evento.getDescripcion());
            psEvento.setDate(6, Date.valueOf(LocalDate.parse(evento.getFechaInicio())));
            psEvento.setDate(7, Date.valueOf(LocalDate.parse(evento.getFechaFin())));
            psEvento.setString(8, evento.getModalidad());
            psEvento.setInt(9, evento.getIdDivision());
            psEvento.setInt(10, evento.getCreadoPor());

            int filasAfectadas = psEvento.executeUpdate();

            if (filasAfectadas > 0) {
                rs = psEvento.getGeneratedKeys();
                if (rs.next()) {
                    int idEventoGenerado = rs.getInt(1);

                    if (evento.getDocentesAsignados() != null && !evento.getDocentesAsignados().isEmpty()) {
                        String queryDocentes = "INSERT INTO participante_evento (id_evento, id_usuario, registrado_por, fecha_registro) VALUES (?, ?, ?, SYSDATE)";
                        psDocentes = con.prepareStatement(queryDocentes);

                        for (Integer idDocente : evento.getDocentesAsignados()) {
                            psDocentes.setInt(1, idEventoGenerado);
                            psDocentes.setInt(2, idDocente);
                            psDocentes.setInt(3, evento.getCreadoPor());
                            psDocentes.addBatch();
                        }
                        psDocentes.executeBatch();
                    }

                    con.commit();
                    estado = true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar el evento: " + e.getMessage());
            e.printStackTrace();
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            //  IMPORTANTE: Cerrar conexión al final para devolverla al pool
            try {
                if (rs != null) rs.close();
                if (psEvento != null) psEvento.close();
                if (psDocentes != null) psDocentes.close();
                if (con != null) {
                    con.setAutoCommit(true);
                    DatabaseConnection.closeConnection(con);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return estado;
    }

    public List<agregarEvento_co> listarEventos1(Integer idDivision) {
        List<agregarEvento_co> eventos = new ArrayList<>();

        StringBuilder query = new StringBuilder(
                "SELECT e.id_evento, e.nombre, e.lugar, e.institucion, e.tipo_evento, " +
                        "e.descripcion, e.fecha_inicio, e.fecha_fin, e.modalidad, d.nombre AS nombre_division " +
                        "FROM evento e LEFT JOIN division d ON e.id_division = d.id_division " +
                        "WHERE e.fecha_fin >= CURRENT_DATE "
        );

        if (idDivision != null) {
            query.append("AND e.id_division = ? ");
        }

        query.append("ORDER BY e.id_evento DESC");

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query.toString()) : null) {

            if (con == null || ps == null) return eventos;

            if (idDivision != null) {
                ps.setInt(1, idDivision);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    agregarEvento_co evento = new agregarEvento_co();
                    evento.setId(rs.getInt("id_evento"));
                    evento.setNombre(rs.getString("nombre"));
                    evento.setLugar(rs.getString("lugar"));
                    evento.setInstitucion(rs.getString("institucion"));
                    evento.setTipo(rs.getString("tipo_evento"));
                    evento.setDescripcion(rs.getString("descripcion"));
                    java.sql.Date dInicio = rs.getDate("fecha_inicio");
                    java.sql.Date dFin = rs.getDate("fecha_fin");
                    evento.setFechaInicio(dInicio != null ? dInicio.toString() : "");
                    evento.setFechaFin(dFin != null ? dFin.toString() : "");
                    evento.setModalidad(rs.getString("modalidad"));
                    evento.setNombreDivision(rs.getString("nombre_division"));
                    eventos.add(evento);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar eventos vigentes: " + e.getMessage());
            e.printStackTrace();
        }
        return eventos;
    }

    public List<agregarEvento_co> listarEventos(Integer idDivision) {
        List<agregarEvento_co> eventos = new ArrayList<>();

        StringBuilder query = new StringBuilder(
                "SELECT e.id_evento, e.nombre, e.lugar, e.institucion, e.tipo_evento, e.descripcion, e.fecha_inicio, e.fecha_fin, e.modalidad, d.nombre AS nombre_division " +
                        "FROM evento e LEFT JOIN division d ON e.id_division = d.id_division " +
                        "WHERE e.fecha_fin >= CURRENT_DATE "
        );

        if (idDivision != null) {
            query.append("AND e.id_division = ? ");
        }

        query.append("ORDER BY e.id_evento DESC");

        //  CORREGIDO: Inserción de la conexión dentro del try-with-resources
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query.toString()) : null) {

            if (con == null || ps == null) return eventos;

            if (idDivision != null) {
                ps.setInt(1, idDivision);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    agregarEvento_co evento = new agregarEvento_co();
                    evento.setId(rs.getInt("id_evento"));
                    evento.setNombre(rs.getString("nombre"));
                    evento.setLugar(rs.getString("lugar"));
                    evento.setInstitucion(rs.getString("institucion"));
                    evento.setTipo(rs.getString("tipo_evento"));
                    evento.setDescripcion(rs.getString("descripcion"));
                    java.sql.Date dInicio = rs.getDate("fecha_inicio");
                    java.sql.Date dFin = rs.getDate("fecha_fin");
                    evento.setFechaInicio(dInicio != null ? dInicio.toString() : "");
                    evento.setFechaFin(dFin != null ? dFin.toString() : "");
                    evento.setModalidad(rs.getString("modalidad"));
                    evento.setNombreDivision(rs.getString("nombre_division"));
                    eventos.add(evento);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar eventos vigentes: " + e.getMessage());
            e.printStackTrace();
        }
        return eventos;
    }

    public boolean eliminarEvento(int idEvento) {
        boolean estado = false;
        Connection con = null;
        PreparedStatement psEvento = null;

        try {
            con = DatabaseConnection.getConnection();
            if (con == null) {
                throw new SQLException("No se pudo obtener conexión a la base de datos.");
            }
            con.setAutoCommit(false);

            psEvento = con.prepareStatement("DELETE FROM evento WHERE id_evento = ?");
            psEvento.setInt(1, idEvento);
            int filasAfectadas = psEvento.executeUpdate();

            con.commit();
            estado = filasAfectadas > 0;
        } catch (Exception e) {
            System.err.println("Error al eliminar el evento: " + e.getMessage());
            e.printStackTrace();
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            // 💡 IMPORTANTE: Devolver la conexión al pool
            try {
                if (psEvento != null) psEvento.close();
                if (con != null) {
                    con.setAutoCommit(true);
                    DatabaseConnection.closeConnection(con);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return estado;
    }

    public String obtenerFechaLimitePorDivision(int idDivision) {
        String query = "SELECT fecha_fin FROM periodo_carga " +
                "WHERE id_division = ? AND activo = 1 " +
                "ORDER BY fecha_fin DESC";

        // 💡 CORREGIDO: Declaración dentro de try-with-resources
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) {
                System.err.println("Error al obtener fecha límite: no se pudo obtener conexión a la base de datos.");
                return null;
            }

            ps.setInt(1, idDivision);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.sql.Date fechaFin = rs.getDate("fecha_fin");
                    return fechaFin != null ? fechaFin.toString() : null;
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener fecha límite del periodo: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public agregarEvento_co obtenerPorId(int idEvento) {
        String query = "SELECT id_evento, nombre, lugar, institucion, tipo_evento, descripcion, fecha_inicio, fecha_fin, modalidad, id_division, creado_por FROM evento WHERE id_evento = ?";

        // 💡 CORREGIDO: Declaración dentro de try-with-resources
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) {
                System.err.println("Error al obtener el evento: no se pudo obtener conexión a la base de datos.");
                return null;
            }

            ps.setInt(1, idEvento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    agregarEvento_co evento = new agregarEvento_co();
                    evento.setId(rs.getInt("id_evento"));
                    evento.setNombre(rs.getString("nombre"));
                    evento.setLugar(rs.getString("lugar"));
                    evento.setInstitucion(rs.getString("institucion"));
                    evento.setTipo(rs.getString("tipo_evento"));
                    evento.setDescripcion(rs.getString("descripcion"));
                    java.sql.Date dInicio = rs.getDate("fecha_inicio");
                    java.sql.Date dFin = rs.getDate("fecha_fin");
                    evento.setFechaInicio(dInicio != null ? dInicio.toString() : "");
                    evento.setFechaFin(dFin != null ? dFin.toString() : "");
                    evento.setModalidad(rs.getString("modalidad"));
                    evento.setIdDivision(rs.getInt("id_division"));
                    evento.setCreadoPor(rs.getInt("creado_por"));
                    return evento;
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener el evento: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean actualizarEvento(agregarEvento_co evento) {
        boolean estado = false;
        Connection con = null;
        PreparedStatement psEvento = null;

        try {
            con = DatabaseConnection.getConnection();
            if (con == null) {
                throw new SQLException("No se pudo obtener conexión a la base de datos.");
            }
            con.setAutoCommit(false);

            String query = "UPDATE evento SET nombre = ?, lugar = ?, institucion = ?, tipo_evento = ?, descripcion = ?, fecha_inicio = ?, fecha_fin = ?, modalidad = ? WHERE id_evento = ?";
            psEvento = con.prepareStatement(query);

            psEvento.setString(1, evento.getNombre());
            psEvento.setString(2, evento.getLugar());
            psEvento.setString(3, evento.getInstitucion());
            psEvento.setString(4, evento.getTipo());
            psEvento.setString(5, evento.getDescripcion());
            psEvento.setTimestamp(6, java.sql.Timestamp.valueOf(evento.getFechaInicio() + " 00:00:00"));
            psEvento.setTimestamp(7, java.sql.Timestamp.valueOf(evento.getFechaFin() + " 00:00:00"));
            psEvento.setString(8, evento.getModalidad());
            psEvento.setInt(9, evento.getId());

            int filasAfectadas = psEvento.executeUpdate();

            con.commit();
            estado = filasAfectadas > 0;
        } catch (Exception e) {
            System.err.println("Error al actualizar el evento: " + e.getMessage());
            e.printStackTrace();
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            //  IMPORTANTE: Devolver la conexión al pool
            try {
                if (psEvento != null) psEvento.close();
                if (con != null) {
                    con.setAutoCommit(true);
                    DatabaseConnection.closeConnection(con);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return estado;
    }

    public List<agregarEvento_co> listarTodosLosEventos() {
        List<agregarEvento_co> eventos = new ArrayList<>();
        String query = "SELECT e.id_evento, e.nombre, e.lugar, e.institucion, e.tipo_evento, " +
                "e.descripcion, e.fecha_inicio, e.fecha_fin, e.modalidad, d.nombre AS nombre_division " +
                "FROM evento e " +
                "LEFT JOIN division d ON e.id_division = d.id_division " +
                "ORDER BY e.id_evento DESC";

        //  CORREGIDO: Declaración dentro de try-with-resources
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) return eventos;

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    agregarEvento_co evento = new agregarEvento_co();
                    evento.setId(rs.getInt("id_evento"));
                    evento.setNombre(rs.getString("nombre"));
                    evento.setLugar(rs.getString("lugar"));
                    evento.setInstitucion(rs.getString("institucion"));
                    evento.setTipo(rs.getString("tipo_evento"));
                    evento.setDescripcion(rs.getString("descripcion"));
                    java.sql.Date dInicio = rs.getDate("fecha_inicio");
                    java.sql.Date dFin = rs.getDate("fecha_fin");
                    evento.setFechaInicio(dInicio != null ? dInicio.toString() : "");
                    evento.setFechaFin(dFin != null ? dFin.toString() : "");
                    evento.setModalidad(rs.getString("modalidad"));
                    evento.setNombreDivision(rs.getString("nombre_division"));
                    eventos.add(evento);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar todos los eventos: " + e.getMessage());
            e.printStackTrace();
        }
        return eventos;
    }
}