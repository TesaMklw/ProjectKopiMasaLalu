package com.pos.pelanggan;

import com.pos.interfaces.Login;
import com.pos.loader.DataLoader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Pelanggan implements Login {
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

        //constructor 2
        public Pelanggan() {

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

        // True kalau objek ini sudah memegang akun yang valid (hasil signin/signup)
        public boolean isLoggedIn() {
            return namaPelanggan != null && passwordPelanggan != null;
        }

        // Dipakai GUI: cari akun di database, null kalau tidak cocok
        public static Pelanggan authenticate(String nama, String pass) {
            if (nama == null || pass == null) return null;
            for (Pelanggan p : database) {
                // cek membandingkan nama dan password yang ada di trim dan data base (equals)
                if (Objects.equals(p.getNamaPelanggan(), nama.trim())
                        && Objects.equals(p.getPasswordPelanggan(), pass)) {
                    return p;
                }
            }
            return null;
        }

        // Dipakai GUI: daftar akun baru + simpan ke txt, null kalau gagal/duplikat
        public static Pelanggan register(String nama, String noHp, String pass) {
            if (nama == null || nama.trim().isEmpty() || pass == null || pass.isEmpty()) {
                return null;
            }
            // mastiin blum ada nama yang sam di database
            for (Pelanggan p : database) {
                if (p.getNamaPelanggan() != null && p.getNamaPelanggan().equalsIgnoreCase(nama.trim())) {
                    return null;
                }
            }
            // mengisi data
            Pelanggan baru = new Pelanggan(nama.trim());
            baru.setNoHp(noHp == null ? "" : noHp.trim());
            baru.setPasswordPelanggan(pass);
            database.add(baru);
            // coba masukin data ke file
            try {
                DataLoader.appendPelanggan("data/pelanggan.txt", baru);
            } 
            //  error
            catch (IOException e) {
            // hapus data
                database.remove(baru);
                return null;
            }
            return baru;
        }

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
