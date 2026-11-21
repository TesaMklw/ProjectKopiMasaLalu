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
public class Transaksi {
    private final StringProperty idTransaksi = new SimpleStringProperty();
    private Gerai gerai;
    private ArrayList<DetailTransaksi> listDetail; // Menyimpan baris pesanan (produk, ukuran, jumlah, catatan) secara dinamis
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
        this.listDetail = new ArrayList<>();
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
        this.listDetail = new ArrayList<>();
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
    public ArrayList<DetailTransaksi> getListDetail() {
        return new ArrayList<>(listDetail);
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

    // method operasional untuk menambahkan baris pesanan ke dalam ArrayList listDetail
    public void addDetail(DetailTransaksi detail) {
        if (detail == null) return;
        listDetail.add(detail);
    }

    public void clearDetail() {
        listDetail.clear();
    }

    // menghitung kembalian/ sisa uang
    public void calcUangKembali(float uangDibayar){
        this.uangKembali.set(uangDibayar - getTotalTransaksi());
    }

    // Hitung subtotal & total otomatis dari listDetail (harga * jumlah) - (for-each loop)
    public void calcSubtotal() {
        float sum = 0;
        for (DetailTransaksi d : listDetail) {
            sum += d.getSubtotal();
        }
        this.subtotalTransaksi.set(sum);
        this.totalTransaksi.set(sum);
    }

    // Isi struk dikembalikan sebagai teks. Cetak ke layar ada di TransaksiTableController.
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
        for (DetailTransaksi d : listDetail) {
            sb.append(" - ").append(d.getNamaTampil()).append(" x").append(d.getJumlah())
            .append(" Rp").append(d.getHargaSatuan());
            if (!d.getCatatan().isEmpty()) {
                sb.append(" [").append(d.getCatatan()).append("]");
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
