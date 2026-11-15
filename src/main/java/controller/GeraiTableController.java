package main.java.controller;

import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.database.GeraiDAO;
import main.java.model.Gerai;

public class GeraiTableController {
    @FXML private TableView<Gerai> tabel;
    @FXML private TableColumn<Gerai, String> colKode;
    @FXML private TableColumn<Gerai, String> colNama;
    @FXML private TableColumn<Gerai, String> colAlamat;
    @FXML private TextField txtKode;
    @FXML private TextField txtNama;
    @FXML private TextArea txtAlamat;

    private final GeraiDAO dao = new GeraiDAO();
    private String kodeDipilih;

    @FXML
    private void initialize() {
        colKode.setCellValueFactory(new PropertyValueFactory<>("kodeGerai"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaGerai"));
        colAlamat.setCellValueFactory(new PropertyValueFactory<>("alamatGerai"));
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
        Gerai gerai = baca();
        if (gerai == null) {
            return;
        }
        try {
            if (dao.findByKode(gerai.getKodeGerai()) != null) {
                Pesan.gagal("Kode gerai sudah ada.");
                return;
            }
            dao.insert(gerai);
            bersih();
            muat();
            Pesan.info("Gerai ditambahkan.");
        } catch (SQLException e) {
            Pesan.gagalSql(e);
        }
    }

    @FXML
    private void ubah() {
        if (kodeDipilih == null) {
            Pesan.gagal("Pilih baris gerai yang ingin diubah.");
            return;
        }
        Gerai gerai = baca();
        if (gerai == null) {
            return;
        }
        try {
            dao.update(gerai, kodeDipilih);
            bersih();
            muat();
            Pesan.info("Gerai diperbarui.");
        } catch (SQLException e) {
            Pesan.gagalSql(e);
        }
    }

    @FXML
    private void hapus() {
        if (kodeDipilih == null) {
            Pesan.gagal("Pilih baris gerai yang ingin dihapus.");
            return;
        }
        if (!Pesan.konfirmasi("Hapus gerai", "Hapus gerai " + kodeDipilih + "?")) {
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
        txtNama.clear();
        txtAlamat.clear();
        tabel.getSelectionModel().clearSelection();
    }

    private void isiForm(Gerai gerai) {
        if (gerai == null) {
            return;
        }
        kodeDipilih = gerai.getKodeGerai();
        txtKode.setText(gerai.getKodeGerai());
        txtNama.setText(gerai.getNamaGerai());
        txtAlamat.setText(gerai.getAlamatGerai());
    }

    private Gerai baca() {
        String kode = txtKode.getText() == null ? "" : txtKode.getText().trim();
        String nama = txtNama.getText() == null ? "" : txtNama.getText().trim();
        String alamat = txtAlamat.getText() == null ? "" : txtAlamat.getText().trim();
        if (kode.isEmpty() || nama.isEmpty() || alamat.isEmpty()) {
            Pesan.gagal("Kode, nama, dan alamat gerai wajib diisi.");
            return null;
        }
        Gerai gerai = new Gerai(nama, alamat);
        gerai.setKodeGerai(kode);
        return gerai;
    }
}
