-- Kopi Kenangan POS
-- Pulihkan di pgAdmin:
--   1. Buat database bernama kopi_kenangan (UTF8).
--   2. Buka Query Tool pada database itu.
--   3. Jalankan seluruh skrip ini.
--
-- Seed di bawah ini adalah isi lengkap data gerai, pelanggan, pembayaran,
-- dan produk. Aplikasi tidak lagi membaca berkas teks; pulihkan lewat pgAdmin.

DROP TABLE IF EXISTS detail_transaksi;
DROP TABLE IF EXISTS transaksi;
DROP TABLE IF EXISTS produk;
DROP TABLE IF EXISTS pelanggan;
DROP TABLE IF EXISTS pembayaran;
DROP TABLE IF EXISTS gerai;

CREATE TABLE gerai (
    kode_gerai   VARCHAR(10) PRIMARY KEY,
    nama_gerai   VARCHAR(200) NOT NULL,
    alamat_gerai TEXT NOT NULL
);

CREATE TABLE pelanggan (
    id_pelanggan       SERIAL PRIMARY KEY,
    nama_pelanggan     VARCHAR(100) NOT NULL UNIQUE,
    no_hp              VARCHAR(30),
    password_pelanggan VARCHAR(100) NOT NULL
);

CREATE TABLE pembayaran (
    kode_pembayaran    VARCHAR(10) PRIMARY KEY,
    metode_pembayaran  VARCHAR(50) NOT NULL
);

CREATE TABLE produk (
    id_produk      SERIAL PRIMARY KEY,
    tipe           VARCHAR(30) NOT NULL CHECK (tipe IN ('Kopi', 'BukanKopi', 'MakananPenutup')),
    nama_produk    VARCHAR(150) NOT NULL,
    harga_produk   NUMERIC(12, 2) NOT NULL,
    ukuran         VARCHAR(30),
    info_tambahan  VARCHAR(150),
    catatan_produk VARCHAR(255),
    jumlah_produk  INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE transaksi (
    id_transaksi        VARCHAR(20) PRIMARY KEY,
    kode_gerai          VARCHAR(10) REFERENCES gerai (kode_gerai),
    id_pelanggan        INTEGER REFERENCES pelanggan (id_pelanggan),
    kode_pembayaran     VARCHAR(10) REFERENCES pembayaran (kode_pembayaran),
    subtotal_transaksi  NUMERIC(12, 2) NOT NULL DEFAULT 0,
    total_transaksi     NUMERIC(12, 2) NOT NULL DEFAULT 0,
    uang_kembali        NUMERIC(12, 2) NOT NULL DEFAULT 0,
    kode_promo          VARCHAR(20),
    total_diskon        NUMERIC(12, 2) NOT NULL DEFAULT 0,
    tanggal_transaksi   DATE
);

CREATE TABLE detail_transaksi (
    id_detail      SERIAL PRIMARY KEY,
    id_transaksi   VARCHAR(20) NOT NULL REFERENCES transaksi (id_transaksi) ON DELETE CASCADE,
    id_produk      INTEGER REFERENCES produk (id_produk) ON DELETE SET NULL,
    nama_produk    VARCHAR(150) NOT NULL,
    tipe           VARCHAR(30),
    harga_produk   NUMERIC(12, 2) NOT NULL,
    jumlah_produk  INTEGER NOT NULL,
    catatan_produk VARCHAR(255),
    ukuran         VARCHAR(30),
    info_tambahan  VARCHAR(150)
);

INSERT INTO gerai (kode_gerai, nama_gerai, alamat_gerai) VALUES
('G001', 'Kopi Masa Lalu Tanjung Duren', 'Jl. Tanjung Duren Raya No. 359, RT.5/RW.3, Tanjung Duren Utara, Kec. Grogol Petamburan, Jakarta Barat'),
('G002', 'Kopi Masa Lalu RS Jantung Harapan Kita (Palmerah)', 'Jl. Letjen S. Parman Kav. 87, Palmerah, Jakarta Barat (dekat pintu masuk utama RS Harapan Kita)'),
('G003', 'Season City Mall (Tambora)', 'Season City Mall, Lantai GF, Jl. Prof. Dr. Latumeten No.33, Jembatan Besi, Tambora, Jakarta Barat 11320'),
('G004', 'Mall Taman Anggrek', 'Mall Taman Anggrek, Lantai LG, Jl. Letjen S. Parman Kav. 21, Tanjung Duren Selatan, Jakarta Barat'),
('G005', 'Central Park Mall', 'Central Park Mall, Lantai LG, Jl. Letjen S. Parman No.28, Tanjung Duren Selatan, Jakarta Barat');

INSERT INTO pelanggan (id_pelanggan, nama_pelanggan, no_hp, password_pelanggan) VALUES
(1, 'Vionanda', '082272293292', 'viona123'),
(2, 'Riza', '085266066497', 'riza123'),
(3, 'Tesalonika', '085216641663', 'tesa123'),
(4, 'Miracle', '082164555543', 'bapakvioadmin'),
(5, 'Teresa', '86474530', 'Tere123'),
(6, 'Mayranda', '1234673', 'may1122');

INSERT INTO pembayaran (kode_pembayaran, metode_pembayaran) VALUES
('CASH', 'Cash'),
('QRIS', 'QRIS'),
('GPY', 'GoPay'),
('OVO', 'OVO'),
('DANA', 'DANA'),
('BCA', 'Debit BCA'),
('BRI', 'Debit BRI');

INSERT INTO produk (id_produk, tipe, nama_produk, harga_produk, ukuran, info_tambahan, catatan_produk, jumlah_produk) VALUES
(1,  'Kopi',           'Kopi Kenangan Mantan', 18000, 'Regular', 'Arabika + Gula Aren',            'Less Sugar',  1),
(2,  'Kopi',           'Kopi Kenangan Mantan', 22000, 'Large',   'Arabika + Gula Aren',            'Less Sugar',  1),
(3,  'Kopi',           'Kopi Hitam',           20000, 'Medium',  'Arabika',                        'Tanpa gula',  1),
(4,  'Kopi',           'Cappuccino',           23000, 'Regular', 'Arabika + Susu',                 'Extra foam',  1),
(5,  'Kopi',           'Mochaccino',           25000, 'Large',   'Arabika + Coklat',               'Less ice',    1),
(6,  'Kopi',           'Macchiato',            24000, 'Regular', 'Arabika + Susu Foam',            'Less sugar',  1),
(7,  'Kopi',           'Creamy Caramel Latte', 26000, 'Large',   'Arabika + Caramel + Susu',       'Less ice',    1),
(8,  'Kopi',           'Caramel Macchiato',    25000, 'Regular', 'Arabika + Caramel + Susu Foam',  'Less sugar',  1),
(9,  'Kopi',           'Caramel Macchiato',    28000, 'Large',   'Arabika + Caramel + Susu Foam',  'Less sugar',  1),
(10, 'Kopi',           'Caramel Latte',        27000, 'Large',   'Arabika + Caramel + Susu',       'Less sugar',  1),
(11, 'BukanKopi',      'Matcha Latte',         22000, 'Regular', 'Matcha',                         'Less sugar',  1),
(12, 'BukanKopi',      'Chocolate',            22000, 'Regular', 'Coklat',                         'Extra coklat', 1),
(13, 'BukanKopi',      'Susu Boba Gula Aren',  19000, 'Regular', 'Brown Sugar',                    'Es sedikit',  1),
(14, 'BukanKopi',      'Teh Leci',             18000, 'Regular', 'Leci',                           'Less ice',    1),
(15, 'MakananPenutup', 'Cheesecake Slice',     15000, '-',       'Cheesecake',                     'Extra cream', 1),
(16, 'MakananPenutup', 'Roti Abon Ayam',       13000, '-',       'Abon Ayam',                      'Hangat',      1),
(17, 'MakananPenutup', 'Croissant Coklat',     17000, '-',       'Coklat',                         'Hangat',      1);

SELECT setval('pelanggan_id_pelanggan_seq', (SELECT MAX(id_pelanggan) FROM pelanggan));
SELECT setval('produk_id_produk_seq', (SELECT MAX(id_produk) FROM produk));
