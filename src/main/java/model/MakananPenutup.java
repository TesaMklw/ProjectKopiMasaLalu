/*
author : Tesalonika Miracle Makalew - 825250020
*/

package main.java.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MakananPenutup extends Produk {
    private final StringProperty variasiMakananPenutup = new SimpleStringProperty();
    // constructor overloading
    public MakananPenutup(String variasiMakananPenutup, String namaProduk, float hargaProduk,
                            int jumlahProduk) {
        super(namaProduk, hargaProduk, jumlahProduk);
        this.variasiMakananPenutup.set(variasiMakananPenutup);
    }

    public MakananPenutup() {// constructor overloading
        super();   // menanyakan nama, harga, jumlah
        System.out.print("Variasi Makanan Penutup = ");
        this.variasiMakananPenutup.set(INPUT_USER.nextLine());
    }

    public void setVariasiMakananPenutup(String variasiMakananPenutup) {
        this.variasiMakananPenutup.set(variasiMakananPenutup);
    }

    public String getVariasiMakananPenutup() {
        return variasiMakananPenutup.get();
    }

    public StringProperty variasiMakananPenutupProperty() {
        return variasiMakananPenutup;
    }

    @Override
    public String getTipeProduk() { return "MakananPenutup"; }

    @Override
    public String getInfoTambahan() { return getVariasiMakananPenutup(); }

    // Makanan penutup: diskon dibatasi maksimal 20%
    @Override
    public void calcDiscount(float persen) {
        if (persen < 0) persen = 0;
        if (persen > 20) persen = 20;
        float potongan = getHargaProduk() * persen / 100;
        setHargaProduk(getHargaProduk() - potongan);
    }
}
