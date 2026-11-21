/*
author : Tesalonika Miracle Makalew - 825250020
*/

package main.java.model;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class Produk {// abstrak
    //encapsualtion — field JavaFX Property agar kolom TableView bisa bind
    private final IntegerProperty idProduk = new SimpleIntegerProperty();
    private final StringProperty namaProduk = new SimpleStringProperty();
    private final FloatProperty hargaProduk = new SimpleFloatProperty();
    // jumlah dan catatan pesanan sekarang ada di DetailTransaksi, bukan di katalog Produk

    // Constructor 1: nilai dikirim lewat parameter
    public Produk(String namaProduk, float hargaProduk) {
        this.namaProduk.set(namaProduk);
        this.hargaProduk.set(hargaProduk);
    }

    public void setIdProduk(int idProduk) { this.idProduk.set(idProduk); }
    public int getIdProduk() { return idProduk.get(); }
    public IntegerProperty idProdukProperty() { return idProduk; }

    public void setNamaProduk(String namaProduk) { this.namaProduk.set(namaProduk); }
    public void setHargaProduk(float hargaProduk) {
        if (hargaProduk < 0) return;
        this.hargaProduk.set(hargaProduk);
    }

    public String getNamaProduk() { return namaProduk.get(); }
    public float getHargaProduk() { return hargaProduk.get(); }

    public StringProperty namaProdukProperty() { return namaProduk; }
    public FloatProperty hargaProdukProperty() { return hargaProduk; }

    // Dipakai tabel dan DAO. Subclass menimpa sesuai jenisnya.
    public String getTipeProduk() { return "Produk"; }
    public String getInfoTambahan() { return ""; }
    public String getUkuranTampil() { return "-"; }

    @Override//polymorphism
    public String toString() {
        return getNamaProduk() + " - Rp" + getHargaProduk();
    }

    // Abstrak: wajib di-override subclass (seperti hitungLuas() di Bentuk)
    public abstract void calcDiscount(float persen);// method abstrak
}
