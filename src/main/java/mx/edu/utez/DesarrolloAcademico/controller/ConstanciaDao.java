package mx.edu.utez.DesarrolloAcademico.controller;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import mx.edu.utez.DesarrolloAcademico.utils.DatabaseConnection;


/**
 * Clase de acceso a datos (DAO) encargada de las operaciones CRUD sobre la base de datos relacionadas con la entidad correspondiente a 'ConstanciaDao'.
 * @author Carlos Apreza Gutierrez
 * @since 2026-08-07
 */
public class ConstanciaDao {





    /**
     * Verifica que la condición o dato indicado sea correcto.
     * @param idParticipante Parámetro `idParticipante`.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean verificarConstanciaExistente(int idParticipante) {
        String sql = "SELECT COUNT(*) FROM constancia WHERE id_participante = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idParticipante);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Método auxiliar de la clase.
     * @param idParticipante Parámetro `idParticipante`.
     * @param rutaArchivo Parámetro `rutaArchivo`.
     * @param nombreArchivo Parámetro `nombreArchivo`.
     * @param tieneVigencia Parámetro `tieneVigencia`.
     * @param fechaVencimiento Parámetro `fechaVencimiento`.
     * @param subidoPor Parámetro `subidoPor`.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean guardarConstancia(int idParticipante, String rutaArchivo, String nombreArchivo, boolean tieneVigencia, String fechaVencimiento, int subidoPor) {
        if (idParticipante <= 0) return false;

        String query;
        if (tieneVigencia && fechaVencimiento != null && !fechaVencimiento.trim().isEmpty()) {
            query = "INSERT INTO constancia (id_participante, ruta_archivo, nombre_archivo, tiene_vigencia, fecha_vencimiento, subido_por) " +
                    "VALUES (?, ?, ?, 1, TO_DATE(?, 'YYYY-MM-DD'), ?)";
        } else {
            query = "INSERT INTO constancia (id_participante, ruta_archivo, nombre_archivo, tiene_vigencia, subido_por) " +
                    "VALUES (?, ?, ?, 0, ?)";
        }

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, idParticipante);
            ps.setString(2, rutaArchivo);
            ps.setString(3, nombreArchivo);

            if (tieneVigencia && fechaVencimiento != null && !fechaVencimiento.trim().isEmpty()) {
                ps.setString(4, fechaVencimiento.trim());
                ps.setInt(5, subidoPor);
            } else {
                ps.setInt(4, subidoPor);
            }

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error SQL en guardarConstancia: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}