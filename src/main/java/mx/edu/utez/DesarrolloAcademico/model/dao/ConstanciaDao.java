package mx.edu.utez.DesarrolloAcademico.model.dao;

import mx.edu.utez.DesarrolloAcademico.utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase de acceso a datos (DAO) encargada de las operaciones CRUD sobre la base de datos relacionadas con la entidad correspondiente a 'ConstanciaDao'.
 * @author Gael Itzaya Velez Reyez
 * @since 2026-08-02
 */
public class ConstanciaDao {

    // Obtiene el id_participante dado un evento y usuario
    /**
     * Obtiene y devuelve el dato solicitado.
     * @param idEvento Identificador del evento.
     * @param idUsuario Identificador único del usuario.
     * @return Valor entero resultante.
     */
    public int obtenerIdParticipante(int idEvento, int idUsuario) {
        int idParticipante = -1;
        String query = "SELECT id_participante FROM participante_evento WHERE id_evento = ? AND id_usuario = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) {
                System.err.println("⚠️ [ConstanciaDao] Imposible obtener idParticipante: Sin conexión a la BD.");
                return idParticipante;
            }

            ps.setInt(1, idEvento);
            ps.setInt(2, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idParticipante = rs.getInt("id_participante");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idParticipante;
    }

    // Verifica si ya existe una constancia para el participante
    /**
     * Verifica que la condición o dato indicado sea correcto.
     * @param idParticipante Parámetro `idParticipante`.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean verificarConstanciaExistente(int idParticipante) {
        String query = "SELECT id_constancia FROM constancia WHERE id_participante = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) return false;

            ps.setInt(1, idParticipante);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Guarda una constancia con el archivo como BLOB en Oracle
    /**
     * Método auxiliar de la clase.
     * @param idParticipante Parámetro `idParticipante`.
     * @param nombreArchivo Parámetro `nombreArchivo`.
     * @param contenidoArchivo Parámetro `contenidoArchivo`.
     * @param contentType Parámetro `contentType`.
     * @param tieneVigencia Parámetro `tieneVigencia`.
     * @param fechaVencimiento Parámetro `fechaVencimiento`.
     * @param subidoPor Parámetro `subidoPor`.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean guardarConstancia(int idParticipante, String nombreArchivo, byte[] contenidoArchivo,
                                     String contentType, boolean tieneVigencia, String fechaVencimiento, int subidoPor) {
        String query;
        if (tieneVigencia && fechaVencimiento != null && !fechaVencimiento.isEmpty()) {
            query = "INSERT INTO constancia (id_participante, nombre_archivo, contenido_archivo, content_type, tiene_vigencia, fecha_vencimiento, subido_por) " +
                    "VALUES (?, ?, ?, ?, 1, TO_DATE(?, 'YYYY-MM-DD'), ?)";
        } else {
            query = "INSERT INTO constancia (id_participante, nombre_archivo, contenido_archivo, content_type, tiene_vigencia, subido_por) " +
                    "VALUES (?, ?, ?, ?, 0, ?)";
        }

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) return false;

            ps.setInt(1, idParticipante);
            ps.setString(2, nombreArchivo);
            ps.setBytes(3, contenidoArchivo);
            ps.setString(4, contentType);
            if (tieneVigencia && fechaVencimiento != null && !fechaVencimiento.isEmpty()) {
                ps.setString(5, fechaVencimiento);
                ps.setInt(6, subidoPor);
            } else {
                ps.setInt(5, subidoPor);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Alias para coordinador — misma lógica
    /**
     * Método auxiliar de la clase.
     * @param idParticipante Parámetro `idParticipante`.
     * @param nombreArchivo Parámetro `nombreArchivo`.
     * @param contenidoArchivo Parámetro `contenidoArchivo`.
     * @param contentType Parámetro `contentType`.
     * @param tieneVigencia Parámetro `tieneVigencia`.
     * @param fechaVencimiento Parámetro `fechaVencimiento`.
     * @param subidoPor Parámetro `subidoPor`.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean guardarConstanciaCO(int idParticipante, String nombreArchivo, byte[] contenidoArchivo,
                                       String contentType, boolean tieneVigencia, String fechaVencimiento, int subidoPor) {
        if (idParticipante <= 0) {
            System.err.println("Error: idParticipante no es válido (" + idParticipante + ")");
            return false;
        }
        return guardarConstancia(idParticipante, nombreArchivo, contenidoArchivo, contentType, tieneVigencia, fechaVencimiento, subidoPor);
    }

    // Obtiene metadatos de la constancia (SIN el BLOB para no saturar memoria)
    /**
     * Obtiene y devuelve el dato solicitado.
     * @param idParticipante Parámetro `idParticipante`.
     * @return Resultado de tipo `Map<String, Object>`.
     */
    public Map<String, Object> obtenerConstancia(int idParticipante) {
        Map<String, Object> datos = null;
        String query = "SELECT id_constancia, nombre_archivo, content_type, tiene_vigencia, fecha_vencimiento, fecha_subida " +
                "FROM constancia WHERE id_participante = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) return null;

            ps.setInt(1, idParticipante);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    datos = new HashMap<>();
                    datos.put("idConstancia", rs.getInt("id_constancia"));
                    datos.put("nombreArchivo", rs.getString("nombre_archivo"));
                    datos.put("contentType", rs.getString("content_type") != null ? rs.getString("content_type") : "application/octet-stream");
                    datos.put("tieneVigencia", rs.getInt("tiene_vigencia"));
                    datos.put("fechaVencimiento", rs.getTimestamp("fecha_vencimiento"));
                    datos.put("fechaSubida", rs.getTimestamp("fecha_subida"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datos;
    }

    // Obtiene el BLOB del archivo para servirlo como descarga
    /**
     * Obtiene y devuelve el dato solicitado.
     * @param idConstancia Parámetro `idConstancia`.
     * @return Resultado de tipo `byte[]`.
     */
    public byte[] obtenerContenidoArchivo(int idConstancia) {
        String query = "SELECT contenido_archivo FROM constancia WHERE id_constancia = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) return null;

            ps.setInt(1, idConstancia);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBytes("contenido_archivo");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Obtiene nombre y content_type de una constancia (para el servlet de descarga)
    /**
     * Obtiene y devuelve el dato solicitado.
     * @param idConstancia Parámetro `idConstancia`.
     * @return Resultado de tipo `Map<String, String>`.
     */
    public Map<String, String> obtenerMetaDescarga(int idConstancia) {
        String query = "SELECT nombre_archivo, content_type FROM constancia WHERE id_constancia = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {

            if (con == null || ps == null) return null;

            ps.setInt(1, idConstancia);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, String> meta = new HashMap<>();
                    meta.put("nombre", rs.getString("nombre_archivo"));
                    String ct = rs.getString("content_type");
                    meta.put("contentType", ct != null ? ct : "application/octet-stream");
                    return meta;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Elimina la constancia de la BD
    /**
     * Elimina de forma permanente el registro indicado de la base de datos.
     * @param idConstancia Parámetro `idConstancia`.
     * @param idParticipante Parámetro `idParticipante`.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean eliminarConstancia(int idConstancia, int idParticipante) {
        String queryDelete = "DELETE FROM constancia WHERE id_constancia = ? AND id_participante = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(queryDelete) : null) {

            if (con == null || ps == null) return false;

            ps.setInt(1, idConstancia);
            ps.setInt(2, idParticipante);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Método auxiliar de la clase.
     * @param idDivision Identificador de la división académica.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean esPeriodoActivo(int idDivision) {
        boolean activo = false;
        String sql = "SELECT 1 FROM periodo_carga WHERE ID_DIVISION = ? AND ACTIVO = 1 AND TRUNC(SYSDATE) BETWEEN TRUNC(FECHA_INICIO) AND TRUNC(FECHA_FIN)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (con == null || ps == null) return false;

            ps.setInt(1, idDivision);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    activo = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activo;
    }
}