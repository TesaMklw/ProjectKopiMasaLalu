package main.java.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import main.java.model.BukanKopi;
import main.java.model.DetailTransaksi;
import main.java.model.Gerai;
import main.java.model.Kopi;
import main.java.model.MakananPenutup;
import main.java.model.Pelanggan;
import main.java.model.Pembayaran;
import main.java.model.Produk;
import main.java.model.Transaksi;

public class TransaksiDAO {
    private static final String HEADER = "SELECT t.id_transaksi, t.kode_gerai, t.id_pelanggan, t.kode_pembayaran, "
            + "t.subtotal_transaksi, t.total_transaksi, t.uang_kembali, t.kode_promo, t.total_diskon, "
            + "t.tanggal_transaksi, g.nama_gerai, g.alamat_gerai, p.nama_pelanggan, p.no_hp, "
            + "p.password_pelanggan, b.metode_pembayaran "
            + "FROM transaksi t "
            + "LEFT JOIN gerai g ON g.kode_gerai = t.kode_gerai "
            + "LEFT JOIN pelanggan p ON p.id_pelanggan = t.id_pelanggan "
            + "LEFT JOIN pembayaran b ON b.kode_pembayaran = t.kode_pembayaran ";

    public List<Transaksi> getAll() throws SQLException {
        String sql = HEADER + "ORDER BY t.tanggal_transaksi DESC, t.id_transaksi";
        List<Transaksi> list = new ArrayList<>();
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Transaksi transaksi = mapHeader(rs);
                isiDetail(conn, transaksi);
                list.add(transaksi);
            }
        }
        return list;
    }

    public void insert(Transaksi transaksi) throws SQLException {
        String sql = "INSERT INTO transaksi (id_transaksi, kode_gerai, id_pelanggan, kode_pembayaran, "
                + "subtotal_transaksi, total_transaksi, uang_kembali, kode_promo, total_diskon, tanggal_transaksi) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = PostgreSQLConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    isiHeader(ps, transaksi);
                    ps.executeUpdate();
                }
                simpanDetail(conn, transaksi);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public void update(Transaksi transaksi) throws SQLException {
        String sql = "UPDATE transaksi SET kode_gerai = ?, id_pelanggan = ?, kode_pembayaran = ?, "
                + "subtotal_transaksi = ?, total_transaksi = ?, uang_kembali = ?, kode_promo = ?, "
                + "total_diskon = ?, tanggal_transaksi = ? WHERE id_transaksi = ?";
        try (Connection conn = PostgreSQLConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    isiRelasi(ps, transaksi, 1);
                    ps.setFloat(4, transaksi.getSubtotalTransaksi());
                    ps.setFloat(5, transaksi.getTotalTransaksi());
                    ps.setFloat(6, transaksi.getUangKembali());
                    ps.setString(7, kosong(transaksi.getKodePromo()));
                    ps.setFloat(8, transaksi.getTotalDiskon());
                    isiTanggal(ps, 9, transaksi.getTanggalTransaksi());
                    ps.setString(10, transaksi.getIdTransaksi());
                    ps.executeUpdate();
                }
                try (PreparedStatement hapus = conn.prepareStatement(
                        "DELETE FROM detail_transaksi WHERE id_transaksi = ?")) {
                    hapus.setString(1, transaksi.getIdTransaksi());
                    hapus.executeUpdate();
                }
                simpanDetail(conn, transaksi);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public boolean idTerpakai(String idTransaksi) throws SQLException {
        String sql = "SELECT 1 FROM transaksi WHERE id_transaksi = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idTransaksi);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void delete(String idTransaksi) throws SQLException {
        try (Connection conn = PostgreSQLConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement hapusDetail = conn.prepareStatement(
                        "DELETE FROM detail_transaksi WHERE id_transaksi = ?")) {
                    hapusDetail.setString(1, idTransaksi);
                    hapusDetail.executeUpdate();
                }
                try (PreparedStatement hapus = conn.prepareStatement(
                        "DELETE FROM transaksi WHERE id_transaksi = ?")) {
                    hapus.setString(1, idTransaksi);
                    hapus.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    private void isiHeader(PreparedStatement ps, Transaksi transaksi) throws SQLException {
        ps.setString(1, transaksi.getIdTransaksi());
        isiRelasi(ps, transaksi, 2);
        ps.setFloat(5, transaksi.getSubtotalTransaksi());
        ps.setFloat(6, transaksi.getTotalTransaksi());
        ps.setFloat(7, transaksi.getUangKembali());
        ps.setString(8, kosong(transaksi.getKodePromo()));
        ps.setFloat(9, transaksi.getTotalDiskon());
        isiTanggal(ps, 10, transaksi.getTanggalTransaksi());
    }

    private void isiRelasi(PreparedStatement ps, Transaksi transaksi, int mulai) throws SQLException {
        if (transaksi.getGerai() == null || transaksi.getGerai().getKodeGerai() == null) {
            ps.setNull(mulai, Types.VARCHAR);
        } else {
            ps.setString(mulai, transaksi.getGerai().getKodeGerai());
        }
        if (transaksi.getPelanggan() == null || transaksi.getPelanggan().getIdPelanggan() <= 0) {
            ps.setNull(mulai + 1, Types.INTEGER);
        } else {
            ps.setInt(mulai + 1, transaksi.getPelanggan().getIdPelanggan());
        }
        if (transaksi.getPembayaran() == null || transaksi.getPembayaran().getKodePembayaran() == null) {
            ps.setNull(mulai + 2, Types.VARCHAR);
        } else {
            ps.setString(mulai + 2, transaksi.getPembayaran().getKodePembayaran());
        }
    }

    private void simpanDetail(Connection conn, Transaksi transaksi) throws SQLException {
        String sql = "INSERT INTO detail_transaksi (id_transaksi, id_produk, nama_produk, tipe, harga_produk, "
                + "jumlah_produk, catatan_produk, ukuran, info_tambahan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (DetailTransaksi detail : transaksi.getListDetail()) {
                Produk produk = detail.getProduk();
                ps.setString(1, transaksi.getIdTransaksi());
                if (produk.getIdProduk() <= 0) {
                    ps.setNull(2, Types.INTEGER);
                } else {
                    ps.setInt(2, produk.getIdProduk());
                }
                // nama, tipe, harga, ukuran, dan info disimpan sebagai salinan (snapshot)
                // supaya riwayat transaksi tetap terbaca walau produk di katalog diubah/dihapus
                ps.setString(3, produk.getNamaProduk());
                ps.setString(4, produk.getTipeProduk());
                ps.setFloat(5, detail.getHargaSatuan());
                ps.setInt(6, detail.getJumlah());
                ps.setString(7, detail.getCatatan());
                ps.setString(8, detail.getUkuran());
                ps.setString(9, produk.getInfoTambahan());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void isiDetail(Connection conn, Transaksi transaksi) throws SQLException {
        String sql = "SELECT id_produk, nama_produk, tipe, harga_produk, jumlah_produk, catatan_produk, "
                + "ukuran, info_tambahan FROM detail_transaksi WHERE id_transaksi = ? ORDER BY id_detail";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, transaksi.getIdTransaksi());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transaksi.addDetail(mapDetail(rs));
                }
            }
        }
    }

    private DetailTransaksi mapDetail(ResultSet rs) throws SQLException {
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
            produk = new Kopi(info, nama, harga);
        } else if ("BukanKopi".equalsIgnoreCase(tipe)) {
            produk = new BukanKopi(info, nama, harga);
        } else if ("MakananPenutup".equalsIgnoreCase(tipe)) {
            produk = new MakananPenutup(info, nama, harga);
        } else {
            throw new SQLException("Tipe produk tidak dikenali: " + tipe);
        }
        int idProduk = rs.getInt("id_produk");
        if (!rs.wasNull()) {
            produk.setIdProduk(idProduk);
        }
        return new DetailTransaksi(produk, jumlah, ukuran, catatan);
    }

    private Transaksi mapHeader(ResultSet rs) throws SQLException {
        String kodeGerai = rs.getString("kode_gerai");
        Gerai gerai = null;
        if (kodeGerai != null) {
            gerai = new Gerai(rs.getString("nama_gerai"), rs.getString("alamat_gerai"));
            gerai.setKodeGerai(kodeGerai);
        }
        int idPelanggan = rs.getInt("id_pelanggan");
        Pelanggan pelanggan = null;
        if (!rs.wasNull()) {
            pelanggan = new Pelanggan(rs.getString("nama_pelanggan"));
            pelanggan.setIdPelanggan(idPelanggan);
            pelanggan.setNoHp(rs.getString("no_hp"));
            pelanggan.setPasswordPelanggan(rs.getString("password_pelanggan"));
        }
        String kodeBayar = rs.getString("kode_pembayaran");
        Pembayaran pembayaran = null;
        if (kodeBayar != null) {
            pembayaran = new Pembayaran(rs.getString("metode_pembayaran"));
            pembayaran.setKodePembayaran(kodeBayar);
        }
        Date tanggal = rs.getDate("tanggal_transaksi");
        Transaksi transaksi = new Transaksi(
                rs.getString("id_transaksi"),
                gerai,
                rs.getFloat("subtotal_transaksi"),
                rs.getFloat("total_transaksi"),
                rs.getFloat("uang_kembali"),
                pelanggan,
                tanggal == null ? "" : tanggal.toLocalDate().toString(),
                pembayaran);
        transaksi.setKodePromo(rs.getString("kode_promo"));
        transaksi.setTotalDiskon(rs.getFloat("total_diskon"));
        return transaksi;
    }

    private static void isiTanggal(PreparedStatement ps, int index, String teks) throws SQLException {
        if (teks == null || teks.isBlank()) {
            ps.setNull(index, Types.DATE);
            return;
        }
        ps.setDate(index, Date.valueOf(LocalDate.parse(teks)));
    }

    private static String kosong(String nilai) {
        return nilai == null ? "" : nilai;
    }
}
