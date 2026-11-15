package main.java.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import main.java.model.Pelanggan;

public class PelangganDAO {
    public List<Pelanggan> getAll() throws SQLException {
        String sql = "SELECT id_pelanggan, nama_pelanggan, no_hp, password_pelanggan "
                + "FROM pelanggan ORDER BY id_pelanggan";
        List<Pelanggan> list = new ArrayList<>();
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Pelanggan findById(int id) throws SQLException {
        String sql = "SELECT id_pelanggan, nama_pelanggan, no_hp, password_pelanggan "
                + "FROM pelanggan WHERE id_pelanggan = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    /** Login peka huruf besar/kecil, sama seperti Pelanggan.signin(). */
    public Pelanggan cariLogin(String nama, String password) throws SQLException {
        String sql = "SELECT id_pelanggan, nama_pelanggan, no_hp, password_pelanggan "
                + "FROM pelanggan WHERE nama_pelanggan = ? AND password_pelanggan = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    /** Nama sudah terdaftar (tidak peka huruf), sama seperti cek signup. */
    public boolean namaTerpakai(String nama) throws SQLException {
        String sql = "SELECT 1 FROM pelanggan WHERE LOWER(nama_pelanggan) = LOWER(?)";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void insert(Pelanggan pelanggan) throws SQLException {
        String sql = "INSERT INTO pelanggan (nama_pelanggan, no_hp, password_pelanggan) VALUES (?, ?, ?)";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pelanggan.getNamaPelanggan());
            ps.setString(2, pelanggan.getNoHp());
            ps.setString(3, pelanggan.getPasswordPelanggan());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    pelanggan.setIdPelanggan(keys.getInt(1));
                }
            }
        }
    }

    public void update(Pelanggan pelanggan) throws SQLException {
        String sql = "UPDATE pelanggan SET nama_pelanggan = ?, no_hp = ?, password_pelanggan = ? "
                + "WHERE id_pelanggan = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pelanggan.getNamaPelanggan());
            ps.setString(2, pelanggan.getNoHp());
            ps.setString(3, pelanggan.getPasswordPelanggan());
            ps.setInt(4, pelanggan.getIdPelanggan());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM pelanggan WHERE id_pelanggan = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Pelanggan map(ResultSet rs) throws SQLException {
        Pelanggan pelanggan = new Pelanggan(rs.getString("nama_pelanggan"));
        pelanggan.setIdPelanggan(rs.getInt("id_pelanggan"));
        pelanggan.setNoHp(rs.getString("no_hp"));
        pelanggan.setPasswordPelanggan(rs.getString("password_pelanggan"));
        return pelanggan;
    }
}
