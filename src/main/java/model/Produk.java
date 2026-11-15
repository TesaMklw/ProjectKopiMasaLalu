/*
author : Tesalonika Miracle Makalew - 825250020
*/

package main.java.model;

import java.util.Scanner;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class Produk {// abstrak
    // Satu Scanner dipakai bersama oleh Produk dan semua subclass-nya
    public static Scanner INPUT_USER = new Scanner(System.in);
    //encapsualtion — field JavaFX Property agar kolom TableView bisa bind
    private final IntegerProperty idProduk = new SimpleIntegerProperty();
    private final StringProperty namaProduk = new SimpleStringProperty();
    private final FloatProperty hargaProduk = new SimpleFloatProperty();
    private final StringProperty catatanProduk = new SimpleStringProperty();
    private final IntegerProperty jumlahProduk = new SimpleIntegerProperty();

    // Constructor 1: nilai dikirim lewat parameter
    public Produk(String namaProduk, float hargaProduk, String catatanProduk, int jumlahProduk) {
        this.namaProduk.set(namaProduk);
        this.hargaProduk.set(hargaProduk);
        this.catatanProduk.set(catatanProduk);
        this.jumlahProduk.set(jumlahProduk);
    }

    // Constructor 2: nilai ditanyakan langsung ke user
    public Produk() {
        System.out.print("Nama Produk = ");
        this.namaProduk.set(INPUT_USER.nextLine());
        this.hargaProduk.set(readHarga());
        System.out.print("Catatan = ");
        this.catatanProduk.set(INPUT_USER.nextLine());
        this.jumlahProduk.set(readJumlah());
    }

    // Input angka aman: ulangi sampai valid, tidak crash kalau user ketik huruf
    protected static float readHarga() {
        while (true) {
            System.out.print("Harga Produk = ");
            try {
                float h = Float.parseFloat(INPUT_USER.nextLine().trim());
                if (h < 0) {
                    System.out.println("Harga tidak boleh negatif. Coba lagi.");
                    continue;
                }
                return h;
            } catch (NumberFormatException e) {
                System.out.println("Harga harus angka. Coba lagi.");
            }
        }
    }

    protected static int readJumlah() {
        while (true) {
            System.out.print("Jumlah = ");
            try {
                int j = Integer.parseInt(INPUT_USER.nextLine().trim());
                if (j <= 0) {
                    System.out.println("Jumlah harus > 0. Coba lagi.");
                    continue;
                }
                return j;
            } catch (NumberFormatException e) {
                System.out.println("Jumlah harus angka bulat. Coba lagi.");
            }
        }
    }

    public void setIdProduk(int idProduk) { this.idProduk.set(idProduk); }
    public int getIdProduk() { return idProduk.get(); }
    public IntegerProperty idProdukProperty() { return idProduk; }

    public void setNamaProduk(String namaProduk) { this.namaProduk.set(namaProduk); }
    public void setHargaProduk(float hargaProduk) {
        if (hargaProduk < 0) return;
        this.hargaProduk.set(hargaProduk);
    }
    public void setCatatanProduk(String catatanProduk) { this.catatanProduk.set(catatanProduk); }
    public void setJumlahProduk(int jumlahProduk) {
        if (jumlahProduk <= 0) return;
        this.jumlahProduk.set(jumlahProduk);
    }

    public String getNamaProduk() { return namaProduk.get(); }
    public float getHargaProduk() { return hargaProduk.get(); }
    public String getCatatanProduk() { return catatanProduk.get(); }
    public int getJumlahProduk() { return jumlahProduk.get(); }

    public StringProperty namaProdukProperty() { return namaProduk; }
    public FloatProperty hargaProdukProperty() { return hargaProduk; }
    public StringProperty catatanProdukProperty() { return catatanProduk; }
    public IntegerProperty jumlahProdukProperty() { return jumlahProduk; }

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
