package main.java.controller;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Layar awal: pilih tampilan pelanggan atau gerai (jendela baru), atau keluar program.
 */
public class PilihInterfaceController {
    @FXML private Label lblJudul;

    @FXML
    private void initialize() {
        if (lblJudul == null) {
            return;
        }
        lblJudul.sceneProperty().addListener((obs, lama, scene) -> {
            if (scene == null) {
                return;
            }
            scene.windowProperty().addListener((obs2, lamaJendela, jendela) -> pasangTutup(jendela));
            pasangTutup(scene.getWindow());
        });
    }

    @FXML
    private void bukaPelanggan() {
        bukaLogin(false);
    }

    @FXML
    private void bukaGerai() {
        bukaLogin(true);
    }

    @FXML
    private void keluarProgram() {
        if (Pesan.konfirmasi("Keluar", "Keluar dari Kopi Masa Lalu?")) {
            Platform.exit();
        }
    }

    public static void bukaLogin(boolean modeGerai) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    PilihInterfaceController.class.getResource("/main/resources/login_form.fxml"));
            Parent root = loader.load();
            LoginController login = loader.getController();
            login.siapkan(modeGerai);
            Stage stage = new Stage();
            stage.setTitle(modeGerai
                    ? "Kopi Masa Lalu — Masuk Gerai"
                    : "Kopi Masa Lalu — Pelanggan");
            Scene scene = new Scene(root);
            var css = PilihInterfaceController.class.getResource("/main/resources/css/style.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            Pesan.gagal("Gagal membuka halaman masuk: " + e.getMessage());
        }
    }

    private void pasangTutup(Window jendela) {
        if (jendela instanceof Stage stage) {
            stage.setOnCloseRequest(event -> {
                event.consume();
                keluarProgram();
            });
        }
    }
}
