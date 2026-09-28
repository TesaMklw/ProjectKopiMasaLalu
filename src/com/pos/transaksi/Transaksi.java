package com.pos.transaksi;

import com.pos.gerai.Gerai;
import com.pos.interfaces.Cetak;
import com.pos.pelanggan.Pelanggan;
import com.pos.pembayaran.Pembayaran;
import com.pos.produk.Produk;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class Transaksi implements Cetak {
    private String idTransaksi;
    private Gerai gerai;
    private ArrayList<Produk> listProduk;
    private float subtotalTransaksi;
    private float totalTransaksi;
    private float uangKembali;
    private Pelanggan pelanggan;
    private String tanggalTransaksi;
    private Pembayaran pembayaran;

    // Constructor 1
    public Transaksi(String idTransaksi, Gerai gerai, float subtotalTransaksi, float totalTransaksi,
                    float uangKembali, Pelanggan pelanggan, String tanggalTransaksi, Pembayaran pembayaran) {
        this.idTransaksi = idTransaksi;
        this.gerai = gerai;
        this.listProduk = new ArrayList<>();
        this.subtotalTransaksi = subtotalTransaksi;
        this.totalTransaksi = totalTransaksi;
        this.uangKembali = uangKembali;
        this.pelanggan = pelanggan;
        this.tanggalTransaksi = tanggalTransaksi;
        this.pembayaran = pembayaran;
    }

    // Constructor 2
    public Transaksi() {
        this.listProduk = new ArrayList<>();
    }

    // setters
    public void setIdTransaksi(String idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public void setGerai(Gerai gerai) {
        this.gerai = gerai;
    }

    public void setSubtotalTransaksi(float subtotalTransaksi) {
        this.subtotalTransaksi = subtotalTransaksi;
    }

    public void setTotalTransaksi(float totalTransaksi) {
        this.totalTransaksi = totalTransaksi;
    }

    public void setUangKembali(float uangKembali) {
        this.uangKembali = uangKembali;
    }

    public void setPelanggan(Pelanggan pelanggan) {
        this.pelanggan = pelanggan;
    }

    public void setTanggalTransaksi(String tanggalTransaksi) {
        this.tanggalTransaksi = tanggalTransaksi;
    }

    public void setPembayaran(Pembayaran pembayaran) {
        this.pembayaran = pembayaran;
    }

    // getters
    public String getIdTransaksi() {
        return idTransaksi;
    }

    public Gerai getGerai() {
        return gerai;
    }

    public ArrayList<Produk> getListProduk() {
        return new ArrayList<>(listProduk);
    }

    public float getSubtotalTransaksi() {
        return subtotalTransaksi;
    }

    public float getTotalTransaksi() {
        return totalTransaksi;
    }

    public float getUangKembali() {
        return uangKembali;
    }

    public Pelanggan getPelanggan() {
        return pelanggan;
    }

    public String getTanggalTransaksi() {
        return tanggalTransaksi;
    }

    public Pembayaran getPembayaran() {
        return pembayaran;
    }

    public void addProduk (Produk produk){
        if (produk == null) return;
        listProduk.add(produk);
    }

    public void calcUangKembali(float uangDibayar){
        this.uangKembali = uangDibayar - totalTransaksi;
        if (this.uangKembali < 0) {
            System.out.println("Peringatan: uang dibayar kurang Rp" + (-this.uangKembali));
        }
    }

    // Hitung subtotal & total otomatis dari listProduk (harga * jumlah)
    public void calcSubtotal() {
        float sum = 0;
        for (Produk p : listProduk) {
            sum += p.getHargaProduk() * p.getJumlahProduk();
        }
        this.subtotalTransaksi = sum;
        this.totalTransaksi = sum;
    }

    @Override
    public void struk() {
        System.out.print(getStrukText());
    }

    // Isi struk yang sama, dikembalikan sebagai teks untuk ditampilkan di GUI
    public String getStrukText() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== STRUK TRANSAKSI ===\n");
        sb.append("ID Transaksi : ").append(idTransaksi).append("\n");
        sb.append("Gerai        : ").append(gerai != null ? gerai.getNamaGerai() : "-").append("\n");
        sb.append("Alamat Gerai : ").append(gerai != null ? gerai.getAlamatGerai() : "-").append("\n");
        sb.append("Pelanggan    : ").append(pelanggan != null ? pelanggan.getNamaPelanggan() : "-").append("\n");
        sb.append("Tanggal      : ").append(tanggalTransaksi).append("\n");
        sb.append("Metode Bayar : ").append(pembayaran != null ? pembayaran.getMetodePembayaran() : "-").append("\n");
        sb.append("Produk       : \n");
        for (Produk p : listProduk) {
            sb.append(" - ").append(p.getNamaProduk()).append(" x").append(p.getJumlahProduk())
            .append(" Rp").append(p.getHargaProduk());
            if (p.getCatatanProduk() != null && !p.getCatatanProduk().trim().isEmpty()) {
                sb.append(" [").append(p.getCatatanProduk().trim()).append("]");
            }
            sb.append("\n");
        }
        sb.append("Subtotal     : ").append(subtotalTransaksi).append("\n");
        sb.append("Total        : ").append(totalTransaksi).append("\n");
        sb.append("Uang Kembali : ").append(uangKembali).append("\n");
        sb.append("========================\n");
        return sb.toString();
    }

    // Simpan struk ke 1 file txt (struk.txt); tiap struk baru ditambah di bawahnya.
    public String simpanStruk() throws IOException {
        Path path = Paths.get("struk.txt");
        String pemisah = (Files.exists(path) && Files.size(path) > 0)
                ? System.lineSeparator() : "";
        Files.write(path,
                (pemisah + getStrukText()).getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        return path.toString();
    }
}
