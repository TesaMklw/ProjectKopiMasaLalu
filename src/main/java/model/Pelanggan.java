/*
author : Riza Rosmeri - 825250153
*/

package main.java.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import main.java.controller.Login;

public class Pelanggan implements Login {// hierarki
    private static Scanner IN = new Scanner(System.in);

    // Dipanggil Main supaya semua input lewat satu Scanner yang sama
    public static void setScanner(Scanner in) {
        if (in != null) {
            IN = in;
        }
    }

    // Daftar akun di memori untuk signup/signin (interface Login).
    // Akun yang dipakai kasir disimpan di PostgreSQL lewat PelangganDAO.
    private static List<Pelanggan> database = new ArrayList<>();

    public static void setDatabase(List<Pelanggan> data) {
        database = (data != null) ? data : new ArrayList<>();
    }

    private final IntegerProperty idPelanggan = new SimpleIntegerProperty();
    private final StringProperty namaPelanggan = new SimpleStringProperty();
    private final StringProperty passwordPelanggan = new SimpleStringProperty();
    private final StringProperty noHp = new SimpleStringProperty();

        //constructor 1
        public Pelanggan(String namaPelanggan) {
            this.namaPelanggan.set(namaPelanggan);
        }

        //constructor 2: default biar tidak null (password kosong = belum login)
        public Pelanggan() {
            this("Tanpa Nama");
            this.noHp.set("-");
            this.passwordPelanggan.set("");
        }

        public void setIdPelanggan(int idPelanggan) {
            this.idPelanggan.set(idPelanggan);
        }
        public int getIdPelanggan() {
            return idPelanggan.get();
        }
        public IntegerProperty idPelangganProperty() {
            return idPelanggan;
        }

        // setter, getter
        public void setNamaPelanggan(String namaPelanggan) {
            this.namaPelanggan.set(namaPelanggan);
        }
        public String getNamaPelanggan() {
            return namaPelanggan.get();
        }
        public StringProperty namaPelangganProperty() {
            return namaPelanggan;
        }

        public void setPasswordPelanggan(String passwordPelanggan) {
            this.passwordPelanggan.set(passwordPelanggan);
        }
        public String getPasswordPelanggan() {
            return passwordPelanggan.get();
        }
        public StringProperty passwordPelangganProperty() {
            return passwordPelanggan;
        }

        public void setNoHp(String noHp) {
            this.noHp.set(noHp);
        }
        public String getNoHp()  {
            return noHp.get();
        }
        public StringProperty noHpProperty() {
            return noHp;
        }

        // True kalau objek ini sudah memegang akun yang valid (hasil signin/signup).
        // Akun default password-nya "" sehingga tetap terbaca belum login.
        public boolean isLoggedIn() {
            return getPasswordPelanggan() != null && !getPasswordPelanggan().isEmpty();
        }

        // polymorphism
        @Override
        public void signup() {
            System.out.println("=== SIGNUP PELANGGAN ===");
            System.out.print("Nama Pelanggan = ");
            String nama = IN.nextLine().trim();
            for (Pelanggan p : database) {
                // cek kesamaan data
                if (p.getNamaPelanggan() != null && p.getNamaPelanggan().equalsIgnoreCase(nama)) {
                    System.out.println("Nama sudah terdaftar. Silakan signin.");
                    return;
                }
            }
            this.namaPelanggan.set(nama);
            System.out.print("No HP = ");
            this.noHp.set(IN.nextLine().trim());
            System.out.print("Password = ");
            this.passwordPelanggan.set(IN.nextLine());
            database.add(this);
            System.out.println("Signup berhasil. Selamat datang, " + this.getNamaPelanggan() + "!");
        }
         // polymorphism
        @Override
        public void signin() {
            System.out.println("=== SIGNIN PELANGGAN ===");
            System.out.print("Nama Pelanggan = ");
            String nama = IN.nextLine().trim();
            System.out.print("Password = ");
            String pass = IN.nextLine();
            for (Pelanggan p : database) {
                // cek data
                if (Objects.equals(p.getNamaPelanggan(), nama)
                        && Objects.equals(p.getPasswordPelanggan(), pass)) {
                    this.namaPelanggan.set(p.getNamaPelanggan());
                    this.noHp.set(p.getNoHp());
                    this.passwordPelanggan.set(p.getPasswordPelanggan());
                    this.idPelanggan.set(p.getIdPelanggan());
                    System.out.println("Login berhasil. Selamat datang, " + this.getNamaPelanggan() + "!");
                    return;
                }
            }
            System.out.println("Login gagal. Nama atau password salah.");
        }

        @Override
        public String toString() {
            return getNamaPelanggan();
        }
}
