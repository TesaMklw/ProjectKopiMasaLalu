package com.pos;

import com.pos.gerai.Gerai;
import com.pos.pelanggan.Pelanggan;
import com.pos.pembayaran.Pembayaran;
import com.pos.produk.Kopi;
import com.pos.produk.MakananPenutup;
import com.pos.transaksi.Transaksi;

public class Main {
    public static void main(String[] args) {
        // Buat gerai
        Gerai gerai = new Gerai("Gerai Kopi Kita", "Jl. Tanjung Duren No. 10");

        // Buat pelanggan
        Pelanggan pelanggan = new Pelanggan("Vionanda");
        pelanggan.setNoHp(812345678);

        // Buat metode pembayaran
        Pembayaran pembayaran = new Pembayaran("Cash");

        // Buat transaksi
        Transaksi transaksi = new Transaksi(
                "TRX001",
                gerai,
                "30000",   // subtotal
                "35000",   // total
                0,         // uang kembali (akan dihitung)
                pelanggan,
                "2026-09-28",
                pembayaran
        );

        // Tambahkan produk
        Kopi kopi = new Kopi("Arabica", "Medium", "Kopi Hitam", 20000, "Tanpa gula", 1);
        MakananPenutup dessert = new MakananPenutup("Cheesecake", "Cheesecake Slice", 15000, "Extra cream", 1);

        transaksi.addProduk(kopi);
        transaksi.addProduk(dessert);

        // Hitung uang kembali (misalnya pelanggan bayar 50000)
        transaksi.calcUangKembali(50000);

        // Cetak struk sederhana
        System.out.println("=== STRUK TRANSAKSI ===");
        System.out.println("ID Transaksi : " + transaksi.getIdTransaksi());
        System.out.println("Gerai        : " + transaksi.getGerai().getGerai());
        System.out.println("Alamat Gerai : " + transaksi.getGerai().getAlamatGerai());
        System.out.println("Pelanggan    : " + transaksi.getPelanggan().getNamaPelanggan());
        System.out.println("Tanggal      : " + transaksi.getTanggalTransaksi());
        System.out.println("Metode Bayar : " + transaksi.getPembayaran().getMetodePembayaran());
        System.out.println("Produk       : ");
        transaksi.getListProduk().forEach(p -> {
            System.out.println(" - " + p.getNamaProduk() + " x" + p.getJumlahProduk() + " Rp" + p.getHargaProduk());
        });
        System.out.println("Subtotal     : " + transaksi.getSubtotalTransaksi());
        System.out.println("Total        : " + transaksi.getTotalTransaksi());
        System.out.println("Uang Kembali : " + transaksi.getUangKembali());
        System.out.println("========================");
    }
}
