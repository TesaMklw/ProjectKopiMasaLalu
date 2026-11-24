package main.java.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import main.java.model.Pelanggan;
import org.mindrot.jbcrypt.BCrypt;

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

    /** Login peka huruf besar/kecil pada nama. Password dicek dengan BCrypt. */
    public Pelanggan cariLogin(String nama, String password) throws SQLException {
        String sql = "SELECT id_pelanggan, nama_pelanggan, no_hp, password_pelanggan "
                + "FROM pelanggan WHERE nama_pelanggan = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pelanggan pelanggan = map(rs);
                    if (passwordCocok(password, pelanggan.getPasswordPelanggan())) {
                        return pelanggan;
                    }
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
            String hash = hashPassword(pelanggan.getPasswordPelanggan());
            pelanggan.setPasswordPelanggan(hash);
            ps.setString(1, pelanggan.getNamaPelanggan());
            ps.setString(2, pelanggan.getNoHp());
            ps.setString(3, hash);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    pelanggan.setIdPelanggan(keys.getInt(1));
                }
            }
        }
    }

    /**
     * Pelanggan yang sedang login hanya mengubah no HP, dan password jika diisi.
     * Password kosong tidak di-hash ulang.
     */
    public void updateAkunSendiri(int idPelanggan, String noHp, String passwordBaru) throws SQLException {
        boolean gantiPassword = passwordBaru != null && !passwordBaru.isBlank();
        String sql = gantiPassword
                ? "UPDATE pelanggan SET no_hp = ?, password_pelanggan = ? WHERE id_pelanggan = ?"
                : "UPDATE pelanggan SET no_hp = ? WHERE id_pelanggan = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, noHp);
            if (gantiPassword) {
                ps.setString(2, hashPassword(passwordBaru));
                ps.setInt(3, idPelanggan);
            } else {
                ps.setInt(2, idPelanggan);
            }
            ps.executeUpdate();
        }
    }

    public void update(Pelanggan pelanggan) throws SQLException {
        String sql = "UPDATE pelanggan SET nama_pelanggan = ?, no_hp = ?, password_pelanggan = ? "
                + "WHERE id_pelanggan = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            String hash = hashPassword(pelanggan.getPasswordPelanggan());
            pelanggan.setPasswordPelanggan(hash);
            ps.setString(1, pelanggan.getNamaPelanggan());
            ps.setString(2, pelanggan.getNoHp());
            ps.setString(3, hash);
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

    private Pelanggan map(ResultSet rs) throws SQLException {
        Pelanggan pelanggan = new Pelanggan(rs.getString("nama_pelanggan"));
        pelanggan.setIdPelanggan(rs.getInt("id_pelanggan"));
        pelanggan.setNoHp(rs.getString("no_hp"));
        pelanggan.setPasswordPelanggan(rs.getString("password_pelanggan"));
        return pelanggan;
    }
}
