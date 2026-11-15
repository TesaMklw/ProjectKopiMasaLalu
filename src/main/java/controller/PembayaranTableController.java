package main.java.controller;

import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.database.PembayaranDAO;
import main.java.model.Pembayaran;

public class PembayaranTableController {
    @FXML private TableView<Pembayaran> tabel;
    @FXML private TableColumn<Pembayaran, String> colKode;
    @FXML private TableColumn<Pembayaran, String> colMetode;
    @FXML private TextField txtKode;
    @FXML private TextField txtMetode;

    private final PembayaranDAO dao = new PembayaranDAO();
    private String kodeDipilih;

    @FXML
    private void initialize() {
        colKode.setCellValueFactory(new PropertyValueFactory<>("kodePembayaran"));
        colMetode.setCellValueFactory(new PropertyValueFactory<>("metodePembayaran"));
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
        Pembayaran pembayaran = baca();
        if (pembayaran == null) {
            return;
        }
        try {
            if (dao.findByKode(pembayaran.getKodePembayaran()) != null) {
                Pesan.gagal("Kode pembayaran sudah ada.");
                return;
            }
            dao.insert(pembayaran);
            bersih();
            muat();
            Pesan.info("Metode pembayaran ditambahkan.");
        } catch (SQLException e) {
            Pesan.gagalSql(e);
        }
    }

    @FXML
    private void ubah() {
        if (kodeDipilih == null) {
            Pesan.gagal("Pilih metode pembayaran yang ingin diubah.");
            return;
        }
        Pembayaran pembayaran = baca();
        if (pembayaran == null) {
            return;
        }
        try {
            dao.update(pembayaran, kodeDipilih);
            bersih();
            muat();
            Pesan.info("Metode pembayaran diperbarui.");
        } catch (SQLException e) {
            Pesan.gagalSql(e);
        }
    }

    @FXML
    private void hapus() {
        if (kodeDipilih == null) {
            Pesan.gagal("Pilih metode pembayaran yang ingin dihapus.");
            return;
        }
        if (!Pesan.konfirmasi("Hapus pembayaran", "Hapus metode " + kodeDipilih + "?")) {
            return;
        }
        try {
            dao.delete(kodeDipilih);
            bersih();
            muat();
        } catch (SQLException e) {
            Pesan.gagalSql(e);
        }
    }

    @FXML
    private void bersih() {
        kodeDipilih = null;
        txtKode.clear();
        txtMetode.clear();
        tabel.getSelectionModel().clearSelection();
    }

    private void isiForm(Pembayaran pembayaran) {
        if (pembayaran == null) {
            return;
        }
        kodeDipilih = pembayaran.getKodePembayaran();
        txtKode.setText(pembayaran.getKodePembayaran());
        txtMetode.setText(pembayaran.getMetodePembayaran());
    }

    private Pembayaran baca() {
        String kode = txtKode.getText() == null ? "" : txtKode.getText().trim();
        String metode = txtMetode.getText() == null ? "" : txtMetode.getText().trim();
        if (kode.isEmpty() || metode.isEmpty()) {
            Pesan.gagal("Kode dan metode pembayaran wajib diisi.");
            return null;
        }
        Pembayaran pembayaran = new Pembayaran(metode);
        pembayaran.setKodePembayaran(kode);
        return pembayaran;
    }
}
