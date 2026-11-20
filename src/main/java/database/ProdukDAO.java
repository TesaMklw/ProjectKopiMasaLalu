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
import main.java.model.Produk;

/**
 * Katalog produk mengikuti hierarki kelas:
 * produk (induk) → minuman → kopi / bukan_kopi, dan produk → makanan_penutup.
 * PK anak sekaligus FK ke induk, ON DELETE CASCADE di skema.
 */
public class ProdukDAO {
    private static final String SELECT_PRODUK =
            "SELECT p.id_produk, p.nama_produk, p.harga_produk, p.jumlah_produk, "
                    + "m.ukuran AS ukuran, "
                    + "COALESCE(k.jenis_kopi, b.jenis_rasa, mp.variasi_makanan_penutup) AS info_tambahan, "
                    + "CASE "
                    + " WHEN k.id_produk IS NOT NULL THEN 'Kopi' "
                    + " WHEN b.id_produk IS NOT NULL THEN 'BukanKopi' "
                    + " WHEN mp.id_produk IS NOT NULL THEN 'MakananPenutup' "
                    + " ELSE '' END AS tipe "
                    + "FROM produk p "
                    + "LEFT JOIN minuman m ON m.id_produk = p.id_produk "
                    + "LEFT JOIN kopi k ON k.id_produk = m.id_produk "
                    + "LEFT JOIN bukan_kopi b ON b.id_produk = m.id_produk "
                    + "LEFT JOIN makanan_penutup mp ON mp.id_produk = p.id_produk";

    public List<Produk> getAll() throws SQLException {
        return bacaList(SELECT_PRODUK + " ORDER BY p.id_produk");
    }

    public List<Produk> getByTipe(String tipe) throws SQLException {
        String filter;
        if ("Kopi".equalsIgnoreCase(tipe)) {
            filter = " WHERE k.id_produk IS NOT NULL";
        } else if ("BukanKopi".equalsIgnoreCase(tipe)) {
            filter = " WHERE b.id_produk IS NOT NULL";
        } else if ("MakananPenutup".equalsIgnoreCase(tipe)) {
            filter = " WHERE mp.id_produk IS NOT NULL";
        } else {
            return new ArrayList<>();
        }
        return bacaList(SELECT_PRODUK + filter + " ORDER BY p.nama_produk");
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
        List<Produk> list = new ArrayList<>();
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public void insert(Produk produk) throws SQLException {
        String sql = "INSERT INTO produk (nama_produk, harga_produk, jumlah_produk) VALUES (?, ?, ?)";
        try (Connection conn = PostgreSQLConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(sql, new String[] {"id_produk"})) {
                    ps.setString(1, produk.getNamaProduk());
                    ps.setFloat(2, produk.getHargaProduk());
                    ps.setInt(3, produk.getJumlahProduk());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("id_produk tidak terbentuk.");
                        }
                        produk.setIdProduk(keys.getInt(1));
                    }
                }
                simpanAnak(conn, produk);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public void update(Produk produk) throws SQLException {
        String sql = "UPDATE produk SET nama_produk = ?, harga_produk = ?, jumlah_produk = ? WHERE id_produk = ?";
        try (Connection conn = PostgreSQLConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, produk.getNamaProduk());
                    ps.setFloat(2, produk.getHargaProduk());
                    ps.setInt(3, produk.getJumlahProduk());
                    ps.setInt(4, produk.getIdProduk());
                    ps.executeUpdate();
                }
                // Hapus anak lama (ganti tipe ikut pindah tabel). CASCADE menghapus kopi/bukan_kopi.
                try (PreparedStatement hapusMinuman = conn.prepareStatement(
                        "DELETE FROM minuman WHERE id_produk = ?")) {
                    hapusMinuman.setInt(1, produk.getIdProduk());
                    hapusMinuman.executeUpdate();
                }
                try (PreparedStatement hapusMakanan = conn.prepareStatement(
                        "DELETE FROM makanan_penutup WHERE id_produk = ?")) {
                    hapusMakanan.setInt(1, produk.getIdProduk());
                    hapusMakanan.executeUpdate();
                }
                simpanAnak(conn, produk);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
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

    private void simpanAnak(Connection conn, Produk produk) throws SQLException {
        if (produk instanceof Kopi) {
            Kopi kopi = (Kopi) produk;
            simpanMinuman(conn, produk.getIdProduk(), kopi.getUkuranMinuman());
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO kopi (id_produk, jenis_kopi) VALUES (?, ?)")) {
                ps.setInt(1, produk.getIdProduk());
                teks(ps, 2, kopi.getJenisKopi());
                ps.executeUpdate();
            }
        } else if (produk instanceof BukanKopi) {
            BukanKopi bukan = (BukanKopi) produk;
            simpanMinuman(conn, produk.getIdProduk(), bukan.getUkuranMinuman());
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO bukan_kopi (id_produk, jenis_rasa) VALUES (?, ?)")) {
                ps.setInt(1, produk.getIdProduk());
                teks(ps, 2, bukan.getJenisRasa());
                ps.executeUpdate();
            }
        } else if (produk instanceof MakananPenutup) {
            MakananPenutup makanan = (MakananPenutup) produk;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO makanan_penutup (id_produk, variasi_makanan_penutup) VALUES (?, ?)")) {
                ps.setInt(1, produk.getIdProduk());
                teks(ps, 2, makanan.getVariasiMakananPenutup());
                ps.executeUpdate();
            }
        } else {
            throw new SQLException("Tipe produk tidak dikenali: " + produk.getTipeProduk());
        }
    }

    private void simpanMinuman(Connection conn, int idProduk, String ukuran) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO minuman (id_produk, ukuran) VALUES (?, ?)")) {
            ps.setInt(1, idProduk);
            teks(ps, 2, ukuran);
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
