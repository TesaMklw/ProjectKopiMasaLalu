/*
author : Tesalonika Miracle Makalew - 825250020
*/

package main.java.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Kopi extends Minuman {// hierarki, inheritace
    private final StringProperty jenisKopi = new SimpleStringProperty();//edcapsulation

    public Kopi(String jenisKopi, String namaProduk, float hargaProduk) {
        super(namaProduk, hargaProduk);
        this.jenisKopi.set(jenisKopi);
    }

    public void setJenisKopi(String jenisKopi) { this.jenisKopi.set(jenisKopi); }
    public String getJenisKopi() { return jenisKopi.get(); }
    public StringProperty jenisKopiProperty() { return jenisKopi; }

    @Override
    public String getTipeProduk() { return "Kopi"; }

    @Override
    public String getInfoTambahan() { return getJenisKopi(); }
}
