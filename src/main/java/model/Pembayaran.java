/*
author : Vionanda Ginting - 825250018
*/

package main.java.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Pembayaran {
    // atribut private (encapsulation), dibungkus JavaFX Property
    private final StringProperty kodePembayaran = new SimpleStringProperty();
    private final StringProperty metodePembayaran = new SimpleStringProperty();

    // Constructor (Parameterized Constructor)
    public Pembayaran(String metodePembayaran){
        this.metodePembayaran.set(metodePembayaran);
    }

    public void setKodePembayaran(String kodePembayaran) {
        this.kodePembayaran.set(kodePembayaran);
    }

    public String getKodePembayaran() {
        return kodePembayaran.get();
    }

    public StringProperty kodePembayaranProperty() {
        return kodePembayaran;
    }

    // setters untuk mangatur/mengubah nilai variabel private
    // dan getters untuk mengambil/membaca nilai variabel private
    public void setMetodePembayaran(String metodePembayaran){
        this.metodePembayaran.set(metodePembayaran);
    }

    public String getMetodePembayaran(){
        return metodePembayaran.get();
    }

    public StringProperty metodePembayaranProperty() {
        return metodePembayaran;
    }

    // method toString() untuk mengembalikan nilai String metodePembayaran (Polymorphism - Method Overriding)
    @Override
    public String toString() {
        return metodePembayaran.get();
    }
}
