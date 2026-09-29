// AUTOR = RIZA ROSMERI
package com.pos.gerai;

public class Gerai {// inheritance
    //encapsulation
    private String namaGerai;
    private String alamatGerai;

        //constructor 1
        public Gerai(String namaGerai, String alamatGerai){
            this.namaGerai = namaGerai;
            this.alamatGerai = alamatGerai;
        }

        //constructur 2: default biar tidak null
        public Gerai() {
            this("Gerai Baru", "-");
        }       
        
        // setter,getter
        public void setNamaGerai(String namaGerai) {
            this.namaGerai = namaGerai;
        }
        public String getNamaGerai() {
            return namaGerai;
        }

        public void setAlamatGerai(String alamatGerai) {
            this.alamatGerai = alamatGerai;
        }
        public String getAlamatGerai() {
            return alamatGerai;
        }
        // polymorphism
        @Override
        public String toString() {
            return namaGerai;
        }
    }
   
