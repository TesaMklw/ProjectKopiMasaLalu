/*
author : Riza Rosmeri - 825250153
*/

package main.java.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Pelanggan {// hierarki
    private final IntegerProperty idPelanggan = new SimpleIntegerProperty();
    private final StringProperty namaPelanggan = new SimpleStringProperty();
    private final StringProperty passwordPelanggan = new SimpleStringProperty();
    private final StringProperty noHp = new SimpleStringProperty();

        //constructor 1
        public Pelanggan(String namaPelanggan) {
            this.namaPelanggan.set(namaPelanggan);
        }

        //constructor 2: default biar tidak null (password kosong = belum login)
        public Pelanggan() {
            this("Tanpa Nama");
            this.noHp.set("-");
            this.passwordPelanggan.set("");
        }

        public void setIdPelanggan(int idPelanggan) {
            this.idPelanggan.set(idPelanggan);
        }
        public int getIdPelanggan() {
            return idPelanggan.get();
        }
        public IntegerProperty idPelangganProperty() {
            return idPelanggan;
        }

        // setter, getter
        public void setNamaPelanggan(String namaPelanggan) {
            this.namaPelanggan.set(namaPelanggan);
        }
        public String getNamaPelanggan() {
            return namaPelanggan.get();
        }
        public StringProperty namaPelangganProperty() {
            return namaPelanggan;
        }

        public void setPasswordPelanggan(String passwordPelanggan) {
            this.passwordPelanggan.set(passwordPelanggan);
        }
        public String getPasswordPelanggan() {
            return passwordPelanggan.get();
        }
        public StringProperty passwordPelangganProperty() {
            return passwordPelanggan;
        }

        public void setNoHp(String noHp) {
            this.noHp.set(noHp);
        }
        public String getNoHp()  {
            return noHp.get();
        }
        public StringProperty noHpProperty() {
            return noHp;
        }

        @Override
        public String toString() {
            return getNamaPelanggan();
        }
}
