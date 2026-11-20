package main.java.controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import main.java.model.Gerai;
import main.java.model.Pelanggan;

/**
 * Controller main_window.fxml: sapaan, menu, dan tab data (UI Baru – Tab Style).
 * Mode gerai = semua tabel; mode pelanggan = hanya tab pesan/transaksi.
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
    @FXML private TabPane tabData;

    private Pelanggan pelangganLogin;
    private Gerai geraiLogin;
    private boolean modePelanggan;
    private boolean sedangLogout;
    private boolean tabSiap;

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
        susunTab();
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
        susunTab();
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
                    "Anda masuk sebagai pelanggan. Buat pesanan lewat tab Pesan. "
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
                    "Anda masuk sebagai gerai. Kelola Gerai, Produk, Pelanggan, Pembayaran, dan Transaksi lewat tab.");
        }
    }

    private void susunTab() {
        if (tabData == null || tabSiap) {
            return;
        }
        tabSiap = true;
        tabData.getTabs().clear();
        if (!modePelanggan) {
            tabData.getTabs().add(buatTab("Gerai", "/main/resources/gerai_table.fxml"));
            tabData.getTabs().add(buatTab("Produk", "/main/resources/produk_table.fxml"));
            tabData.getTabs().add(buatTab("Pelanggan", "/main/resources/pelanggan_table.fxml"));
            tabData.getTabs().add(buatTab("Pembayaran", "/main/resources/pembayaran_table.fxml"));
        }
        tabData.getTabs().add(buatTabTransaksi());
        if (!tabData.getTabs().isEmpty()) {
            tabData.getSelectionModel().select(modePelanggan ? tabData.getTabs().size() - 1 : 0);
        }
    }

    private Tab buatTab(String judul, String fxml) {
        Tab tab = new Tab(judul);
        tab.setClosable(false);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            tab.setContent(bungkus(root));
        } catch (IOException e) {
            Pesan.gagal("Gagal membuka " + judul + ": " + e.getMessage());
            tab.setContent(new Label("Gagal memuat " + judul));
        }
        return tab;
    }

    private Tab buatTabTransaksi() {
        String judul = modePelanggan ? "Pesan" : "Transaksi";
        Tab tab = new Tab(judul);
        tab.setClosable(false);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/transaksi_table.fxml"));
            Parent root = loader.load();
            TransaksiTableController controller = loader.getController();
            if (modePelanggan) {
                controller.setPelangganLogin(pelangganLogin);
            } else if (geraiLogin != null) {
                controller.setGeraiLogin(geraiLogin);
            }
            tab.setContent(bungkus(root));
        } catch (IOException e) {
            Pesan.gagal("Gagal membuka transaksi: " + e.getMessage());
            tab.setContent(new Label("Gagal memuat transaksi"));
        }
        return tab;
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

    private void pilihTab(String judul) {
        susunTab();
        if (tabData == null) {
            return;
        }
        for (Tab tab : tabData.getTabs()) {
            if (judul.equals(tab.getText())) {
                tabData.getSelectionModel().select(tab);
                return;
            }
        }
    }

    @Override
    @FXML
    public void bukaGerai() {
        pilihTab("Gerai");
    }

    @Override
    @FXML
    public void bukaProduk() {
        pilihTab("Produk");
    }

    @Override
    @FXML
    public void bukaPelanggan() {
        pilihTab("Pelanggan");
    }

    @Override
    @FXML
    public void bukaPembayaran() {
        pilihTab("Pembayaran");
    }

    @Override
    @FXML
    public void bukaTransaksi() {
        pilihTab(modePelanggan ? "Pesan" : "Transaksi");
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
                if (sedangLogout) {
                    return;
                }
                event.consume();
                keluar();
            });
        }
    }
}
