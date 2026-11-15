# Kopi Kenangan POS

Kerangka proyek JavaFX MVC untuk kasir Kopi Kenangan. Kelas domain yang sudah ada dipindah ke paket baru. Kelas, FXML, CSS, dan template struk yang baru masih kerangka kosong. Tabel PostgreSQL dibuat manual di pgAdmin; repositori ini tidak menyertakan `schema.sql`.

## Pohon folder

```
src/App.java                                  paket default (kerangka)
src/main/java/controller/                     Cetak, Login, dan controller FXML (kerangka)
src/main/java/database/                       PostgreSQLConnection dan DAO (kerangka)
src/main/java/model/                          Gerai, Pelanggan, Pembayaran, Produk dan turunannya, Transaksi
src/main/java/view/Main.java                  alur kasir yang sudah ada
src/main/resources/login_form.fxml
src/main/resources/main_window.fxml
src/main/resources/gerai_table.fxml
src/main/resources/produk_table.fxml
src/main/resources/pelanggan_table.fxml
src/main/resources/pembayaran_table.fxml
src/main/resources/transaksi_table.fxml
src/main/resources/css/style.css
src/main/resources/struk/invoice.html
.vscode/settings.json
.vscode/launch.json                           konfigurasi "App"
```

Keluaran kompilasi VS Code ada di `bin/` dan tidak di-commit.

## Menjalankan di VS Code

1. Pasang JDK dan JavaFX SDK 27 di `C:\javafx-sdk-27`.
2. Salin `postgresql-42.7.14.jar` ke `C:\javafx-sdk-27\lib\`.
3. Buka folder ini di VS Code (Extension Pack for Java).
4. Konfigurasi launch **App** memakai:

```
--module-path "C:\javafx-sdk-27\lib" --add-modules javafx.controls,javafx.fxml,javafx.web --enable-native-access=javafx.graphics,javafx.web
```

`App` masih kelas kosong. Logika kasir yang sudah ada ada di `main.java.view.Main`.
