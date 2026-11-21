package main.java.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import main.java.model.Gerai;
import org.mindrot.jbcrypt.BCrypt;

public class GeraiDAO {
    public List<Gerai> getAll() throws SQLException {
        String sql = "SELECT kode_gerai, nama_gerai, alamat_gerai, password_gerai FROM gerai ORDER BY kode_gerai";
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
        String sql = "SELECT kode_gerai, nama_gerai, alamat_gerai, password_gerai FROM gerai WHERE kode_gerai = ?";
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

    /** Login gerai: kode_gerai + password (dicek dengan BCrypt, sama seperti pelanggan). */
    public Gerai cariLogin(String kode, String password) throws SQLException {
        Gerai gerai = findByKode(kode);
        if (gerai == null || !passwordCocok(password, gerai.getPasswordGerai())) {
            return null;
        }
        return gerai;
    }

    public void insert(Gerai gerai) throws SQLException {
        String sql = "INSERT INTO gerai (kode_gerai, nama_gerai, alamat_gerai, password_gerai) VALUES (?, ?, ?, ?)";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, gerai.getKodeGerai());
            ps.setString(2, gerai.getNamaGerai());
            ps.setString(3, gerai.getAlamatGerai());
            ps.setString(4, hashPassword(gerai.getPasswordGerai()));
            ps.executeUpdate();
        }
    }

    /** Password kosong = password lama tidak diubah. */
    public void update(Gerai gerai, String kodeLama) throws SQLException {
        boolean gantiPassword = gerai.getPasswordGerai() != null && !gerai.getPasswordGerai().isEmpty();
        String sql = gantiPassword
                ? "UPDATE gerai SET kode_gerai = ?, nama_gerai = ?, alamat_gerai = ?, password_gerai = ? WHERE kode_gerai = ?"
                : "UPDATE gerai SET kode_gerai = ?, nama_gerai = ?, alamat_gerai = ? WHERE kode_gerai = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, gerai.getKodeGerai());
            ps.setString(2, gerai.getNamaGerai());
            ps.setString(3, gerai.getAlamatGerai());
            if (gantiPassword) {
                ps.setString(4, hashPassword(gerai.getPasswordGerai()));
                ps.setString(5, kodeLama);
            } else {
                ps.setString(4, kodeLama);
            }
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
        gerai.setPasswordGerai(rs.getString("password_gerai"));
        return gerai;
    }

    /** Hash plaintext sekali. Hash BCrypt yang sudah ada tidak di-hash ulang. */
    private String hashPassword(String password) {
        if (sudahHash(password)) {
            return password;
        }
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private boolean passwordCocok(String plain, String hash) {
        if (plain == null || !sudahHash(hash)) {
            return false;
        }
        try {
            return BCrypt.checkpw(plain, hash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static boolean sudahHash(String password) {
        return password != null
                && (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
    }
}
