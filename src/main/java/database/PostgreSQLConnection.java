package main.java.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Koneksi PostgreSQL untuk POS Kopi Masa Lalu.
 * Ubah USER / PASSWORD kalau akun postgres di komputer berbeda.
 */
public class PostgreSQLConnection {
    public static final String URL = "jdbc:postgresql://localhost:5432/KopiMasaLalu";
    public static final String USER = "Vionanda";
    public static final String PASSWORD = "vionanda";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "Driver org.postgresql.Driver tidak ditemukan. Tambahkan postgresql-42.7.14.jar.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
