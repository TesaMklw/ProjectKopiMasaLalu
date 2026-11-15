package main.java.controller;

import java.io.IOException;
import java.sql.SQLException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.java.database.GeraiDAO;
import main.java.database.PelangganDAO;
import main.java.model.Gerai;
import main.java.model.Pelanggan;

/**
 * Form masuk dual-mode: Pelanggan (nama + password) atau Gerai (kode + nama gerai).
 * Tidak ada kolom password di tabel gerai, jadi gerai memakai kode_gerai + nama_gerai.
 */
public class LoginController {
    @FXML private ToggleButton btnModePelanggan;
    @FXML private ToggleButton btnModeGerai;
    @FXML private VBox panelPelanggan;
    @FXML private VBox panelGerai;
    @FXML private TextField txtNama;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtNoHp;
    @FXML private TextField txtKodeGerai;
    @FXML private TextField txtNamaGerai;
    @FXML private Label lblPesan;
    @FXML private Label lblSubtitle;
    @FXML private Label lblHint;
    @FXML private Button btnDaftar;

    private final PelangganDAO pelangganDAO = new PelangganDAO();
    private final GeraiDAO geraiDAO = new GeraiDAO();
    private boolean modeGerai;
    private boolean pindahDashboard;

    @FXML
    private void initialize() {
        pilihModePelanggan();
        txtNama.sceneProperty().addListener((obs, lama, scene) -> {
            if (scene == null) {
                return;
            }
            scene.windowProperty().addListener((obs2, lamaJendela, jendela) -> {
                if (jendela instanceof Stage stage) {
                    stage.setOnCloseRequest(event -> {
                        if (pindahDashboard) {
                            return;
                        }
                        event.consume();
                        keluar();
                    });
                }
            });
        });
    }

    @FXML
    private void pilihModePelanggan() {
        modeGerai = false;
        if (btnModePelanggan != null) {
            btnModePelanggan.setSelected(true);
        }
        setPanel(panelPelanggan, true);
        setPanel(panelGerai, false);
        if (btnDaftar != null) {
            btnDaftar.setVisible(true);
            btnDaftar.setManaged(true);
        }
        if (lblSubtitle != null) {
            lblSubtitle.setText("Masuk sebagai pelanggan");
        }
        if (lblHint != null) {
            lblHint.setText("Pelanggan: nama + password dari tabel pelanggan. Bisa daftar akun baru.");
        }
        tampil("");
    }

    @FXML
    private void pilihModeGerai() {
        modeGerai = true;
        if (btnModeGerai != null) {
            btnModeGerai.setSelected(true);
        }
        setPanel(panelPelanggan, false);
        setPanel(panelGerai, true);
        if (btnDaftar != null) {
            btnDaftar.setVisible(false);
            btnDaftar.setManaged(false);
        }
        if (lblSubtitle != null) {
            lblSubtitle.setText("Masuk sebagai gerai / kasir");
        }
        if (lblHint != null) {
            lblHint.setText("Gerai: isi kode_gerai dan nama_gerai yang sudah ada di tabel gerai (tanpa password).");
        }
        tampil("");
    }

    @FXML
    private void masuk() {
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
        String nama = teks(txtNamaGerai);
        if (kode.isEmpty() || nama.isEmpty()) {
            tampil("Isi kode gerai dan nama gerai.");
            return;
        }
        try {
            Gerai gerai = geraiDAO.cariLogin(kode, nama);
            if (gerai == null) {
                tampil("Login gerai gagal. Kode atau nama tidak cocok.");
                return;
            }
            bukaDashboard(null, gerai);
        } catch (SQLException e) {
            Pesan.gagalSql(e);
        }
    }

    @FXML
    private void daftar() {
        if (modeGerai) {
            tampil("Pendaftaran gerai hanya lewat menu admin setelah login gerai.");
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
    private void keluar() {
        if (Pesan.konfirmasi("Keluar", "Keluar dari Kopi Kenangan?")) {
            Platform.exit();
        }
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
            stage.setTitle(gerai != null
                    ? "Kopi Kenangan — Dashboard Gerai"
                    : "Kopi Kenangan — Dashboard Pelanggan");
            Scene scene = new Scene(root);
            var css = getClass().getResource("/main/resources/css/style.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }
            stage.setScene(scene);
            stage.show();
            pindahDashboard = true;
            Stage login = (Stage) (modeGerai ? txtKodeGerai : txtNama).getScene().getWindow();
            login.close();
        } catch (IOException e) {
            Pesan.gagal("Gagal membuka dashboard: " + e.getMessage());
        }
    }

    private void setPanel(VBox panel, boolean tampil) {
        if (panel == null) {
            return;
        }
        panel.setVisible(tampil);
        panel.setManaged(tampil);
    }

    private void tampil(String teks) {
        if (lblPesan != null) {
            lblPesan.setText(teks);
        }
    }

    private static String teks(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }
}
