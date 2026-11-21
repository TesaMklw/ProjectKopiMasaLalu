package main.java.controller;

import java.sql.SQLException;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/** Dialog singkat untuk layar JavaFX Kopi Masa Lalu. */
public final class Pesan {
    private Pesan() {
    }

    public static void info(String teks) {
        alert(Alert.AlertType.INFORMATION, "Kopi Masa Lalu", teks);
    }

    public static void gagal(String teks) {
        alert(Alert.AlertType.ERROR, "Kopi Masa Lalu", teks);
    }

    public static void gagalSql(SQLException e) {
        String state = e.getSQLState();
        String pesan = e.getMessage() == null ? "" : e.getMessage();
        if ("23505".equals(state)) {
            gagal("Data duplikat. Kode atau nama sudah dipakai.");
            return;
        }
        if ("23503".equals(state)) {
            gagal("Data masih dipakai tabel lain (misalnya transaksi). Hapus data terkait lebih dulu.");
            return;
        }
        if ("08001".equals(state) || "28P01".equals(state) || "3D000".equals(state)
                || pesan.contains("Connection") || pesan.contains("refused")
                || pesan.contains("password authentication")) {
            gagal("Tidak bisa terhubung ke PostgreSQL (" + main.java.database.PostgreSQLConnection.URL
                    + "). Nyalakan server, pastikan database kopimasalalu sudah dibuat di pgAdmin, lalu cek user/password di PostgreSQLConnection.");
            return;
        }
        gagal(pesan.isBlank() ? "Kesalahan database." : pesan);
    }

    public static boolean konfirmasi(String judul, String teks) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(judul);
        alert.setHeaderText(judul);
        alert.setContentText(teks);
        Optional<ButtonType> hasil = alert.showAndWait();
        return hasil.isPresent() && hasil.get() == ButtonType.OK;
    }

    private static void alert(Alert.AlertType tipe, String judul, String teks) {
        Alert alert = new Alert(tipe);
        alert.setTitle(judul);
        alert.setHeaderText(null);
        alert.setContentText(teks);
        alert.showAndWait();
    }
}
