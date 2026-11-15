package main.java.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import main.java.model.BukanKopi;
import main.java.model.Kopi;
import main.java.model.MakananPenutup;
import main.java.model.Produk;

public class ProdukDAO {
    public List<Produk> getAll() throws SQLException {
        String sql = "SELECT id_produk, tipe, nama_produk, harga_produk, ukuran, info_tambahan, "
                + "catatan_produk, jumlah_produk FROM produk ORDER BY id_produk";
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

    public Produk findById(int id) throws SQLException {
        String sql = "SELECT id_produk, tipe, nama_produk, harga_produk, ukuran, info_tambahan, "
                + "catatan_produk, jumlah_produk FROM produk WHERE id_produk = ?";
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

    public void insert(Produk produk) throws SQLException {
        String sql = "INSERT INTO produk (tipe, nama_produk, harga_produk, ukuran, info_tambahan, "
                + "catatan_produk, jumlah_produk) VALUES (?, ?, ?, ?, ?, ?, ?)";
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
                + "info_tambahan = ?, catatan_produk = ?, jumlah_produk = ? WHERE id_produk = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            isi(ps, produk);
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

    private void isi(PreparedStatement ps, Produk produk) throws SQLException {
        ps.setString(1, produk.getTipeProduk());
        ps.setString(2, produk.getNamaProduk());
        ps.setFloat(3, produk.getHargaProduk());
        ps.setString(4, produk.getUkuranTampil());
        ps.setString(5, produk.getInfoTambahan());
        ps.setString(6, produk.getCatatanProduk());
        ps.setInt(7, produk.getJumlahProduk());
    }

    static Produk map(ResultSet rs) throws SQLException {
        String tipe = rs.getString("tipe");
        String nama = rs.getString("nama_produk");
        float harga = rs.getFloat("harga_produk");
        String ukuran = rs.getString("ukuran");
        String info = rs.getString("info_tambahan");
        String catatan = rs.getString("catatan_produk");
        int jumlah = rs.getInt("jumlah_produk");
        if (jumlah <= 0) {
            jumlah = 1;
        }
        Produk produk;
        if ("Kopi".equalsIgnoreCase(tipe)) {
            produk = new Kopi(info, ukuran, nama, harga, catatan, jumlah);
        } else if ("BukanKopi".equalsIgnoreCase(tipe)) {
            produk = new BukanKopi(info, ukuran, nama, harga, catatan, jumlah);
        } else {
            produk = new MakananPenutup(info, nama, harga, catatan, jumlah);
        }
        produk.setIdProduk(rs.getInt("id_produk"));
        return produk;
    }
}
