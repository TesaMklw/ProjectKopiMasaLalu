/*
author : Vionanda Ginting - 825250018
*/

package main.java.model;

import java.util.Scanner;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Pembayaran {
    // atribut private (encapsulation), dibungkus JavaFX Property
    private final StringProperty kodePembayaran = new SimpleStringProperty();
    private final StringProperty metodePembayaran = new SimpleStringProperty();

    // static final itu satu scanner bisa dipakai bersama (objek class) tapi variabel nya bersifat kostanta
    private static final Scanner INPUT_USER = new Scanner(System.in);

    // Constructor 1 (Parameterized Constructor)
    public Pembayaran(String metodePembayaran){
        this.metodePembayaran.set(metodePembayaran);
    }

    // Constructor 2 (Default/No-Arg Constructor)
    public Pembayaran(){
        System.out.print("Metode Pembayaran = ");
        this.metodePembayaran.set(INPUT_USER.nextLine());
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
