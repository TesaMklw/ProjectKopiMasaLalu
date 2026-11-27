package main.java.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Worker;
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
import javafx.print.PrinterJob;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import main.java.database.GeraiDAO;
import main.java.database.PelangganDAO;
import main.java.database.PembayaranDAO;
import main.java.database.ProdukDAO;
import main.java.database.TransaksiDAO;
import main.java.model.BukanKopi;
import main.java.model.DetailTransaksi;
import main.java.model.Gerai;
import main.java.model.Kopi;
import main.java.model.MakananPenutup;
import main.java.model.Minuman;
import main.java.model.Pelanggan;
import main.java.model.Pembayaran;
import main.java.model.Produk;
import main.java.model.Transaksi;
import main.java.model.UkuranMinuman;

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
    @FXML private ComboBox<UkuranMinuman> cmbUkuran;
    @FXML private TextField txtJumlahItem;
    @FXML private TextField txtCatatanItem;
    @FXML private TableView<DetailTransaksi> tabelKeranjang;
    @FXML private TableColumn<DetailTransaksi, String> colItemNama;
    @FXML private TableColumn<DetailTransaksi, String> colItemUkuran;
    @FXML private TableColumn<DetailTransaksi, Number> colItemJumlah;
    @FXML private TableColumn<DetailTransaksi, Number> colItemHarga;
    @FXML private TableColumn<DetailTransaksi, String> colItemCatatan;
    @FXML private Label lblJudul;
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
    private final ObservableList<DetailTransaksi> keranjang = FXCollections.observableArrayList();
    private final Map<DetailTransaksi, Float> hargaDasar = new IdentityHashMap<>();
    // true = hargaDasar adalah harga katalog (sebelum diskon). false = harga di detail sudah harga akhir.
    private final Map<DetailTransaksi, Boolean> hargaDariKatalog = new IdentityHashMap<>();
    // Harga satuan yang tersimpan di detail (sudah didiskon). Dipakai ulang saat Ubah tanpa ubah item.
    private final Map<DetailTransaksi, Float> hargaTersimpan = new IdentityHashMap<>();
    private List<String> tandaTersimpan;
    private String promoTersimpan;
    private float subtotalTersimpan;
    private float totalTersimpan;
    private float diskonTersimpan;
    private float uangKembaliTersimpan;
    private String uangTeksTersimpan;
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
        colItemNama.setCellValueFactory(cell -> cell.getValue().getProduk().namaProdukProperty());
        colItemUkuran.setCellValueFactory(cell -> cell.getValue().ukuranProperty());
        colItemJumlah.setCellValueFactory(cell -> cell.getValue().jumlahProperty());
        colItemHarga.setCellValueFactory(cell -> cell.getValue().getProduk().hargaProdukProperty());
        colItemCatatan.setCellValueFactory(cell -> cell.getValue().catatanProperty());
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
        // pilihan ukuran hanya aktif untuk minuman, isinya ukuran yang dijual produk itu
        cmbProduk.valueProperty().addListener((obs, lama, baru) -> isiPilihanUkuran(baru));
        isiPilihanUkuran(null);
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
        if (lblJudul != null) {
            lblJudul.setText("Transaksi");
        }
        pilihPelanggan(pelanggan);
        muat();
    }

    public void setGeraiLogin(Gerai gerai) {
        this.geraiLogin = gerai;
        this.pelangganLogin = null;
        this.modePelanggan = false;
        if (lblJudul != null) {
            lblJudul.setText("Kasir");
        }
        if (tabel != null) {
            tabel.setPlaceholder(new Label("Belum ada riwayat."));
        }
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

    private void isiPilihanUkuran(Produk produk) {
        if (cmbUkuran == null) {
            return;
        }
        if (produk instanceof Minuman minuman) {
            cmbUkuran.setItems(FXCollections.observableArrayList(minuman.getDaftarUkuran()));
            cmbUkuran.setDisable(false);
            cmbUkuran.setPromptText("Pilih ukuran");
            if (cmbUkuran.getItems().size() == 1) {
                cmbUkuran.getSelectionModel().selectFirst();
            } else {
                cmbUkuran.setValue(null);
            }
        } else {
            cmbUkuran.getItems().clear();
            cmbUkuran.setValue(null);
            cmbUkuran.setDisable(true);
            cmbUkuran.setPromptText(produk == null ? "Ukuran" : "Tanpa ukuran");
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
        float harga = asal.getHargaProduk();
        String ukuran = "-";
        if (asal instanceof Minuman) {
            UkuranMinuman pilihan = cmbUkuran == null ? null : cmbUkuran.getValue();
            if (pilihan == null) {
                Pesan.gagal("Pilih ukuran minuman.");
                return;
            }
            harga = pilihan.getHarga();
            ukuran = pilihan.getUkuran();
        }
        Produk salinan = salinProduk(asal);
        if (salinan == null) {
            Pesan.gagal("Jenis produk tidak dikenali.");
            return;
        }
        salinan.setHargaProduk(harga);
        String catatan = txtCatatanItem.getText() == null ? "" : txtCatatanItem.getText().trim();
        DetailTransaksi detail = new DetailTransaksi(salinan, jumlah, ukuran, catatan);
        hargaDasar.put(detail, harga);
        hargaDariKatalog.put(detail, Boolean.TRUE);
        keranjang.add(detail);
        txtCatatanItem.clear();
        perkiraan();
    }

    @FXML
    private void hapusItem() {
        DetailTransaksi dipilih = tabelKeranjang.getSelectionModel().getSelectedItem();
        if (dipilih == null) {
            Pesan.gagal("Pilih item keranjang yang ingin dihapus.");
            return;
        }
        keranjang.remove(dipilih);
        hargaDasar.remove(dipilih);
        hargaDariKatalog.remove(dipilih);
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
        try {
            if (transaksiDAO.idTerpakai(transaksi.getIdTransaksi())) {
                Pesan.gagal("ID sudah ada. Pakai Ubah, atau Bersihkan untuk ID baru.");
                return;
            }
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
        hargaDariKatalog.clear();
        hargaTersimpan.clear();
        tandaTersimpan = null;
        promoTersimpan = null;
        uangTeksTersimpan = null;
        txtId.setText(idBaru());
        cmbGerai.getSelectionModel().clearSelection();
        cmbBayar.getSelectionModel().clearSelection();
        cmbProduk.getSelectionModel().clearSelection();
        isiPilihanUkuran(null);
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

    @FXML
    private void printWebView() {
        Transaksi transaksi = susun(false);
        if (transaksi == null) {
            return;
        }
        try {
            WebView web = new WebView();
            web.setPrefSize(440, 700);
            Stage stage = new Stage();
            stage.setTitle("Print " + transaksi.getIdTransaksi());
            stage.setScene(new Scene(web, 440, 700));
            boolean[] sudah = new boolean[] {false};
            web.getEngine().getLoadWorker().stateProperty().addListener((obs, lama, state) -> {
                if (state == Worker.State.SUCCEEDED && !sudah[0]) {
                    sudah[0] = true;
                    Platform.runLater(() -> kirimKePrinter(web, stage));
                } else if (state == Worker.State.FAILED) {
                    Pesan.gagal("Gagal menyiapkan struk untuk printer.");
                }
            });
            web.getEngine().loadContent(render(transaksi));
            stage.show();
        } catch (IOException e) {
            Pesan.gagal("Gagal menampilkan struk: " + e.getMessage());
        }
    }

    private void kirimKePrinter(WebView web, Stage stage) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            Pesan.gagal("Printer tidak tersedia.");
            return;
        }
        if (!job.showPrintDialog(stage)) {
            return;
        }
        try {
            web.getEngine().print(job);
            job.endJob();
            Pesan.info("Struk dikirim ke printer.");
        } catch (RuntimeException e) {
            Pesan.gagal("Gagal mencetak struk: " + e.getMessage());
        }
    }

    private void isiTransaksi(Transaksi transaksi) {
        if (transaksi == null) {
            return;
        }
        keranjang.clear();
        hargaDasar.clear();
        hargaDariKatalog.clear();
        hargaTersimpan.clear();
        tandaTersimpan = null;
        promoTersimpan = null;
        uangTeksTersimpan = null;
        txtId.setText(transaksi.getIdTransaksi());
        pilihGerai(transaksi.getGerai());
        pilihPelanggan(transaksi.getPelanggan());
        pilihBayar(transaksi.getPembayaran());
        if (transaksi.getTanggalTransaksi() != null && !transaksi.getTanggalTransaksi().isBlank()) {
            dpTanggal.setValue(LocalDate.parse(transaksi.getTanggalTransaksi()));
        }
        String promo = normalPromo(transaksi.getKodePromo(), true);
        txtPromo.setText(promo);
        uangKembaliTersimpan = transaksi.getUangKembali();
        uangTeksTersimpan = Float.toString(transaksi.getTotalTransaksi() + transaksi.getUangKembali());
        txtUang.setText(uangTeksTersimpan);
        Map<String, Float> katalog = muatHargaKatalog();
        List<String> tanda = new ArrayList<>();
        for (DetailTransaksi baris : transaksi.getListDetail()) {
            Produk produkBaris = baris.getProduk();
            Produk produkSalinan = salinProduk(produkBaris);
            if (produkSalinan == null) {
                continue;
            }
            // Detail menyimpan harga setelah diskon. Harga katalog dicari per produk + ukuran, bukan combo kategori.
            float tersimpan = baris.getHargaSatuan();
            Float asli = produkBaris.getIdProduk() > 0
                    ? katalog.get(kunciHarga(produkBaris.getIdProduk(), baris.getUkuran())) : null;
            produkSalinan.setHargaProduk(tersimpan);
            DetailTransaksi salinan = new DetailTransaksi(produkSalinan, baris.getJumlah(),
                    baris.getUkuran(), baris.getCatatan());
            hargaTersimpan.put(salinan, tersimpan);
            if (asli != null) {
                hargaDasar.put(salinan, asli);
                hargaDariKatalog.put(salinan, Boolean.TRUE);
            } else {
                hargaDasar.put(salinan, tersimpan);
                hargaDariKatalog.put(salinan, Boolean.FALSE);
            }
            keranjang.add(salinan);
            tanda.add(tandaItem(salinan));
        }
        promoTersimpan = promo;
        subtotalTersimpan = transaksi.getSubtotalTransaksi();
        totalTersimpan = transaksi.getTotalTransaksi();
        diskonTersimpan = transaksi.getTotalDiskon();
        tandaTersimpan = tanda;
        susun(true);
    }

    // kunci "id|ukuran": harga minuman beda per ukuran, makanan pakai ukuran "-"
    private Map<String, Float> muatHargaKatalog() {
        Map<String, Float> katalog = new HashMap<>();
        try {
            for (Produk produk : produkDAO.getAll()) {
                if (produk instanceof Minuman minuman) {
                    for (UkuranMinuman ukuran : minuman.getDaftarUkuran()) {
                        katalog.put(kunciHarga(produk.getIdProduk(), ukuran.getUkuran()), ukuran.getHarga());
                    }
                } else {
                    katalog.put(kunciHarga(produk.getIdProduk(), "-"), produk.getHargaProduk());
                }
            }
        } catch (SQLException e) {
            Pesan.gagalSql(e);
        }
        return katalog;
    }

    private static String kunciHarga(int idProduk, String ukuran) {
        return idProduk + "|" + (ukuran == null ? "-" : ukuran.toLowerCase(Locale.ROOT));
    }

    private Transaksi susun(boolean diam) {
        if (keranjang.isEmpty()) {
            if (!diam) {
                Pesan.gagal("Keranjang kosong.");
            }
            return null;
        }
        if (cmbGerai.getValue() == null || (modePelanggan && cmbPelanggan.getValue() == null) || cmbBayar.getValue() == null) {
            if (!diam) {
                Pesan.gagal(modePelanggan
                    ? "Pilih gerai, pelanggan, dan metode pembayaran."
                    : "Pilih gerai dan metode pembayaran.");
            }
        return null;
        }
        String id = txtId.getText() == null ? "" : txtId.getText().trim();
        if (id.isEmpty()) {
            id = idBaru();
            txtId.setText(id);
        }
        if (id.length() > 20) {
            if (!diam) {
                Pesan.gagal("ID transaksi maksimal 20 karakter.");
            }
            return null;
        }
        String kode = normalPromo(txtPromo.getText(), diam);
        txtPromo.setText(kode);
        Float persen = persenPromo(kode);
        boolean tetap = keranjangMasihSama(kode);
        float subtotalAwal = 0;
        if (tetap) {
            for (DetailTransaksi detail : keranjang) {
                Float harga = hargaTersimpan.get(detail);
                if (harga != null) {
                    detail.getProduk().setHargaProduk(harga);
                }
            }
        } else {
            for (DetailTransaksi detail : keranjang) {
                Produk produk = detail.getProduk();
                float dasar = hargaDasar.getOrDefault(detail, produk.getHargaProduk());
                boolean dariKatalog = hargaDariKatalog.getOrDefault(detail, Boolean.TRUE);
                if (dariKatalog) {
                    produk.setHargaProduk(dasar);
                    subtotalAwal += dasar * detail.getJumlah();
                    if (persen != null) {
                        produk.calcDiscount(persen); // polymorphism: Minuman vs MakananPenutup (maks 20%)
                    }
                } else {
                    subtotalAwal += detail.getSubtotal();
                }
            }
        }
        LocalDate tanggal = dpTanggal.getValue() == null ? LocalDate.now() : dpTanggal.getValue();
        dpTanggal.setValue(tanggal);
        Transaksi transaksi = new Transaksi(id, cmbGerai.getValue(), 0, 0, 0,
                cmbPelanggan.getValue(), tanggal.toString(), cmbBayar.getValue());
        for (DetailTransaksi detail : keranjang) {
            transaksi.addDetail(detail);
        }
        transaksi.setKodePromo(kode);
        if (tetap) {
            transaksi.setSubtotalTransaksi(subtotalTersimpan);
            transaksi.setTotalTransaksi(totalTersimpan);
            transaksi.setTotalDiskon(diskonTersimpan);
        } else {
            transaksi.calcSubtotal();
            transaksi.setSubtotalTransaksi(subtotalAwal);
            transaksi.setTotalDiskon(subtotalAwal - transaksi.getTotalTransaksi());
        }
        String teksUang = txtUang.getText() == null ? "" : txtUang.getText().trim();
        if (tetap && uangTeksTersimpan != null && teksUang.equals(uangTeksTersimpan.trim())) {
            transaksi.setUangKembali(uangKembaliTersimpan);
            tampilkanAngka(transaksi);
            return transaksi;
        }
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
        for (DetailTransaksi detail : keranjang) {
            float dasar = hargaDasar.getOrDefault(detail, detail.getHargaSatuan());
            sum += dasar * detail.getJumlah();
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

    private String normalPromo(String kode, boolean diam) {
        if (kode == null) {
            return "";
        }
        kode = kode.trim().toUpperCase();
        switch (kode) {
            case "HEMAT10":
            case "HEMAT20":
            case "HEMAT50":
                return kode;
            default:
                if (!kode.isEmpty() && !diam) {
                    Pesan.info("Kode tidak dikenal. Lanjut tanpa diskon.");
                }
                return "";
        }
    }

    private static Float persenPromo(String kode) {
        if ("HEMAT10".equals(kode)) {
            return 10f;
        }
        if ("HEMAT20".equals(kode)) {
            return 20f;
        }
        if ("HEMAT50".equals(kode)) {
            return 50f;
        }
        return null;
    }

    // Item dan promo sama dengan saat dimuat: jangan hitung diskon lagi.
    private boolean keranjangMasihSama(String kode) {
        if (promoTersimpan == null || tandaTersimpan == null || !promoTersimpan.equals(kode)) {
            return false;
        }
        if (tandaTersimpan.size() != keranjang.size()) {
            return false;
        }
        for (int i = 0; i < keranjang.size(); i++) {
            if (!tandaTersimpan.get(i).equals(tandaItem(keranjang.get(i)))) {
                return false;
            }
        }
        return true;
    }

    private static String tandaItem(DetailTransaksi detail) {
        return detail.getProduk().getIdProduk() + "|" + detail.getUkuran() + "|" + detail.getJumlah();
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
        // Metode sudah dihapus: jangan masukkan ke daftar pilihan, tapi tetap tampilkan nama snapshot.
        String nama = pembayaran.getMetodePembayaran();
        if (nama != null && !nama.isBlank()) {
            cmbBayar.setValue(pembayaran);
        }
    }

    // Salin produk sesuai jenisnya supaya harga diskon di keranjang tidak mengubah objek katalog
    private Produk salinProduk(Produk produk) {
        Produk salinan;
        if (produk instanceof Kopi kopi) {
            salinan = new Kopi(kopi.getJenisKopi(), kopi.getNamaProduk(), kopi.getHargaProduk());
        } else if (produk instanceof BukanKopi bukan) {
            salinan = new BukanKopi(bukan.getJenisRasa(), bukan.getNamaProduk(), bukan.getHargaProduk());
        } else if (produk instanceof MakananPenutup makanan) {
            salinan = new MakananPenutup(makanan.getVariasiMakananPenutup(),
                    makanan.getNamaProduk(), makanan.getHargaProduk());
        } else {
            return null;
        }
        salinan.setIdProduk(produk.getIdProduk());
        return salinan;
    }

    private String render(Transaksi transaksi) throws IOException {
        String html = bacaTemplate();
        StringBuilder baris = new StringBuilder();
        for (DetailTransaksi detail : transaksi.getListDetail()) {
            baris.append("<tr><td>").append(esc(detail.getNamaTampil()))
                    .append("</td><td class=\"num\">x").append(detail.getJumlah())
                    .append("</td><td class=\"num\">").append(rupiah(detail.getHargaSatuan()))
                    .append("</td><td class=\"num\">").append(rupiah(detail.getSubtotal()))
                    .append("</td><td>").append(esc(kosong(detail.getCatatan())))
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

    private String idBaru() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        for (int i = 0; i < 30; i++) {
            String id = "TRX" + LocalDateTime.now().format(fmt)
                    + String.format("%03d", ThreadLocalRandom.current().nextInt(1000));
            if (idBebas(id)) {
                return id;
            }
        }
        for (int i = 0; i < 30; i++) {
            String angka = Long.toUnsignedString(System.nanoTime());
            if (angka.length() > 16) {
                angka = angka.substring(angka.length() - 16);
            }
            String id = "TRX" + angka;
            if (idBebas(id)) {
                return id;
            }
        }
        String cadangan = "TRX" + Long.toUnsignedString(System.nanoTime());
        if (cadangan.length() > 20) {
            cadangan = cadangan.substring(0, 20);
        }
        return cadangan;
    }

    private boolean idBebas(String id) {
        if (tabel != null) {
            for (Transaksi ada : tabel.getItems()) {
                if (id.equals(ada.getIdTransaksi())) {
                    return false;
                }
            }
        }
        try {
            return !transaksiDAO.idTerpakai(id);
        } catch (SQLException e) {
            return true;
        }
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
