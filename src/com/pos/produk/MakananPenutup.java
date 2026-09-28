package com.pos.produk;

public class MakananPenutup extends Produk {
    private String variasiMakananPenutup;

    public MakananPenutup(String variasiMakananPenutup, String namaProduk, float hargaProduk,
                            String catatanProduk, int jumlahProduk) {
        super(namaProduk, hargaProduk, catatanProduk, jumlahProduk);
        this.variasiMakananPenutup = variasiMakananPenutup;
    }

    public MakananPenutup() {
        super();   // menanyakan nama, harga, catatan, jumlah
        System.out.print("Variasi Makanan Penutup = ");
        this.variasiMakananPenutup = inputUser.nextLine();
    }

    public void setVariasiMakananPenutup(String variasiMakananPenutup) {
        this.variasiMakananPenutup = variasiMakananPenutup;
    }

    public String getVariasiMakananPenutup() {
        return variasiMakananPenutup;
    }

    // Makanan penutup: diskon dibatasi maksimal 20% (contoh aturan, silakan ubah)
    @Override
    public void calcDiscount(float persen) {
        if (persen < 0) persen = 0;
        if (persen > 20) persen = 20;
        float potongan = getHargaProduk() * persen / 100;
        setHargaProduk(getHargaProduk() - potongan);
    }
}