package main.java.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import main.java.model.BukanKopi;
import main.java.model.Kopi;
import main.java.model.MakananPenutup;
import main.java.model.Minuman;
import main.java.model.Produk;

/**
 * Katalog produk. Skema database memakai satu tabel {@code produk} dengan
 * kolom {@code tipe}, {@code ukuran}, {@code info_tambahan}, dan
 * {@code catatan_produk} (single-table inheritance), sesuai hierarki kelas:
 * Produk → Minuman → Kopi / BukanKopi, dan Produk → MakananPenutup.
 */
public class ProdukDAO {
    private static final String SELECT_PRODUK =
            "SELECT p.id_produk, p.nama_produk, p.harga_produk, p.jumlah_produk, "
                    + "p.ukuran AS ukuran, "
                    + "p.info_tambahan AS info_tambahan, "
                    + "p.catatan_produk AS catatan_produk, "
                    + "p.tipe AS tipe "
                    + "FROM produk p";

    public List<Produk> getAll() throws SQLException {
        return bacaList(SELECT_PRODUK + " ORDER BY p.id_produk");
    }

    public List<Produk> getByTipe(String tipe) throws SQLException {
        String kode;
        if ("Kopi".equalsIgnoreCase(tipe)) {
            kode = "Kopi";
        } else if ("BukanKopi".equalsIgnoreCase(tipe)) {
            kode = "BukanKopi";
        } else if ("MakananPenutup".equalsIgnoreCase(tipe)) {
            kode = "MakananPenutup";
        } else {
            return new ArrayList<>();
        }
        return bacaList(SELECT_PRODUK + " WHERE p.tipe = ? ORDER BY p.nama_produk", kode);
    }

    public Produk findById(int id) throws SQLException {
        String sql = SELECT_PRODUK + " WHERE p.id_produk = ?";
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

    private List<Produk> bacaList(String sql) throws SQLException {
        return bacaList(sql, null);
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
        String sql = "INSERT INTO produk "
                + "(tipe, nama_produk, harga_produk, jumlah_produk, ukuran, info_tambahan, catatan_produk) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, new String[] {"id_produk"})) {
            isiNilai(ps, produk, 1);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("id_produk tidak terbentuk.");
                }
                produk.setIdProduk(keys.getInt(1));
            }
        }
    }

    public void update(Produk produk) throws SQLException {
        String sql = "UPDATE produk SET tipe = ?, nama_produk = ?, harga_produk = ?, jumlah_produk = ?, "
                + "ukuran = ?, info_tambahan = ?, catatan_produk = ? WHERE id_produk = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            isiNilai(ps, produk, 1);
            ps.setInt(8, produk.getIdProduk());
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

    /** Mengisi 7 parameter sesuai tipe konkret produk. */
    private static void isiNilai(PreparedStatement ps, Produk produk, int mulai) throws SQLException {
        ps.setString(mulai, produk.getTipeProduk());
        ps.setString(mulai + 1, produk.getNamaProduk());
        ps.setFloat(mulai + 2, produk.getHargaProduk());
        ps.setInt(mulai + 3, produk.getJumlahProduk());
        if (produk instanceof Minuman minuman) {
            teks(ps, mulai + 4, minuman.getUkuranMinuman());
        } else {
            ps.setNull(mulai + 4, Types.VARCHAR);
        }
        teks(ps, mulai + 5, produk.getInfoTambahan());
        String catatan = produk.getCatatanProduk();
        teks(ps, mulai + 6, catatan == null ? "" : catatan);
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
        String catatan = rs.getString("catatan_produk");
        produk.setCatatanProduk(catatan == null ? "" : catatan);
        produk.setIdProduk(rs.getInt("id_produk"));
        return produk;
    }
}
