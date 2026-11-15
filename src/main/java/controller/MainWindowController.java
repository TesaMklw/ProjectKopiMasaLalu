package main.java.controller;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;
import main.java.model.Gerai;
import main.java.model.Pelanggan;

/**
 * Controller main_window.fxml: sapaan, menu tabel, dan konfirmasi keluar.
 * Mode gerai = menu admin penuh; mode pelanggan = hanya pesan/transaksi.
 */
public class MainWindowController extends DashboardController {
    @FXML private Label lblSapaan;
    @FXML private Label lblMenuSub;
    @FXML private Label lblJudul;
    @FXML private Label lblDeskripsi;
    @FXML private Button btnGerai;
    @FXML private Button btnProduk;
    @FXML private Button btnPelanggan;
    @FXML private Button btnPembayaran;
    @FXML private Button btnTransaksi;
    @FXML private Button btnBukaTransaksi;
    @FXML private Button btnKelolaProduk;

    private Pelanggan pelangganLogin;
    private Gerai geraiLogin;
    private boolean modePelanggan;

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
        this.geraiLogin = null;
        this.modePelanggan = true;
        if (lblSapaan != null && pelanggan != null && pelanggan.getNamaPelanggan() != null) {
            lblSapaan.setText("Halo, " + pelanggan.getNamaPelanggan() + " — selamat memesan.");
        }
        terapkanModePelanggan();
    }

    public void setGerai(Gerai gerai) {
        this.geraiLogin = gerai;
        this.pelangganLogin = null;
        this.modePelanggan = false;
        if (lblSapaan != null && gerai != null) {
            String nama = gerai.getNamaGerai() == null ? "gerai" : gerai.getNamaGerai();
            String kode = gerai.getKodeGerai() == null ? "" : " (" + gerai.getKodeGerai() + ")";
            lblSapaan.setText("Halo, kasir " + nama + kode + " — selamat bertugas.");
        }
        terapkanModeGerai();
    }

    private void terapkanModePelanggan() {
        setVisible(btnGerai, false);
        setVisible(btnProduk, false);
        setVisible(btnPelanggan, false);
        setVisible(btnPembayaran, false);
        setVisible(btnKelolaProduk, false);
        if (btnTransaksi != null) {
            btnTransaksi.setText("Pesan");
        }
        if (btnBukaTransaksi != null) {
            btnBukaTransaksi.setText("Buat Pesanan");
        }
        if (lblMenuSub != null) {
            lblMenuSub.setText("Menu pelanggan");
        }
        if (lblJudul != null) {
            lblJudul.setText("Pesan Kopi");
        }
        if (lblDeskripsi != null) {
            lblDeskripsi.setText(
                    "Anda masuk sebagai pelanggan. Buat pesanan lewat menu Pesan. "
                            + "Data gerai, produk, dan admin lainnya hanya untuk kasir gerai.");
        }
    }

    private void terapkanModeGerai() {
        setVisible(btnGerai, true);
        setVisible(btnProduk, true);
        setVisible(btnPelanggan, true);
        setVisible(btnPembayaran, true);
        setVisible(btnKelolaProduk, true);
        if (btnTransaksi != null) {
            btnTransaksi.setText("Transaksi");
        }
        if (btnBukaTransaksi != null) {
            btnBukaTransaksi.setText("Buka Transaksi");
        }
        if (lblMenuSub != null) {
            lblMenuSub.setText("Menu kasir gerai");
        }
        if (lblJudul != null) {
            lblJudul.setText("Dashboard Gerai");
        }
        if (lblDeskripsi != null) {
            lblDeskripsi.setText(
                    "Anda masuk sebagai gerai. Kelola Gerai, Produk, Pelanggan, Pembayaran, dan Transaksi dari sidebar.");
        }
    }

    @Override
    @FXML
    public void bukaTransaksi() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/transaksi_table.fxml"));
            Parent root = loader.load();
            TransaksiTableController controller = loader.getController();
            if (modePelanggan) {
                controller.setPelangganLogin(pelangganLogin);
            } else {
                controller.setGeraiLogin(geraiLogin);
            }
            tampil(root, modePelanggan ? "Pesan — Kopi Kenangan" : "Transaksi — Kopi Kenangan");
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

    private void setVisible(Button button, boolean tampil) {
        if (button == null) {
            return;
        }
        button.setVisible(tampil);
        button.setManaged(tampil);
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
