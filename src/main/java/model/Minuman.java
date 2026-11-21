/*
author : Tesalonika Miracle Makalew - 825250020
*/

package main.java.model;

import java.util.ArrayList;
import java.util.List;

// abstract: tidak ada produk yang hanya "minuman", selalu Kopi atau BukanKopi
public abstract class Minuman extends Produk {// inheritance
    // satu minuman bisa punya beberapa ukuran, masing-masing dengan harganya sendiri (tabel ukuran_minuman)
    private final ArrayList<UkuranMinuman> daftarUkuran = new ArrayList<>();// encapsulation

    public Minuman(String namaProduk, float hargaProduk) {
        super(namaProduk, hargaProduk);
    }

    public void tambahUkuran(UkuranMinuman ukuran) {
        if (ukuran == null) return;
        daftarUkuran.removeIf(u -> u.getUkuran().equalsIgnoreCase(ukuran.getUkuran()));
        daftarUkuran.add(ukuran);
        daftarUkuran.sort((a, b) -> Integer.compare(UkuranMinuman.urutan(a.getUkuran()),
                UkuranMinuman.urutan(b.getUkuran())));
        // harga produk = harga ukuran termurah (harga mulai)
        float termurah = daftarUkuran.get(0).getHarga();
        for (UkuranMinuman u : daftarUkuran) {
            termurah = Math.min(termurah, u.getHarga());
        }
        setHargaProduk(termurah);
    }

    public void clearUkuran() { daftarUkuran.clear(); }

    // salinan supaya list asli tidak diubah dari luar
    public List<UkuranMinuman> getDaftarUkuran() { return new ArrayList<>(daftarUkuran); }

    public UkuranMinuman cariUkuran(String ukuran) {
        for (UkuranMinuman u : daftarUkuran) {
            if (u.getUkuran().equalsIgnoreCase(ukuran)) return u;
        }
        return null;
    }

    @Override
    public String getTipeProduk() { return "Minuman"; }

    @Override
    public String getUkuranTampil() {
        if (daftarUkuran.isEmpty()) return "-";
        StringBuilder sb = new StringBuilder();
        for (UkuranMinuman u : daftarUkuran) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(u.getUkuran());
        }
        return sb.toString();
    }

    // Minuman: diskon sesuai persen yang diberikan
    @Override// polymorphism
    public void calcDiscount(float persen) {
        if (persen < 0) persen = 0;
        if (persen > 100) persen = 100;
        float potongan = getHargaProduk() * persen / 100;
        setHargaProduk(getHargaProduk() - potongan);
    }
}
