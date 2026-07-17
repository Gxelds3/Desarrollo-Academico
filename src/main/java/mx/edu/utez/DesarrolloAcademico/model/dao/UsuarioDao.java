package mx.edu.utez.DesarrolloAcademico.model.dao;

import mx.edu.utez.DesarrolloAcademico.model.Usuario;
import mx.edu.utez.DesarrolloAcademico.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDao {

    public Usuario buscarPorEmailOUsername(String dato) {
        Usuario usuario = null;
        String query = "SELECT * FROM Usuario WHERE email = ? OR username = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
             
            ps.setString(1, dato);
            ps.setString(2, dato);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setId(rs.getInt("id_usuario"));
                    usuario.setUsername(rs.getString("username"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setPassword(rs.getString("pass"));
                    usuario.setCodigoRecuperacion(rs.getString("codigo_recuperacion"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    public boolean guardarCodigoRecuperacion(int idUsuario, String codigo) {
        String query = "UPDATE Usuario SET codigo_recuperacion = ? WHERE id_usuario = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
             
            ps.setString(1, codigo);
            ps.setInt(2, idUsuario);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Usuario verificarCodigo(String codigo) {
        Usuario usuario = null;
        String query = "SELECT * FROM Usuario WHERE codigo_recuperacion = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
             
            ps.setString(1, codigo);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setId(rs.getInt("id_usuario"));
                    usuario.setUsername(rs.getString("username"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setPassword(rs.getString("pass"));
                    usuario.setCodigoRecuperacion(rs.getString("codigo_recuperacion"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    public boolean actualizarPasswordLimpiaCodigo(int idUsuario, String nuevaPassword) {
        String query = "UPDATE Usuario SET pass = ?, codigo_recuperacion = NULL WHERE id_usuario = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
             
            ps.setString(1, nuevaPassword); // Nota: En producción, usar hash (Bcrypt)
            ps.setInt(2, idUsuario);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
