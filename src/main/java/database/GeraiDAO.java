package main.java.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import main.java.model.Gerai;

public class GeraiDAO {
    public List<Gerai> getAll() throws SQLException {
        String sql = "SELECT kode_gerai, nama_gerai, alamat_gerai FROM gerai ORDER BY kode_gerai";
        List<Gerai> list = new ArrayList<>();
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Gerai findByKode(String kode) throws SQLException {
        String sql = "SELECT kode_gerai, nama_gerai, alamat_gerai FROM gerai WHERE kode_gerai = ?";
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

    public void insert(Gerai gerai) throws SQLException {
        String sql = "INSERT INTO gerai (kode_gerai, nama_gerai, alamat_gerai) VALUES (?, ?, ?)";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, gerai.getKodeGerai());
            ps.setString(2, gerai.getNamaGerai());
            ps.setString(3, gerai.getAlamatGerai());
            ps.executeUpdate();
        }
    }

    public void update(Gerai gerai, String kodeLama) throws SQLException {
        String sql = "UPDATE gerai SET kode_gerai = ?, nama_gerai = ?, alamat_gerai = ? WHERE kode_gerai = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, gerai.getKodeGerai());
            ps.setString(2, gerai.getNamaGerai());
            ps.setString(3, gerai.getAlamatGerai());
            ps.setString(4, kodeLama);
            ps.executeUpdate();
        }
    }

    public void delete(String kode) throws SQLException {
        String sql = "DELETE FROM gerai WHERE kode_gerai = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kode);
            ps.executeUpdate();
        }
    }

    private Gerai map(ResultSet rs) throws SQLException {
        Gerai gerai = new Gerai(rs.getString("nama_gerai"), rs.getString("alamat_gerai"));
        gerai.setKodeGerai(rs.getString("kode_gerai"));
        return gerai;
    }
}
