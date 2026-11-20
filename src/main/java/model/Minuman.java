/*
author : Tesalonika Miracle Makalew - 825250020
*/

package main.java.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Minuman extends Produk {// inheritance
    private final StringProperty ukuranMinuman = new SimpleStringProperty();// escapsulation

    public Minuman(String ukuranMinuman, String namaProduk, float hargaProduk, int jumlahProduk) {
        super(namaProduk, hargaProduk, jumlahProduk);
        this.ukuranMinuman.set(ukuranMinuman);
    }

    public void setUkuranMinuman(String ukuranMinuman) { this.ukuranMinuman.set(ukuranMinuman); }
    public String getUkuranMinuman() { return ukuranMinuman.get(); }
    public StringProperty ukuranMinumanProperty() { return ukuranMinuman; }

    @Override
    public String getTipeProduk() { return "Minuman"; }

    @Override
    public String getUkuranTampil() {
        return getUkuranMinuman() == null ? "-" : getUkuranMinuman();
    }

    // Minuman: diskon sesuai persen yang diberikan
    @Override// polymorphism
    public void calcDiscount(float persen) {
        if (persen < 0) persen = 0;
        if (persen > 100) persen = 100;
        float potongan = getHargaProduk() * persen / 100;
        setHargaProduk(getHargaProduk() - potongan);
    }
}
