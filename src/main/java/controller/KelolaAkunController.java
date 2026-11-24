package main.java.controller;

import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.java.database.PelangganDAO;
import main.java.model.Pelanggan;

/**
 * Pelanggan yang sedang login mengubah no HP dan, jika diisi, kata sandi akunnya sendiri.
 */
public class KelolaAkunController {
    @FXML private TextField txtNama;
    @FXML private TextField txtHp;
    @FXML private PasswordField txtSandi;
    @FXML private PasswordField txtKonfirmasi;

    private final PelangganDAO dao = new PelangganDAO();
    private Pelanggan pelangganLogin;

    public void setPelanggan(Pelanggan pelanggan) {
        this.pelangganLogin = pelanggan;
        if (pelanggan == null) {
            return;
        }
        if (txtNama != null) {
            txtNama.setText(pelanggan.getNamaPelanggan() == null ? "" : pelanggan.getNamaPelanggan());
        }
        if (txtHp != null) {
            txtHp.setText(pelanggan.getNoHp() == null ? "" : pelanggan.getNoHp());
        }
        kosongkanSandi();
    }

    @FXML
    private void simpan() {
        if (pelangganLogin == null) {
            Pesan.gagal("Akun pelanggan tidak dikenali. Masuk ulang lalu coba lagi.");
            return;
        }
        String noHp = txtHp.getText() == null ? "" : txtHp.getText().trim();
        if (noHp.isEmpty()) {
            Pesan.gagal("No. HP wajib diisi.");
            return;
        }
        String sandi = txtSandi.getText() == null ? "" : txtSandi.getText();
        String konfirmasi = txtKonfirmasi.getText() == null ? "" : txtKonfirmasi.getText();
        boolean gantiSandi = !sandi.isBlank();
        if (gantiSandi && !sandi.equals(konfirmasi)) {
            Pesan.gagal("Kata sandi baru dan konfirmasi tidak cocok.");
            return;
        }
        if (!gantiSandi && !konfirmasi.isBlank()) {
            Pesan.gagal("Isi kata sandi baru, lalu ulangi di kolom konfirmasi.");
            return;
        }
        int id = pelangganLogin.getIdPelanggan();
        try {
            dao.updateAkunSendiri(id, noHp, gantiSandi ? sandi : "");
            pelangganLogin.setNoHp(noHp);
            if (gantiSandi) {
                Pelanggan segar = dao.findById(id);
                if (segar != null && segar.getPasswordPelanggan() != null) {
                    pelangganLogin.setPasswordPelanggan(segar.getPasswordPelanggan());
                }
            }
            kosongkanSandi();
            Pesan.info(gantiSandi
                    ? "Nomor HP dan kata sandi diperbarui."
                    : "Nomor HP diperbarui.");
        } catch (SQLException e) {
            Pesan.gagalSql(e);
        }
    }

    private void kosongkanSandi() {
        if (txtSandi != null) {
            txtSandi.clear();
        }
        if (txtKonfirmasi != null) {
            txtKonfirmasi.clear();
        }
    }
}
