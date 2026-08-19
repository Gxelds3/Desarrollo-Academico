package mx.edu.utez.DesarrolloAcademico.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static HikariDataSource dataSource;

    static {
        try {
            Properties props = new Properties();

            // 1. Cargar archivo credentials.properties
            try (InputStream in = DatabaseConnection.class.getClassLoader().getResourceAsStream("credentials.properties")) {
                if (in != null) {
                    props.load(in);
                }
            }

            String dbUser = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : props.getProperty("db.user");
            String dbPass = System.getenv("DB_PASS") != null ? System.getenv("DB_PASS") : props.getProperty("db.password");

            // 2. Localizar carpeta wallet
            URL walletUrl = DatabaseConnection.class.getClassLoader().getResource("wallet");
            if (walletUrl == null) {
                System.err.println("CRÍTICO: La carpeta 'wallet' no existe en src/main/resources/");
            } else {
                File walletDir = new File(walletUrl.toURI());
                System.setProperty("oracle.net.tns_admin", walletDir.getAbsolutePath());
            }

            // 3. Configuración de HikariCP Pool
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("oracle.jdbc.OracleDriver");
            config.setJdbcUrl("jdbc:oracle:thin:@desarrolloacademico_medium");
            config.setUsername(dbUser);
            config.setPassword(dbPass);

            // Ajustes optimizados para Oracle Cloud
            config.setMaximumPoolSize(5);         // Máximo de conexiones simultáneas
            config.setMinimumIdle(2);            // Conexiones mínimas abiertas en espera
            config.setIdleTimeout(30000);         // 30 segundos
            config.setConnectionTimeout(10000);   // 10 segundos de espera por conexión
            config.setMaxLifetime(600000);       // Reciclar conexiones cada 10 mins

            dataSource = new HikariDataSource(config);
            System.out.println("✅ Pool de conexiones HikariCP iniciado correctamente.");

        } catch (Exception e) {
            System.err.println("❌ Error inicializando el Pool de Conexiones: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            if (dataSource != null) {
                return dataSource.getConnection();
            }
        } catch (SQLException e) {
            System.err.println(" Error al obtener conexión del pool: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void closeConnection(Connection con) {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}