package main.java.controller;

import java.sql.SQLException;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.database.ProdukDAO;
import main.java.model.BukanKopi;
import main.java.model.Kopi;
import main.java.model.MakananPenutup;
import main.java.model.Minuman;
import main.java.model.Produk;
import main.java.model.UkuranMinuman;

public class ProdukTableController {
    @FXML private TableView<Produk> tabel;
    @FXML private TableColumn<Produk, Number> colId;
    @FXML private TableColumn<Produk, String> colTipe;
    @FXML private TableColumn<Produk, String> colNama;
    @FXML private TableColumn<Produk, Number> colHarga;
    @FXML private TableColumn<Produk, String> colUkuran;
    @FXML private TableColumn<Produk, String> colInfo;
    @FXML private ComboBox<String> cmbTipe;
    @FXML private TextField txtNama;
    @FXML private Label lblHarga;
    @FXML private TextField txtHarga;
    @FXML private Label lblUkuran;
    @FXML private HBox boxUkuran;
    @FXML private TextField txtHargaSmall;
    @FXML private TextField txtHargaMedium;
    @FXML private TextField txtHargaLarge;
    @FXML private Label lblInfo;
    @FXML private ComboBox<String> cmbInfo;

    private static final List<String> JENIS_KOPI = List.of("Americano", "Latte", "Cappuccino", "Espresso");
    private static final List<String> JENIS_RASA = List.of("Matcha", "Cokelat", "Thai Tea", "Lemon Tea");
    private static final List<String> VARIASI_MAKANAN = List.of("Croissant", "Brownies", "Roti Bakar");

    private final ProdukDAO dao = new ProdukDAO();
    private int idDipilih;
    private boolean sedangIsi;

    @FXML
    private void initialize() {
        cmbTipe.setItems(FXCollections.observableArrayList("Makanan", "Kopi", "Non Kopi"));
        cmbTipe.valueProperty().addListener((obs, lama, baru) -> {
            if (sedangIsi) {
                return;
            }
            aturPilihan(kodeTipe(baru), true);
        });
        colId.setCellValueFactory(cell -> cell.getValue().idProdukProperty());
        colTipe.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTipeProduk()));
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaProduk"));
        colHarga.setCellValueFactory(cell -> cell.getValue().hargaProdukProperty());
        colUkuran.setCellValueFactory(cell -> new SimpleStringProperty(teksUkuran(cell.getValue())));
        colInfo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getInfoTambahan()));
        tabel.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabel.getSelectionModel().selectedItemProperty().addListener((obs, lama, baru) -> isiForm(baru));
        muat();
    }

    @FXML
    private void muat() {
        try {
            tabel.getItems().setAll(dao.getAll());
        } catch (SQLException e) {
            Pesan.gagalSql(e);
        }
    }

    @FXML
    private void tambah() {
        Produk produk = baca();
        if (produk == null) {
            return;
        }
        try {
            dao.insert(produk);
            bersih();
            muat();
            Pesan.info("Produk ditambahkan.");
        } catch (SQLException e) {
            Pesan.gagalSql(e);
        }
    }

    @FXML
    private void ubah() {
        if (idDipilih <= 0) {
            Pesan.gagal("Pilih produk yang ingin diubah.");
            return;
        }
        Produk produk = baca();
        if (produk == null) {
            return;
        }
        produk.setIdProduk(idDipilih);
        try {
            dao.update(produk);
            bersih();
            muat();
            Pesan.info("Produk diperbarui.");
        } catch (SQLException e) {
            Pesan.gagalSql(e);
        }
    }

    @FXML
    private void hapus() {
        if (idDipilih <= 0) {
            Pesan.gagal("Pilih produk yang ingin dihapus.");
            return;
        }
        if (!Pesan.konfirmasi("Hapus produk", "Hapus produk ini dari katalog?")) {
            return;
        }
        try {
            dao.delete(idDipilih);
            bersih();
            muat();
        } catch (SQLException e) {
            Pesan.gagalSql(e);
        }
    }

    @FXML
    private void bersih() {
        idDipilih = 0;
        cmbTipe.getSelectionModel().clearSelection();
        txtNama.clear();
        txtHarga.clear();
        aturPilihan(null, true);
        tabel.getSelectionModel().clearSelection();
    }

    private void isiForm(Produk produk) {
        if (produk == null) {
            return;
        }
        sedangIsi = true;
        idDipilih = produk.getIdProduk();
        String kode = produk.getTipeProduk();
        cmbTipe.setValue(labelTipe(kode));
        aturPilihan(kode, false);
        txtNama.setText(produk.getNamaProduk());
        kosongkanHargaUkuran();
        if (produk instanceof Minuman minuman) {
            txtHarga.clear();
            for (UkuranMinuman ukuran : minuman.getDaftarUkuran()) {
                TextField field = fieldUkuran(ukuran.getUkuran());
                if (field != null) {
                    field.setText(Float.toString(ukuran.getHarga()));
                }
            }
        } else {
            txtHarga.setText(Float.toString(produk.getHargaProduk()));
        }
        if (produk instanceof Kopi kopi) {
            pilihInfo(kopi.getJenisKopi());
        } else if (produk instanceof BukanKopi bukan) {
            pilihInfo(bukan.getJenisRasa());
        } else if (produk instanceof MakananPenutup makanan) {
            pilihInfo(makanan.getVariasiMakananPenutup());
        } else {
            pilihInfo(produk.getInfoTambahan());
        }
        sedangIsi = false;
    }

    private Produk baca() {
        String tipe = kodeTipe(cmbTipe.getValue());
        String nama = nilai(txtNama);
        String info = cmbInfo.getValue() == null ? "" : cmbInfo.getValue().trim();
        if (tipe == null || tipe.isBlank() || nama.isEmpty()) {
            Pesan.gagal("Pilih tipe dan isi nama produk.");
            return null;
        }
        if (info.isEmpty()) {
            Pesan.gagal(pesanInfoKosong(tipe));
            return null;
        }
        if ("MakananPenutup".equals(tipe)) {
            Float harga = bacaHarga(txtHarga, "Harga");
            if (harga == null) {
                return null;
            }
            return new MakananPenutup(info, nama, harga);
        }
        Minuman minuman = "Kopi".equals(tipe) ? new Kopi(info, nama, 0) : new BukanKopi(info, nama, 0);
        for (String ukuran : UkuranMinuman.PILIHAN) {
            TextField field = fieldUkuran(ukuran);
            if (nilai(field).isEmpty()) {
                continue; // ukuran ini tidak dijual
            }
            Float harga = bacaHarga(field, "Harga " + ukuran);
            if (harga == null) {
                return null;
            }
            minuman.tambahUkuran(new UkuranMinuman(ukuran, harga));
        }
        if (minuman.getDaftarUkuran().isEmpty()) {
            Pesan.gagal("Isi harga minimal satu ukuran (Small, Medium, atau Large).");
            return null;
        }
        return minuman;
    }

    /** null kalau tidak valid (pesan error sudah ditampilkan). */
    private static Float bacaHarga(TextField field, String label) {
        try {
            float harga = Float.parseFloat(nilai(field));
            if (harga < 0) {
                Pesan.gagal(label + " tidak boleh negatif.");
                return null;
            }
            return harga;
        } catch (NumberFormatException e) {
            Pesan.gagal(label + " harus angka. Contoh: 18000");
            return null;
        }
    }

    private TextField fieldUkuran(String ukuran) {
        if ("Small".equalsIgnoreCase(ukuran)) {
            return txtHargaSmall;
        }
        if ("Medium".equalsIgnoreCase(ukuran)) {
            return txtHargaMedium;
        }
        if ("Large".equalsIgnoreCase(ukuran)) {
            return txtHargaLarge;
        }
        return null;
    }

    private void kosongkanHargaUkuran() {
        txtHargaSmall.clear();
        txtHargaMedium.clear();
        txtHargaLarge.clear();
    }

    /** Contoh: "Small 15000, Large 20000" untuk minuman, "-" untuk makanan. */
    private static String teksUkuran(Produk produk) {
        if (!(produk instanceof Minuman minuman)) {
            return "-";
        }
        StringBuilder sb = new StringBuilder();
        for (UkuranMinuman ukuran : minuman.getDaftarUkuran()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(ukuran.getUkuran()).append(' ').append(String.format("%.0f", ukuran.getHarga()));
        }
        return sb.length() == 0 ? "-" : sb.toString();
    }

    private void aturPilihan(String kode, boolean reset) {
        boolean makanan = "MakananPenutup".equals(kode);
        boolean minuman = "Kopi".equals(kode) || "BukanKopi".equals(kode);
        if (lblUkuran != null) {
            lblUkuran.setVisible(minuman);
            lblUkuran.setManaged(minuman);
        }
        boxUkuran.setVisible(minuman);
        boxUkuran.setManaged(minuman);
        // minuman pakai harga per ukuran, jadi field harga tunggal hanya untuk makanan
        lblHarga.setVisible(!minuman);
        lblHarga.setManaged(!minuman);
        txtHarga.setVisible(!minuman);
        txtHarga.setManaged(!minuman);
        if (reset) {
            txtHarga.clear();
            kosongkanHargaUkuran();
        }
        if ("Kopi".equals(kode)) {
            lblInfo.setText("Jenis kopi");
            cmbInfo.setPromptText("Pilih jenis kopi");
            cmbInfo.setItems(FXCollections.observableArrayList(JENIS_KOPI));
        } else if ("BukanKopi".equals(kode)) {
            lblInfo.setText("Jenis rasa");
            cmbInfo.setPromptText("Pilih rasa non kopi");
            cmbInfo.setItems(FXCollections.observableArrayList(JENIS_RASA));
        } else if (makanan) {
            lblInfo.setText("Variasi");
            cmbInfo.setPromptText("Pilih variasi makanan");
            cmbInfo.setItems(FXCollections.observableArrayList(VARIASI_MAKANAN));
        } else {
            lblInfo.setText("Jenis / variasi");
            cmbInfo.setPromptText("Pilih kategori dulu");
            cmbInfo.getItems().clear();
        }
        if (reset) {
            cmbInfo.setValue(null);
        }
    }

    private void pilihInfo(String info) {
        if (info == null || info.isBlank()) {
            cmbInfo.setValue(null);
            return;
        }
        if (!cmbInfo.getItems().contains(info)) {
            cmbInfo.getItems().add(info);
        }
        cmbInfo.setValue(info);
    }

    private static String labelTipe(String kode) {
        if ("MakananPenutup".equals(kode)) {
            return "Makanan";
        }
        if ("BukanKopi".equals(kode)) {
            return "Non Kopi";
        }
        return kode;
    }

    private static String kodeTipe(String label) {
        if ("Makanan".equals(label)) {
            return "MakananPenutup";
        }
        if ("Non Kopi".equals(label)) {
            return "BukanKopi";
        }
        return label;
    }

    private static String pesanInfoKosong(String tipe) {
        if ("Kopi".equals(tipe)) {
            return "Pilih jenis kopi.";
        }
        if ("BukanKopi".equals(tipe)) {
            return "Pilih rasa non kopi.";
        }
        return "Pilih variasi makanan.";
    }

    private static String nilai(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }
}
