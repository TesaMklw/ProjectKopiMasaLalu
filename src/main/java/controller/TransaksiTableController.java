package main.java.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;
import java.sql.SQLException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import main.java.database.GeraiDAO;
import main.java.database.PelangganDAO;
import main.java.database.PembayaranDAO;
import main.java.database.ProdukDAO;
import main.java.database.TransaksiDAO;
import main.java.model.BukanKopi;
import main.java.model.Gerai;
import main.java.model.Kopi;
import main.java.model.MakananPenutup;
import main.java.model.Pelanggan;
import main.java.model.Pembayaran;
import main.java.model.Produk;
import main.java.model.Transaksi;

/**
 * Kasir transaksi. Promo HEMAT10/HEMAT20/HEMAT50 memakai calcDiscount
 * (makanan penutup tetap maksimal 20%). Struk HTML ditampilkan di WebView.
 */
public class TransaksiTableController implements Cetak {
    @FXML private TableView<Transaksi> tabel;
    @FXML private TableColumn<Transaksi, String> colId;
    @FXML private TableColumn<Transaksi, String> colTanggal;
    @FXML private TableColumn<Transaksi, String> colPelanggan;
    @FXML private TableColumn<Transaksi, String> colGerai;
    @FXML private TableColumn<Transaksi, String> colMetode;
    @FXML private TableColumn<Transaksi, Number> colTotal;
    @FXML private TextField txtId;
    @FXML private ComboBox<Gerai> cmbGerai;
    @FXML private ComboBox<Pelanggan> cmbPelanggan;
    @FXML private ComboBox<Pembayaran> cmbBayar;
    @FXML private DatePicker dpTanggal;
    @FXML private TextField txtPromo;
    @FXML private TextField txtUang;
    @FXML private ToggleGroup grupKategori;
    @FXML private RadioButton rbMakanan;
    @FXML private RadioButton rbKopi;
    @FXML private RadioButton rbNonKopi;
    @FXML private ComboBox<Produk> cmbProduk;
    @FXML private TextField txtJumlahItem;
    @FXML private TextField txtCatatanItem;
    @FXML private TableView<Produk> tabelKeranjang;
    @FXML private TableColumn<Produk, String> colItemNama;
    @FXML private TableColumn<Produk, Number> colItemJumlah;
    @FXML private TableColumn<Produk, Number> colItemHarga;
    @FXML private TableColumn<Produk, String> colItemCatatan;
    @FXML private Label lblSubtotal;
    @FXML private Label lblDiskon;
    @FXML private Label lblTotal;
    @FXML private Label lblKembali;
    @FXML private Button btnUbah;
    @FXML private Button btnHapus;

    private final TransaksiDAO transaksiDAO = new TransaksiDAO();
    private final GeraiDAO geraiDAO = new GeraiDAO();
    private final PelangganDAO pelangganDAO = new PelangganDAO();
    private final PembayaranDAO pembayaranDAO = new PembayaranDAO();
    private final ProdukDAO produkDAO = new ProdukDAO();
    private final ObservableList<Produk> keranjang = FXCollections.observableArrayList();
    private final Map<Produk, Float> hargaDasar = new IdentityHashMap<>();
    private Pelanggan pelangganLogin;
    private Gerai geraiLogin;
    private boolean modePelanggan;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idTransaksi"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggalTransaksi"));
        colPelanggan.setCellValueFactory(new PropertyValueFactory<>("namaPelanggan"));
        colGerai.setCellValueFactory(new PropertyValueFactory<>("namaGerai"));
        colMetode.setCellValueFactory(new PropertyValueFactory<>("metodePembayaranTampil"));
        colTotal.setCellValueFactory(cell -> cell.getValue().totalTransaksiProperty());
        colItemNama.setCellValueFactory(new PropertyValueFactory<>("namaProduk"));
        colItemJumlah.setCellValueFactory(cell -> cell.getValue().jumlahProdukProperty());
        colItemHarga.setCellValueFactory(cell -> cell.getValue().hargaProdukProperty());
        colItemCatatan.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getCatatanProduk() == null ? "" : cell.getValue().getCatatanProduk()));
        tabel.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabelKeranjang.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabelKeranjang.setItems(keranjang);
        tabel.getSelectionModel().selectedItemProperty().addListener((obs, lama, baru) -> isiTransaksi(baru));
        txtJumlahItem.setText("1");
        dpTanggal.setValue(LocalDate.now());
        txtId.setText(idBaru());
        ListCell<Produk> selProduk = new ListCell<>() {
            @Override
            protected void updateItem(Produk item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : teksPilihan(item));
            }
        };
        cmbProduk.setButtonCell(selProduk);
        cmbProduk.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Produk item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : teksPilihan(item));
            }
        });
        if (grupKategori != null) {
            grupKategori.selectedToggleProperty().addListener((obs, lama, baru) -> {
                if (cmbProduk != null) {
                    cmbProduk.setValue(null);
                }
                muatDaftarProduk();
            });
        }
        muat();
    }

    public void setPelangganLogin(Pelanggan pelanggan) {
        this.pelangganLogin = pelanggan;
        this.geraiLogin = null;
        this.modePelanggan = true;
        if (cmbPelanggan != null) {
            cmbPelanggan.setDisable(true);
        }
        if (btnUbah != null) {
            btnUbah.setVisible(false);
            btnUbah.setManaged(false);
        }
        if (btnHapus != null) {
            btnHapus.setVisible(false);
            btnHapus.setManaged(false);
        }
        pilihPelanggan(pelanggan);
        muat();
    }

    public void setGeraiLogin(Gerai gerai) {
        this.geraiLogin = gerai;
        this.pelangganLogin = null;
        this.modePelanggan = false;
        pilihGerai(gerai);
        muat();
    }

    @FXML
    private void muat() {
        try {
            cmbGerai.setItems(FXCollections.observableArrayList(geraiDAO.getAll()));
            cmbPelanggan.setItems(FXCollections.observableArrayList(pelangganDAO.getAll()));
            cmbBayar.setItems(FXCollections.observableArrayList(pembayaranDAO.getAll()));
            muatDaftarProduk();
            if (modePelanggan && pelangganLogin != null) {
                ObservableList<Transaksi> milik = FXCollections.observableArrayList();
                for (Transaksi t : transaksiDAO.getAll()) {
                    if (t.getPelanggan() != null
                            && t.getPelanggan().getIdPelanggan() == pelangganLogin.getIdPelanggan()) {
                        milik.add(t);
                    }
                }
                tabel.getItems().setAll(milik);
            } else {
                tabel.getItems().setAll(transaksiDAO.getAll());
            }
            pilihPelanggan(pelangganLogin);
            pilihGerai(geraiLogin);
        } catch (SQLException e) {
            Pesan.gagalSql(e);
        }
    }

    private void muatDaftarProduk() {
        if (cmbProduk == null) {
            return;
        }
        String tipe = tipeKeranjang();
        if (tipe == null) {
            cmbProduk.setPromptText("Pilih kategori dulu");
            cmbProduk.getItems().clear();
            cmbProduk.setValue(null);
            return;
        }
        if ("Kopi".equals(tipe)) {
            cmbProduk.setPromptText("Pilih kopi");
        } else if ("BukanKopi".equals(tipe)) {
            cmbProduk.setPromptText("Pilih non kopi");
        } else {
            cmbProduk.setPromptText("Pilih makanan");
        }
        try {
            cmbProduk.setItems(FXCollections.observableArrayList(produkDAO.getByTipe(tipe)));
            cmbProduk.setValue(null);
        } catch (SQLException e) {
            Pesan.gagalSql(e);
        }
    }

    private String teksPilihan(Produk produk) {
        String info;
        if (produk instanceof Kopi kopi) {
            info = kopi.getJenisKopi();
        } else if (produk instanceof BukanKopi bukan) {
            info = bukan.getJenisRasa();
        } else if (produk instanceof MakananPenutup makanan) {
            info = makanan.getVariasiMakananPenutup();
        } else {
            info = produk.getInfoTambahan();
        }
        if (info == null || info.isBlank()) {
            return produk.getNamaProduk();
        }
        return produk.getNamaProduk() + " — " + info;
    }

    private String tipeKeranjang() {
        if (rbMakanan != null && rbMakanan.isSelected()) {
            return "MakananPenutup";
        }
        if (rbKopi != null && rbKopi.isSelected()) {
            return "Kopi";
        }
        if (rbNonKopi != null && rbNonKopi.isSelected()) {
            return "BukanKopi";
        }
        return null;
    }

    @FXML
    private void tambahItem() {
        Produk asal = cmbProduk.getValue();
        if (asal == null) {
            Pesan.gagal("Pilih produk.");
            return;
        }
        int jumlah;
        try {
            jumlah = Integer.parseInt(txtJumlahItem.getText().trim());
            if (jumlah <= 0) {
                Pesan.gagal("Jumlah harus lebih dari 0.");
                return;
            }
        } catch (NumberFormatException e) {
            Pesan.gagal("Jumlah harus angka bulat.");
            return;
        }
        Produk salinan = salinProduk(asal, jumlah);
        if (salinan == null) {
            Pesan.gagal("Jenis produk tidak dikenali.");
            return;
        }
        String catatan = txtCatatanItem.getText() == null ? "" : txtCatatanItem.getText().trim();
        salinan.setCatatanProduk(catatan);
        hargaDasar.put(salinan, asal.getHargaProduk());
        keranjang.add(salinan);
        txtCatatanItem.clear();
        perkiraan();
    }

    @FXML
    private void hapusItem() {
        Produk dipilih = tabelKeranjang.getSelectionModel().getSelectedItem();
        if (dipilih == null) {
            Pesan.gagal("Pilih item keranjang yang ingin dihapus.");
            return;
        }
        keranjang.remove(dipilih);
        hargaDasar.remove(dipilih);
        perkiraan();
    }

    @FXML
    private void hitung() {
        susun(false);
    }

    @FXML
    private void simpan() {
        Transaksi transaksi = susun(false);
        if (transaksi == null) {
            return;
        }
        for (Transaksi ada : tabel.getItems()) {
            if (transaksi.getIdTransaksi().equals(ada.getIdTransaksi())) {
                Pesan.gagal("ID sudah ada. Pakai Ubah, atau Bersihkan untuk ID baru.");
                return;
            }
        }
        try {
            transaksiDAO.insert(transaksi);
            Pesan.info("Transaksi tersimpan.");
            bersih();
            muat();
        } catch (SQLException e) {
            Pesan.gagalSql(e);
        }
    }

    @FXML
    private void ubah() {
        if (modePelanggan) {
            Pesan.gagal("Pelanggan hanya bisa membuat pesanan baru.");
            return;
        }
        Transaksi transaksi = susun(false);
        if (transaksi == null) {
            return;
        }
        boolean ada = false;
        for (Transaksi baris : tabel.getItems()) {
            if (transaksi.getIdTransaksi().equals(baris.getIdTransaksi())) {
                ada = true;
                break;
            }
        }
        if (!ada) {
            Pesan.gagal("Transaksi belum tersimpan. Pakai Simpan.");
            return;
        }
        try {
            transaksiDAO.update(transaksi);
            Pesan.info("Transaksi diperbarui.");
            bersih();
            muat();
        } catch (SQLException e) {
            Pesan.gagalSql(e);
        }
    }

    @FXML
    private void hapus() {
        if (modePelanggan) {
            Pesan.gagal("Pelanggan tidak dapat menghapus transaksi.");
            return;
        }
        Transaksi dipilih = tabel.getSelectionModel().getSelectedItem();
        if (dipilih == null) {
            Pesan.gagal("Pilih transaksi yang ingin dihapus.");
            return;
        }
        if (!Pesan.konfirmasi("Hapus transaksi", "Hapus " + dipilih.getIdTransaksi() + "?")) {
            return;
        }
        try {
            transaksiDAO.delete(dipilih.getIdTransaksi());
            bersih();
            muat();
        } catch (SQLException e) {
            Pesan.gagalSql(e);
        }
    }

    @FXML
    private void bersih() {
        tabel.getSelectionModel().clearSelection();
        keranjang.clear();
        hargaDasar.clear();
        txtId.setText(idBaru());
        cmbGerai.getSelectionModel().clearSelection();
        cmbBayar.getSelectionModel().clearSelection();
        cmbProduk.getSelectionModel().clearSelection();
        pilihPelanggan(pelangganLogin);
        pilihGerai(geraiLogin);
        dpTanggal.setValue(LocalDate.now());
        txtPromo.clear();
        txtUang.clear();
        txtJumlahItem.setText("1");
        txtCatatanItem.clear();
        lblSubtotal.setText("Rp 0");
        lblDiskon.setText("Rp 0");
        lblTotal.setText("Rp 0");
        lblKembali.setText("Rp 0");
    }

    @Override
    @FXML
    public void struk() {
        Transaksi transaksi = susun(false);
        if (transaksi == null) {
            return;
        }
        try {
            WebView web = new WebView();
            web.getEngine().loadContent(render(transaksi));
            Stage stage = new Stage();
            stage.setTitle("Struk " + transaksi.getIdTransaksi());
            stage.setScene(new Scene(web, 440, 700));
            stage.show();
        } catch (IOException e) {
            Pesan.gagal("Gagal menampilkan struk: " + e.getMessage());
        }
    }

    private void isiTransaksi(Transaksi transaksi) {
        if (transaksi == null) {
            return;
        }
        keranjang.clear();
        hargaDasar.clear();
        txtId.setText(transaksi.getIdTransaksi());
        pilihGerai(transaksi.getGerai());
        pilihPelanggan(transaksi.getPelanggan());
        pilihBayar(transaksi.getPembayaran());
        if (transaksi.getTanggalTransaksi() != null && !transaksi.getTanggalTransaksi().isBlank()) {
            dpTanggal.setValue(LocalDate.parse(transaksi.getTanggalTransaksi()));
        }
        txtPromo.setText(transaksi.getKodePromo() == null ? "" : transaksi.getKodePromo());
        txtUang.setText(Float.toString(transaksi.getTotalTransaksi() + transaksi.getUangKembali()));
        for (Produk baris : transaksi.getListProduk()) {
            Produk salinan = salinProduk(baris, baris.getJumlahProduk());
            if (salinan == null) {
                continue;
            }
            if (baris.getCatatanProduk() != null) {
                salinan.setCatatanProduk(baris.getCatatanProduk());
            }
            float dasar = hargaKatalog(baris);
            salinan.setHargaProduk(dasar);
            hargaDasar.put(salinan, dasar);
            keranjang.add(salinan);
        }
        susun(true);
    }

    private Transaksi susun(boolean diam) {
        if (keranjang.isEmpty()) {
            if (!diam) {
                Pesan.gagal("Keranjang kosong.");
            }
            return null;
        }
        if (cmbGerai.getValue() == null || cmbPelanggan.getValue() == null || cmbBayar.getValue() == null) {
            if (!diam) {
                Pesan.gagal("Pilih gerai, pelanggan, dan metode pembayaran.");
            }
            return null;
        }
        String id = txtId.getText() == null ? "" : txtId.getText().trim();
        if (id.isEmpty()) {
            id = idBaru();
            txtId.setText(id);
        }
        String kode = txtPromo.getText() == null ? "" : txtPromo.getText().trim().toUpperCase();
        txtPromo.setText(kode);
        Float persen = null;
        if (!kode.isEmpty()) {
            switch (kode) {
                case "HEMAT10":
                    persen = 10f;
                    break;
                case "HEMAT20":
                    persen = 20f;
                    break;
                case "HEMAT50":
                    persen = 50f;
                    break;
                default:
                    if (!diam) {
                        Pesan.info("Kode tidak dikenal. Lanjut tanpa diskon.");
                    }
                    kode = "";
            }
        }
        float subtotalAwal = 0;
        for (Produk produk : keranjang) {
            float dasar = hargaDasar.getOrDefault(produk, produk.getHargaProduk());
            produk.setHargaProduk(dasar);
            subtotalAwal += dasar * produk.getJumlahProduk();
            if (persen != null) {
                produk.calcDiscount(persen);
            }
        }
        LocalDate tanggal = dpTanggal.getValue() == null ? LocalDate.now() : dpTanggal.getValue();
        dpTanggal.setValue(tanggal);
        Transaksi transaksi = new Transaksi(id, cmbGerai.getValue(), 0, 0, 0,
                cmbPelanggan.getValue(), tanggal.toString(), cmbBayar.getValue());
        for (Produk produk : keranjang) {
            transaksi.addProduk(produk);
        }
        transaksi.calcSubtotal();
        transaksi.setSubtotalTransaksi(subtotalAwal);
        transaksi.setKodePromo(kode);
        transaksi.setTotalDiskon(subtotalAwal - transaksi.getTotalTransaksi());
        String teksUang = txtUang.getText() == null ? "" : txtUang.getText().trim();
        if (teksUang.isEmpty()) {
            if (!diam) {
                Pesan.gagal("Isi uang dibayar.");
            }
            tampilkanAngka(transaksi);
            return null;
        }
        float uang;
        try {
            uang = Float.parseFloat(teksUang);
        } catch (NumberFormatException e) {
            if (!diam) {
                Pesan.gagal("Uang dibayar harus angka.");
            }
            return null;
        }
        if (uang < transaksi.getTotalTransaksi()) {
            if (!diam) {
                Pesan.gagal("Uang kurang Rp" + (transaksi.getTotalTransaksi() - uang) + ". Coba lagi.");
            }
            tampilkanAngka(transaksi);
            return null;
        }
        transaksi.calcUangKembali(uang);
        tampilkanAngka(transaksi);
        return transaksi;
    }

    private void perkiraan() {
        float sum = 0;
        for (Produk produk : keranjang) {
            float dasar = hargaDasar.getOrDefault(produk, produk.getHargaProduk());
            sum += dasar * produk.getJumlahProduk();
        }
        lblSubtotal.setText(rupiah(sum));
        lblDiskon.setText("Rp 0");
        lblTotal.setText(rupiah(sum));
        lblKembali.setText("Rp 0");
    }

    private void tampilkanAngka(Transaksi transaksi) {
        lblSubtotal.setText(rupiah(transaksi.getSubtotalTransaksi()));
        lblDiskon.setText(rupiah(transaksi.getTotalDiskon()));
        lblTotal.setText(rupiah(transaksi.getTotalTransaksi()));
        lblKembali.setText(rupiah(transaksi.getUangKembali()));
    }

    private float hargaKatalog(Produk baris) {
        if (baris.getIdProduk() > 0) {
            for (Produk katalog : cmbProduk.getItems()) {
                if (katalog.getIdProduk() == baris.getIdProduk()) {
                    return katalog.getHargaProduk();
                }
            }
        }
        return baris.getHargaProduk();
    }

    private void pilihGerai(Gerai gerai) {
        if (gerai == null) {
            return;
        }
        for (Gerai item : cmbGerai.getItems()) {
            if (gerai.getKodeGerai() != null && gerai.getKodeGerai().equals(item.getKodeGerai())) {
                cmbGerai.getSelectionModel().select(item);
                return;
            }
        }
    }

    private void pilihPelanggan(Pelanggan pelanggan) {
        if (pelanggan == null || cmbPelanggan == null) {
            return;
        }
        for (Pelanggan item : cmbPelanggan.getItems()) {
            if (pelanggan.getIdPelanggan() > 0 && pelanggan.getIdPelanggan() == item.getIdPelanggan()) {
                cmbPelanggan.getSelectionModel().select(item);
                return;
            }
            if (pelanggan.getNamaPelanggan() != null
                    && pelanggan.getNamaPelanggan().equals(item.getNamaPelanggan())) {
                cmbPelanggan.getSelectionModel().select(item);
                return;
            }
        }
    }

    private void pilihBayar(Pembayaran pembayaran) {
        if (pembayaran == null) {
            return;
        }
        for (Pembayaran item : cmbBayar.getItems()) {
            if (pembayaran.getKodePembayaran() != null
                    && pembayaran.getKodePembayaran().equals(item.getKodePembayaran())) {
                cmbBayar.getSelectionModel().select(item);
                return;
            }
        }
    }

    // Salin produk sesuai jenisnya supaya jumlah tiap transaksi tidak saling timpa
    private Produk salinProduk(Produk produk, int jumlah) {
        Produk salinan;
        if (produk instanceof Kopi) {
            Kopi kopi = (Kopi) produk;
            salinan = new Kopi(kopi.getJenisKopi(), kopi.getUkuranMinuman(),
                    kopi.getNamaProduk(), kopi.getHargaProduk(), jumlah);
        } else if (produk instanceof BukanKopi) {
            BukanKopi bukan = (BukanKopi) produk;
            salinan = new BukanKopi(bukan.getJenisRasa(), bukan.getUkuranMinuman(),
                    bukan.getNamaProduk(), bukan.getHargaProduk(), jumlah);
        } else if (produk instanceof MakananPenutup) {
            MakananPenutup makanan = (MakananPenutup) produk;
            salinan = new MakananPenutup(makanan.getVariasiMakananPenutup(),
                    makanan.getNamaProduk(), makanan.getHargaProduk(), jumlah);
        } else {
            return null;
        }
        salinan.setIdProduk(produk.getIdProduk());
        return salinan;
    }

    private String render(Transaksi transaksi) throws IOException {
        String html = bacaTemplate();
        StringBuilder baris = new StringBuilder();
        for (Produk produk : transaksi.getListProduk()) {
            float sub = produk.getHargaProduk() * produk.getJumlahProduk();
            baris.append("<tr><td>").append(esc(produk.getNamaProduk()))
                    .append("</td><td class=\"num\">x").append(produk.getJumlahProduk())
                    .append("</td><td class=\"num\">").append(rupiah(produk.getHargaProduk()))
                    .append("</td><td class=\"num\">").append(rupiah(sub))
                    .append("</td><td>").append(esc(kosong(produk.getCatatanProduk())))
                    .append("</td></tr>");
        }
        String promo = transaksi.getKodePromo() == null || transaksi.getKodePromo().isEmpty()
                ? "-" : transaksi.getKodePromo();
        Pelanggan pelanggan = transaksi.getPelanggan();
        Gerai gerai = transaksi.getGerai();
        Pembayaran bayar = transaksi.getPembayaran();
        return html.replace("{{id}}", esc(transaksi.getIdTransaksi()))
                .replace("{{gerai}}", esc(gerai == null ? "-" : gerai.getNamaGerai()))
                .replace("{{alamat}}", esc(gerai == null ? "-" : gerai.getAlamatGerai()))
                .replace("{{pelanggan}}", esc(pelanggan == null ? "-" : pelanggan.getNamaPelanggan()))
                .replace("{{nohp}}", esc(pelanggan == null ? "-" : kosong(pelanggan.getNoHp())))
                .replace("{{tanggal}}", esc(transaksi.getTanggalTransaksi()))
                .replace("{{metode}}", esc(bayar == null ? "-" : bayar.getMetodePembayaran()))
                .replace("{{subtotal}}", esc(rupiah(transaksi.getSubtotalTransaksi())))
                .replace("{{promo}}", esc(promo))
                .replace("{{diskon}}", esc(rupiah(transaksi.getTotalDiskon())))
                .replace("{{total}}", esc(rupiah(transaksi.getTotalTransaksi())))
                .replace("{{kembali}}", esc(rupiah(transaksi.getUangKembali())))
                .replace("{{baris}}", baris.toString());
    }

    private String bacaTemplate() throws IOException {
        var url = getClass().getResource("/main/resources/struk/invoice.html");
        if (url == null) {
            throw new IOException("Template struk tidak ditemukan.");
        }
        try (InputStream in = url.openStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static String idBaru() {
        return "TRX" + (System.currentTimeMillis() % 100000);
    }

    private static String rupiah(float nilai) {
        return String.format(Locale.forLanguageTag("id-ID"), "Rp %,.0f", nilai);
    }

    private static String esc(String teks) {
        if (teks == null) {
            return "";
        }
        return teks.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String kosong(String teks) {
        if (teks == null || teks.isBlank()) {
            return "-";
        }
        return teks;
    }
}
