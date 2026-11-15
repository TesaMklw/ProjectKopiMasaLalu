package main.java.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Koneksi PostgreSQL untuk POS Kopi Kenangan.
 * Ubah USER / PASSWORD kalau akun postgres di komputer teman berbeda.
 */
public final class PostgreSQLConnection {
    public static final String URL = "jdbc:postgresql://localhost:5432/kopi_kenangan";
    public static final String USER = "postgres";
    public static final String PASSWORD = "postgres";

    private PostgreSQLConnection() {
    }

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
