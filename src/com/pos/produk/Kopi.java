/*
author : Tesalonika Miracle Makalew - 825250020
*/

package com.pos.produk;

public class Kopi extends Minuman {// hierarki, inheritace
    private String jenisKopi;//edcapsulation

    public Kopi(String jenisKopi, String ukuranMinuman, String namaProduk, float hargaProduk,
                String catatanProduk, int jumlahProduk) {
        super(ukuranMinuman, namaProduk, hargaProduk, catatanProduk, jumlahProduk);
        this.jenisKopi = jenisKopi;
    }

    public Kopi() {
        super();   // menanyakan semua data Produk + ukuran Minuman
        System.out.print("Jenis Kopi = ");
        this.jenisKopi = INPUT_USER.nextLine();
    }

    public void setJenisKopi(String jenisKopi) { this.jenisKopi = jenisKopi; }
    public String getJenisKopi() { return jenisKopi; }
}