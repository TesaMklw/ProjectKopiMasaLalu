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
        System.out.print("Harga Produk = ");
        this.hargaProduk = Float.parseFloat(inputUser.nextLine());
        System.out.print("Catatan = ");
        this.catatanProduk = inputUser.nextLine();
        System.out.print("Jumlah = ");
        this.jumlahProduk = Integer.parseInt(inputUser.nextLine());
    }

    public void setNamaProduk(String namaProduk) { this.namaProduk = namaProduk; }
    public void setHargaProduk(float hargaProduk) { this.hargaProduk = hargaProduk; }
    public void setCatatanProduk(String catatanProduk) { this.catatanProduk = catatanProduk; }
    public void setJumlahProduk(int jumlahProduk) { this.jumlahProduk = jumlahProduk; }

    public String getNamaProduk() { return namaProduk; }
    public float getHargaProduk() { return hargaProduk; }
    public String getCatatanProduk() { return catatanProduk; }
    public int getJumlahProduk() { return jumlahProduk; }

    // Abstrak: wajib di-override subclass (seperti hitungLuas() di Bentuk)
    public abstract void calcDiscount(float persen);
}