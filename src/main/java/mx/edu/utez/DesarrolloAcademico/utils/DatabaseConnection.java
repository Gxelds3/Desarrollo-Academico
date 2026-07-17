package mx.edu.utez.DesarrolloAcademico.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static Connection connection;

    private DatabaseConnection() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Properties props = new Properties();
                try (InputStream in = DatabaseConnection.class.getClassLoader().getResourceAsStream("credentials.properties")) {
                    if (in != null) {
                        props.load(in);
                    }
                }

                // Leer de variables de entorno (prioridad alta)
                String dbUser = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : props.getProperty("db.user");
                String dbPass = System.getenv("DB_PASS") != null ? System.getenv("DB_PASS") : props.getProperty("db.password");
                
                // Obtener ruta absoluta del wallet dinámicamente desde resources
                java.net.URL walletUrl = DatabaseConnection.class.getClassLoader().getResource("wallet");
                String walletPath = "";
                if (walletUrl != null) {
                    walletPath = new java.io.File(walletUrl.toURI()).getAbsolutePath();
                }
                
                // Configurar propiedades de conexión
                Properties info = new Properties();
                info.put("user", dbUser);
                info.put("password", dbPass);
                info.put("oracle.net.tns_admin", walletPath);

                // Cargar el driver
                Class.forName("oracle.jdbc.OracleDriver");

                // Conectar usando solo el alias y pasando las propiedades
                String dbUrl = "jdbc:oracle:thin:@desarrolloacademico_medium";
                connection = DriverManager.getConnection(dbUrl, info);
                System.out.println("Conexión a Oracle exitosa!");
            }
        } catch (Exception e) {
            System.err.println("Error en la conexión a Base de Datos: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
