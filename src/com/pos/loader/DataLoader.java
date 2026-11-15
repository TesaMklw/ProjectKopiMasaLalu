/*
author : Tesalonika Miracle Makalew - 825250020
*/
package com.pos.loader;

import com.pos.gerai.Gerai;
import com.pos.pelanggan.Pelanggan;
import com.pos.pembayaran.Pembayaran;
import com.pos.produk.BukanKopi;
import com.pos.produk.Kopi;
import com.pos.produk.MakananPenutup;
import com.pos.produk.Produk;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {// inheritance

    private static List<String> readLines(String path) throws IOException {
        List<String> out = new ArrayList<>();// polymorphism
        for (String line : Files.readAllLines(Paths.get(path))) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            out.add(line);
        }
        return out;
    }

    // Format: Tipe|NamaProduk|Harga|VarianUkuran|InfoTambahan|ContohCatatan
    public static List<Produk> loadProduk(String path) throws IOException {
        List<Produk> list = new ArrayList<>();
        for (String line : readLines(path)) {
            String[] p = line.split("\\|");
            if (p.length < 6) continue;
            String tipe = p[0].trim();
            String nama = p[1].trim();
            float harga = Float.parseFloat(p[2].trim());
            String ukuran = p[3].trim();
            String info = p[4].trim();
            String catatan = p[5].trim();
            if (tipe.equalsIgnoreCase("Kopi")) {
                list.add(new Kopi(info, ukuran, nama, harga, catatan, 1));
            } else if (tipe.equalsIgnoreCase("BukanKopi")) {
                list.add(new BukanKopi(info, ukuran, nama, harga, catatan, 1));
            } else {
                list.add(new MakananPenutup(info, nama, harga, catatan, 1));
            }
        }
        return list;
    }

    // Format: Kode|NamaMetode
    public static List<Pembayaran> loadPembayaran(String path) throws IOException {
        List<Pembayaran> list = new ArrayList<>();
        for (String line : readLines(path)) {
            String[] p = line.split("\\|");
            if (p.length < 2) continue;
            list.add(new Pembayaran(p[1].trim()));
        }
        return list;
    }

    // Format: KodeGerai|NamaGerai|AlamatGerai
    public static List<Gerai> loadGerai(String path) throws IOException {
        List<Gerai> list = new ArrayList<>();
        for (String line : readLines(path)) {
            String[] p = line.split("\\|");
            if (p.length < 3) continue;
            list.add(new Gerai(p[1].trim(), p[2].trim()));
        }
        return list;
    }

    // Format: NamaPelanggan|NoHP|Password
    public static List<Pelanggan> loadPelanggan(String path) throws IOException {
        List<Pelanggan> list = new ArrayList<>();
        for (String line : readLines(path)) {
            String[] p = line.split("\\|");
            if (p.length < 3) continue;
            Pelanggan pl = new Pelanggan(p[0].trim());
            pl.setNoHp(p[1].trim());
            pl.setPasswordPelanggan(p[2].trim());
            list.add(pl);
        }
        return list;
    }

    // Simpan akun hasil signup ke akhir file txt
    public static void appendPelanggan(String path, Pelanggan p) throws IOException {
        String line = System.lineSeparator()
                //encapsulation
                + p.getNamaPelanggan() + "|" + p.getNoHp() + "|" + p.getPasswordPelanggan();
        Files.write(Paths.get(path), line.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.APPEND);
    }
}
