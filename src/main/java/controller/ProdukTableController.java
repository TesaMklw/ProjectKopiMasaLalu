package main.java.controller;

import java.sql.SQLException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.database.ProdukDAO;
import main.java.model.BukanKopi;
import main.java.model.Kopi;
import main.java.model.MakananPenutup;
import main.java.model.Produk;

public class ProdukTableController {
    @FXML private TableView<Produk> tabel;
    @FXML private TableColumn<Produk, Number> colId;
    @FXML private TableColumn<Produk, String> colTipe;
    @FXML private TableColumn<Produk, String> colNama;
    @FXML private TableColumn<Produk, Number> colHarga;
    @FXML private TableColumn<Produk, String> colUkuran;
    @FXML private TableColumn<Produk, String> colInfo;
    @FXML private TableColumn<Produk, String> colCatatan;
    @FXML private TableColumn<Produk, Number> colJumlah;
    @FXML private ComboBox<String> cmbTipe;
    @FXML private TextField txtNama;
    @FXML private TextField txtHarga;
    @FXML private TextField txtUkuran;
    @FXML private TextField txtInfo;
    @FXML private TextField txtCatatan;
    @FXML private TextField txtJumlah;

    private final ProdukDAO dao = new ProdukDAO();
    private int idDipilih;

    @FXML
    private void initialize() {
        cmbTipe.setItems(FXCollections.observableArrayList("Kopi", "BukanKopi", "MakananPenutup"));
        cmbTipe.valueProperty().addListener((obs, lama, baru) -> {
            boolean penutup = "MakananPenutup".equals(baru);
            txtUkuran.setDisable(penutup);
            if (penutup && (txtUkuran.getText() == null || txtUkuran.getText().isBlank())) {
                txtUkuran.setText("-");
            }
        });
        colId.setCellValueFactory(cell -> cell.getValue().idProdukProperty());
        colTipe.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTipeProduk()));
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaProduk"));
        colHarga.setCellValueFactory(cell -> cell.getValue().hargaProdukProperty());
        colUkuran.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUkuranTampil()));
        colInfo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getInfoTambahan()));
        colCatatan.setCellValueFactory(new PropertyValueFactory<>("catatanProduk"));
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
        txtUkuran.clear();
        txtUkuran.setDisable(false);
        txtInfo.clear();
        txtCatatan.clear();
        txtJumlah.setText("1");
        tabel.getSelectionModel().clearSelection();
    }

    private void isiForm(Produk produk) {
        if (produk == null) {
            return;
        }
        idDipilih = produk.getIdProduk();
        cmbTipe.setValue(produk.getTipeProduk());
        txtNama.setText(produk.getNamaProduk());
        txtHarga.setText(Float.toString(produk.getHargaProduk()));
        txtUkuran.setText(produk.getUkuranTampil());
        txtInfo.setText(produk.getInfoTambahan());
        txtCatatan.setText(produk.getCatatanProduk());
        txtJumlah.setText(Integer.toString(produk.getJumlahProduk()));
    }

    private Produk baca() {
        String tipe = cmbTipe.getValue();
        String nama = nilai(txtNama);
        String ukuran = nilai(txtUkuran);
        String info = nilai(txtInfo);
        String catatan = nilai(txtCatatan);
        if (tipe == null || tipe.isBlank() || nama.isEmpty()) {
            Pesan.gagal("Pilih tipe dan isi nama produk.");
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
            return new MakananPenutup(info, nama, harga, catatan, jumlah);
        }
        if (ukuran.isEmpty()) {
            Pesan.gagal("Ukuran minuman wajib diisi.");
            return null;
        }
        if ("Kopi".equals(tipe)) {
            return new Kopi(info, ukuran, nama, harga, catatan, jumlah);
        }
        return new BukanKopi(info, ukuran, nama, harga, catatan, jumlah);
    }

    private static String nilai(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }
}
