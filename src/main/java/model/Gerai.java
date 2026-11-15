/*
author : Riza Rosmeri - 825250153
*/

package main.java.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Gerai {// inheritance
    //encapsulation — properti JavaFX supaya bisa diikat ke TableView
    private final StringProperty kodeGerai = new SimpleStringProperty();
    private final StringProperty namaGerai = new SimpleStringProperty();
    private final StringProperty alamatGerai = new SimpleStringProperty();

        //constructor 1
        public Gerai(String namaGerai, String alamatGerai){
            this.namaGerai.set(namaGerai);
            this.alamatGerai.set(alamatGerai);
        }

        //constructur 2: default biar tidak null
        public Gerai() {
            this("Gerai Baru", "-");
        }

        public void setKodeGerai(String kodeGerai) {
            this.kodeGerai.set(kodeGerai);
        }
        public String getKodeGerai() {
            return kodeGerai.get();
        }
        public StringProperty kodeGeraiProperty() {
            return kodeGerai;
        }

        // setter,getter
        public void setNamaGerai(String namaGerai) {
            this.namaGerai.set(namaGerai);
        }
        public String getNamaGerai() {
            return namaGerai.get();
        }
        public StringProperty namaGeraiProperty() {
            return namaGerai;
        }

        public void setAlamatGerai(String alamatGerai) {
            this.alamatGerai.set(alamatGerai);
        }
        public String getAlamatGerai() {
            return alamatGerai.get();
        }
        public StringProperty alamatGeraiProperty() {
            return alamatGerai;
        }
        // polymorphism
        @Override
        public String toString() {
            return namaGerai.get();
        }
}
