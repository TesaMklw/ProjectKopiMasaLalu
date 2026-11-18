package main.java.controller;

import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.database.PelangganDAO;
import main.java.model.Pelanggan;

public class PelangganTableController {
    @FXML private TableView<Pelanggan> tabel;
    @FXML private TableColumn<Pelanggan, Number> colId;
    @FXML private TableColumn<Pelanggan, String> colNama;
    @FXML private TableColumn<Pelanggan, String> colHp;
    @FXML private TableColumn<Pelanggan, String> colPassword;
    @FXML private TextField txtNama;
    @FXML private TextField txtHp;
    @FXML private PasswordField txtPassword;

    private final PelangganDAO dao = new PelangganDAO();
    private int idDipilih;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(cell -> cell.getValue().idPelangganProperty());
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaPelanggan"));
        colHp.setCellValueFactory(new PropertyValueFactory<>("noHp"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("passwordPelanggan"));
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
        Pelanggan pelanggan = baca(false);
        if (pelanggan == null) {
            return;
        }
        try {
            if (dao.namaTerpakai(pelanggan.getNamaPelanggan())) {
                Pesan.gagal("Nama sudah terdaftar. Silakan signin.");
                return;
            }
            dao.insert(pelanggan);
            bersih();
            muat();
            Pesan.info("Pelanggan ditambahkan.");
        } catch (SQLException e) {
            Pesan.gagalSql(e);
        }
    }

    @FXML
    private void ubah() {
        if (idDipilih <= 0) {
            Pesan.gagal("Pilih pelanggan yang ingin diubah.");
            return;
        }
        Pelanggan pelanggan = baca(true);
        if (pelanggan == null) {
            return;
        }
        pelanggan.setIdPelanggan(idDipilih);
        try {
            dao.update(pelanggan);
            bersih();
            muat();
            Pesan.info("Pelanggan diperbarui.");
        } catch (SQLException e) {
            Pesan.gagalSql(e);
        }
    }

    @FXML
    private void hapus() {
        if (idDipilih <= 0) {
            Pesan.gagal("Pilih pelanggan yang ingin dihapus.");
            return;
        }
        if (!Pesan.konfirmasi("Hapus pelanggan", "Hapus pelanggan ini?")) {
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
        txtNama.clear();
        txtHp.clear();
        txtPassword.clear();
        tabel.getSelectionModel().clearSelection();
    }

    private void isiForm(Pelanggan pelanggan) {
        if (pelanggan == null) {
            return;
        }
        idDipilih = pelanggan.getIdPelanggan();
        txtNama.setText(pelanggan.getNamaPelanggan());
        txtHp.setText(pelanggan.getNoHp());
        txtPassword.clear();
    }

    private Pelanggan baca(boolean ubah) {
        String nama = txtNama.getText() == null ? "" : txtNama.getText().trim();
        String hp = txtHp.getText() == null ? "" : txtHp.getText().trim();
        String password = txtPassword.getText() == null ? "" : txtPassword.getText();
        if (nama.isEmpty() || hp.isEmpty() || (!ubah && password.isEmpty())) {
            Pesan.gagal("Nama, no HP, dan password wajib diisi.");
            return null;
        }
        Pelanggan pelanggan = new Pelanggan(nama);
        pelanggan.setNoHp(hp);
        if (password.isEmpty()) {
            Pelanggan dipilih = tabel.getSelectionModel().getSelectedItem();
            if (dipilih == null || dipilih.getPasswordPelanggan() == null
                    || dipilih.getPasswordPelanggan().isEmpty()) {
                Pesan.gagal("Password tidak boleh kosong.");
                return null;
            }
            pelanggan.setPasswordPelanggan(dipilih.getPasswordPelanggan());
        } else {
            pelanggan.setPasswordPelanggan(password);
        }
        return pelanggan;
    }
}
