package main.java.controller;

import java.io.IOException;
import java.sql.SQLException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.java.database.PelangganDAO;
import main.java.model.Pelanggan;

/** Form masuk. Kredensial dicek lewat PelangganDAO, lalu dashboard dibuka. */
public class LoginController {
    @FXML private TextField txtNama;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtNoHp;
    @FXML private Label lblPesan;

    private final PelangganDAO pelangganDAO = new PelangganDAO();
    private boolean pindahDashboard;

    @FXML
    private void initialize() {
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
    private void masuk() {
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
            bukaDashboard(pelanggan);
        } catch (SQLException e) {
            Pesan.gagalSql(e);
        }
    }

    @FXML
    private void daftar() {
        String nama = teks(txtNama);
        String password = txtPassword.getText() == null ? "" : txtPassword.getText();
        String noHp = teks(txtNoHp);
        if (nama.isEmpty() || password.isEmpty() || noHp.isEmpty()) {
            tampil("Lengkapi nama, password, dan no HP.");
            return;
        }
        try {
            if (pelangganDAO.namaTerpakai(nama)) {
                tampil("Nama sudah terdaftar. Silakan signin.");
                return;
            }
            Pelanggan pelanggan = new Pelanggan(nama);
            pelanggan.setNoHp(noHp);
            pelanggan.setPasswordPelanggan(password);
            pelangganDAO.insert(pelanggan);
            Pesan.info("Signup berhasil. Selamat datang, " + pelanggan.getNamaPelanggan() + "!");
            bukaDashboard(pelanggan);
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

    private void bukaDashboard(Pelanggan pelanggan) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/main_window.fxml"));
            Parent root = loader.load();
            MainWindowController dashboard = loader.getController();
            dashboard.setPelanggan(pelanggan);
            Stage stage = new Stage();
            stage.setTitle("Kopi Kenangan — Dashboard");
            Scene scene = new Scene(root);
            var css = getClass().getResource("/main/resources/css/style.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }
            stage.setScene(scene);
            stage.show();
            pindahDashboard = true;
            Stage login = (Stage) txtNama.getScene().getWindow();
            login.close();
        } catch (IOException e) {
            Pesan.gagal("Gagal membuka dashboard: " + e.getMessage());
        }
    }

    private void tampil(String teks) {
        lblPesan.setText(teks);
    }

    private static String teks(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }
}
