--
-- PostgreSQL database dump
--

\restrict MLUY6OOQJd3Hqog0ib2o6fllaEvG34srrTYFU1nmMez9NwMreI6L7HtAhdYlSAi

-- Dumped from database version 18.6
-- Dumped by pg_dump version 18.6

-- Started on 2026-11-27 10:30:35

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 232 (class 1259 OID 16623)
-- Name: bukan_kopi; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.bukan_kopi (
    id_produk integer NOT NULL,
    jenis_rasa character varying(150)
);


--
-- TOC entry 227 (class 1259 OID 16569)
-- Name: detail_transaksi; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.detail_transaksi (
    id_detail integer NOT NULL,
    id_transaksi character varying(20) NOT NULL,
    id_produk integer,
    nama_produk character varying(150) NOT NULL,
    harga_produk numeric(12,2) NOT NULL,
    jumlah_produk integer NOT NULL,
    tipe character varying(30),
    catatan_produk character varying(255),
    ukuran character varying(30),
    info_tambahan character varying(150)
);


--
-- TOC entry 228 (class 1259 OID 16576)
-- Name: detail_transaksi_id_detail_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.detail_transaksi_id_detail_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5108 (class 0 OID 0)
-- Dependencies: 228
-- Name: detail_transaksi_id_detail_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.detail_transaksi_id_detail_seq OWNED BY public.detail_transaksi.id_detail;


--
-- TOC entry 221 (class 1259 OID 16494)
-- Name: gerai; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.gerai (
    kode_gerai character varying(10) NOT NULL,
    nama_gerai character varying(200) NOT NULL,
    alamat_gerai text NOT NULL,
    password_gerai character varying(100) NOT NULL
);


--
-- TOC entry 231 (class 1259 OID 16612)
-- Name: kopi; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.kopi (
    id_produk integer NOT NULL,
    jenis_kopi character varying(150)
);


--
-- TOC entry 233 (class 1259 OID 16634)
-- Name: makanan_penutup; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.makanan_penutup (
    id_produk integer NOT NULL,
    variasi_makanan_penutup character varying(150)
);


--
-- TOC entry 230 (class 1259 OID 16601)
-- Name: minuman; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.minuman (
    id_produk integer NOT NULL
);


--
-- TOC entry 222 (class 1259 OID 16505)
-- Name: pelanggan; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.pelanggan (
    id_pelanggan integer NOT NULL,
    nama_pelanggan character varying(100) NOT NULL,
    no_hp character varying(30) NOT NULL,
    password_pelanggan character varying(100) NOT NULL
);


--
-- TOC entry 229 (class 1259 OID 16600)
-- Name: pelanggan_id_pelanggan_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.pelanggan ALTER COLUMN id_pelanggan ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.pelanggan_id_pelanggan_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 223 (class 1259 OID 16516)
-- Name: pembayaran; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.pembayaran (
    kode_pembayaran character varying(10) NOT NULL,
    metode_pembayaran character varying(50) NOT NULL
);


--
-- TOC entry 225 (class 1259 OID 16524)
-- Name: produk; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.produk (
    id_produk integer NOT NULL,
    nama_produk character varying(150) NOT NULL,
    harga_produk numeric(12,2) NOT NULL
);


--
-- TOC entry 224 (class 1259 OID 16523)
-- Name: produk_id_produk_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.produk_id_produk_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5109 (class 0 OID 0)
-- Dependencies: 224
-- Name: produk_id_produk_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.produk_id_produk_seq OWNED BY public.produk.id_produk;


--
-- TOC entry 226 (class 1259 OID 16539)
-- Name: transaksi; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.transaksi (
    id_transaksi character varying(20) NOT NULL,
    kode_gerai character varying(10),
    id_pelanggan integer,
    kode_pembayaran character varying(10),
    subtotal_transaksi numeric(12,2) DEFAULT 0 NOT NULL,
    total_transaksi numeric(12,2) DEFAULT 0 NOT NULL,
    uang_kembali numeric(12,2) DEFAULT 0 NOT NULL,
    total_diskon numeric(12,2) DEFAULT 0 NOT NULL,
    kode_promo character varying(20),
    tanggal_transaksi date,
    metode_pembayaran character varying(50)
);


--
-- TOC entry 234 (class 1259 OID 16647)
-- Name: ukuran_minuman; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ukuran_minuman (
    id_produk integer NOT NULL,
    ukuran character varying(30) NOT NULL,
    harga numeric(12,2) NOT NULL,
    CONSTRAINT ukuran_minuman_harga_check CHECK ((harga >= (0)::numeric)),
    CONSTRAINT ukuran_minuman_ukuran_check CHECK (((ukuran)::text = ANY ((ARRAY['Small'::character varying, 'Medium'::character varying, 'Large'::character varying])::text[])))
);


--
-- TOC entry 4905 (class 2604 OID 16645)
-- Name: detail_transaksi id_detail; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.detail_transaksi ALTER COLUMN id_detail SET DEFAULT nextval('public.detail_transaksi_id_detail_seq'::regclass);


--
-- TOC entry 4900 (class 2604 OID 16646)
-- Name: produk id_produk; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.produk ALTER COLUMN id_produk SET DEFAULT nextval('public.produk_id_produk_seq'::regclass);


--
-- TOC entry 5100 (class 0 OID 16623)
-- Dependencies: 232
-- Data for Name: bukan_kopi; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.bukan_kopi (id_produk, jenis_rasa) FROM stdin;
11	Matcha
12	Coklat
13	Brown Sugar
14	Leci
\.


--
-- TOC entry 5095 (class 0 OID 16569)
-- Dependencies: 227
-- Data for Name: detail_transaksi; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.detail_transaksi (id_detail, id_transaksi, id_produk, nama_produk, harga_produk, jumlah_produk, tipe, catatan_produk, ukuran, info_tambahan) FROM stdin;
1	TRX261124045618234	15	Cheesecake Slice	15000.00	1	MakananPenutup		-	Cheesecake
2	TRX261124045618234	4	Cappuccino	23000.00	1	Kopi		Medium	Arabika + Susu
3	TRX261124053333757	15	Cheesecake Slice	15000.00	1	MakananPenutup		-	Cheesecake
4	TRX261124053432869	17	Croissant Coklat	15300.00	1	MakananPenutup		-	Coklat
5	TRX261124053432869	10	Caramel Latte	24300.00	1	Kopi		Large	Arabika + Caramel + Susu
6	TRX261124053621334	16	Roti Abon Ayam	10400.00	1	MakananPenutup		-	Abon Ayam
7	TRX261124053621334	4	Cappuccino	11500.00	1	Kopi		Medium	Arabika + Susu
8	TRX261124125120197	9	Caramel Macchiato	14000.00	1	Kopi		Large	Arabika + Caramel + Susu Foam
9	TRX261124125120197	17	Croissant Coklat	13600.00	1	MakananPenutup		-	Coklat
10	TRX261124131347189	15	Cheesecake Slice	12000.00	1	MakananPenutup		-	Cheesecake
11	TRX261124131347189	9	Caramel Macchiato	14000.00	1	Kopi		Large	Arabika + Caramel + Susu Foam
\.


--
-- TOC entry 5089 (class 0 OID 16494)
-- Dependencies: 221
-- Data for Name: gerai; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.gerai (kode_gerai, nama_gerai, alamat_gerai, password_gerai) FROM stdin;
G002	Kopi Kenangan RS Jantung Harapan Kita (Palmerah)	Jl. Letjen S. Parman Kav. 87, Palmerah, Jakarta Barat (dekat pintu masuk utama RS Harapan Kita)	$2a$10$9v4geSBp22b.pTjDcJ254OIWJ6zMJsTYWNF4ti2GKUkJX4XnRbBge
G003	Season City Mall (Tambora)	Season City Mall, Lantai GF, Jl. Prof. Dr. Latumeten No.33, Jembatan Besi, Tambora, Jakarta Barat 11320	$2a$10$9v4geSBp22b.pTjDcJ254OIWJ6zMJsTYWNF4ti2GKUkJX4XnRbBge
G004	Mall Taman Anggrek	Mall Taman Anggrek, Lantai LG, Jl. Letjen S. Parman Kav. 21, Tanjung Duren Selatan, Jakarta Barat	$2a$10$9v4geSBp22b.pTjDcJ254OIWJ6zMJsTYWNF4ti2GKUkJX4XnRbBge
G005	Central Park Mall	Central Park Mall, Lantai LG, Jl. Letjen S. Parman No.28, Tanjung Duren Selatan, Jakarta Barat	$2a$10$9v4geSBp22b.pTjDcJ254OIWJ6zMJsTYWNF4ti2GKUkJX4XnRbBge
G001	Kopi Masa Lalu Tanjung Duren	Kelurahan Tanjung Duren Utara: Jl. Tanjung Duren Raya No. 2, RT 010/RW 02, Tanjung Duren Utara, Grogol Petamburan, Jakarta Barat 11470.	$2a$10$9v4geSBp22b.pTjDcJ254OIWJ6zMJsTYWNF4ti2GKUkJX4XnRbBge
G006	Kopi Masa Lalu Manado	Kawasan Mega Mas	$2a$10$9Jlozn8Am3paLgVvfksOzemBnSF/Z8Q0nwyR6ewsObwAWlBhXzArm
\.


--
-- TOC entry 5099 (class 0 OID 16612)
-- Dependencies: 231
-- Data for Name: kopi; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.kopi (id_produk, jenis_kopi) FROM stdin;
1	Arabika + Gula Aren
3	Arabika
4	Arabika + Susu
5	Arabika + Coklat
6	Arabika + Susu Foam
7	Arabika + Caramel + Susu
8	Arabika + Caramel + Susu Foam
9	Arabika + Caramel + Susu Foam
10	Arabika + Caramel + Susu
36	Latte
\.


--
-- TOC entry 5101 (class 0 OID 16634)
-- Dependencies: 233
-- Data for Name: makanan_penutup; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.makanan_penutup (id_produk, variasi_makanan_penutup) FROM stdin;
15	Cheesecake
16	Abon Ayam
17	Coklat
\.


--
-- TOC entry 5098 (class 0 OID 16601)
-- Dependencies: 230
-- Data for Name: minuman; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.minuman (id_produk) FROM stdin;
1
3
4
5
6
7
8
9
10
11
12
13
14
36
\.


--
-- TOC entry 5090 (class 0 OID 16505)
-- Dependencies: 222
-- Data for Name: pelanggan; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.pelanggan (id_pelanggan, nama_pelanggan, no_hp, password_pelanggan) FROM stdin;
1	VionandaCantik	081234567890	$2a$10$XonT1d.pVx3azWVQOUwDpui6egP1XpPn5Ar8Dl2arMHoy.fOfENhy
2	Vionanda	082272293292	$2a$10$qRGGJC1B2yG2xX1s2qWTreTpRyhtmfRqP.N6RySwXQPcN/eSILIsy
3	Riza	085266066497	$2a$10$NgkqTcidhMK0L5aGzIg1nOE82U/MCrEzsTmj/AN5BQChzh5wfztmm
4	Tesalonika	085216641663	$2a$10$0UWevqqwRiw9KqIT7RzAkOhxiP6m53E/25oqnoL0ZHy2OBVr3t1wi
5	Miracle	082164555543	$2a$10$772RWoEzDfMwMWFyc.zfv.shIhXqfMAwZTblBg0KZm58TH64Zkxai
14	Kelompok12	08123456789	$2a$10$3wTvudK/emzTZUz28gwEOen1IwEQzZMMH.cgp3p.tAqgk5o8008um
15	Rizabaik	089765442678	$2a$10$0KM7JJmXpG4Qy6Dx4cIZv.nzt3ot9kA17FKb3hTcuA8HWwZlqeQkW
\.


--
-- TOC entry 5091 (class 0 OID 16516)
-- Dependencies: 223
-- Data for Name: pembayaran; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.pembayaran (kode_pembayaran, metode_pembayaran) FROM stdin;
CASH	Cash
QRIS	QRIS
DANA	DANA
BCA	Debit BCA
BRI	Debit BRI
GPY	GoPay
OVO	OVO
BNI	Debit BNI
\.


--
-- TOC entry 5093 (class 0 OID 16524)
-- Dependencies: 225
-- Data for Name: produk; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.produk (id_produk, nama_produk, harga_produk) FROM stdin;
15	Cheesecake Slice	15000.00
16	Roti Abon Ayam	13000.00
17	Croissant Coklat	17000.00
1	Kopi Kenangan Mantan	18000.00
3	Kopi Hitam	20000.00
4	Cappuccino	23000.00
5	Mochaccino	25000.00
6	Macchiato	24000.00
7	Creamy Caramel Latte	26000.00
8	Caramel Macchiato	25000.00
10	Caramel Latte	27000.00
11	Matcha Latte	22000.00
12	Chocolate	22000.00
13	Susu Boba Gula Aren	19000.00
14	Teh Leci	18000.00
9	Caramel Macchiato	28000.00
36	Butterscot Aren Latte	20000.00
\.


--
-- TOC entry 5094 (class 0 OID 16539)
-- Dependencies: 226
-- Data for Name: transaksi; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.transaksi (id_transaksi, kode_gerai, id_pelanggan, kode_pembayaran, subtotal_transaksi, total_transaksi, uang_kembali, total_diskon, kode_promo, tanggal_transaksi, metode_pembayaran) FROM stdin;
TRX261124045618234	G001	1	QRIS	38000.00	38000.00	12000.00	0.00		2026-11-24	QRIS
TRX261124053333757	G001	\N	BRI	15000.00	15000.00	35000.00	0.00		2026-11-24	Debit BRI
TRX261124053432869	G001	\N	QRIS	44000.00	39600.00	10400.00	4400.00	HEMAT10	2026-11-24	QRIS
TRX261124053621334	G001	\N	DANA	36000.00	21900.00	28100.00	14100.00	HEMAT50	2026-11-24	DANA
TRX261124125120197	G005	2	CASH	45000.00	27600.00	0.00	17400.00	HEMAT50	2026-11-24	Cash
TRX261124131347189	G005	15	\N	43000.00	26000.00	24000.00	17000.00	HEMAT50	2026-11-24	OVO
\.


--
-- TOC entry 5102 (class 0 OID 16647)
-- Dependencies: 234
-- Data for Name: ukuran_minuman; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.ukuran_minuman (id_produk, ukuran, harga) FROM stdin;
1	Medium	18000.00
3	Medium	20000.00
4	Medium	23000.00
5	Large	25000.00
6	Medium	24000.00
7	Large	26000.00
8	Medium	25000.00
9	Large	28000.00
10	Large	27000.00
11	Medium	22000.00
12	Medium	22000.00
13	Medium	19000.00
14	Medium	18000.00
1	Large	22000.00
8	Large	28000.00
36	Small	20000.00
36	Medium	25000.00
36	Large	30000.00
\.


--
-- TOC entry 5110 (class 0 OID 0)
-- Dependencies: 228
-- Name: detail_transaksi_id_detail_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.detail_transaksi_id_detail_seq', 11, true);


--
-- TOC entry 5111 (class 0 OID 0)
-- Dependencies: 229
-- Name: pelanggan_id_pelanggan_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.pelanggan_id_pelanggan_seq', 15, true);


--
-- TOC entry 5112 (class 0 OID 0)
-- Dependencies: 224
-- Name: produk_id_produk_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.produk_id_produk_seq', 36, true);


--
-- TOC entry 4927 (class 2606 OID 16628)
-- Name: bukan_kopi bukan_kopi_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bukan_kopi
    ADD CONSTRAINT bukan_kopi_pkey PRIMARY KEY (id_produk);


--
-- TOC entry 4921 (class 2606 OID 16589)
-- Name: detail_transaksi detail_transaksi_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.detail_transaksi
    ADD CONSTRAINT detail_transaksi_pkey PRIMARY KEY (id_detail);


--
-- TOC entry 4909 (class 2606 OID 16503)
-- Name: gerai gerai_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gerai
    ADD CONSTRAINT gerai_pkey PRIMARY KEY (kode_gerai);


--
-- TOC entry 4925 (class 2606 OID 16617)
-- Name: kopi kopi_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.kopi
    ADD CONSTRAINT kopi_pkey PRIMARY KEY (id_produk);


--
-- TOC entry 4929 (class 2606 OID 16639)
-- Name: makanan_penutup makanan_penutup_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.makanan_penutup
    ADD CONSTRAINT makanan_penutup_pkey PRIMARY KEY (id_produk);


--
-- TOC entry 4923 (class 2606 OID 16606)
-- Name: minuman minuman_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.minuman
    ADD CONSTRAINT minuman_pkey PRIMARY KEY (id_produk);


--
-- TOC entry 4911 (class 2606 OID 16515)
-- Name: pelanggan pelanggan_nama_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pelanggan
    ADD CONSTRAINT pelanggan_nama_unique UNIQUE (nama_pelanggan);


--
-- TOC entry 4913 (class 2606 OID 16510)
-- Name: pelanggan pelanggan_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pelanggan
    ADD CONSTRAINT pelanggan_pkey PRIMARY KEY (id_pelanggan);


--
-- TOC entry 4915 (class 2606 OID 16522)
-- Name: pembayaran pembayaran_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pembayaran
    ADD CONSTRAINT pembayaran_pkey PRIMARY KEY (kode_pembayaran);


--
-- TOC entry 4917 (class 2606 OID 16538)
-- Name: produk produk_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.produk
    ADD CONSTRAINT produk_pkey PRIMARY KEY (id_produk);


--
-- TOC entry 4919 (class 2606 OID 16544)
-- Name: transaksi transaksi_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.transaksi
    ADD CONSTRAINT transaksi_pkey PRIMARY KEY (id_transaksi);


--
-- TOC entry 4931 (class 2606 OID 16656)
-- Name: ukuran_minuman ukuran_minuman_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ukuran_minuman
    ADD CONSTRAINT ukuran_minuman_pkey PRIMARY KEY (id_produk, ukuran);


--
-- TOC entry 4935 (class 2606 OID 16595)
-- Name: detail_transaksi detail_produk_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.detail_transaksi
    ADD CONSTRAINT detail_produk_fk FOREIGN KEY (id_produk) REFERENCES public.produk(id_produk) ON DELETE SET NULL NOT VALID;


--
-- TOC entry 4936 (class 2606 OID 16590)
-- Name: detail_transaksi detail_transaksi_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.detail_transaksi
    ADD CONSTRAINT detail_transaksi_fk FOREIGN KEY (id_transaksi) REFERENCES public.transaksi(id_transaksi) ON DELETE CASCADE NOT VALID;


--
-- TOC entry 4939 (class 2606 OID 16629)
-- Name: bukan_kopi fk_bukan_kopi_minuman; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bukan_kopi
    ADD CONSTRAINT fk_bukan_kopi_minuman FOREIGN KEY (id_produk) REFERENCES public.minuman(id_produk) ON DELETE CASCADE;


--
-- TOC entry 4938 (class 2606 OID 16618)
-- Name: kopi fk_kopi_minuman; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.kopi
    ADD CONSTRAINT fk_kopi_minuman FOREIGN KEY (id_produk) REFERENCES public.minuman(id_produk) ON DELETE CASCADE;


--
-- TOC entry 4940 (class 2606 OID 16640)
-- Name: makanan_penutup fk_makanan_penutup_produk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.makanan_penutup
    ADD CONSTRAINT fk_makanan_penutup_produk FOREIGN KEY (id_produk) REFERENCES public.produk(id_produk) ON DELETE CASCADE;


--
-- TOC entry 4937 (class 2606 OID 16607)
-- Name: minuman fk_minuman_produk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.minuman
    ADD CONSTRAINT fk_minuman_produk FOREIGN KEY (id_produk) REFERENCES public.produk(id_produk) ON DELETE CASCADE;


--
-- TOC entry 4932 (class 2606 OID 16545)
-- Name: transaksi transaksi_gerai_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.transaksi
    ADD CONSTRAINT transaksi_gerai_fk FOREIGN KEY (kode_gerai) REFERENCES public.gerai(kode_gerai);


--
-- TOC entry 4933 (class 2606 OID 16558)
-- Name: transaksi transaksi_pelanggan_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.transaksi
    ADD CONSTRAINT transaksi_pelanggan_fk FOREIGN KEY (id_pelanggan) REFERENCES public.pelanggan(id_pelanggan) NOT VALID;


--
-- TOC entry 4934 (class 2606 OID 16563)
-- Name: transaksi transaksi_pembayaran_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.transaksi
    ADD CONSTRAINT transaksi_pembayaran_fk FOREIGN KEY (kode_pembayaran) REFERENCES public.pembayaran(kode_pembayaran) NOT VALID;


--
-- TOC entry 4941 (class 2606 OID 16657)
-- Name: ukuran_minuman ukuran_minuman_id_produk_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ukuran_minuman
    ADD CONSTRAINT ukuran_minuman_id_produk_fkey FOREIGN KEY (id_produk) REFERENCES public.minuman(id_produk) ON DELETE CASCADE;


-- Completed on 2026-11-27 10:30:35

--
-- PostgreSQL database dump complete
--

\unrestrict MLUY6OOQJd3Hqog0ib2o6fllaEvG34srrTYFU1nmMez9NwMreI6L7HtAhdYlSAi

