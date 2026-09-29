package com.pos;

import com.pos.gerai.Gerai;
import com.pos.interfaces.Cetak;
import com.pos.interfaces.Login;
import com.pos.loader.DataLoader;
import com.pos.pelanggan.Pelanggan;
import com.pos.pembayaran.Pembayaran;
import com.pos.produk.BukanKopi;
import com.pos.produk.Kopi;
import com.pos.produk.MakananPenutup;
import com.pos.produk.Produk;
import com.pos.transaksi.Transaksi;
import java.time.LocalDate;
import java.util.ArrayList; 
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner IN = new Scanner(System.in);

    public static void main(String[] args) {
        List<Gerai> daftarGerai;
        List<Pelanggan> daftarPelanggan;
        List<Pembayaran> daftarBayar;
        List<Produk> daftarProduk;
        try {
            daftarGerai = DataLoader.loadGerai("data/gerai.txt");
            daftarPelanggan = DataLoader.loadPelanggan("data/pelanggan.txt");
            daftarBayar = DataLoader.loadPembayaran("data/pembayaran.txt");
            daftarProduk = DataLoader.loadProduk("data/produk.txt");
        } catch (Exception e) {
            System.out.println("Gagal memuat data txt: " + e.getMessage());
            return;
        }
        System.out.println("Data dimuat: " + daftarProduk.size() + " produk, "
                + daftarGerai.size() + " gerai, "
                + daftarPelanggan.size() + " pelanggan, "
                + daftarBayar.size() + " metode bayar.");

        // 1. Login lewat interface Login
        Pelanggan.setDatabase(daftarPelanggan);
        Pelanggan.setScanner(IN);
        Login akun = new Pelanggan();
        Pelanggan pelanggan = menuAkun(akun);
        if (pelanggan == null) {
            System.out.println("Keluar dari program.");
            return;
        }

        // 2. Pilih gerai & pembayaran dari data txt
        Gerai gerai = pilihGerai(daftarGerai);
        Pembayaran bayar = pilihBayar(daftarBayar);

        // 3. Pilih produk satu per satu masuk keranjang
        List<Produk> keranjang = pilihProduk(daftarProduk);
        if (keranjang.isEmpty()) {
            System.out.println("Keranjang kosong. Keluar dari program.");
            return;
        }

        // 4. Bayar lalu cetak struk lewat interface Cetak
        Transaksi t = new Transaksi("TRX" + (System.currentTimeMillis() % 100000),
                gerai, 0, 0, 0, pelanggan, LocalDate.now().toString(), bayar);
        for (Produk p : keranjang) {
            t.addProduk(p);
        }
        t.calcSubtotal();
        System.out.println("Total belanja: Rp" + t.getTotalTransaksi());
        t.calcUangKembali(bacaUang(t.getTotalTransaksi()));

        Cetak cetakan = t;
        cetakan.struk();
        try {
            System.out.println("Struk tersimpan di: " + t.simpanStruk());
        } catch (java.io.IOException e) {
            System.out.println("Gagal menyimpan struk: " + e.getMessage());
        }
    }

    private static Pelanggan menuAkun(Login akun) {
        while (true) {
            System.out.println("=== SELAMAT DATANG DI KOPI KENANGAN ===");
            System.out.println("1. Signin");
            System.out.println("2. Signup");
            System.out.println("0. Keluar");
            System.out.print("Pilih = ");
            String pilih = IN.nextLine().trim();
            if (pilih.equals("1")) {
                akun.signin();
                Pelanggan p = (Pelanggan) akun;
                if (p.isLoggedIn()) {
                    return p;
                }
            } else if (pilih.equals("2")) {
                akun = new Pelanggan();
                akun.signup();
                Pelanggan p = (Pelanggan) akun;
                if (p.isLoggedIn()) {
                    try {
                        DataLoader.appendPelanggan("data/pelanggan.txt", p);
                    } catch (Exception e) {
                        System.out.println("Gagal menyimpan akun: " + e.getMessage());
                        return null;
                    }
                    return p;
                }
            } else if (pilih.equals("0")) {
                return null;
            } else {
                System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private static Gerai pilihGerai(List<Gerai> daftar) {
        System.out.println("=== PILIH GERAI ===");
        for (int i = 0; i < daftar.size(); i++) {
            System.out.println((i + 1) + ". " + daftar.get(i).getNamaGerai());
        }
        return daftar.get(bacaAngka("Pilih gerai", 1, daftar.size()) - 1);
    }

    private static Pembayaran pilihBayar(List<Pembayaran> daftar) {
        System.out.println("=== PILIH PEMBAYARAN ===");
        for (int i = 0; i < daftar.size(); i++) {
            System.out.println((i + 1) + ". " + daftar.get(i).getMetodePembayaran());
        }
        return daftar.get(bacaAngka("Pilih pembayaran", 1, daftar.size()) - 1);
    }

    private static List<Produk> pilihProduk(List<Produk> daftar) {
        List<Produk> keranjang = new ArrayList<>();
        System.out.println("=== DAFTAR PRODUK ===");
        for (int i = 0; i < daftar.size(); i++) {
            Produk p = daftar.get(i);
            System.out.println((i + 1) + ". " + p.getNamaProduk() + " - Rp" + p.getHargaProduk());
        }
        System.out.println("0 = selesai, -1 = hapus item keranjang");
        while (true) {
            int no = bacaAngka("Pilih produk", -1, daftar.size());
            if (no == 0) {
                return keranjang;
            }
            if (no == -1) {
                hapusItem(keranjang);
                continue;
            }
            int jumlah = bacaAngka("Jumlah", 1, 20);
            System.out.print("Catatan (Enter = pakai bawaan) = ");
            String catatan = IN.nextLine().trim();
            Produk p = copyProduk(daftar.get(no - 1), jumlah);
            if (!catatan.isEmpty()) {
                p.setCatatanProduk(catatan);
            }
            keranjang.add(p);
            System.out.println("Ditambah: " + p.getNamaProduk() + " x" + jumlah);
            tampilKeranjang(keranjang);
        }
    }

    private static void hapusItem(List<Produk> keranjang) {
        if (keranjang.isEmpty()) {
            System.out.println("Keranjang masih kosong.");
            return;
        }
        tampilKeranjang(keranjang);
        int no = bacaAngka("Hapus nomor", 1, keranjang.size());
        Produk buang = keranjang.remove(no - 1);
        System.out.println("Dihapus: " + buang.getNamaProduk());
        tampilKeranjang(keranjang);
    }

    private static void tampilKeranjang(List<Produk> keranjang) {
        System.out.println("--- Keranjang ---");
        float sum = 0;
        for (int i = 0; i < keranjang.size(); i++) {
            Produk p = keranjang.get(i);
            float sub = p.getHargaProduk() * p.getJumlahProduk();
            sum += sub;
            System.out.println((i + 1) + ". " + p.getNamaProduk()
                    + " x" + p.getJumlahProduk() + " = Rp" + sub
                    + labelCatatan(p));
        }
        System.out.println("Sementara total: Rp" + sum);
    }

    private static String labelCatatan(Produk p) {
        if (p.getCatatanProduk() == null || p.getCatatanProduk().trim().isEmpty()) {
            return "";
        }
        return " [" + p.getCatatanProduk().trim() + "]";
    }

    private static float bacaUang(float total) {
        while (true) {
            System.out.print("Uang dibayar = ");
            try {
                float uang = Float.parseFloat(IN.nextLine().trim());
                if (uang < total) {
                    System.out.println("Uang kurang Rp" + (total - uang) + ". Coba lagi.");
                    continue;
                }
                return uang;
            } catch (NumberFormatException e) {
                System.out.println("Harus angka. Coba lagi.");
            }
        }
    }

    private static int bacaAngka(String label, int min, int maks) {
        while (true) {
            System.out.print(label + " (" + min + ".." + maks + ") = ");
            try {
                int n = Integer.parseInt(IN.nextLine().trim());
                if (n < min || n > maks) {
                    System.out.println("Di luar rentang. Coba lagi.");
                    continue;
                }
                return n;
            } catch (NumberFormatException e) {
                System.out.println("Harus angka bulat. Coba lagi.");
            }
        }
    }

    // Salin produk dari daftar supaya jumlah tiap transaksi tidak saling timpa
    private static Produk copyProduk(Produk p, int jumlah) {
        if (p instanceof Kopi) {
            Kopi k = (Kopi) p;
            return new Kopi(k.getJenisKopi(), k.getUkuranMinuman(),
                    k.getNamaProduk(), k.getHargaProduk(),
                    k.getCatatanProduk(), jumlah);
        }
        if (p instanceof BukanKopi) {
            BukanKopi b = (BukanKopi) p;
            return new BukanKopi(b.getJenisRasa(), b.getUkuranMinuman(),
                    b.getNamaProduk(), b.getHargaProduk(),
                    b.getCatatanProduk(), jumlah);
        }
        MakananPenutup m = (MakananPenutup) p;
        return new MakananPenutup(m.getVariasiMakananPenutup(),
                m.getNamaProduk(), m.getHargaProduk(),
                m.getCatatanProduk(), jumlah);
    }
}
