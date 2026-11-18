package main.java.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import main.java.model.BukanKopi;
import main.java.model.Kopi;
import main.java.model.MakananPenutup;
import main.java.model.Produk;

public class ProdukDAO {
    private static final String SELECT_PRODUK =
            "SELECT id_produk, tipe, nama_produk, harga_produk, ukuran, info_tambahan, "
                    + "jumlah_produk FROM produk";

    public List<Produk> getAll() throws SQLException {
        return bacaList(SELECT_PRODUK + " ORDER BY id_produk", null);
    }

    public List<Produk> getByTipe(String tipe) throws SQLException {
        return bacaList(SELECT_PRODUK + " WHERE tipe = ? ORDER BY nama_produk", tipe);
    }

    public Produk findById(int id) throws SQLException {
        String sql = SELECT_PRODUK + " WHERE id_produk = ?";
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

    private List<Produk> bacaList(String sql, String tipe) throws SQLException {
        List<Produk> list = new ArrayList<>();
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            if (tipe != null) {
                ps.setString(1, tipe);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public void insert(Produk produk) throws SQLException {
        String sql = "INSERT INTO produk (tipe, nama_produk, harga_produk, ukuran, info_tambahan, "
                + "jumlah_produk) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            isi(ps, produk);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    produk.setIdProduk(keys.getInt(1));
                }
            }
        }
    }

    public void update(Produk produk) throws SQLException {
        String sql = "UPDATE produk SET tipe = ?, nama_produk = ?, harga_produk = ?, ukuran = ?, "
                + "info_tambahan = ?, jumlah_produk = ? WHERE id_produk = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            isi(ps, produk);
            ps.setInt(7, produk.getIdProduk());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM produk WHERE id_produk = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private void isi(PreparedStatement ps, Produk produk) throws SQLException {
        ps.setString(1, produk.getTipeProduk());
        ps.setString(2, produk.getNamaProduk());
        ps.setFloat(3, produk.getHargaProduk());
        String ukuran = null;
        String info = null;
        if (produk instanceof Kopi) {
            Kopi kopi = (Kopi) produk;
            ukuran = kopi.getUkuranMinuman();
            info = kopi.getJenisKopi();
        } else if (produk instanceof BukanKopi) {
            BukanKopi bukan = (BukanKopi) produk;
            ukuran = bukan.getUkuranMinuman();
            info = bukan.getJenisRasa();
        } else if (produk instanceof MakananPenutup) {
            info = ((MakananPenutup) produk).getVariasiMakananPenutup();
        } else {
            throw new SQLException("Tipe produk tidak dikenali: " + produk.getTipeProduk());
        }
        teks(ps, 4, ukuran);
        teks(ps, 5, info);
        ps.setInt(6, produk.getJumlahProduk());
    }

    private static void teks(PreparedStatement ps, int index, String nilai) throws SQLException {
        if (nilai == null || nilai.isBlank()) {
            ps.setNull(index, Types.VARCHAR);
        } else {
            ps.setString(index, nilai);
        }
    }

    static Produk map(ResultSet rs) throws SQLException {
        String tipe = rs.getString("tipe");
        String nama = rs.getString("nama_produk");
        float harga = rs.getFloat("harga_produk");
        String ukuran = rs.getString("ukuran");
        String info = rs.getString("info_tambahan");
        int jumlah = rs.getInt("jumlah_produk");
        if (jumlah <= 0) {
            jumlah = 1;
        }
        Produk produk;
        if ("Kopi".equalsIgnoreCase(tipe)) {
            produk = new Kopi(info, ukuran, nama, harga, jumlah);
        } else if ("BukanKopi".equalsIgnoreCase(tipe)) {
            produk = new BukanKopi(info, ukuran, nama, harga, jumlah);
        } else if ("MakananPenutup".equalsIgnoreCase(tipe)) {
            produk = new MakananPenutup(info, nama, harga, jumlah);
        } else {
            throw new SQLException("Tipe produk tidak dikenali: " + tipe);
        }
        produk.setIdProduk(rs.getInt("id_produk"));
        return produk;
    }
}
