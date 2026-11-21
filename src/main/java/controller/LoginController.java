package main.java.controller;

import java.io.IOException;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.java.database.GeraiDAO;
import main.java.database.PelangganDAO;
import main.java.model.Gerai;
import main.java.model.Pelanggan;

/**
 * Halaman masuk satu peran: pelanggan (sign in / sign up) atau gerai (sign in saja).
 * Interface Login dipakai di sini, lewat PelangganDAO / GeraiDAO. No HP hanya saat sign up.
 */
public class LoginController implements Login {
    @FXML private HBox barisAkun;
    @FXML private ToggleButton btnModeMasuk;
    @FXML private ToggleButton btnModeDaftar;
    @FXML private VBox panelPelanggan;
    @FXML private VBox panelNoHp;
    @FXML private VBox panelGerai;
    @FXML private TextField txtNama;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtNoHp;
    @FXML private TextField txtKodeGerai;
    @FXML private PasswordField txtPasswordGerai;
    @FXML private Label lblPesan;
    @FXML private Label lblSubtitle;
    @FXML private Label lblHint;
    @FXML private Button btnMasuk;
    @FXML private Button btnDaftar;

    private final PelangganDAO pelangganDAO = new PelangganDAO();
    private final GeraiDAO geraiDAO = new GeraiDAO();
    private boolean modeGerai;
    private boolean modeDaftar;

    public void siapkan(boolean sebagaiGerai) {
        this.modeGerai = sebagaiGerai;
        if (sebagaiGerai) {
            tampilkanNode(barisAkun, false);
            tampilkanNode(panelPelanggan, false);
            tampilkanNode(panelGerai, true);
            tampilkanNode(btnMasuk, true);
            tampilkanNode(btnDaftar, false);
            if (btnMasuk != null) {
                btnMasuk.setDefaultButton(true);
            }
            if (lblSubtitle != null) {
                lblSubtitle.setText("Masuk sebagai gerai / kasir");
            }
            if (lblHint != null) {
                lblHint.setText("Gerai hanya sign in: isi kode gerai dan password gerai.");
            }
            tampil("");
            return;
        }
        tampilkanNode(panelGerai, false);
        tampilkanNode(panelPelanggan, true);
        tampilkanNode(barisAkun, true);
        tampilkanMasuk();
    }

    @FXML
    private void tampilkanMasuk() {
        modeDaftar = false;
        if (btnModeMasuk != null) {
            btnModeMasuk.setSelected(true);
        }
        tampilkanNode(panelNoHp, false);
        tampilkanNode(btnMasuk, true);
        tampilkanNode(btnDaftar, false);
        if (btnMasuk != null) {
            btnMasuk.setDefaultButton(true);
        }
        if (btnDaftar != null) {
            btnDaftar.setDefaultButton(false);
        }
        if (lblSubtitle != null) {
            lblSubtitle.setText("Sign in pelanggan");
        }
        if (lblHint != null) {
            lblHint.setText("Masuk dengan nama dan password. No HP hanya diisi saat sign up.");
        }
        tampil("");
    }

    @FXML
    private void tampilkanDaftar() {
        modeDaftar = true;
        if (btnModeDaftar != null) {
            btnModeDaftar.setSelected(true);
        }
        tampilkanNode(panelNoHp, true);
        tampilkanNode(btnMasuk, false);
        tampilkanNode(btnDaftar, true);
        if (btnMasuk != null) {
            btnMasuk.setDefaultButton(false);
        }
        if (btnDaftar != null) {
            btnDaftar.setDefaultButton(true);
        }
        if (lblSubtitle != null) {
            lblSubtitle.setText("Sign up pelanggan");
        }
        if (lblHint != null) {
            lblHint.setText("Daftar akun baru dengan nama, password, dan no HP.");
        }
        tampil("");
    }

    @FXML
    private void tekanEnterPelanggan() {
        if (modeDaftar) {
            signup();
        } else {
            signin();
        }
    }

    @Override
    @FXML
    public void signin() {
        if (modeGerai) {
            masukGerai();
        } else {
            masukPelanggan();
        }
    }

    private void masukPelanggan() {
        String nama = teks(txtNama);
        String password = txtPassword.getText() == null ? "" : txtPassword.getText();
        if (nama.isEmpty() || password.isEmpty()) {
            tampil("Isi nama pelanggan dan password.");
            return;
        }
        try {
            Pelanggan pelanggan = pelangganDAO.cariLogin(nama, password);
            if (pelanggan == null) {
                tampil("Login gagal. Nama atau password salah.");
                return;
            }
            bukaDashboard(pelanggan, null);
        } catch (SQLException e) {
            Pesan.gagalSql(e);
        }
    }

    private void masukGerai() {
        String kode = teks(txtKodeGerai);
        String password = txtPasswordGerai.getText() == null ? "" : txtPasswordGerai.getText();
        if (kode.isEmpty() || password.isEmpty()) {
            tampil("Isi kode gerai dan password gerai.");
            return;
        }
        try {
            Gerai gerai = geraiDAO.cariLogin(kode, password);
            if (gerai == null) {
                tampil("Login gerai gagal. Kode atau password salah.");
                return;
            }
            bukaDashboard(null, gerai);
        } catch (SQLException e) {
            Pesan.gagalSql(e);
        }
    }

    @Override
    @FXML
    public void signup() {
        if (modeGerai) {
            tampil("Gerai tidak punya sign up. Masuk dengan kode dan password gerai.");
            return;
        }
        String nama = teks(txtNama);
        String password = txtPassword.getText() == null ? "" : txtPassword.getText();
        String noHp = teks(txtNoHp);
        if (nama.isEmpty() || password.isEmpty() || noHp.isEmpty()) {
            tampil("Lengkapi nama, password, dan no HP.");
            return;
        }
        try {
            if (pelangganDAO.namaTerpakai(nama)) {
                tampil("Nama sudah terdaftar. Silakan masuk.");
                return;
            }
            Pelanggan pelanggan = new Pelanggan(nama);
            pelanggan.setNoHp(noHp);
            pelanggan.setPasswordPelanggan(password);
            pelangganDAO.insert(pelanggan);
            Pesan.info("Signup berhasil. Selamat datang, " + pelanggan.getNamaPelanggan() + "!");
            bukaDashboard(pelanggan, null);
        } catch (SQLException e) {
            Pesan.gagalSql(e);
        }
    }

    @FXML
    private void tutup() {
        Node sumber = modeGerai ? txtKodeGerai : txtNama;
        if (sumber == null || sumber.getScene() == null) {
            return;
        }
        Stage stage = (Stage) sumber.getScene().getWindow();
        stage.close();
    }

    private void bukaDashboard(Pelanggan pelanggan, Gerai gerai) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/main_window.fxml"));
            Parent root = loader.load();
            MainWindowController dashboard = loader.getController();
            if (gerai != null) {
                dashboard.setGerai(gerai);
            } else {
                dashboard.setPelanggan(pelanggan);
            }
            Stage stage = new Stage();
            stage.setTitle(gerai != null ? "Dashboard Gerai" : "Dashboard Pelanggan");
            Scene scene = new Scene(root);
            var css = getClass().getResource("/main/resources/css/style.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }
            stage.setScene(scene);
            stage.show();
            Stage login = (Stage) (modeGerai ? txtKodeGerai : txtNama).getScene().getWindow();
            login.close();
        } catch (IOException e) {
            Pesan.gagal("Gagal membuka dashboard: " + e.getMessage());
        }
    }

    private void tampilkanNode(Node node, boolean tampil) {
        if (node == null) {
            return;
        }
        node.setVisible(tampil);
        node.setManaged(tampil);
    }

    private void tampil(String teksPesan) {
        if (lblPesan != null) {
            lblPesan.setText(teksPesan);
        }
    }

    private static String teks(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }
}
