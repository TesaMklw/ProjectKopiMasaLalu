package main.java.controller;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;
import main.java.model.Pelanggan;

/** Controller main_window.fxml: sapaan kasir, menu tabel, dan konfirmasi keluar. */
public class MainWindowController extends DashboardController {
    @FXML private Label lblSapaan;
    private Pelanggan pelangganLogin;

    @FXML
    private void initialize() {
        if (lblSapaan != null && (lblSapaan.getText() == null || lblSapaan.getText().isBlank())) {
            lblSapaan.setText("Halo, kasir Kopi Kenangan");
        }
        if (lblSapaan != null) {
            lblSapaan.sceneProperty().addListener((obs, lama, scene) -> {
                if (scene == null) {
                    return;
                }
                scene.windowProperty().addListener((obs2, lamaJendela, jendela) -> pasangTutup(jendela));
                pasangTutup(scene.getWindow());
            });
        }
    }

    public void setPelanggan(Pelanggan pelanggan) {
        this.pelangganLogin = pelanggan;
        if (lblSapaan != null && pelanggan != null && pelanggan.getNamaPelanggan() != null) {
            lblSapaan.setText("Halo, " + pelanggan.getNamaPelanggan() + " — selamat bertugas.");
        }
    }

    @Override
    @FXML
    public void bukaTransaksi() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/transaksi_table.fxml"));
            Parent root = loader.load();
            TransaksiTableController controller = loader.getController();
            controller.setPelangganLogin(pelangganLogin);
            tampil(root, "Transaksi — Kopi Kenangan");
        } catch (IOException e) {
            Pesan.gagal("Gagal membuka transaksi: " + e.getMessage());
        }
    }

    @FXML
    public void keluar() {
        if (Pesan.konfirmasi("Keluar", "Keluar dari Kopi Kenangan?")) {
            Platform.exit();
        }
    }

    private void pasangTutup(Window jendela) {
        if (jendela instanceof Stage stage) {
            stage.setOnCloseRequest(event -> {
                event.consume();
                keluar();
            });
        }
    }
}
