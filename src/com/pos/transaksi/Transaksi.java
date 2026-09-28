package com.pos.transaksi;

import com.pos.gerai.Gerai;
import com.pos.pelanggan.Pelanggan;
import com.pos.pembayaran.Pembayaran;
import com.pos.produk.Produk;
import java.util.ArrayList;
import java.util.Scanner;

public class Transaksi {
    private String idTransaksi;
    private Gerai gerai;
    private ArrayList<Produk> listProduk;
    private String subtotalTransaksi;
    private String totalTransaksi;
    private float uangKembali;
    private Pelanggan pelanggan;
    private String tanggalTransaksi;
    private Pembayaran pembayaran;

    Scanner inputUser = new Scanner(System.in);

    // Constructor 1
    public Transaksi(String idTransaksi, Gerai gerai, String subtotalTransaksi, String totalTransaksi,
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

    public void setSubtotalTransaksi(String subtotalTransaksi) {
        this.subtotalTransaksi = subtotalTransaksi;
    }

    public void setTotalTransaksi(String totalTransaksi) {
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
        return listProduk;
    }

    public String getSubtotalTransaksi() {
        return subtotalTransaksi;
    }

    public String getTotalTransaksi() {
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
}
