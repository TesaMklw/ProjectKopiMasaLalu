# Kopi Kenangan POS

Aplikasi kasir Kopi Kenangan (JavaFX, pola MVC) untuk mata kuliah OOP. Data gerai, produk, pelanggan, pembayaran, dan transaksi disimpan di PostgreSQL. Aplikasi tidak membaca berkas teks.

Hierarki produk tetap: `Produk` → `Minuman` → `Kopi` / `BukanKopi`, dan `Produk` → `MakananPenutup`. Diskon makanan penutup dibatasi 20%. Kode promo: `HEMAT10`, `HEMAT20`, `HEMAT50`.

## Pohon folder

```
src/App.java                                  paket default, memanggil Main.main
src/main/java/controller/                     FXML controller + interface Cetak dan Login
src/main/java/database/                       PostgreSQLConnection dan DAO
src/main/java/model/                          entitas (JavaFX Property)
src/main/java/view/Main.java                  javafx.application.Application
src/main/resources/login_form.fxml
src/main/resources/main_window.fxml
src/main/resources/gerai_table.fxml
src/main/resources/produk_table.fxml
src/main/resources/pelanggan_table.fxml
src/main/resources/pembayaran_table.fxml
src/main/resources/transaksi_table.fxml
src/main/resources/css/style.css
src/main/resources/struk/invoice.html         template struk (WebView)
database/schema.sql                           CREATE TABLE + seed
.vscode/settings.json
.vscode/launch.json                           konfigurasi "App"
```

Keluaran kompilasi VS Code ada di `bin/` dan tidak di-commit.

## Menjalankan

1. Pasang JDK 27 dan [JavaFX SDK 27](https://jdk.java.net/javafx27/) di `C:\javafx-sdk-27` (folder `lib` berisi jar JavaFX).
2. Unduh `postgresql-42.7.14.jar` lalu salin ke `C:\javafx-sdk-27\lib\`.
3. Buka folder proyek di VS Code (Extension Pack for Java).
4. Pulihkan database (bagian di bawah).
5. Jika password PostgreSQL bukan `postgres`, ubah `USER` dan `PASSWORD` di `src/main/java/database/PostgreSQLConnection.java`. URL-nya `jdbc:postgresql://localhost:5432/kopi_kenangan`.
6. Run konfigurasi **App**. Argumen VM sudah di `.vscode/launch.json`:

```
--module-path "C:\javafx-sdk-27\lib" --add-modules javafx.controls,javafx.fxml,javafx.web --enable-native-access=javafx.graphics,javafx.web
```

Login contoh setelah seed: **Vionanda** / **viona123**.

## Database

Cara paling singkat: di pgAdmin buat database `kopi_kenangan` (UTF8), buka Query Tool, lalu jalankan seluruh `database/schema.sql`. Skrip itu menghapus tabel lama, membuat tabel, mengisi data awal, dan menyetel sequence.

Kalau tabel dibuat lewat dialog pgAdmin (bukan dengan menempel skrip), ikuti panduan di bawah. Tipe, nullability, kunci, dan aksi `ON DELETE` mengikuti `schema.sql` apa adanya.

### Urutan buat tabel

Tabel 1–4 tidak saling merujuk, tetapi `transaksi` membutuhkan ketiganya plus `produk`, dan `detail_transaksi` membutuhkan `transaksi` serta `produk`. Buat persis dalam urutan ini:

1. `gerai`
2. `pelanggan`
3. `pembayaran`
4. `produk`
5. `transaksi`
6. `detail_transaksi`

Menghapus dibalik: `detail_transaksi`, `transaksi`, `produk`, `pelanggan`, `pembayaran`, `gerai`.

Di pgAdmin, untuk setiap kolom: klik kanan **Schemas → public → Tables → Create → Table**. Isi nama tabel, lalu di tab **Columns** tambah kolom. Di tab **Constraints** pasang primary key, foreign key, unique, dan check. Jangan centang **Identity**. Skema ini memakai `SERIAL` (sequence), bukan `GENERATED ALWAYS AS IDENTITY`.

`SERIAL` di PostgreSQL sama dengan `INTEGER NOT NULL` yang default-nya `nextval('nama_sequence')`. Sequence dibuat otomatis: `pelanggan_id_pelanggan_seq`, `produk_id_produk_seq`, `detail_transaksi_id_detail_seq`.

Primary key selalu `NOT NULL` dan unik, meskipun kata `NOT NULL` tidak ditulis di skrip.

Foreign key yang tidak menulis `ON DELETE` memakai bawaan PostgreSQL: **ON DELETE NO ACTION** dan **ON UPDATE NO ACTION**.

### 1. gerai

Tidak ada foreign key, unique tambahan, atau check.

| Kolom | Tipe | Null | Kunci / default |
| --- | --- | --- | --- |
| `kode_gerai` | `VARCHAR(10)` | NOT NULL | PRIMARY KEY. Bukan sequence: isi sendiri (`G001`, …) |
| `nama_gerai` | `VARCHAR(200)` | NOT NULL | — |
| `alamat_gerai` | `TEXT` | NOT NULL | — |

### 2. pelanggan

| Kolom | Tipe | Null | Kunci / default |
| --- | --- | --- | --- |
| `id_pelanggan` | `SERIAL` | NOT NULL | PRIMARY KEY. Diisi sequence `pelanggan_id_pelanggan_seq`, **bukan** `GENERATED ALWAYS AS IDENTITY` |
| `nama_pelanggan` | `VARCHAR(100)` | NOT NULL | UNIQUE (`pelanggan_nama_pelanggan_key` jika nama constraint dikosongkan) |
| `no_hp` | `VARCHAR(30)` | boleh NULL | — |
| `password_pelanggan` | `VARCHAR(100)` | NOT NULL | — |

### 3. pembayaran

Tidak ada foreign key, unique tambahan, atau check.

| Kolom | Tipe | Null | Kunci / default |
| --- | --- | --- | --- |
| `kode_pembayaran` | `VARCHAR(10)` | NOT NULL | PRIMARY KEY. Bukan sequence: isi sendiri (`CASH`, `QRIS`, …) |
| `metode_pembayaran` | `VARCHAR(50)` | NOT NULL | — |

### 4. produk

| Kolom | Tipe | Null | Kunci / default |
| --- | --- | --- | --- |
| `id_produk` | `SERIAL` | NOT NULL | PRIMARY KEY. Sequence `produk_id_produk_seq`, **bukan** identity |
| `tipe` | `VARCHAR(30)` | NOT NULL | CHECK `tipe IN ('Kopi', 'BukanKopi', 'MakananPenutup')` |
| `nama_produk` | `VARCHAR(150)` | NOT NULL | — |
| `harga_produk` | `NUMERIC(12, 2)` | NOT NULL | precision 12, scale 2 |
| `ukuran` | `VARCHAR(30)` | boleh NULL | — |
| `info_tambahan` | `VARCHAR(150)` | boleh NULL | jenis kopi, rasa, atau variasi |
| `catatan_produk` | `VARCHAR(255)` | boleh NULL | — |
| `jumlah_produk` | `INTEGER` | NOT NULL | DEFAULT `1` |

Di tab **Constraints → Check**, ekspresi persis:

```sql
tipe IN ('Kopi', 'BukanKopi', 'MakananPenutup')
```

Nama constraint tidak ditulis di `schema.sql`. PostgreSQL menamainya `produk_tipe_check` jika dikosongkan.

### 5. transaksi

`id_transaksi` tidak di-generate database. Aplikasi mengisinya, bentuknya `TRX` diikuti sisa milidetik.

| Kolom | Tipe | Null | Kunci / default |
| --- | --- | --- | --- |
| `id_transaksi` | `VARCHAR(20)` | NOT NULL | PRIMARY KEY. Bukan SERIAL dan bukan identity |
| `kode_gerai` | `VARCHAR(10)` | boleh NULL | FK → `gerai(kode_gerai)`, ON DELETE NO ACTION, ON UPDATE NO ACTION |
| `id_pelanggan` | `INTEGER` | boleh NULL | FK → `pelanggan(id_pelanggan)`, ON DELETE NO ACTION, ON UPDATE NO ACTION. Bukan SERIAL |
| `kode_pembayaran` | `VARCHAR(10)` | boleh NULL | FK → `pembayaran(kode_pembayaran)`, ON DELETE NO ACTION, ON UPDATE NO ACTION |
| `subtotal_transaksi` | `NUMERIC(12, 2)` | NOT NULL | DEFAULT `0` |
| `total_transaksi` | `NUMERIC(12, 2)` | NOT NULL | DEFAULT `0` |
| `uang_kembali` | `NUMERIC(12, 2)` | NOT NULL | DEFAULT `0` |
| `kode_promo` | `VARCHAR(20)` | boleh NULL | — |
| `total_diskon` | `NUMERIC(12, 2)` | NOT NULL | DEFAULT `0` |
| `tanggal_transaksi` | `DATE` | boleh NULL | — |

Tidak ada UNIQUE atau CHECK selain primary key.

### 6. detail_transaksi

Satu baris = satu item di struk. `tipe` di sini **tidak** punya CHECK (berbeda dari `produk.tipe`).

| Kolom | Tipe | Null | Kunci / default |
| --- | --- | --- | --- |
| `id_detail` | `SERIAL` | NOT NULL | PRIMARY KEY. Sequence `detail_transaksi_id_detail_seq`, **bukan** identity |
| `id_transaksi` | `VARCHAR(20)` | NOT NULL | FK → `transaksi(id_transaksi)`, **ON DELETE CASCADE**, ON UPDATE NO ACTION |
| `id_produk` | `INTEGER` | boleh NULL | FK → `produk(id_produk)`, **ON DELETE SET NULL**, ON UPDATE NO ACTION. Bukan SERIAL |
| `nama_produk` | `VARCHAR(150)` | NOT NULL | salinan nama saat transaksi |
| `tipe` | `VARCHAR(30)` | boleh NULL | tanpa CHECK |
| `harga_produk` | `NUMERIC(12, 2)` | NOT NULL | tanpa default |
| `jumlah_produk` | `INTEGER` | NOT NULL | tanpa default |
| `catatan_produk` | `VARCHAR(255)` | boleh NULL | — |
| `ukuran` | `VARCHAR(30)` | boleh NULL | — |
| `info_tambahan` | `VARCHAR(150)` | boleh NULL | — |

### Setelah tabel jadi

Jalankan bagian `INSERT` di `database/schema.sql` (gerai 5 baris, pelanggan 6, pembayaran 7, produk 17). Karena `id_pelanggan` dan `id_produk` diisi angka eksplisit, sequence harus digeser atau insert berikutnya bentrok:

```sql
SELECT setval('pelanggan_id_pelanggan_seq', (SELECT MAX(id_pelanggan) FROM pelanggan));
SELECT setval('produk_id_produk_seq', (SELECT MAX(id_produk) FROM produk));
```

`detail_transaksi` tidak punya seed, jadi sequencenya tetap mulai dari 1.

Akun seed: Vionanda/viona123, Riza/riza123, Tesalonika/tesa123, Miracle/bapakvioadmin, Teresa/Tere123, Mayranda/may1122.
