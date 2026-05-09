-- MySQL dump 10.13  Distrib 8.0.44, for macos15 (arm64)
--
-- Host: localhost    Database: xettuyen2026
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `xt_bangquydoi`
--

DROP TABLE IF EXISTS `xt_bangquydoi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xt_bangquydoi` (
  `idqd` int NOT NULL AUTO_INCREMENT,
  `d_phuongthuc` varchar(45) DEFAULT NULL,
  `d_tohop` varchar(45) DEFAULT NULL,
  `d_mon` varchar(45) DEFAULT NULL,
  `d_diema` decimal(6,2) DEFAULT NULL,
  `d_diemb` decimal(6,2) DEFAULT NULL,
  `d_diemc` decimal(6,2) DEFAULT NULL,
  `d_diemd` decimal(6,2) DEFAULT NULL,
  `d_maquydoi` varchar(45) DEFAULT NULL,
  `d_phanvi` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idqd`),
  UNIQUE KEY `d_maquydoi_UNIQUE` (`d_maquydoi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xt_bangquydoi`
--

LOCK TABLES `xt_bangquydoi` WRITE;
/*!40000 ALTER TABLE `xt_bangquydoi` DISABLE KEYS */;
/*!40000 ALTER TABLE `xt_bangquydoi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xt_diemcongxettuyen`
--

DROP TABLE IF EXISTS `xt_diemcongxettuyen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `xt_diemcongxettuyen` (
  `iddiemcong` int NOT NULL AUTO_INCREMENT,
  `ts_cccd` varchar(45) DEFAULT NULL,
  `manganh` varchar(20) DEFAULT NULL,
  `matohop` varchar(10) DEFAULT NULL,
  `phuongthuc` varchar(45) DEFAULT NULL,
  `diemCC` decimal(6,2) DEFAULT NULL,
  `diemUtxt` decimal(6,2) DEFAULT NULL,
  `diemTong` decimal(6,2) DEFAULT NULL,
  `ghichu` text,
  `dc_keys` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`iddiemcong`)
);

--
-- Dumping data for table `xt_diemcongxettuyen`
--

LOCK TABLES `xt_diemcongxettuyen` WRITE;
/*!40000 ALTER TABLE `xt_diemcongxettuyen` DISABLE KEYS */;
/*!40000 ALTER TABLE `xt_diemcongxettuyen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xt_diemthixettuyen`
--

DROP TABLE IF EXISTS `xt_diemthixettuyen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xt_diemthixettuyen` (
  `iddiemthi` int NOT NULL AUTO_INCREMENT,
  `cccd` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `sobaodanh` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `d_phuongthuc` varchar(10) DEFAULT NULL,
  `TO` decimal(8,2) DEFAULT '0.00',
  `LI` decimal(8,2) DEFAULT '0.00',
  `HO` decimal(8,2) DEFAULT '0.00',
  `SI` decimal(8,2) DEFAULT '0.00',
  `SU` decimal(8,2) DEFAULT '0.00',
  `DI` decimal(8,2) DEFAULT '0.00',
  `VA` decimal(8,2) DEFAULT '0.00',
  `N1_THI` decimal(8,2) DEFAULT NULL COMMENT 'Điểm thi gốc',
  `N1_CC` decimal(8,2) DEFAULT '0.00' COMMENT 'max(N1_Thi, N1_QD)',
  `CNCN` decimal(8,2) DEFAULT '0.00',
  `CNNN` decimal(8,2) DEFAULT '0.00',
  `TI` decimal(8,2) DEFAULT '0.00',
  `KTPL` decimal(8,2) DEFAULT '0.00',
  `NL1` decimal(8,2) DEFAULT NULL,
  `NK1` decimal(8,2) DEFAULT NULL,
  `NK2` decimal(8,2) DEFAULT NULL,
  PRIMARY KEY (`iddiemthi`),
  UNIQUE KEY `cccd_UNIQUE` (`cccd`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xt_diemthixettuyen`
--

LOCK TABLES `xt_diemthixettuyen` WRITE;
/*!40000 ALTER TABLE `xt_diemthixettuyen` DISABLE KEYS */;
/*!40000 ALTER TABLE `xt_diemthixettuyen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xt_nganh`
--

DROP TABLE IF EXISTS `xt_nganh`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xt_nganh` (
  `idnganh` int NOT NULL AUTO_INCREMENT,
  `manganh` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `tennganh` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `n_tohopgoc` varchar(3) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `n_chitieu` int NOT NULL DEFAULT '0',
  `n_diemsan` decimal(10,2) DEFAULT NULL,
  `n_diemtrungtuyen` decimal(10,2) DEFAULT NULL,
  `n_tuyenthang` varchar(1) COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `n_dgnl` varchar(1) COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `n_thpt` varchar(1) COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `n_vsat` varchar(1) COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `sl_xtt` int DEFAULT NULL,
  `sl_dgnl` int DEFAULT NULL,
  `sl_vsat` int DEFAULT NULL,
  `sl_thpt` varchar(45) COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`idnganh`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xt_nganh`
--

LOCK TABLES `xt_nganh` WRITE;
/*!40000 ALTER TABLE `xt_nganh` DISABLE KEYS */;
/*!40000 ALTER TABLE `xt_nganh` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xt_nganh_tohop`
--

DROP TABLE IF EXISTS `xt_nganh_tohop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xt_nganh_tohop` (
  `id` int NOT NULL AUTO_INCREMENT,
  `manganh` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `matohop` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `th_mon1` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `hsmon1` tinyint DEFAULT NULL,
  `th_mon2` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `hsmon2` tinyint DEFAULT NULL,
  `th_mon3` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `hsmon3` tinyint DEFAULT NULL,
  `tb_keys` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT 'manganh_matohop',
  `N1` tinyint(1) DEFAULT NULL,
  `TO` tinyint(1) DEFAULT NULL,
  `LI` tinyint(1) DEFAULT NULL,
  `HO` tinyint(1) DEFAULT NULL,
  `SI` tinyint(1) DEFAULT NULL,
  `VA` tinyint(1) DEFAULT NULL,
  `SU` tinyint(1) DEFAULT NULL,
  `DI` tinyint(1) DEFAULT NULL,
  `TI` tinyint(1) DEFAULT NULL,
  `KHAC` tinyint(1) DEFAULT NULL,
  `KTPL` tinyint(1) DEFAULT NULL,
  `dolech` decimal(6,2) DEFAULT '0.00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_UNIQUE` (`tb_keys`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xt_nganh_tohop`
--

LOCK TABLES `xt_nganh_tohop` WRITE;
/*!40000 ALTER TABLE `xt_nganh_tohop` DISABLE KEYS */;
/*!40000 ALTER TABLE `xt_nganh_tohop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xt_nguyenvongxettuyen`
--

DROP TABLE IF EXISTS `xt_nguyenvongxettuyen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xt_nguyenvongxettuyen` (
  `idnv` int NOT NULL AUTO_INCREMENT,
  `nn_cccd` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `nv_manganh` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `nv_tt` int NOT NULL,
  `diem_thxt` decimal(10,5) DEFAULT NULL COMMENT 'đã cộng điểm môn chính',
  `diem_utqd` decimal(10,5) DEFAULT NULL COMMENT 'Điểm UTQD theo tổ họp sẽ khác nhau.',
  `diem_cong` decimal(6,2) DEFAULT NULL COMMENT 'Tong 3 mon chua tinh mon chinh + diem uu tien\\\\\\\\n',
  `diem_xettuyen` decimal(10,5) DEFAULT NULL COMMENT 'đã cộng điểm ưu tiên',
  `nv_ketqua` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `nv_keys` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `tt_phuongthuc` varchar(45) DEFAULT NULL,
  `tt_thm` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idnv`),
  UNIQUE KEY `nv_keys_UNIQUE` (`nv_keys`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xt_nguyenvongxettuyen`
--

LOCK TABLES `xt_nguyenvongxettuyen` WRITE;
/*!40000 ALTER TABLE `xt_nguyenvongxettuyen` DISABLE KEYS */;
/*!40000 ALTER TABLE `xt_nguyenvongxettuyen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xt_thisinhxettuyen25`
--

DROP TABLE IF EXISTS `xt_thisinhxettuyen25`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xt_thisinhxettuyen25` (
  `idthisinh` int NOT NULL AUTO_INCREMENT,
  `cccd` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sobaodanh` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `ho` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `ten` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `ngay_sinh` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `dien_thoai` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `password` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `gioi_tinh` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `noi_sinh` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `updated_at` date DEFAULT NULL,
  `doi_tuong` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `khu_vuc` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`idthisinh`),
  UNIQUE KEY `cccd_UNIQUE` (`cccd`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xt_thisinhxettuyen25`
--

LOCK TABLES `xt_thisinhxettuyen25` WRITE;
/*!40000 ALTER TABLE `xt_thisinhxettuyen25` DISABLE KEYS */;
/*!40000 ALTER TABLE `xt_thisinhxettuyen25` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xt_tohop_monthi`
--

DROP TABLE IF EXISTS `xt_tohop_monthi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xt_tohop_monthi` (
  `idtohop` int NOT NULL AUTO_INCREMENT,
  `matohop` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `mon1` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `mon2` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `mon3` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `tentohop` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`idtohop`),
  UNIQUE KEY `matohop_UNIQUE` (`matohop`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xt_tohop_monthi`
--

LOCK TABLES `xt_tohop_monthi` WRITE;
/*!40000 ALTER TABLE `xt_tohop_monthi` DISABLE KEYS */;
/*!40000 ALTER TABLE `xt_tohop_monthi` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- 1. Tạo bảng users (nếu chưa tồn tại)
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    full_name VARCHAR(100),
    role VARCHAR(20) DEFAULT 'user',
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Admin account
INSERT INTO users (username, password, email, full_name, role, is_active) 
VALUES ('admin', 'admin123', 'admin@xettuyen.edu.vn', 'Administrator', 'admin', true);

-- User accounts
INSERT INTO users (username, password, email, full_name, role, is_active) 
VALUES ('user1', 'user123', 'user1@xettuyen.edu.vn', 'Nguyễn Văn A', 'user', true);

INSERT INTO users (username, password, email, full_name, role, is_active) 
VALUES ('user2', 'pass456', 'user2@xettuyen.edu.vn', 'Trần Thị B', 'user', true);

INSERT INTO users (username, password, email, full_name, role, is_active) 
VALUES ('user3', 'secure789', 'user3@xettuyen.edu.vn', 'Lê Văn C', 'user', true);

ALTER TABLE `xt_diemthixettuyen` 
ADD COLUMN `Loai_Chung_Chi` VARCHAR(50) NULL 
COMMENT 'Loại chứng chỉ ngoại ngữ thí sinh sử dụng (VD: IELTS, TOEIC, VSTEP...)';

ALTER TABLE `xt_thisinhxettuyen25` 
ADD COLUMN `trang_thai_trung_tuyen` VARCHAR(20) NULL DEFAULT 'Chưa xét' 
COMMENT 'Trạng thái trúng tuyển chính thức (VD: Đã trúng tuyển, Chưa xét, Trượt)',
ADD COLUMN `nganh_trung_tuyen` VARCHAR(45) NULL 
COMMENT 'Mã ngành thí sinh trúng tuyển chính thức sau khi lọc ảo';

INSERT INTO `xt_nganh` (`idnganh`, `manganh`, `tennganh`, `n_tohopgoc`, `n_chitieu`, `n_diemsan`) VALUES
(1, '7140114', 'Quản lý giáo dục', 'D01', 40, 17.00),
(2, '7140201', 'Giáo dục Mầm non', 'M01', 200, 20.00),
(3, '7140202', 'Giáo dục Tiểu học', 'C01', 200, 21.00),
(4, '7140205', 'Giáo dục chính trị', 'C01', 10, 23.00),
(5, '7140209', 'Sư phạm Toán học', 'A00', 40, 24.50),
(6, '7140211', 'Sư phạm Vật lý', 'A00', 10, 24.00),
(7, '7140212', 'Sư phạm Hoá học', 'A00', 10, 24.00),
(8, '7140213', 'Sư phạm Sinh học', 'B00', 10, 23.00),
(9, '7140217', 'Sư phạm Ngữ văn', 'C01', 50, 24.00),
(10, '7140218', 'Sư phạm Lịch sử', 'C00', 10, 25.00);
INSERT INTO `xt_tohop_monthi` (`idtohop`, `matohop`, `mon1`, `mon2`, `mon3`, `tentohop`) VALUES
(2, 'A01', 'TO', 'LI', 'N1', 'Toán, Vật lí, Tiếng Anh'),
(5, 'B00', 'TO', 'HO', 'SI', 'Toán, Hóa học, Sinh học'),
(6, 'C00', 'VA', 'SU', 'DI', 'Ngữ văn, Lịch sử, Địa lí'),
(7, 'C03', 'TO', 'VA', 'SU', 'Toán, Lịch sử, Ngữ văn'),
(8, 'C04', 'TO', 'VA', 'DI', 'Toán, Địa lí, Ngữ văn'),
(9, 'C19', 'VA', 'SU', 'GD', 'Văn - Sử - GDCD'),
(10, 'D01', 'TO', 'VA', 'N1', 'Toán, Tiếng Anh, Ngữ văn'),
(11, 'H00', 'VA', 'NK3', 'NK4', 'Ngữ văn, Hình họa, Trang trí'),
(12, 'M01', 'VA', 'NK1', 'NK2', 'Ngữ văn, Kể chuyện - Đọc diễn cảm, Hát - Nhạc'),
(13, 'M02', 'TO', 'NK1', 'NK2', 'Toán, Kể chuyện - Đọc diễn cảm, Hát - Nhạc'),
(14, 'N01', 'VA', 'NK5', 'NK6', 'Ngữ văn, Hát - Nhạc cụ, Xướng âm - Thẩm âm, Tiết tấu');
INSERT INTO `xt_nganh_tohop` (`id`, `manganh`, `matohop`, `th_mon1`, `hsmon1`, `th_mon2`, `hsmon2`, `th_mon3`, `hsmon3`, `tb_keys`) VALUES
(1, '7140114', 'B03', 'TO', 3, 'VA', 3, 'SI', 1, '7140114_B03'),
(2, '7140114', 'C01', 'TO', 3, 'VA', 3, 'LI', 1, '7140114_C01'),
(3, '7140114', 'C02', 'TO', 3, 'VA', 3, 'HO', 1, '7140114_C02'),
(4, '7140114', 'C03', 'TO', 3, 'VA', 3, 'SU', 1, '7140114_C03'),
(5, '7140114', 'C04', 'TO', 3, 'VA', 3, 'DI', 1, '7140114_C04'),
(6, '7140114', 'D01', 'TO', 3, 'VA', 3, 'N1', 1, '7140114_D01'),
(7, '7140114', 'X01', 'TO', 3, 'VA', 3, 'KTPL', 1, '7140114_X01'),
(8, '7140114', 'X02', 'TO', 3, 'VA', 3, 'TI', 1, '7140114_X02'),
(9, '7140114', 'X03', 'TO', 3, 'VA', 3, 'CNCN', 1, '7140114_X03'),
(10, '7140114', 'X04', 'TO', 3, 'VA', 3, 'CNNN', 1, '7140114_X04');
INSERT INTO `xt_diemthixettuyen` (`iddiemthi`, `cccd`, `sobaodanh`, `d_phuongthuc`, `TO`, `LI`, `HO`, `SI`, `SU`, `DI`, `VA`, `N1_THI`, `N1_CC`, `CNCN`, `CNNN`, `TI`, `KTPL`, `NL1`, `NK1`, `NK2`) VALUES
(2, '001207000049', NULL, '0', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, NULL, 0.00, 0.00, 0.00, 0.00, 0.00, 730.00, NULL, NULL),
(3, '001207004846', NULL, '0', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, NULL, 0.00, 0.00, 0.00, 0.00, 0.00, 534.00, NULL, NULL),
(4, '001207005157', NULL, '0', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, NULL, 0.00, 0.00, 0.00, 0.00, 0.00, 533.00, NULL, NULL),
(5, '001207006913', NULL, '0', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, NULL, 10.00, 0.00, 0.00, 0.00, 0.00, 747.00, NULL, NULL),
(6, '001207008593', NULL, '4', 4.38, 6.03, 0.00, 0.00, 0.00, 0.00, 7.59, 7.00, 7.00, 0.00, 0.00, 0.00, 0.00, 772.00, NULL, NULL),
(7, '001207008830', NULL, '3', 5.16, 0.00, 0.00, 0.00, 0.00, 0.00, 7.53, 7.18, 7.18, 0.00, 0.00, 0.00, 0.00, 833.00, NULL, NULL),
(8, '001207009704', NULL, '0', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, NULL, 9.00, 0.00, 0.00, 0.00, 0.00, 794.00, NULL, NULL),
(9, '001207011459', NULL, '0', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, NULL, 9.00, 0.00, 0.00, 0.00, 0.00, 743.00, NULL, NULL),
(10, '001207012341', NULL, '4', 6.07, 0.00, 0.00, 0.00, 8.77, 9.64, 9.31, NULL, 0.00, 0.00, 0.00, 0.00, 0.00, 749.00, NULL, NULL),
(11, '001207012439', NULL, '0', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, NULL, 0.00, 0.00, 0.00, 0.00, 0.00, 960.00, NULL, NULL),
(12, '001207012684', NULL, '0', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, NULL, 0.00, 0.00, 0.00, 0.00, 0.00, 756.00, NULL, NULL);
INSERT INTO `xt_diemcongxettuyen` (`iddiemcong`, `ts_cccd`, `diemTong`, `manganh`, `matohop`, `phuongthuc`, `ghichu`, `dc_keys`, `diemCC`, `diemUtxt`) VALUES
(1, '056307010216', 1.00, '7340101', 'B00', 'PT4', NULL, '056307010216_7340101_B00', NULL, NULL),
(2, '056307010216', 1.00, '7340101', 'X22', 'PT4', NULL, '056307010216_7340101_X22', NULL, NULL),
(3, '056307010216', 1.00, '7340101', 'B02', 'PT4', NULL, '056307010216_7340101_B02', NULL, NULL),
(4, '056307010216', 1.00, '7340101', 'B01', 'PT4', NULL, '056307010216_7340101_B01', NULL, NULL),
(5, '056307010216', 1.00, '7340101', 'A07', 'PT4', NULL, '056307010216_7340101_A07', NULL, NULL),
(6, '056307010216', 1.00, '7340101', 'A06', 'PT4', NULL, '056307010216_7340101_A06', NULL, NULL),
(7, '056307010216', 1.00, '7340101', 'A05', 'PT4', NULL, '056307010216_7340101_A05', NULL, NULL),
(8, '056307010216', 1.00, '7340101', 'A04', 'PT4', NULL, '056307010216_7340101_A04', NULL, NULL),
(9, '056307010216', 1.00, '7340101', 'A03', 'PT4', NULL, '056307010216_7340101_A03', NULL, NULL),
(10, '056307010216', 1.00, '7340101', 'A02', 'PT4', NULL, '056307010216_7340101_A02', NULL, NULL);
INSERT INTO `xt_bangquydoi` (`idqd`, `d_phuongthuc`, `d_tohop`, `d_mon`, `d_diema`, `d_diemb`, `d_diemc`, `d_diemd`, `d_maquydoi`, `d_phanvi`) VALUES
(2, 'DGNL', 'A01', NULL, 998.00, 1018.00, 26.25, 26.75, 'DGNL_A01_2', '2'),
(3, 'DGNL', 'A01', NULL, 984.00, 997.00, 25.75, 26.10, 'DGNL_A01_3', '3'),
(4, 'DGNL', 'A01', NULL, 973.00, 983.00, 25.35, 25.65, 'DGNL_A01_4', '4'),
(5, 'DGNL', 'A01', NULL, 962.00, 972.00, 25.05, 25.25, 'DGNL_A01_5', '5'),
(6, 'DGNL', 'A01', NULL, 954.00, 961.00, 24.85, 25.00, 'DGNL_A01_6', '6'),
(7, 'DGNL', 'A01', NULL, 946.00, 953.00, 24.60, 24.75, 'DGNL_A01_7', '7'),
(8, 'DGNL', 'A01', NULL, 939.00, 945.00, 24.30, 24.50, 'DGNL_A01_8', '8'),
(9, 'DGNL', 'A01', NULL, 932.00, 938.00, 24.25, 24.25, 'DGNL_A01_9', '9'),
(10, 'DGNL', 'A01', NULL, 926.00, 931.00, 24.00, 24.20, 'DGNL_A01_10', '10'),
(11, 'DGNL', 'A01', NULL, 919.00, 925.00, 23.80, 23.95, 'DGNL_A01_11', '11'),
(12, 'DGNL', 'A01', NULL, 913.00, 918.00, 23.55, 23.75, 'DGNL_A01_12', '12');
INSERT INTO `xt_thisinhxettuyen25` 
(`cccd`, `sobaodanh`, `ho`, `ten`, `ngay_sinh`, `dien_thoai`, `password`, `gioi_tinh`, `email`, `noi_sinh`, `updated_at`, `doi_tuong`, `khu_vuc`, `trang_thai_trung_tuyen`, `nganh_trung_tuyen`) 
VALUES
-- Nhóm 1: Thí sinh rớt (duoisan trong bảng nguyện vọng)
('001207000049', 'SGU001', 'Nguyễn Văn', 'An', '2007-01-15', '0901234567', 'hash_pass_1', 'Nam', 'an.nguyen@email.com', 'Hà Nội', '2025-06-01', '01', 'KV1', 'Trượt', NULL),
('001207004846', 'SGU002', 'Trần Thị', 'Bình', '2007-03-22', '0902345678', 'hash_pass_2', 'Nữ', 'binh.tran@email.com', 'Hải Phòng', '2025-06-01', '02', 'KV2', 'Trượt', NULL),
('001207005157', 'SGU003', 'Lê Hoàng', 'Cường', '2007-05-10', '0903456789', 'hash_pass_3', 'Nam', 'cuong.le@email.com', 'Đà Nẵng', '2025-06-01', '00', 'KV3', 'Trượt', NULL),
('001207006913', 'SGU004', 'Phạm Thị', 'Dung', '2007-07-30', '0904567890', 'hash_pass_4', 'Nữ', 'dung.pham@email.com', 'TP.HCM', '2025-06-01', '00', 'KV1', 'Trượt', NULL),

-- Nhóm 2: Thí sinh đậu (yes trong bảng nguyện vọng)
('001207008593', 'SGU005', 'Hoàng Thanh', 'E', '2007-09-05', '0905678901', 'hash_pass_5', 'Nam', 'e.hoang@email.com', 'Cần Thơ', '2025-06-01', '03', 'KV2NT', 'Đã trúng tuyển', '7310401'),
('001207008830', 'SGU006', 'Vũ Thị', 'Giang', '2007-11-12', '0906789012', 'hash_pass_6', 'Nữ', 'giang.vu@email.com', 'Đồng Nai', '2025-06-01', '00', 'KV2', 'Đã trúng tuyển', '7310401'),
('001207009704', 'SGU007', 'Đặng Văn', 'Hải', '2007-12-25', '0907890123', 'hash_pass_7', 'Nam', 'hai.dang@email.com', 'Bình Dương', '2025-06-01', '01', 'KV1', 'Đã trúng tuyển', '7340101'),
('001207012341', 'SGU008', 'Bùi Thị', 'Kim', '2007-02-14', '0908901234', 'hash_pass_8', 'Nữ', 'kim.bui@email.com', 'Long An', '2025-06-01', '00', 'KV3', 'Đã trúng tuyển', '7380101'),
('001207012439', 'SGU009', 'Đỗ Thành', 'Long', '2007-04-18', '0909012345', 'hash_pass_9', 'Nam', 'long.do@email.com', 'Vũng Tàu', '2025-06-01', '04', 'KV2', 'Đã trúng tuyển', '7480201'),

-- Nhóm 3: Thí sinh có điểm thi/điểm cộng nhưng chưa đăng ký nguyện vọng hoặc chưa chạy lọc ảo
('001207011459', 'SGU010', 'Ngô Thị', 'Mai', '2007-08-20', '0910123456', 'hash_pass_10', 'Nữ', 'mai.ngo@email.com', 'Tây Ninh', '2025-06-01', '00', 'KV1', 'Chưa xét', NULL),
('001207012684', 'SGU011', 'Lý Văn', 'Nam', '2007-10-10', '0911234567', 'hash_pass_11', 'Nam', 'nam.ly@email.com', 'Tiền Giang', '2025-06-01', '01', 'KV2', 'Chưa xét', NULL),
('056307010216', 'SGU012', 'Hồ Thu', 'Oanh', '2007-06-05', '0912345678', 'hash_pass_12', 'Nữ', 'oanh.ho@email.com', 'Bến Tre', '2025-06-01', '00', 'KV3', 'Chưa xét', NULL);