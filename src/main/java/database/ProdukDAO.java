package main.java.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import main.java.model.BukanKopi;
import main.java.model.Kopi;
import main.java.model.MakananPenutup;
import main.java.model.Minuman;
import main.java.model.Produk;
import main.java.model.UkuranMinuman;

/**
 * Katalog produk. Skema database memakai satu tabel per kelas
 * (class-table inheritance), sesuai hierarki kelas:
 * produk (id_produk, nama_produk, harga_produk)
 * -> minuman -> kopi (jenis_kopi) / bukan_kopi (jenis_rasa),
 * dan produk -> makanan_penutup (variasi_makanan_penutup).
 * Ukuran dan harga per ukuran minuman ada di tabel ukuran_minuman.
 * Untuk minuman, produk.harga_produk diisi harga ukuran termurah (harga mulai).
 * Tipe produk ditentukan dari tabel anak mana yang punya id_produk tersebut.
 */
public class ProdukDAO {
    private static final String SELECT_PRODUK =
            "SELECT * FROM ("
                    + "SELECT pr.id_produk, pr.nama_produk, pr.harga_produk, "
                    + "COALESCE(k.jenis_kopi, bk.jenis_rasa, mp.variasi_makanan_penutup) AS info_tambahan, "
                    + "CASE WHEN k.id_produk IS NOT NULL THEN 'Kopi' "
                    + "WHEN bk.id_produk IS NOT NULL THEN 'BukanKopi' "
                    + "WHEN mp.id_produk IS NOT NULL THEN 'MakananPenutup' END AS tipe "
                    + "FROM produk pr "
                    + "LEFT JOIN minuman m ON m.id_produk = pr.id_produk "
                    + "LEFT JOIN kopi k ON k.id_produk = pr.id_produk "
                    + "LEFT JOIN bukan_kopi bk ON bk.id_produk = pr.id_produk "
                    + "LEFT JOIN makanan_penutup mp ON mp.id_produk = pr.id_produk"
                    + ") p";

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
                    Produk produk = map(rs);
                    List<Produk> satu = new ArrayList<>();
                    satu.add(produk);
                    isiUkuran(conn, satu);
                    return produk;
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
            isiUkuran(conn, list);
        }
        return list;
    }

    /** Ambil semua baris ukuran_minuman lalu pasangkan ke objek Minuman yang sesuai. */
    private static void isiUkuran(Connection conn, List<Produk> list) throws SQLException {
        Map<Integer, Minuman> minuman = new HashMap<>();
        for (Produk produk : list) {
            if (produk instanceof Minuman m) {
                minuman.put(m.getIdProduk(), m);
            }
        }
        if (minuman.isEmpty()) {
            return;
        }
        String sql = "SELECT id_produk, ukuran, harga FROM ukuran_minuman";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Minuman m = minuman.get(rs.getInt("id_produk"));
                if (m != null) {
                    m.tambahUkuran(new UkuranMinuman(rs.getString("ukuran"), rs.getFloat("harga")));
                }
            }
        }
    }

    /** Simpan ke tabel produk dulu, lalu ke tabel anak sesuai tipe (dalam satu transaksi). */
    public void insert(Produk produk) throws SQLException {
        String sql = "INSERT INTO produk (nama_produk, harga_produk) VALUES (?, ?)";
        try (Connection conn = PostgreSQLConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(sql, new String[] {"id_produk"})) {
                    isiNilai(ps, produk, 1);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("id_produk tidak terbentuk.");
                        }
                        produk.setIdProduk(keys.getInt(1));
                    }
                }
                simpanTabelAnak(conn, produk);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    /**
     * Update tabel produk, lalu baris di tabel anak dihapus dan diisi ulang
     * supaya tetap benar kalau tipe produknya diganti.
     */
    public void update(Produk produk) throws SQLException {
        String sql = "UPDATE produk SET nama_produk = ?, harga_produk = ? WHERE id_produk = ?";
        try (Connection conn = PostgreSQLConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    isiNilai(ps, produk, 1);
                    ps.setInt(3, produk.getIdProduk());
                    ps.executeUpdate();
                }
                hapusTabelAnak(conn, produk.getIdProduk());
                simpanTabelAnak(conn, produk);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    /** Baris di minuman, ukuran_minuman, kopi, bukan_kopi, dan makanan_penutup ikut terhapus (ON DELETE CASCADE). */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM produk WHERE id_produk = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /** Mengisi 2 parameter kolom tabel produk. */
    private static void isiNilai(PreparedStatement ps, Produk produk, int mulai) throws SQLException {
        ps.setString(mulai, produk.getNamaProduk());
        ps.setFloat(mulai + 1, produk.getHargaProduk());
    }

    private static void simpanTabelAnak(Connection conn, Produk produk) throws SQLException {
        int id = produk.getIdProduk();
        if (produk instanceof Minuman minuman) {
            if (minuman.getDaftarUkuran().isEmpty()) {
                throw new SQLException("Minuman wajib punya minimal satu ukuran.");
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO minuman (id_produk) VALUES (?)")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO ukuran_minuman (id_produk, ukuran, harga) VALUES (?, ?, ?)")) {
                for (UkuranMinuman ukuran : minuman.getDaftarUkuran()) {
                    ps.setInt(1, id);
                    ps.setString(2, ukuran.getUkuran());
                    ps.setFloat(3, ukuran.getHarga());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            if (produk instanceof Kopi kopi) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO kopi (id_produk, jenis_kopi) VALUES (?, ?)")) {
                    ps.setInt(1, id);
                    teks(ps, 2, kopi.getJenisKopi());
                    ps.executeUpdate();
                }
            } else if (produk instanceof BukanKopi bukanKopi) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO bukan_kopi (id_produk, jenis_rasa) VALUES (?, ?)")) {
                    ps.setInt(1, id);
                    teks(ps, 2, bukanKopi.getJenisRasa());
                    ps.executeUpdate();
                }
            }
        } else if (produk instanceof MakananPenutup makanan) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO makanan_penutup (id_produk, variasi_makanan_penutup) VALUES (?, ?)")) {
                ps.setInt(1, id);
                teks(ps, 2, makanan.getVariasiMakananPenutup());
                ps.executeUpdate();
            }
        }
    }

    /** Hapus dari minuman (kopi/bukan_kopi/ukuran_minuman ikut terhapus lewat CASCADE) dan makanan_penutup. */
    private static void hapusTabelAnak(Connection conn, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM minuman WHERE id_produk = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM makanan_penutup WHERE id_produk = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
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
        String info = rs.getString("info_tambahan");
        Produk produk;
        if ("Kopi".equalsIgnoreCase(tipe)) {
            produk = new Kopi(info, nama, harga);
        } else if ("BukanKopi".equalsIgnoreCase(tipe)) {
            produk = new BukanKopi(info, nama, harga);
        } else if ("MakananPenutup".equalsIgnoreCase(tipe)) {
            produk = new MakananPenutup(info, nama, harga);
        } else {
            throw new SQLException("Produk id " + rs.getInt("id_produk")
                    + " belum punya baris di tabel kopi, bukan_kopi, atau makanan_penutup.");
        }
        produk.setIdProduk(rs.getInt("id_produk"));
        return produk;
    }
}
