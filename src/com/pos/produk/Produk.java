/*
author : Tesalonika Miracle Makalew - 825250020
*/

package com.pos.produk;

import java.util.Scanner;

public abstract class Produk {// abstrak
    // Satu Scanner dipakai bersama oleh Produk dan semua subclass-nya
    public static Scanner INPUT_USER = new Scanner(System.in);
    //encapsualtion
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
        this.namaProduk = INPUT_USER.nextLine();
        this.hargaProduk = readHarga();
        System.out.print("Catatan = ");
        this.catatanProduk = INPUT_USER.nextLine();
        this.jumlahProduk = readJumlah();
    }

    // Input angka aman: ulangi sampai valid, tidak crash kalau user ketik huruf
    protected static float readHarga() {
        while (true) {
            System.out.print("Harga Produk = ");
            try {
                float h = Float.parseFloat(INPUT_USER.nextLine().trim());
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
                int j = Integer.parseInt(INPUT_USER.nextLine().trim());
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

    @Override//polymorphism
    public String toString() {
        return namaProduk + " - Rp" + hargaProduk;
    }

    // Abstrak: wajib di-override subclass (seperti hitungLuas() di Bentuk)
    public abstract void calcDiscount(float persen);// method abstrak
}