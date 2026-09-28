package com.pos.produk;

public class Kopi extends Minuman {
    private String jenisKopi;

    public Kopi(String jenisKopi, String ukuranMinuman, String namaProduk, float hargaProduk,
                String catatanProduk, int jumlahProduk) {
        super(ukuranMinuman, namaProduk, hargaProduk, catatanProduk, jumlahProduk);
        this.jenisKopi = jenisKopi;
    }

    public Kopi() {
        super();   // menanyakan semua data Produk + ukuran Minuman
        System.out.print("Jenis Kopi = ");
        this.jenisKopi = inputUser.nextLine();
    }

    public void setJenisKopi(String jenisKopi) { this.jenisKopi = jenisKopi; }
    public String getJenisKopi() { return jenisKopi; }
}