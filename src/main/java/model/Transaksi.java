/*
author : Vionanda Ginting - 825250018
*/

package main.java.model;

// Import class-class dari package lain
import java.util.ArrayList;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import main.java.controller.Cetak;

public class Transaksi implements Cetak {
    private final StringProperty idTransaksi = new SimpleStringProperty();
    private Gerai gerai;
    private ArrayList<Produk> listProduk; // Menyimpan daftar objek Produk secara dinamis ke dalam arraylist nya
    private final FloatProperty subtotalTransaksi = new SimpleFloatProperty();
    private final FloatProperty totalTransaksi = new SimpleFloatProperty();
    private final FloatProperty uangKembali = new SimpleFloatProperty();
    private final StringProperty kodePromo = new SimpleStringProperty("");
    private final FloatProperty totalDiskon = new SimpleFloatProperty();
    private Pelanggan pelanggan;
    private final StringProperty tanggalTransaksi = new SimpleStringProperty();
    private Pembayaran pembayaran;
    // Teks singkat untuk kolom TableView (ikut berubah lewat setter relasi)
    private final StringProperty namaGerai = new SimpleStringProperty("");
    private final StringProperty namaPelanggan = new SimpleStringProperty("");
    private final StringProperty metodePembayaranTampil = new SimpleStringProperty("");

    // Constructor 1 (Parameterized Constructor)
    public Transaksi(String idTransaksi, Gerai gerai, float subtotalTransaksi, float totalTransaksi,
                    float uangKembali, Pelanggan pelanggan, String tanggalTransaksi, Pembayaran pembayaran) {
        this.listProduk = new ArrayList<>();
        setIdTransaksi(idTransaksi);
        setGerai(gerai);
        setSubtotalTransaksi(subtotalTransaksi);
        setTotalTransaksi(totalTransaksi);
        setUangKembali(uangKembali);
        setPelanggan(pelanggan);
        setTanggalTransaksi(tanggalTransaksi);
        setPembayaran(pembayaran);
    }

    // Constructor 2 (Default/No-Arg Constructor)
    public Transaksi() {
        this.listProduk = new ArrayList<>();
    }

    // setters untuk mengatur/mengubah nilai variabel private
    public void setIdTransaksi(String idTransaksi) {
        this.idTransaksi.set(idTransaksi);
    }

    public void setGerai(Gerai gerai) {
        this.gerai = gerai;
        this.namaGerai.set(gerai != null && gerai.getNamaGerai() != null ? gerai.getNamaGerai() : "");
    }

    public void setSubtotalTransaksi(float subtotalTransaksi) {
        this.subtotalTransaksi.set(subtotalTransaksi);
    }

    public void setTotalTransaksi(float totalTransaksi) {
        this.totalTransaksi.set(totalTransaksi);
    }

    public void setUangKembali(float uangKembali) {
        this.uangKembali.set(uangKembali);
    }

    public void setKodePromo(String kodePromo) {
        this.kodePromo.set(kodePromo);
    }

    public void setTotalDiskon(float totalDiskon) {
        this.totalDiskon.set(totalDiskon);
    }

    public void setPelanggan(Pelanggan pelanggan) {
        this.pelanggan = pelanggan;
        this.namaPelanggan.set(pelanggan != null && pelanggan.getNamaPelanggan() != null
                ? pelanggan.getNamaPelanggan() : "");
    }

    public void setTanggalTransaksi(String tanggalTransaksi) {
        this.tanggalTransaksi.set(tanggalTransaksi);
    }

    public void setPembayaran(Pembayaran pembayaran) {
        this.pembayaran = pembayaran;
        this.metodePembayaranTampil.set(pembayaran != null && pembayaran.getMetodePembayaran() != null
                ? pembayaran.getMetodePembayaran() : "");
    }

    // getters untuk mengambil/membaca nilai variabel private
    public String getIdTransaksi() {
        return idTransaksi.get();
    }

    public StringProperty idTransaksiProperty() {
        return idTransaksi;
    }

    public Gerai getGerai() {
        return gerai;
    }

    public String getNamaGerai() {
        return namaGerai.get();
    }

    public StringProperty namaGeraiProperty() {
        return namaGerai;
    }

    // Mengembalikan salinan (copy) ArrayList
    public ArrayList<Produk> getListProduk() {
        return new ArrayList<>(listProduk);
    }

    public float getSubtotalTransaksi() {
        return subtotalTransaksi.get();
    }

    public FloatProperty subtotalTransaksiProperty() {
        return subtotalTransaksi;
    }

    public float getTotalTransaksi() {
        return totalTransaksi.get();
    }

    public FloatProperty totalTransaksiProperty() {
        return totalTransaksi;
    }

    public float getUangKembali() {
        return uangKembali.get();
    }

    public FloatProperty uangKembaliProperty() {
        return uangKembali;
    }

    public String getKodePromo() {
        return kodePromo.get();
    }

    public StringProperty kodePromoProperty() {
        return kodePromo;
    }

    public float getTotalDiskon() {
        return totalDiskon.get();
    }

    public FloatProperty totalDiskonProperty() {
        return totalDiskon;
    }

    public Pelanggan getPelanggan() {
        return pelanggan;
    }

    public String getNamaPelanggan() {
        return namaPelanggan.get();
    }

    public StringProperty namaPelangganProperty() {
        return namaPelanggan;
    }

    public String getTanggalTransaksi() {
        return tanggalTransaksi.get();
    }

    public StringProperty tanggalTransaksiProperty() {
        return tanggalTransaksi;
    }

    public Pembayaran getPembayaran() {
        return pembayaran;
    }

    public String getMetodePembayaranTampil() {
        return metodePembayaranTampil.get();
    }

    public StringProperty metodePembayaranTampilProperty() {
        return metodePembayaranTampil;
    }

    // method operasional yang untuk menambahkan item Produk ke dalam ArrayList listProduk
    public void addProduk (Produk produk){
        if (produk == null) return;
        listProduk.add(produk);
    }

    public void clearProduk() {
        listProduk.clear();
    }

    // menghitung kembalian/ sisa uang
    public void calcUangKembali(float uangDibayar){
        this.uangKembali.set(uangDibayar - getTotalTransaksi());
        if (getUangKembali() < 0) {
            System.out.println("Peringatan: uang dibayar kurang Rp" + (-getUangKembali()));
        }
    }

    // Hitung subtotal & total otomatis dari listProduk (harga * jumlah) - (for-each loop)
    public void calcSubtotal() {
        float sum = 0;
        for (Produk p : listProduk) {
            sum += p.getHargaProduk() * p.getJumlahProduk();
        }
        this.subtotalTransaksi.set(sum);
        this.totalTransaksi.set(sum);
    }

    // memenuhi kontrak interface Cetak untuk menampilkan isi struk di konsol (Polymorphism - Method Overriding)
    @Override
    public void struk() {
        System.out.print(getStrukText());
    }

    // Isi struk yang sama, dikembalikan sebagai teks untuk ditampilkan di GUI
    public String getStrukText() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== STRUK TRANSAKSI ===\n");
        sb.append("ID Transaksi : ").append(getIdTransaksi()).append("\n");

        // ternary operator (? :) untuk mencegah NullPointerException
        sb.append("Gerai        : ").append(gerai != null ? gerai.getNamaGerai() : "-").append("\n");
        sb.append("Alamat Gerai : ").append(gerai != null ? gerai.getAlamatGerai() : "-").append("\n");
        sb.append("Pelanggan    : ").append(pelanggan != null ? pelanggan.getNamaPelanggan() : "-").append("\n");
        sb.append("Tanggal      : ").append(getTanggalTransaksi()).append("\n");
        sb.append("Metode Bayar : ").append(pembayaran != null ? pembayaran.getMetodePembayaran() : "-").append("\n");
        sb.append("Produk       : \n");

        // for untuk baca seluruh produk dalam transaksi
        for (Produk p : listProduk) {
            sb.append(" - ").append(p.getNamaProduk()).append(" x").append(p.getJumlahProduk())
            .append(" Rp").append(p.getHargaProduk());
            if (p.getCatatanProduk() != null && !p.getCatatanProduk().trim().isEmpty()) {
                sb.append(" [").append(p.getCatatanProduk().trim()).append("]");
            }
            sb.append("\n");
        }
        sb.append("Subtotal     : ").append(getSubtotalTransaksi()).append("\n");
        if (getKodePromo() != null && !getKodePromo().isEmpty()) {
            sb.append("Promo        : ").append(getKodePromo()).append("\n");
            sb.append("Potongan     : Rp").append(getTotalDiskon()).append("\n");
        }
        sb.append("Total        : ").append(getTotalTransaksi()).append("\n");
        sb.append("Uang Kembali : ").append(getUangKembali()).append("\n");
        sb.append("========================\n");
        return sb.toString();
    }
}
