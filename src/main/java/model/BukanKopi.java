/*
author : Tesalonika Miracle Makalew - 825250020
*/

package main.java.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BukanKopi extends Minuman {// struktur hierarki, inheritence
    private final StringProperty jenisRasa = new SimpleStringProperty();// encapsulation

    public BukanKopi(String jenisRasa, String ukuranMinuman, String namaProduk, float hargaProduk,
                    int jumlahProduk) {
        super(ukuranMinuman, namaProduk, hargaProduk, jumlahProduk);
        this.jenisRasa.set(jenisRasa);
    }

    public BukanKopi() {
        super();   // menanyakan semua data Produk + ukuran Minuman
        System.out.print("Jenis Rasa = ");
        this.jenisRasa.set(INPUT_USER.nextLine());
    }

    public void setJenisRasa(String jenisRasa) { this.jenisRasa.set(jenisRasa); }
    public String getJenisRasa() { return jenisRasa.get(); }
    public StringProperty jenisRasaProperty() { return jenisRasa; }

    @Override
    public String getTipeProduk() { return "BukanKopi"; }

    @Override
    public String getInfoTambahan() { return getJenisRasa(); }
}
