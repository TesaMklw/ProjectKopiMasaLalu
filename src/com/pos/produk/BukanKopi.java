/*
author : Tesalonika Miracle Makalew - 825250020
*/

package com.pos.produk;

public class BukanKopi extends Minuman {// struktur hierarki, inheritence
    private String jenisRasa;// encapsulation

    public BukanKopi(String jenisRasa, String ukuranMinuman, String namaProduk, float hargaProduk,
                    String catatanProduk, int jumlahProduk) {
        super(ukuranMinuman, namaProduk, hargaProduk, catatanProduk, jumlahProduk);
        this.jenisRasa = jenisRasa;
    }

    public BukanKopi() {
        super();   // menanyakan semua data Produk + ukuran Minuman
        System.out.print("Jenis Rasa = ");
        this.jenisRasa = INPUT_USER.nextLine();
    }

    public void setJenisRasa(String jenisRasa) { this.jenisRasa = jenisRasa; }
    public String getJenisRasa() { return jenisRasa; }
}