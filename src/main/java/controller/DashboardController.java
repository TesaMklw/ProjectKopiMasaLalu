package main.java.controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Membuka setiap jendela tabel dari dashboard.
 * MainWindowController mewarisi method ini dan menambah sapaan plus konfirmasi keluar.
 */
public class DashboardController {

    @FXML
    public void bukaGerai() {
        buka("/main/resources/gerai_table.fxml", "Gerai — Kopi Kenangan");
    }

    @FXML
    public void bukaProduk() {
        buka("/main/resources/produk_table.fxml", "Produk — Kopi Kenangan");
    }

    @FXML
    public void bukaPelanggan() {
        buka("/main/resources/pelanggan_table.fxml", "Pelanggan — Kopi Kenangan");
    }

    @FXML
    public void bukaPembayaran() {
        buka("/main/resources/pembayaran_table.fxml", "Pembayaran — Kopi Kenangan");
    }

    @FXML
    public void bukaTransaksi() {
        buka("/main/resources/transaksi_table.fxml", "Transaksi — Kopi Kenangan");
    }

    protected void buka(String fxml, String judul) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            tampil(root, judul);
        } catch (IOException e) {
            Pesan.gagal("Gagal membuka jendela: " + e.getMessage());
        }
    }

    protected Stage tampil(Parent root, String judul) {
        Stage stage = new Stage();
        stage.setTitle(judul);
        Scene scene = new Scene(root);
        var css = getClass().getResource("/main/resources/css/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
        stage.setScene(scene);
        stage.show();
        return stage;
    }
}
