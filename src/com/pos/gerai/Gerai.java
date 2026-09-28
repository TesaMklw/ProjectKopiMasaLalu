package com.pos.gerai;

public class Gerai {
    private String namaGerai;
    private String alamatGerai;

        //constructor 1
        public Gerai(String namaGerai, String alamatGerai){
            this.namaGerai = namaGerai;
            this.alamatGerai = alamatGerai;
        }

        //constructur 2
        public Gerai() {

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

        @Override
        public String toString() {
            return namaGerai;
        }
    }
   
