/*
author : Vionanda Ginting - 825250018
*/

package main.java.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Satu baris pesanan di dalam transaksi (tabel detail_transaksi).
 * produk berisi salinan produk dengan harga satuan sesuai ukuran yang dipilih,
 * jadi calcDiscount tetap dipanggil lewat Produk (polymorphism).
 */
public class DetailTransaksi {
    private final Produk produk;
    private final IntegerProperty jumlah = new SimpleIntegerProperty();
    private final StringProperty ukuran = new SimpleStringProperty();
    private final StringProperty catatan = new SimpleStringProperty("");

    public DetailTransaksi(Produk produk, int jumlah, String ukuran, String catatan) {
        this.produk = produk;
        setJumlah(jumlah);
        this.ukuran.set(ukuran == null || ukuran.isBlank() ? "-" : ukuran);
        setCatatan(catatan);
    }

    public Produk getProduk() { return produk; }

    public void setJumlah(int jumlah) {
        if (jumlah <= 0) return;
        this.jumlah.set(jumlah);
    }
    public int getJumlah() { return jumlah.get(); }
    public IntegerProperty jumlahProperty() { return jumlah; }

    public String getUkuran() { return ukuran.get(); }
    public StringProperty ukuranProperty() { return ukuran; }

    public void setCatatan(String catatan) { this.catatan.set(catatan == null ? "" : catatan.trim()); }
    public String getCatatan() { return catatan.get(); }
    public StringProperty catatanProperty() { return catatan; }

    public float getHargaSatuan() { return produk.getHargaProduk(); }

    public float getSubtotal() { return getHargaSatuan() * getJumlah(); }

    // nama untuk keranjang dan struk, contoh: "Kopi Kenangan Mantan (Large)"
    public String getNamaTampil() {
        if (produk instanceof Minuman && !"-".equals(getUkuran())) {
            return produk.getNamaProduk() + " (" + getUkuran() + ")";
        }
        return produk.getNamaProduk();
    }
}
