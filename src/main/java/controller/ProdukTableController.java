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
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.database.ProdukDAO;
import main.java.model.BukanKopi;
import main.java.model.Kopi;
import main.java.model.MakananPenutup;
import main.java.model.Minuman;
import main.java.model.Produk;

public class ProdukTableController {
    @FXML private TableView<Produk> tabel;
    @FXML private TableColumn<Produk, Number> colId;
    @FXML private TableColumn<Produk, String> colTipe;
    @FXML private TableColumn<Produk, String> colNama;
    @FXML private TableColumn<Produk, Number> colHarga;
    @FXML private TableColumn<Produk, String> colUkuran;
    @FXML private TableColumn<Produk, String> colInfo;
    @FXML private TableColumn<Produk, Number> colJumlah;
    @FXML private ComboBox<String> cmbTipe;
    @FXML private TextField txtNama;
    @FXML private TextField txtHarga;
    @FXML private Label lblUkuran;
    @FXML private TextField txtUkuran;
    @FXML private Label lblInfo;
    @FXML private ComboBox<String> cmbInfo;
    @FXML private TextField txtJumlah;

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
        colUkuran.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUkuranTampil()));
        colInfo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getInfoTambahan()));
        colJumlah.setCellValueFactory(cell -> cell.getValue().jumlahProdukProperty());
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
        txtJumlah.setText("1");
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
        txtHarga.setText(Float.toString(produk.getHargaProduk()));
        if (produk instanceof Minuman minuman) {
            txtUkuran.setText(minuman.getUkuranMinuman());
        } else {
            txtUkuran.setText("-");
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
        txtJumlah.setText(Integer.toString(produk.getJumlahProduk()));
        sedangIsi = false;
    }

    private Produk baca() {
        String tipe = kodeTipe(cmbTipe.getValue());
        String nama = nilai(txtNama);
        String ukuran = nilai(txtUkuran);
        String info = cmbInfo.getValue() == null ? "" : cmbInfo.getValue().trim();
        if (tipe == null || tipe.isBlank() || nama.isEmpty()) {
            Pesan.gagal("Pilih tipe dan isi nama produk.");
            return null;
        }
        if (info.isEmpty()) {
            Pesan.gagal(pesanInfoKosong(tipe));
            return null;
        }
        float harga;
        int jumlah;
        try {
            harga = Float.parseFloat(nilai(txtHarga));
            if (harga < 0) {
                Pesan.gagal("Harga tidak boleh negatif.");
                return null;
            }
        } catch (NumberFormatException e) {
            Pesan.gagal("Harga harus angka. Contoh: 18000");
            return null;
        }
        try {
            String teksJumlah = nilai(txtJumlah);
            jumlah = teksJumlah.isEmpty() ? 1 : Integer.parseInt(teksJumlah);
            if (jumlah <= 0) {
                Pesan.gagal("Jumlah harus lebih dari 0.");
                return null;
            }
        } catch (NumberFormatException e) {
            Pesan.gagal("Jumlah harus angka bulat.");
            return null;
        }
        if ("MakananPenutup".equals(tipe)) {
            return new MakananPenutup(info, nama, harga, jumlah);
        }
        if (ukuran.isEmpty()) {
            Pesan.gagal("Ukuran minuman wajib diisi.");
            return null;
        }
        if ("Kopi".equals(tipe)) {
            return new Kopi(info, ukuran, nama, harga, jumlah);
        }
        return new BukanKopi(info, ukuran, nama, harga, jumlah);
    }

    private void aturPilihan(String kode, boolean reset) {
        boolean makanan = "MakananPenutup".equals(kode);
        boolean minuman = "Kopi".equals(kode) || "BukanKopi".equals(kode);
        if (lblUkuran != null) {
            lblUkuran.setVisible(minuman);
            lblUkuran.setManaged(minuman);
        }
        txtUkuran.setVisible(minuman);
        txtUkuran.setManaged(minuman);
        txtUkuran.setDisable(!minuman);
        if (makanan) {
            txtUkuran.setText("-");
        } else if (reset) {
            txtUkuran.clear();
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
