package main.java.controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import main.java.model.Gerai;
import main.java.model.Pelanggan;

/**
 * Controller main_window.fxml: sapaan, menu samping, dan area konten.
 * Semua halaman dibuka di area konten lewat menu samping (tanpa tab).
 * Mode gerai = semua menu; mode pelanggan = hanya form pemesanan.
 */
public class MainWindowController extends DashboardController {
    @FXML private Label lblPageTitle;
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
    @FXML private Node boxAksi;
    @FXML private StackPane konten;

    private Pelanggan pelangganLogin;
    private Gerai geraiLogin;
    private boolean modePelanggan;
    private boolean sedangLogout;

    @FXML
    private void initialize() {
        if (lblSapaan != null && (lblSapaan.getText() == null || lblSapaan.getText().isBlank())) {
            lblSapaan.setText("Halo, kasir Kopi Masa Lalu");
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
        bukaTransaksi();
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
        tampilkanSambutan();
    }

    private void terapkanModePelanggan() {
        setVisible(btnGerai, false);
        setVisible(btnProduk, false);
        setVisible(btnPelanggan, false);
        setVisible(btnPembayaran, false);
        setVisible(boxAksi, false);
        if (btnTransaksi != null) {
            btnTransaksi.setText("Pesan");
        }
        if (lblPageTitle != null) {
            lblPageTitle.setText("Dashboard Pelanggan");
        }
        if (lblMenuSub != null) {
            lblMenuSub.setText("Menu pelanggan");
        }
        if (lblJudul != null) {
            lblJudul.setText("Pesan Kopi");
        }
        if (lblDeskripsi != null) {
            lblDeskripsi.setText(
                    "Anda masuk sebagai pelanggan. Form pemesanan langsung ditampilkan. "
                            + "Data gerai, produk, dan admin lainnya hanya untuk kasir gerai.");
        }
    }

    private void terapkanModeGerai() {
        setVisible(btnGerai, true);
        setVisible(btnProduk, true);
        setVisible(btnPelanggan, true);
        setVisible(btnPembayaran, true);
        setVisible(boxAksi, true);
        if (btnTransaksi != null) {
            btnTransaksi.setText("Transaksi");
        }
        if (lblPageTitle != null) {
            lblPageTitle.setText("Dashboard Gerai");
        }
        if (lblMenuSub != null) {
            lblMenuSub.setText("Menu kasir gerai");
        }
        if (lblJudul != null) {
            lblJudul.setText("Dashboard Gerai");
        }
        if (lblDeskripsi != null) {
            lblDeskripsi.setText(
                    "Pilih Gerai, Produk, Pelanggan, Pembayaran, atau Transaksi di menu samping.");
        }
    }

    private void tampilkanSambutan() {
        if (konten == null) {
            return;
        }
        konten.getChildren().clear();
        Label sambutan = new Label("Selamat datang di Dashboard Gerai. Pilih menu di samping untuk memulai.");
        sambutan.setWrapText(true);
        sambutan.setStyle("-fx-font-size: 15px; -fx-text-fill: #94857B;");
        konten.getChildren().add(sambutan);
    }

    /** Mengganti isi area konten dengan FXML yang dipilih dari menu samping. */
    private void tampilkan(String judul, String fxml) {
        if (konten == null) {
            return;
        }
        konten.getChildren().clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            if ("/main/resources/transaksi_table.fxml".equals(fxml)) {
                TransaksiTableController controller = loader.getController();
                if (modePelanggan) {
                    controller.setPelangganLogin(pelangganLogin);
                } else if (geraiLogin != null) {
                    controller.setGeraiLogin(geraiLogin);
                }
            }
            konten.getChildren().add(bungkus(root));
        } catch (IOException e) {
            Pesan.gagal("Gagal membuka " + judul + ": " + e.getMessage());
            konten.getChildren().add(new Label("Gagal memuat " + judul));
        }
    }

    private Parent bungkus(Parent root) {
        if (root instanceof ScrollPane) {
            return root;
        }
        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(false);
        return scroll;
    }

    @Override
    @FXML
    public void bukaGerai() {
        tampilkan("Gerai", "/main/resources/gerai_table.fxml");
    }

    @Override
    @FXML
    public void bukaProduk() {
        tampilkan("Produk", "/main/resources/produk_table.fxml");
    }

    @Override
    @FXML
    public void bukaPelanggan() {
        tampilkan("Pelanggan", "/main/resources/pelanggan_table.fxml");
    }

    @Override
    @FXML
    public void bukaPembayaran() {
        tampilkan("Pembayaran", "/main/resources/pembayaran_table.fxml");
    }

    @Override
    @FXML
    public void bukaTransaksi() {
        tampilkan("Transaksi", "/main/resources/transaksi_table.fxml");
    }

    @FXML
    public void keluar() {
        if (sedangLogout) {
            return;
        }
        if (!Pesan.konfirmasi("Logout", "Keluar dari akun dan kembali ke halaman masuk?")) {
            return;
        }
        sedangLogout = true;
        PilihInterfaceController.bukaLogin(!modePelanggan);
        if (lblSapaan != null && lblSapaan.getScene() != null) {
            ((Stage) lblSapaan.getScene().getWindow()).close();
        }
    }

    private void setVisible(Node node, boolean tampil) {
        if (node == null) {
            return;
        }
        node.setVisible(tampil);
        node.setManaged(tampil);
    }

    private void pasangTutup(Window jendela) {
        if (jendela instanceof Stage stage) {
            stage.setOnCloseRequest(event -> {
                if (sedangLogout) {
                    return;
                }
                event.consume();
                keluar();
            });
        }
    }
}
