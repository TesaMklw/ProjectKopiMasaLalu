/*
author : Riza Rosmeri - 825250153
*/

package com.pos.pelanggan;

import com.pos.interfaces.Login;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Pelanggan implements Login {// hierarki
    private static Scanner IN = new Scanner(System.in);

    // Dipanggil Main supaya semua input lewat satu Scanner yang sama
    public static void setScanner(Scanner in) {
        if (in != null) {
            IN = in;
        }
    }

    // Database akun (diisi dari data/pelanggan.txt lewat setDatabase)
    private static List<Pelanggan> database = new ArrayList<>();

    public static void setDatabase(List<Pelanggan> data) {
        database = (data != null) ? data : new ArrayList<>();
    }

    private String namaPelanggan;
    private String passwordPelanggan;
    private String noHp;

        //constructor 1
        public Pelanggan(String namaPelanggan) {
            this.namaPelanggan = namaPelanggan;
        }

        //constructor 2: default biar tidak null (password kosong = belum login)
        public Pelanggan() {
            this("Tanpa Nama");
            this.noHp = "-";
            this.passwordPelanggan = "";
        }

        // setter, getter
        public void setNamaPelanggan(String namaPelanggan) {
            this.namaPelanggan = namaPelanggan;
        }
        public String getNamaPelanggan() {
            return namaPelanggan;
        }

        public void setPasswordPelanggan(String passwordPelanggan) {
            this.passwordPelanggan = passwordPelanggan;
        }
        public String getPasswordPelanggan() {
            return passwordPelanggan;
        }

        public void setNoHp(String noHp) {
            this.noHp = noHp;
        }
        public String getNoHp()  {
            return noHp;
        }

        // True kalau objek ini sudah memegang akun yang valid (hasil signin/signup).
        // Akun default password-nya "" sehingga tetap terbaca belum login.
        public boolean isLoggedIn() {
            return passwordPelanggan != null && !passwordPelanggan.isEmpty();
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
            this.namaPelanggan = nama;
            System.out.print("No HP = ");
            this.noHp = IN.nextLine().trim();
            System.out.print("Password = ");
            this.passwordPelanggan = IN.nextLine();
            database.add(this);
            System.out.println("Signup berhasil. Selamat datang, " + this.namaPelanggan + "!");
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
                    this.namaPelanggan = p.getNamaPelanggan();
                    this.noHp = p.getNoHp();
                    this.passwordPelanggan = p.getPasswordPelanggan();
                    System.out.println("Login berhasil. Selamat datang, " + this.namaPelanggan + "!");
                    return;
                }
            }
            System.out.println("Login gagal. Nama atau password salah.");
        }
}
