package com.xettuyen.service;

import com.xettuyen.entity.DiemCongXettuyen;
import com.xettuyen.entity.DiemThiXettuyen;
import com.xettuyen.entity.NganhTohop;
import com.xettuyen.entity.ThiSinhXettuyen;
import com.xettuyen.util.HibernateUtil;
import com.xettuyen.entity.NguyenVongXettuyen;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * ExcelImportService - Xử lý import dữ liệu từ file Excel (.xlsx)
 * Sử dụng Apache POI để đọc file
 */
public class ExcelImportService {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    private static class DsThiSinhRow {
        private String cccd;
        private String ho;
        private String ten;
        private String ngaySinh;
        private String gioiTinh;
        private String doiTuong;
        private String khuVuc;
        private String noiSinh;
        private Double to;
        private Double va;
        private Double li;
        private Double hoMon;
        private Double si;
        private Double su;
        private Double di;
        private Double n1Thi;
        private Double n1Cc;
        private Double cncn;
        private Double cnnn;
        private Double ti;
        private Double ktpl;
        private Double nl1;
        private Double nk1;
        private Double nk2;
    }

     private static class IeltsRow {
        private String cccd;
        private String chungChi;
        private Double diemQuyDoi;
        private Double diemCong;
    }

    /**
     * Import danh sách thí sinh và điểm thi từ file Excel "Ds thi sinh.xlsx".
     * Dữ liệu sẽ được lưu trong một transaction; nếu có lỗi ở bất kỳ dòng nào thì
     * rollback toàn bộ.
     *
     * @param filePath đường dẫn file Excel
     * @return số dòng dữ liệu đã import thành công
     */
    public int importThiSinhVaDiemThi(String filePath) {
        List<DsThiSinhRow> rows = readDsThiSinhRows(filePath);
        if (rows.isEmpty()) {
            return 0;
        }

        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            int importedRows = 0;
            for (DsThiSinhRow row : rows) {
                ThiSinhXettuyen thiSinh = new ThiSinhXettuyen();
                thiSinh.setCccd(row.cccd);
                thiSinh.setHo(row.ho);
                thiSinh.setTen(row.ten);
                thiSinh.setNgaySinh(row.ngaySinh);
                thiSinh.setGioiTinh(row.gioiTinh);
                thiSinh.setDoiTuong(row.doiTuong);
                thiSinh.setKhuVuc(row.khuVuc);
                thiSinh.setNoiSinh(row.noiSinh);
                session.persist(thiSinh);

                DiemThiXettuyen diemThi = new DiemThiXettuyen();
                diemThi.setCccd(row.cccd);
                diemThi.setTo(row.to);
                diemThi.setVa(row.va);
                diemThi.setLi(row.li);
                diemThi.setHo(row.hoMon);
                diemThi.setSi(row.si);
                diemThi.setSu(row.su);
                diemThi.setDi(row.di);
                diemThi.setN1Thi(row.n1Thi);
                diemThi.setN1Cc(row.n1Cc);
                diemThi.setCncn(row.cncn);
                diemThi.setCnnn(row.cnnn);
                diemThi.setTi(row.ti);
                diemThi.setKtpl(row.ktpl);
                diemThi.setNl1(row.nl1);
                diemThi.setNk1(row.nk1);
                diemThi.setNk2(row.nk2);
                session.persist(diemThi);

                importedRows++;
            }

            transaction.commit();
            return importedRows;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Lỗi khi import Excel, dữ liệu đã được rollback: " + e.getMessage(), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    

    public int importIelts(String filePath) {
        Map<String, IeltsRow> bestByCccd = new LinkedHashMap<>();
        Map<String, Double> bestScoreByCccd = new HashMap<>();

        try (FileInputStream file = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(file)) {

            if (workbook.getNumberOfSheets() == 0) {
                return 0;
            }

            Sheet sheet = workbook.getSheetAt(0);
            Row header = sheet.getRow(0);
            if (header == null) {
                return 0;
            }

            int cccdIdx = findHeaderIndex(header, "cccd", "cmnd");
            int chungChiIdx = findHeaderIndex(header, "chung chi ngoai ngu", "chung chi", "ngoai ngu");
            int diemRawIdx = findHeaderIndex(header, "diem/");
            int diemQuyDoiIdx = findHeaderIndex(header, "diem quy", "diem quy doi");
            int diemCongIdx = findHeaderIndex(header, "diem cong");

            // Fallback theo layout ảnh: TT, CCCD, Chứng chỉ ngoại ngữ, Điểm/, Điểm Quy, Điểm cộng
            if (cccdIdx == -1) cccdIdx = 1;
            if (chungChiIdx == -1) chungChiIdx = 2;
            if (diemRawIdx == -1) diemRawIdx = 3;
            if (diemQuyDoiIdx == -1) diemQuyDoiIdx = 4;
            if (diemCongIdx == -1) diemCongIdx = 5;

            if (cccdIdx < 0 || chungChiIdx < 0 || diemQuyDoiIdx < 0 || diemCongIdx < 0) {
                throw new IllegalArgumentException(
                        "Không xác định được cột trong file IELTS. Vui lòng kiểm tra tiêu đề: TT, CCCD, Chứng chỉ ngoại ngữ, Điểm/, Điểm Quy, Điểm cộng");
            }

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) {
                    continue;
                }

                String cccd = getCellValueAsString(row, cccdIdx);
                if (cccd.isEmpty()) {
                    continue;
                }

                Double diemQuyDoi = getCellValueAsDouble(row, diemQuyDoiIdx);
                Double diemCong = getCellValueAsDouble(row, diemCongIdx);
                String chungChi = getCellValueAsString(row, chungChiIdx);

                double fileRowScore = valueOrZero(diemQuyDoi);
                Double prevBest = bestScoreByCccd.get(cccd);
                if (prevBest == null || fileRowScore > prevBest) {
                    IeltsRow item = new IeltsRow();
                    item.cccd = cccd;
                    item.chungChi = chungChi;
                    item.diemQuyDoi = diemQuyDoi;
                    item.diemCong = diemCong;
                    bestByCccd.put(cccd, item);
                    bestScoreByCccd.put(cccd, fileRowScore);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi đọc file IELTS: " + e.getMessage(), e);
        }

        if (bestByCccd.isEmpty()) {
            return 0;
        }

        Session session = null;
        Transaction transaction = null;
        int updatedCount = 0;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            for (IeltsRow row : bestByCccd.values()) {
                List<DiemThiXettuyen> existingList = session.createQuery(
                                "from DiemThiXettuyen where cccd = :cccd", DiemThiXettuyen.class)
                        .setParameter("cccd", row.cccd)
                        .getResultList();

                if (existingList.isEmpty()) {
                    continue;
                }
                
                double valueA = valueOrZero(row.diemQuyDoi);

                double valueB = existingList.stream()
                    .filter(e -> "4".equals(e.getDPhuongThuc()))
                    .map(DiemThiXettuyen::getN1Thi)
                    .filter(Objects::nonNull)
                    .max(Double::compare)
                    .orElse(0.0);

                double candidateMax = Math.max(valueA, valueB);

                DiemThiXettuyen diemThi = existingList.get(0);
                double currentN1 = valueOrZero(diemThi.getN1Thi());

                if (candidateMax > currentN1) {
                    diemThi.setN1Thi(candidateMax);
                    if (row.chungChi != null && !row.chungChi.isBlank()) {
                        diemThi.setLoaiChungChi(truncate(row.chungChi, 50));
                    }
                    session.merge(diemThi);
                    updatedCount++;
                }

            }

            transaction.commit();
            return updatedCount;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Lỗi khi import IELTS: " + e.getMessage(), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    /**
     * Import danh sách thí sinh từ file Excel
     * Định dạng Excel: [CCCD] [SoBaoDanh] [Ho] [Ten] [NgaySinh] [DienThoai]
     * [GioiTinh] [Email] [NoiSinh] [DoiTuong] [KhuVuc]
     *
     * @param filePath Đường dẫn đến file Excel
     * @return Danh sách thí sinh đã import
     */
    public List<ThiSinhXettuyen> importThiSinh(String filePath) {
        List<DsThiSinhRow> rows = readDsThiSinhRows(filePath);
        List<ThiSinhXettuyen> persisted = new ArrayList<>();

        if (rows.isEmpty())
            return persisted;

        Session session = null;
        Transaction tx = null;

        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();

            for (DsThiSinhRow r : rows) {
                ThiSinhXettuyen thiSinh = new ThiSinhXettuyen();
                thiSinh.setCccd(r.cccd);
                thiSinh.setHo(r.ho);
                thiSinh.setTen(r.ten);
                thiSinh.setNgaySinh(r.ngaySinh);
                thiSinh.setGioiTinh(r.gioiTinh);
                thiSinh.setDoiTuong(r.doiTuong);
                thiSinh.setKhuVuc(r.khuVuc);
                thiSinh.setNoiSinh(r.noiSinh);

                session.persist(thiSinh);
                persisted.add(thiSinh);
            }

            tx.commit();
            System.out.println(
                    "✅ Import thành công (thông tin thí sinh) : " + persisted.size() + " bản ghi từ " + filePath);
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            throw new RuntimeException("Lỗi khi import thí sinh: " + e.getMessage(), e);
        } finally {
            if (session != null)
                session.close();
        }

        return persisted;
    }

    private List<DsThiSinhRow> readDsThiSinhRows(String filePath) {
        List<DsThiSinhRow> rows = new ArrayList<>();

        try (FileInputStream file = new FileInputStream(filePath);
                Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheetAt(0);
            // Tìm header để lấy chỉ mục các cột (đỡ dùng chỉ mục cứng như 35)
            Row header = sheet.getRow(0);
            int noiSinhIdx = -1;
            if (header != null) {
                noiSinhIdx = findHeaderIndex(header, "noi sinh", "noisinh", "noi_sinh");
            }
            // Nếu không tìm thấy header, dùng chỉ mục mặc định (8) — phù hợp format chuẩn
            if (noiSinhIdx == -1) {
                noiSinhIdx = 8;
            }

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) {
                    continue;
                }

                DsThiSinhRow data = new DsThiSinhRow();
                data.cccd = getCellValueAsString(row, 1);
                if (data.cccd == null || data.cccd.trim().isEmpty()) {
                    throw new IllegalArgumentException("Dòng " + (i + 1) + " thiếu CCCD");
                }

                String[] nameParts = splitFullName(getCellValueAsString(row, 2));
                data.ho = nameParts[0];
                data.ten = nameParts[1];
                data.ngaySinh = getCellValueAsString(row, 3);
                data.gioiTinh = getCellValueAsString(row, 4);
                data.doiTuong = getCellValueAsString(row, 5);
                data.khuVuc = getCellValueAsString(row, 6);
                data.noiSinh = getCellValueAsString(row, noiSinhIdx);

                data.to = getCellValueAsDouble(row, 7);
                data.va = getCellValueAsDouble(row, 8);
                data.li = getCellValueAsDouble(row, 9);
                data.hoMon = getCellValueAsDouble(row, 10);
                data.si = getCellValueAsDouble(row, 11);
                data.su = getCellValueAsDouble(row, 12);
                data.di = getCellValueAsDouble(row, 13);
                data.n1Thi = getCellValueAsDouble(row, 15);
                data.n1Cc = data.n1Thi;
                data.ktpl = getCellValueAsDouble(row, 17);
                data.ti = getCellValueAsDouble(row, 18);
                data.cncn = getCellValueAsDouble(row, 19);
                data.cnnn = getCellValueAsDouble(row, 20);
                data.nl1 = getCellValueAsDouble(row, 32);
                data.nk1 = getCellValueAsDouble(row, 22);
                data.nk2 = getCellValueAsDouble(row, 23);

                rows.add(data);
            }

            return rows;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage(), e);
        }
    }

    private String[] splitFullName(String fullName) {
        String normalized = fullName == null ? "" : fullName.trim().replaceAll("\\s+", " ");
        if (normalized.isEmpty()) {
            return new String[] { "", "" };
        }

        int lastSpaceIndex = normalized.lastIndexOf(' ');
        if (lastSpaceIndex < 0) {
            return new String[] { "", normalized };
        }

        return new String[] {
                normalized.substring(0, lastSpaceIndex).trim(),
                normalized.substring(lastSpaceIndex + 1).trim()
        };
    }
    
    /**
     * Import danh sách điểm thi từ file Excel
     * Định dạng Excel: [CCCD] [SoBaoDanh] [Toan] [Ly] [Hoa] [Sinh] [SuAn] [DiaLi]
     * [VanAn] [N1-Thi] [CNCN] [CNNN] [TI] [KTPL]
     *
     * @param filePath Đường dẫn đến file Excel
     * @return Danh sách điểm thi đã import
     */
    public java.util.List<com.xettuyen.entity.DiemThiXettuyen> importDiemThi(String filePath) {
        List<com.xettuyen.entity.DiemThiXettuyen> diemThiList = new ArrayList<>();

        try (FileInputStream file = new FileInputStream(filePath);
                Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = 0;

            // Bỏ qua dòng header (dòng 0)
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);

                if (row == null || isRowEmpty(row)) {
                    continue;
                }

                try {
                    com.xettuyen.entity.DiemThiXettuyen diemThi = new com.xettuyen.entity.DiemThiXettuyen();

                    diemThi.setIdDiemThi(getCellValueAsInteger(row, 0));
                    diemThi.setCccd(getCellValueAsString(row, 1));

                    // Các môn thi (kiểu numeric - Double)
                    diemThi.setTo(getCellValueAsDouble(row, 7)); // Toán
                    diemThi.setVa(getCellValueAsDouble(row, 8)); // Văn
                    diemThi.setLi(getCellValueAsDouble(row, 9)); // Lý
                    diemThi.setHo(getCellValueAsDouble(row, 10)); // Hoá
                    diemThi.setSi(getCellValueAsDouble(row, 11)); // Sinh
                    diemThi.setSu(getCellValueAsDouble(row, 12)); // Sử
                    diemThi.setDi(getCellValueAsDouble(row, 13)); // Địa

                    // Các cột khác
                    diemThi.setN1Thi(getCellValueAsDouble(row, 15)); // N1-Thi
                    // diemThi.setLoaiChungChi(getCellValueAsString(row, 16));
                    diemThi.setCncn(getCellValueAsDouble(row, 19)); // CNCN
                    diemThi.setCnnn(getCellValueAsDouble(row, 20)); // CNNN
                    diemThi.setTi(getCellValueAsDouble(row, 18)); // TI
                    diemThi.setKtpl(getCellValueAsDouble(row, 17)); // KTPL

                    diemThi.setNk1(getCellValueAsDouble(row, 22));
                    diemThi.setNk2(getCellValueAsDouble(row, 23));
                    diemThi.setDPhuongThuc("4");
                    diemThiList.add(diemThi);
                    rowCount++;

                } catch (Exception e) {
                    System.err.println("⚠️ Lỗi khi xử lý dòng " + (i + 1) + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.println("✅ Import thành công " + rowCount + " bản ghi điểm thi từ " + filePath);

        } catch (IOException e) {
            System.err.println("❌ Lỗi khi đọc file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Lỗi không xác định khi import điểm thi: " + e.getMessage());
            e.printStackTrace();
        }

        return diemThiList;
    }

    public int importDiemCongUuTien(String filePath) {
        // Lớp chứa dữ liệu tạm thời
        class UuTienRow {
            String cccd;
            String maMon;
            Double diemCoMon;
            Double diemKhongMon;
        }

        Map<String, UuTienRow> dataMap = new LinkedHashMap<>();

        try (FileInputStream file = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(file)) {

            // ĐỌC SHEET 0 (ds thi sinh) - Vì có cột "Mã môn" (N1, SU, DI...) cực kỳ chuẩn xác
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                String cccd = getCellValueAsString(row, 1); // Cột B: CCCD
                if (cccd == null || cccd.isEmpty()) continue;

                UuTienRow ut = new UuTienRow();
                ut.cccd = cccd;
                ut.maMon = getCellValueAsString(row, 4); // Cột E (Index 4): Mã môn
                
                // Cột 7 (Index 6) và Cột 8 (Index 7) theo yêu cầu của bạn
                ut.diemCoMon = getCellValueAsDouble(row, 6); 
                ut.diemKhongMon = getCellValueAsDouble(row, 7); 

                dataMap.put(cccd, ut);
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi đọc file Excel Điểm Ưu Tiên: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }

        if (dataMap.isEmpty()) return 0;

        Session session = null;
        Transaction transaction = null;
        int updatedCount = 0;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            // 1. TẢI TẤT CẢ TỔ HỢP LÊN RAM (Duyệt siêu nhanh)
            List<NganhTohop> allNganhTohop = session.createQuery("FROM NganhTohop", NganhTohop.class).getResultList();

            int count = 0;
            for (UuTienRow rowData : dataMap.values()) {
                
                // 2. Tải toàn bộ Điểm Cộng của 1 thí sinh lên RAM
                List<DiemCongXettuyen> existingDcList = session.createQuery(
                        "FROM DiemCongXettuyen WHERE tsCccd = :cccd", DiemCongXettuyen.class)
                        .setParameter("cccd", rowData.cccd)
                        .getResultList();

                Map<String, DiemCongXettuyen> dcMap = new HashMap<>();
                for (DiemCongXettuyen dc : existingDcList) {
                    dcMap.put(dc.getDcKeys(), dc);
                }

                // 3. Quét TẤT CẢ ngành - tổ hợp để tính điểm UTXT
                for (NganhTohop nt : allNganhTohop) {
                    String maNganh = nt.getMaNganh() != null ? nt.getMaNganh() : "";
                    String maTohop = nt.getMaTohop() != null ? nt.getMaTohop() : "";
                    if (maNganh.isEmpty() || maTohop.isEmpty()) continue;

                    String dcKey = rowData.cccd + "_" + maNganh + "_" + maTohop;

                    // KIỂM TRA: Tổ hợp này có chứa mã môn thi đạt giải không?
                    boolean hasMon = false;
                    String maMon = rowData.maMon != null ? rowData.maMon.trim() : "";
                    if (!maMon.isEmpty()) {
                        if (nt.getThMon1() != null && nt.getThMon1().equalsIgnoreCase(maMon)) hasMon = true;
                        if (nt.getThMon2() != null && nt.getThMon2().equalsIgnoreCase(maMon)) hasMon = true;
                        if (nt.getThMon3() != null && nt.getThMon3().equalsIgnoreCase(maMon)) hasMon = true;
                    }

                    // LOGIC CHÍNH: Lấy Cột 7 nếu có môn, Cột 8 nếu không có
                    double diemUtxt = hasMon ? rowData.diemCoMon : rowData.diemKhongMon;

                    DiemCongXettuyen dc = dcMap.get(dcKey);

                    if (dc != null) {
                        // Nếu đã tồn tại -> Cập nhật utxt và điểm tổng
                        dc.setDiemUtxt(diemUtxt);
                        double dCc = dc.getDiemCc() != null ? dc.getDiemCc() : 0.0;
                        double tong = dCc + diemUtxt;
                        dc.setDiemTong(tong > 3.0 ? 3.0 : tong);
                        session.merge(dc);
                    } else {
                        // Chưa có -> Chỉ tạo mới khi điểm Ưu tiên > 0 (Đỡ rác database)
                        if (diemUtxt > 0) {
                            DiemCongXettuyen newDc = new DiemCongXettuyen();
                            newDc.setTsCccd(rowData.cccd);
                            newDc.setMaNganh(maNganh);
                            newDc.setMaTohop(maTohop);
                            newDc.setPhuongThuc("4");
                            newDc.setDiemCc(0.0);
                            newDc.setDiemUtxt(diemUtxt);
                            newDc.setDiemTong(diemUtxt > 3.0 ? 3.0 : diemUtxt);
                            newDc.setDcKeys(dcKey);
                            newDc.setGhiChu("Cập nhật UTXT Giải HSG");
                            session.persist(newDc);
                        }
                    }
                }

                updatedCount++;
                count++;
                
                // Batch flush xả bộ nhớ định kỳ
                if (count % 50 == 0) {
                    session.flush();
                    session.clear();
                }
            }

            transaction.commit();
            System.out.println("✅ Import thành công " + updatedCount + " thí sinh có Điểm Ưu Tiên UTXT");
            return updatedCount;
            
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Lỗi import UTXT: " + e.getMessage(), e);
        } finally {
            if (session != null) session.close();
        }
    }


    public int importDiemCongCC(String filePath) {
        class DiemCongRow {
            String cccd;
            Double diemCong;
        }

        Map<String, DiemCongRow> dataMap = new LinkedHashMap<>();

        try (FileInputStream file = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(file)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                String cccd = getCellValueAsString(row, 1); // Cột B (Index 1): CCCD
                if (cccd == null || cccd.isEmpty()) continue;

                // "row 6" trong Excel chính là Cột F (Index 5)
                Double diemCongFile = getCellValueAsDouble(row, 5); 

                if (diemCongFile == null || diemCongFile == 0) continue;

                // Nếu 1 thí sinh có nhiều dòng, lấy điểm cộng cao nhất
                DiemCongRow existing = dataMap.get(cccd);
                if (existing == null || diemCongFile > existing.diemCong) {
                    DiemCongRow dcRow = new DiemCongRow();
                    dcRow.cccd = cccd;
                    dcRow.diemCong = diemCongFile;
                    dataMap.put(cccd, dcRow);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi đọc file Excel Điểm Cộng CC: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }

        if (dataMap.isEmpty()) return 0;

        Session session = null;
        Transaction transaction = null;
        int updatedCount = 0;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            // 1. TẢI TẤT CẢ TỔ HỢP KHÔNG CÓ MÔN N1 LÊN RAM
            List<Object[]> listNganhKhongN1 = session.createQuery(
                "SELECT n.maNganh, n.maTohop FROM NganhTohop n WHERE n.n1 = 0 OR n.n1 IS NULL", Object[].class)
                .getResultList();

            int count = 0;
            for (DiemCongRow rowData : dataMap.values()) {
                
                // 2. Tải toàn bộ Điểm Cộng hiện tại của 1 thí sinh lên RAM
                List<DiemCongXettuyen> existingDcList = session.createQuery(
                        "FROM DiemCongXettuyen WHERE tsCccd = :cccd", DiemCongXettuyen.class)
                        .setParameter("cccd", rowData.cccd)
                        .getResultList();

                Map<String, DiemCongXettuyen> dcMap = new HashMap<>();
                for (DiemCongXettuyen dc : existingDcList) {
                    dcMap.put(dc.getDcKeys(), dc);
                }

                // 3. Cập nhật diemCC cho các ngành không có N1
                for (Object[] nt : listNganhKhongN1) {
                    String maNganh = nt[0] != null ? nt[0].toString() : "";
                    String maTohop = nt[1] != null ? nt[1].toString() : "";
                    if (maNganh.isEmpty() || maTohop.isEmpty()) continue;

                    String dcKey = rowData.cccd + "_" + maNganh + "_" + maTohop;

                    DiemCongXettuyen dc = dcMap.get(dcKey);

                    if (dc != null) {
                        // Đã có -> Cập nhật diemCC và tính lại điểm tổng
                        dc.setDiemCc(rowData.diemCong);
                        double dUtxt = dc.getDiemUtxt() != null ? dc.getDiemUtxt() : 0.0;
                        double tong = rowData.diemCong + dUtxt;
                        dc.setDiemTong(tong > 3.0 ? 3.0 : tong);
                        session.merge(dc);
                    } else {
                        // Chưa có -> Tạo mới
                        DiemCongXettuyen newDc = new DiemCongXettuyen();
                        newDc.setTsCccd(rowData.cccd);
                        newDc.setMaNganh(maNganh);
                        newDc.setMaTohop(maTohop);
                        newDc.setPhuongThuc("4");
                        newDc.setDiemCc(rowData.diemCong);
                        newDc.setDiemUtxt(0.0);
                        newDc.setDiemTong(rowData.diemCong > 3.0 ? 3.0 : rowData.diemCong);
                        newDc.setDcKeys(dcKey);
                        newDc.setGhiChu("Cập nhật Điểm cộng Chứng chỉ (CC)");
                        session.persist(newDc);
                    }
                }

                updatedCount++;
                count++;
                
                // Batch flush xả bộ nhớ định kỳ
                if (count % 50 == 0) {
                    session.flush();
                    session.clear();
                }
            }

            transaction.commit();
            System.out.println("✅ Import thành công " + updatedCount + " thí sinh có Điểm Cộng CC");
            return updatedCount;
            
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Lỗi import Điểm Cộng CC: " + e.getMessage(), e);
        } finally {
            if (session != null) session.close();
        }
    }
    /**
     * Import điểm DGNL và VSAT từ cùng file Excel.
     * Sheet 1: VSAT, Sheet 2: DGNL.
     * VSAT được gom theo CCCD và map TENMONTHI/MAMONTHI sang đúng cột DB.
     * DGNL được lưu vào NL1.
     */
    public List<DiemThiXettuyen> importDGNLvaVSAT(String filePath) {
        Map<String, DiemThiXettuyen> result = new LinkedHashMap<>();

        try (FileInputStream file = new FileInputStream(filePath);
                Workbook workbook = WorkbookFactory.create(file)) {


            if (workbook.getNumberOfSheets() > 1) {
                importDgnlSheet(workbook.getSheetAt(1), result);
            }

            System.out.println("✅ Import DGNL & VSAT: đọc được " + result.size() + " thí sinh từ " + filePath);

        } catch (IOException e) {
            System.err.println("❌ Lỗi khi đọc file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Lỗi không xác định khi import DGNL/VSAT: " + e.getMessage());
            e.printStackTrace();
        }

        return new ArrayList<>(result.values());
    }


    private void importDgnlSheet(Sheet sheet, Map<String, DiemThiXettuyen> result) {
        if (sheet == null || sheet.getPhysicalNumberOfRows() == 0)
            return;

        Row header = sheet.getRow(0);
        if (header == null)
            return;

        int cccdIdx = findHeaderIndex(header, "cmnd", "cccd");
        int diemIdx = findHeaderIndex(header, "diem");

        if (cccdIdx == -1)
            cccdIdx = 1;
        if (diemIdx == -1)
            diemIdx = 8;

        // Multiple DGNL rows per candidate may exist across sessions; keep the highest
        // DGNL value
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            if (row == null || isRowEmpty(row))
                continue;

            String cccd = getCellValueAsString(row, cccdIdx);
            if (cccd.isEmpty())
                continue;

            Double diem = getCellValueAsDouble(row, diemIdx);
            if (diem == null)
                continue;

            DiemThiXettuyen diemThi = result.computeIfAbsent(cccd, this::loadOrCreateDiemThiByCccd);

            if (diemThi.getDPhuongThuc() == null || diemThi.getDPhuongThuc().isBlank()) {
                diemThi.setDPhuongThuc("2");
            }

            Double current = diemThi.getNl1();
            if (current == null || diem > current) {
                diemThi.setNl1(diem);
            }
        }
    }

    private DiemThiXettuyen loadOrCreateDiemThiByCccd(String cccd) {
        DiemThiXettuyen existing = findDiemThiByCccd(cccd);
        if (existing != null) {
            return existing;
        }

        DiemThiXettuyen item = new DiemThiXettuyen();
        item.setCccd(cccd);
        return item;
    }

    private DiemThiXettuyen findDiemThiByCccd(String cccd) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            List<DiemThiXettuyen> list = session.createQuery(
                    "FROM DiemThiXettuyen WHERE cccd = :cccd", DiemThiXettuyen.class)
                    .setParameter("cccd", cccd)
                    .setMaxResults(1)
                    .list();
            return list.isEmpty() ? null : list.get(0);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    private void applyScoreToDiemThi(DiemThiXettuyen diemThi, String tenMon, String maMon, Double diem) {
        if (diemThi == null || diem == null)
            return;

        String subject = normalizeSubjectName(tenMon, maMon);
        if (subject.isEmpty())
            return;

        if (matchesAny(subject, "toan", "toan hoc", "math")) {
            diemThi.setTo(diem);
            return;
        }
        if (matchesAny(subject, "ngu van", "van", "van hoc", "literature")) {
            diemThi.setVa(diem);
            return;
        }
        if (matchesAny(subject, "vat ly", "ly", "physics")) {
            diemThi.setLi(diem);
            return;
        }
        if (matchesAny(subject, "hoa hoc", "hoa", "chemistry")) {
            diemThi.setHo(diem);
            return;
        }
        if (matchesAny(subject, "sinh hoc", "sinh", "biology")) {
            diemThi.setSi(diem);
            return;
        }
        if (matchesAny(subject, "lich su", "su", "history")) {
            diemThi.setSu(diem);
            return;
        }
        if (matchesAny(subject, "dia ly", "dia", "geography")) {
            diemThi.setDi(diem);
            return;
        }
        if (matchesAny(subject, "tieng anh", "anh", "english")) {
            if (diemThi.getN1Thi() == null) {
                diemThi.setN1Thi(diem);
                diemThi.setN1Cc(diem);
            } else if (diemThi.getN1Cc() == null) {
                diemThi.setN1Cc(diem);
            }
            return;
        }
        if (matchesAny(subject, "tin hoc", "tin", "informatics")) {
            diemThi.setTi(diem);
            return;
        }
        if (matchesAny(subject, "cong nghe cn", "cong nghe cong nghiep", "cncn")) {
            diemThi.setCncn(diem);
            return;
        }
        if (matchesAny(subject, "cong nghe nn", "cong nghe nong nghiep", "cnnn")) {
            diemThi.setCnnn(diem);
            return;
        }
        if (matchesAny(subject, "kt phap luat", "gdkpl", "ktpl")) {
            diemThi.setKtpl(diem);
            return;
        }
        if (matchesAny(subject, "nang khieu 1", "nk1")) {
            diemThi.setNk1(diem);
            return;
        }
        if (matchesAny(subject, "nang khieu 2", "nk2")) {
            diemThi.setNk2(diem);
            return;
        }

        if (diemThi.getLoaiChungChi() == null || diemThi.getLoaiChungChi().isBlank()) {
            diemThi.setLoaiChungChi(truncate(tenMon, 50));
        }
    }

    private int findHeaderIndex(Row header, String... keywords) {
        if (header == null)
            return -1;
        int first = header.getFirstCellNum();
        int last = header.getLastCellNum();
        for (int i = first; i < last; i++) {
            String value = normalizeText(getCellValueAsString(header, i));
            for (String keyword : keywords) {
                if (!keyword.isBlank() && value.contains(normalizeText(keyword))) {
                    return i;
                }
            }
        }
        return -1;
    }

    private String normalizeSubjectName(String tenMon, String maMon) {
        String subject = normalizeText(tenMon);
        if (subject.isEmpty())
            subject = normalizeText(maMon);
        return subject;
    }

    private String normalizeText(String text) {
        if (text == null)
            return "";
        return java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[\\p{Punct}]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private boolean matchesAny(String value, String... candidates) {
        String normalized = normalizeText(value);
        for (String candidate : candidates) {
            String normalizedCandidate = normalizeText(candidate);
            if (!normalizedCandidate.isEmpty()
                    && (normalized.equals(normalizedCandidate) || normalized.contains(normalizedCandidate))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Import danh sách nguyện vọng từ file Excel
     * Định dạng Excel: [CCCD] [MaNganh] [ThuTu] [PhuongThuc] [Ghi Chu]
     *
     * @param filePath Đường dẫn đến file Excel
     * @return Danh sách nguyện vọng đã import
     */
    public List<NguyenVongXettuyen> importNguyenVong(String filePath) {
        List<NguyenVongXettuyen> nguyenVongList = new ArrayList<>();
        try (FileInputStream file = new FileInputStream(filePath); Workbook workbook = new XSSFWorkbook(file)) {
            // ✅ Chỉ lấy 2 sheet cần thiết theo tên
            String[] sheetNames = { "Sheet1", "Sheet2" };
            for (String sheetName : sheetNames) {
                Sheet sheet = workbook.getSheet(sheetName);
                if (sheet == null) {
                    System.err.println("⚠️ Không tìm thấy sheet: " + sheetName + " — bỏ qua.");
                    continue;
                }
                System.out.println("📄 Đang xử lý sheet: " + sheetName);
                int rowCount = 0;
                for (int i = 5; i < sheet.getPhysicalNumberOfRows(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null || isRowEmpty(row))
                        continue;
                    String firstCell = getCellValueAsString(row, 0);
                    if (firstCell.contains("Tổng") || firstCell.contains("Cộng"))
                        break;
                    try {
                        NguyenVongXettuyen nguyenVong = new NguyenVongXettuyen();
                        String cccd = getCellValueAsString(row, 1);
                        String maNganh = getCellValueAsString(row, 5);
                        Integer nvTt = getCellValueAsInteger(row, 2);
                        String nvTuyenThang = getCellValueAsString(row, 7);
                        if (cccd.isEmpty() || maNganh.isEmpty() || nvTt == null || nvTt == 0) {
                            System.err.println(
                                    "⚠️ [" + sheetName + "] Bỏ qua dòng " + (i + 1) + ": thiếu dữ liệu bắt buộc");
                            continue;
                        }
                        nguyenVong.setNnCccd(cccd);
                        nguyenVong.setNvMaNganh(maNganh);
                        nguyenVong.setNvTt(nvTt);
                        nguyenVong.setDiemThxt(null);
                        nguyenVong.setDiemUtqd(null);
                        nguyenVong.setDiemCong(null);
                        nguyenVong.setDiemXettuyen(null);
                        nguyenVong.setNvKetqua("Chờ xét");
                        nguyenVong.setNvKeys(cccd + "_" + maNganh + "_" + (nvTuyenThang.isEmpty() ? "PT4" : "PT1"));
                        nguyenVong.setTtPhuongThuc(nvTuyenThang.isEmpty() ? null : "PT1");
                        nguyenVong.setTtThm(null);
                        nguyenVongList.add(nguyenVong);
                        rowCount++;
                    } catch (Exception e) {
                        System.err.println("⚠️ [" + sheetName + "] Lỗi tại dòng " + (i + 1) + ": " + e.getMessage());
                    }
                }
                System.out.println("✅ [" + sheetName + "] Đọc được " + rowCount + " nguyện vọng.");
            }
            System.out.println("🎯 Tổng cộng: " + nguyenVongList.size() + " nguyện vọng từ tất cả sheet.");
        } catch (IOException e) {
            System.err.println("❌ Lỗi khi đọc file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Lỗi không xác định khi import nguyện vọng: " + e.getMessage());
            e.printStackTrace();
        }
        return nguyenVongList;
    }

    /**
     * Kiểm tra xem row có toàn bộ cell trống hay không
     */
    private boolean isRowEmpty(Row row) {
        if (row == null)
            return true;

        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    /**
     * Lấy giá trị cell dưới dạng String an toàn
     */
    private String getCellValueAsString(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);

        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getRichStringCellValue().getString().trim();
            case NUMERIC:
                // Nếu là số, chuyển về string
                if (DateUtil.isCellDateFormatted(cell)) {
                    return dateFormat.format(cell.getDateCellValue());
                } else {
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    /**
     * Lấy giá trị cell dưới dạng Double an toàn
     */
    private Double getCellValueAsDouble(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);

        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }

        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return cell.getNumericCellValue();
                case STRING:
                    String value = cell.getStringCellValue().trim();
                    return value.isEmpty() ? 0.0 : Double.parseDouble(value);
                case BOOLEAN:
                    return cell.getBooleanCellValue() ? 1.0 : 0.0;
                default:
                    return 0.0;
            }
        } catch (NumberFormatException e) {
            System.err.println("⚠️ Không thể chuyển '" + cell + "' sang kiểu Double, sử dụng giá trị mặc định 0.0");
            return 0.0;
        }
    }

    /**
     * Lấy giá trị cell dưới dạng Integer an toàn
     */
    private Integer getCellValueAsInteger(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);

        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return 0;
        }

        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return (int) cell.getNumericCellValue();
                case STRING:
                    String value = cell.getStringCellValue().trim();
                    return value.isEmpty() ? 0 : Integer.parseInt(value);
                case BOOLEAN:
                    return cell.getBooleanCellValue() ? 1 : 0;
                default:
                    return 0;
            }
        } catch (NumberFormatException e) {
            System.err.println("⚠️ Không thể chuyển '" + cell + "' sang kiểu Integer, sử dụng giá trị mặc định 0");
            return 0;
        }
    }
     private double valueOrZero(Double value) {
        return value == null ? 0.0 : value;
    }

    private String truncate(String s, int maxLen) {
        if (s == null) return null;
        String t = s.trim();
        if (t.length() <= maxLen) return t;
        return t.substring(0, maxLen);
    }    

}
