/*
author : Tesalonika Miracle Makalew - 825250020
*/

package main.java.model;

// Satu baris tabel ukuran_minuman: ukuran (Small/Medium/Large) dan harganya
public class UkuranMinuman {
    public static final String[] PILIHAN = {"Small", "Medium", "Large"};

    private String ukuran;// encapsulation
    private float harga;

    public UkuranMinuman(String ukuran, float harga) {
        this.ukuran = ukuran;
        setHarga(harga);
    }

    public String getUkuran() { return ukuran; }
    public void setUkuran(String ukuran) { this.ukuran = ukuran; }

    public float getHarga() { return harga; }
    public void setHarga(float harga) {
        if (harga < 0) return;
        this.harga = harga;
    }

    // urutan tampil Small, Medium, Large
    public static int urutan(String ukuran) {
        for (int i = 0; i < PILIHAN.length; i++) {
            if (PILIHAN[i].equalsIgnoreCase(ukuran)) return i;
        }
        return PILIHAN.length;
    }

    @Override// polymorphism
    public String toString() {
        return ukuran + " - Rp" + String.format(java.util.Locale.forLanguageTag("id-ID"), "%,.0f", harga);
    }
}
