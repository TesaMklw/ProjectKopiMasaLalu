package com.pos.pembayaran;

import java.util.Scanner;

public class Pembayaran {
    private String metodePembayaran;

    private static final Scanner inputUser = new Scanner(System.in);

    public Pembayaran(String metodePembayaran){
        this.metodePembayaran = metodePembayaran;
    }

    public Pembayaran(){
        System.out.print("Metode Pembayaran = ");
        this.metodePembayaran = inputUser.nextLine();
    }

    // setters dan getters
    public void setMetodePembayaran(String metodePembayaran){
        this.metodePembayaran = metodePembayaran;
    }

    public String getMetodePembayaran(){
        return metodePembayaran;
    }

    @Override
    public String toString() {
        return metodePembayaran;
    }
}
