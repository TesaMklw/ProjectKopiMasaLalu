/*
author : Vionanda Ginting

*/

package com.pos.pembayaran;

import java.util.Scanner;

public class Pembayaran {
    // atribut private (encapsulation)
    private String metodePembayaran;

    // static final itu satu scanner bisa dipakai bersama (objek class) tapi variabel nya bersifat kostanta
    private static final Scanner inputUser = new Scanner(System.in);

    // Constructor 1 (Parameterized Constructor)
    public Pembayaran(String metodePembayaran){
        this.metodePembayaran = metodePembayaran;
    }

    // Constructor 2 (Default/No-Arg Constructor)
    public Pembayaran(){
        System.out.print("Metode Pembayaran = ");
        this.metodePembayaran = inputUser.nextLine();
    }

    // setters untuk mangatur/mengubah nilai variabel private 
    // dan getters untuk mengambil/membaca nilai variabel private
    public void setMetodePembayaran(String metodePembayaran){
        this.metodePembayaran = metodePembayaran;
    }

    public String getMetodePembayaran(){
        return metodePembayaran;
    }

    // method toString() untuk mengembalikan nilai String metodePembayaran (Polymorphism - Method Overriding)
    @Override
    public String toString() {
        return metodePembayaran;
    }
}
