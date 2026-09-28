package com.pos.produk;

import java.util.Scanner;

public abstract class Produk {
    // Satu Scanner dipakai bersama oleh Produk dan semua subclass-nya
    public static Scanner inputUser = new Scanner(System.in);

    private String namaProduk;
    private float hargaProduk;
    private String catatanProduk;
    private int jumlahProduk;

    // Constructor 1: nilai dikirim lewat parameter
    public Produk(String namaProduk, float hargaProduk, String catatanProduk, int jumlahProduk) {
        this.namaProduk = namaProduk;
        this.hargaProduk = hargaProduk;
        this.catatanProduk = catatanProduk;
        this.jumlahProduk = jumlahProduk;
    }

    // Constructor 2: nilai ditanyakan langsung ke user
    public Produk() {
        System.out.print("Nama Produk = ");
        this.namaProduk = inputUser.nextLine();
        this.hargaProduk = readHarga();
        System.out.print("Catatan = ");
        this.catatanProduk = inputUser.nextLine();
        this.jumlahProduk = readJumlah();
    }

    // Input angka aman: ulangi sampai valid, tidak crash kalau user ketik huruf
    protected static float readHarga() {
        while (true) {
            System.out.print("Harga Produk = ");
            try {
                float h = Float.parseFloat(inputUser.nextLine().trim());
                if (h < 0) {
                    System.out.println("Harga tidak boleh negatif. Coba lagi.");
                    continue;
                }
                return h;
            } catch (NumberFormatException e) {
                System.out.println("Harga harus angka. Coba lagi.");
            }
        }
    }

    protected static int readJumlah() {
        while (true) {
            System.out.print("Jumlah = ");
            try {
                int j = Integer.parseInt(inputUser.nextLine().trim());
                if (j <= 0) {
                    System.out.println("Jumlah harus > 0. Coba lagi.");
                    continue;
                }
                return j;
            } catch (NumberFormatException e) {
                System.out.println("Jumlah harus angka bulat. Coba lagi.");
            }
        }
    }

    public void setNamaProduk(String namaProduk) { this.namaProduk = namaProduk; }
    public void setHargaProduk(float hargaProduk) {
        if (hargaProduk < 0) return;
        this.hargaProduk = hargaProduk;
    }
    public void setCatatanProduk(String catatanProduk) { this.catatanProduk = catatanProduk; }
    public void setJumlahProduk(int jumlahProduk) {
        if (jumlahProduk <= 0) return;
        this.jumlahProduk = jumlahProduk;
    }

    public String getNamaProduk() { return namaProduk; }
    public float getHargaProduk() { return hargaProduk; }
    public String getCatatanProduk() { return catatanProduk; }
    public int getJumlahProduk() { return jumlahProduk; }

    @Override
    public String toString() {
        return namaProduk + " - Rp" + hargaProduk;
    }

    // Abstrak: wajib di-override subclass (seperti hitungLuas() di Bentuk)
    public abstract void calcDiscount(float persen);
}