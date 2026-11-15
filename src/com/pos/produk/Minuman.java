/*
author : Tesalonika Miracle Makalew - 825250020
*/

package com.pos.produk;

public class Minuman extends Produk {// inheritance
    private String ukuranMinuman;// escapsulation

    public Minuman(String ukuranMinuman, String namaProduk, float hargaProduk,
                    String catatanProduk, int jumlahProduk) {
        super(namaProduk, hargaProduk, catatanProduk, jumlahProduk);
        this.ukuranMinuman = ukuranMinuman;
    }

    public Minuman() {
        super();   // menanyakan nama, harga, catatan, jumlah
        System.out.print("Ukuran Minuman = ");
        this.ukuranMinuman = INPUT_USER.nextLine();
    }

    public void setUkuranMinuman(String ukuranMinuman) { this.ukuranMinuman = ukuranMinuman; }
    public String getUkuranMinuman() { return ukuranMinuman; }

    // Minuman: diskon sesuai persen yang diberikan
    @Override// polymorphism
    public void calcDiscount(float persen) {
        if (persen < 0) persen = 0;
        if (persen > 100) persen = 100;
        float potongan = getHargaProduk() * persen / 100;
        setHargaProduk(getHargaProduk() - potongan);
    }
}