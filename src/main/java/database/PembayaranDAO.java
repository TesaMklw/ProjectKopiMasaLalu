package main.java.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import main.java.model.Pembayaran;

public class PembayaranDAO {
    public List<Pembayaran> getAll() throws SQLException {
        String sql = "SELECT kode_pembayaran, metode_pembayaran FROM pembayaran ORDER BY kode_pembayaran";
        List<Pembayaran> list = new ArrayList<>();
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Pembayaran findByKode(String kode) throws SQLException {
        String sql = "SELECT kode_pembayaran, metode_pembayaran FROM pembayaran WHERE kode_pembayaran = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public void insert(Pembayaran pembayaran) throws SQLException {
        String sql = "INSERT INTO pembayaran (kode_pembayaran, metode_pembayaran) VALUES (?, ?)";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pembayaran.getKodePembayaran());
            ps.setString(2, pembayaran.getMetodePembayaran());
            ps.executeUpdate();
        }
    }

    public void update(Pembayaran pembayaran, String kodeLama) throws SQLException {
        String sql = "UPDATE pembayaran SET kode_pembayaran = ?, metode_pembayaran = ? WHERE kode_pembayaran = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pembayaran.getKodePembayaran());
            ps.setString(2, pembayaran.getMetodePembayaran());
            ps.setString(3, kodeLama);
            ps.executeUpdate();
        }
    }

    public void delete(String kode) throws SQLException {
        String sql = "DELETE FROM pembayaran WHERE kode_pembayaran = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kode);
            ps.executeUpdate();
        }
    }

    private Pembayaran map(ResultSet rs) throws SQLException {
        Pembayaran pembayaran = new Pembayaran(rs.getString("metode_pembayaran"));
        pembayaran.setKodePembayaran(rs.getString("kode_pembayaran"));
        return pembayaran;
    }
}
