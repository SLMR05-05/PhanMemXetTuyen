package com.xettuyen.service;

import com.xettuyen.dao.*;
import com.xettuyen.entity.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * NganhService - Service Layer cho Nganh
 * Minh hoạ cách viết Service cho các entities khác
 */
public class NganhService {
    private static final Logger log = LoggerFactory.getLogger(NganhService.class);
    private NganhDAO nganhDAO = DAOFactory.getNganhDAO();
    private NganhToHopDAO nganhToHopDAO = DAOFactory.getNganhToHopDAO();

    public static class ImportResult {
        public final int inserted;
        public final int updated;
        public final int skipped;

        public ImportResult(int inserted, int updated, int skipped) {
            this.inserted = inserted;
            this.updated = updated;
            this.skipped = skipped;
        }

        public int getTotalProcessed() {
            return inserted + updated;
        }
    }

    /**
     * Lấy thông tin ngành và danh sách tổ hợp môn của ngành đó
     */
    public NganhDetail getNganhDetail(String maNganh) {
        Nganh nganh = nganhDAO.findByMaNganh(maNganh);
        if (nganh == null) {
            return null;
        }

        // Lấy tất cả tổ hợp của ngành này
        List<NganhTohop> allNganhToHop = nganhToHopDAO.findAll(NganhTohop.class);
        List<NganhTohop> nganhToHopList = allNganhToHop.stream()
                .filter(nt -> nt.getMaNganh().equals(maNganh))
                .collect(Collectors.toList());

        return new NganhDetail(nganh, nganhToHopList);
    }

    /**
     * Lưu ngành mới.
     */
    public boolean saveNganh(Nganh nganh) {
        try {
            if (nganh != null) {
                nganhDAO.save(nganh);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Lỗi khi lưu ngành mới: " + e.getMessage());
            return false;
        }
    }

    /**
     * Tạo ngành mới theo từng trường dữ liệu.
     */
    public void createNganh(String maNganh, String tenNganh, int chiTieu) {
        Nganh nganh = new Nganh();
        nganh.setMaNganh(maNganh);
        nganh.setTenNganh(tenNganh);
        nganh.setNChiTieu(chiTieu);
        nganhDAO.save(nganh);
    }

    /**
     * Cập nhật ngành hiện có.
     */
    public boolean updateNganh(Nganh nganh) {
        try {
            if (nganh != null && nganh.getIdNganh() != null) {
                nganhDAO.update(nganh);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật ngành: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa ngành theo ID.
     */
    public boolean deleteNganh(Integer idNganh) {
        try {
            Nganh nganh = nganhDAO.findById(Nganh.class, idNganh);
            if (nganh != null) {
                nganhDAO.delete(nganh);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa ngành: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa một tổ hợp môn của ngành.
     */
    public boolean deleteNganhTohop(Integer id) {
        try {
            NganhTohop toDelete = nganhToHopDAO.findById(NganhTohop.class, id);
            if (toDelete != null) {
                nganhToHopDAO.delete(toDelete);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa tổ hợp ngành: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lấy danh sách ngành với phân trang
     */
    public List<Nganh> getNganhWithPagination(int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;
        return nganhDAO.findAllWithPagination(Nganh.class, offset, pageSize);
    }

    /**
     * Lấy tổng số ngành
     */
    public long getTotalNganh() {
        return nganhDAO.countAll(Nganh.class);
    }

    public ImportResult importNganhToHopFromExcel(String filePath) {
        return importToHopMonFromExcel(filePath);
    }

    public ImportResult importToHopMonFromExcel(String filePath) {
        int inserted = 0;
        int updated = 0;
        int skipped = 0;

        Pattern maTohopPattern = Pattern.compile("^\\s*([A-Za-z0-9]+)\\s*\\(([^)]*)\\)\\s*$");

        try (FileInputStream input = new FileInputStream(filePath);
                Workbook workbook = WorkbookFactory.create(input)) {

            if (workbook.getNumberOfSheets() == 0) {
                return new ImportResult(0, 0, 0);
            }

            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            org.apache.poi.ss.usermodel.FormulaEvaluator evaluator = workbook.getCreationHelper()
                    .createFormulaEvaluator();
            Row header = sheet.getRow(0);
            Map<String, Integer> columns = buildHeaderIndexMap(header, formatter);

            // Run processing in a single transaction managed by NganhToHopDAO to allow
            // batching
            int[] totals = nganhToHopDAO.executeInTransaction(session -> {
                int localInserted = 0;
                int localUpdated = 0;
                int localSkipped = 0;
                int processed = 0;

                java.util.Set<String> processedTohops = new java.util.HashSet<>();
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null || isRowEmpty(row, formatter, evaluator)) {
                        localSkipped++;
                        continue;
                    }

                    int rowNumber = row.getRowNum() + 1;
                    try {
                        String maNganh = readCell(row, columns, formatter, 0, evaluator, "manganh", "ma nganh");
                        String maToHopRaw = readCell(row, columns, formatter, 1, evaluator,
                                "ma_to_hop", "ma_tohop", "ma to hop", "mato hop", "matohop", "ten_to_hop");
                        String tbKeys = readCell(row, columns, formatter, 2, evaluator, "tb_keys", "tbkeys", "tb key");
                        String goc = readCell(row, columns, formatter, 3, evaluator, "goc", "gốc");
                        String doLechValue = readCell(row, columns, formatter, 4, evaluator, "dolech", "do lech",
                                "độ lệch");

                        if (maNganh.isEmpty()) {
                            log.warn("Dòng {}: thiếu MANGANH nên bỏ qua", rowNumber);
                            localSkipped++;
                            continue;
                        }

                        String maTohop = maToHopRaw != null ? maToHopRaw.trim() : "";
                        String thMon1 = null, thMon2 = null, thMon3 = null;
                        Integer hs1 = null, hs2 = null, hs3 = null;

                        // 2. Xử lý bóc tách Regex
                        Matcher m = maTohopPattern.matcher(maTohop);
                        if (m.matches()) {
                            maTohop = m.group(1).trim(); // Lấy mã gốc (VD: B03, X17)
                            String inside = m.group(2).trim(); // Lấy phần trong ngoặc
                            String[] parts = inside.split(",");
                            for (int p = 0; p < parts.length && p < 3; p++) {
                                String part = parts[p].trim();
                                String[] kv = part.split("-");
                                String mon = kv.length > 0 ? kv[0].trim() : null;
                                Integer hs = null;
                                if (kv.length > 1) {
                                    try {
                                        hs = Integer.parseInt(kv[1].trim());
                                    } catch (Exception ex) {
                                        hs = null;
                                    }
                                }
                                if (p == 0) {
                                    thMon1 = mon;
                                    hs1 = hs;
                                } else if (p == 1) {
                                    thMon2 = mon;
                                    hs2 = hs;
                                } else if (p == 2) {
                                    thMon3 = mon;
                                    hs3 = hs;
                                }
                            }
                        } else if (!maTohop.isEmpty()) {
                            maTohop = maTohop.split("\\s+")[0];
                        }

                        if (maTohop != null && !maTohop.isEmpty() && processedTohops.add(maTohop)) {
                            try {
                                // Sử dụng Native Query UPSERT của MySQL để tự động Insert hoặc Update
                                String sqlTohop = "INSERT INTO xt_tohop_monthi (matohop, mon1, mon2, mon3) " +
                                        "VALUES (:maTohop, :mon1, :mon2, :mon3) " +
                                        "ON DUPLICATE KEY UPDATE mon1 = :mon1, mon2 = :mon2, mon3 = :mon3";

                                session.createNativeQuery(sqlTohop)
                                        .setParameter("maTohop", maTohop)
                                        .setParameter("mon1", thMon1)
                                        .setParameter("mon2", thMon2)
                                        .setParameter("mon3", thMon3)
                                        .executeUpdate();
                            } catch (Exception ex) {
                                log.error("Dòng {}: Lỗi khi lưu tổ hợp {} vào bảng tohop_monthi - {}",
                                        rowNumber, maTohop, ex.getMessage());
                            }
                        }
                        Double doLech = parseDoubleOrDefault(doLechValue, 0.0);

                        // 3. Xử lý tb_keys (Fallback an toàn nếu đọc công thức Excel bị lỗi)
                        if (tbKeys == null || tbKeys.trim().isEmpty()) {
                            // Lúc này maTohop đã được chuẩn hóa, ví dụ: 7140114_B03
                            tbKeys = maNganh + "_" + maTohop;
                        }

                        // Build NganhTohop entity for upsert
                        NganhTohop nt = new NganhTohop();
                        nt.setMaNganh(maNganh);
                        nt.setMaTohop(maTohop);
                        nt.setThMon1(thMon1);
                        nt.setHsMon1(hs1);
                        nt.setThMon2(thMon2);
                        nt.setHsMon2(hs2);
                        nt.setThMon3(thMon3);
                        nt.setHsMon3(hs3);
                        nt.setTbKeys(tbKeys.trim());
                        nt.setDoLech(doLech);
                        // Map subject codes into subject-specific integer columns (TO, VA, SI...)
                        mapSubjectToEntity(nt, thMon1, hs1);
                        mapSubjectToEntity(nt, thMon2, hs2);
                        mapSubjectToEntity(nt, thMon3, hs3);
                        nt.setDoLech(doLech);

                        boolean insertedNow = nganhToHopDAO.upsertByTbKeys(session, nt);
                        if (insertedNow)
                            localInserted++;
                        else
                            localUpdated++;

                        // If Gốc column indicates original tohop, update nganh.nTohopGoc
                        if (isGocValue(goc)) {
                            if (maTohop == null || maTohop.isEmpty()) {
                                log.warn("Dòng {}: Gốc = Gốc nhưng MA_TO_HOP rỗng, không cập nhật nTohopgoc",
                                        rowNumber);
                            } else {
                                int affected = nganhDAO.updateTohopGocByMaNganh(session, maNganh, maTohop);
                                if (affected <= 0) {
                                    log.warn("Dòng {}: không tìm thấy ngành {} để cập nhật nTohopgoc", rowNumber,
                                            maNganh);
                                }
                            }
                        }

                    } catch (Exception rowException) {
                        localSkipped++;
                        log.error("Lỗi import dòng {} từ file {}", rowNumber, filePath, rowException);
                    }

                    if (++processed % 50 == 0) {
                        session.flush();
                        session.clear();
                    }
                }

                return new int[] { localInserted, localUpdated, localSkipped };
            });

            if (totals != null && totals.length == 3) {
                inserted = totals[0];
                updated = totals[1];
                skipped = totals[2];
            }

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi đọc file Excel tổ hợp môn: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi import tổ hợp môn: " + e.getMessage(), e);
        }

        return new ImportResult(inserted, updated, skipped);
    }

    public ImportResult importNguongDauVaoFromExcel(String filePath) {
        return importNganhScalarFromExcel(filePath, true);
    }

    public ImportResult importChiTieuFromExcel(String filePath) {
        return importNganhScalarFromExcel(filePath, false);
    }

    private ImportResult importNganhScalarFromExcel(String filePath, boolean updateNguongDauVao) {
        int inserted = 0;
        int updated = 0;
        int skipped = 0;

        try (FileInputStream input = new FileInputStream(filePath);
                Workbook workbook = WorkbookFactory.create(input)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return new ImportResult(0, 0, 0);
            }

            DataFormatter formatter = new DataFormatter();
            org.apache.poi.ss.usermodel.FormulaEvaluator evaluator = workbook.getCreationHelper()
                    .createFormulaEvaluator();

            // 1. TỰ ĐỘNG TÌM DÒNG HEADER (Quét 5 dòng đầu tiên để tìm dòng chứa chữ 'ma
            // nganh', 'ma ctdt' hoặc 'ma xet tuyen')
            Row header = null;
            Map<String, Integer> columns = null;
            int headerRowIndex = 0;

            for (int r = 0; r <= Math.min(5, sheet.getLastRowNum()); r++) {
                Row tempRow = sheet.getRow(r);
                Map<String, Integer> tempCols = buildHeaderIndexMap(tempRow, formatter);

                // BỔ SUNG: "ma xet tuyen", "maxettuyen" để đọc được file Ngưỡng đầu vào
                if (findColumn(tempCols, "manganh", "ma nganh", "mactdt", "ma ctdt", "ma xet tuyen",
                        "maxettuyen") != null) {
                    header = tempRow;
                    columns = tempCols;
                    headerRowIndex = r;
                    break;
                }
            }

            if (header == null || columns == null) {
                throw new RuntimeException(
                        "Không tìm thấy dòng tiêu đề chứa 'Mã CTĐT', 'Mã ngành' hoặc 'Mã xét tuyển' trong file Excel.");
            }

            final Map<String, Integer> finalColumns = columns;
            final int finalHeaderRowIndex = headerRowIndex;

            // 2. Chạy trong một Transaction đồng nhất
            int[] totals = nganhToHopDAO.executeInTransaction(session -> {
                int localUpdated = 0;
                int localSkipped = 0;
                int processed = 0;

                // Bắt đầu đọc từ dòng ngay dưới dòng header
                for (int i = finalHeaderRowIndex + 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);

                    if (row == null || isRowEmpty(row, formatter, evaluator)) {
                        localSkipped++;
                        continue;
                    }

                    // 3. BỔ SUNG: alias "ma xet tuyen", "maxettuyen"
                    String maNganh = readCell(row, finalColumns, formatter, evaluator, "manganh", "ma nganh", "mactdt",
                            "ma ctdt", "ma xet tuyen", "maxettuyen");

                    if (maNganh.isEmpty()) {
                        localSkipped++;
                        continue;
                    }

                    // Sử dụng trực tiếp `session` của transaction để query tránh Detached Entity
                    org.hibernate.query.Query<Nganh> query = session.createQuery("FROM Nganh WHERE maNganh = :ma",
                            Nganh.class);
                    query.setParameter("ma", maNganh);
                    Nganh nganh = query.getResultStream().findFirst().orElse(null);

                    if (nganh == null) {
                        log.warn("Dòng {}: Không tìm thấy mã ngành/CTĐT {} trong cơ sở dữ liệu", row.getRowNum() + 1,
                                maNganh);
                        localSkipped++;
                        continue;
                    }

                    boolean isChanged = false;

                    if (updateNguongDauVao) {
                        String diemSan = readCell(row, finalColumns, formatter, evaluator, "n_diemsan", "diem san",
                                "nguong dau vao", "nguong", "diem chuan dau vao");
                        if (!diemSan.isEmpty()) {
                            Double parsedDiemSan = parseDouble(diemSan);
                            if (parsedDiemSan != null) {
                                nganh.setNDiemSan(parsedDiemSan);
                                isChanged = true;
                            }
                        }
                    } else {
                        String chiTieu = readCell(row, finalColumns, formatter, evaluator, "n_chitieu", "chi tieu",
                                "chitieu", "chi tieu chot", "chitieuchot");
                        if (!chiTieu.isEmpty()) {
                            Integer parsedChiTieu = parseInteger(chiTieu);
                            if (parsedChiTieu != null) {
                                nganh.setNChiTieu(parsedChiTieu);
                                isChanged = true;
                            }
                        }
                    }

                    if (isChanged) {
                        // Sử dụng session.merge() để bám sát Transaction đang mở
                        session.merge(nganh);
                        localUpdated++;
                    } else {
                        localSkipped++;
                    }

                    // Batching flush để tối ưu RAM
                    if (++processed % 50 == 0) {
                        session.flush();
                        session.clear();
                    }
                }
                return new int[] { 0, localUpdated, localSkipped };
            });

            if (totals != null && totals.length == 3) {
                inserted = totals[0];
                updated = totals[1];
                skipped = totals[2];
            }

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi import dữ liệu: " + e.getMessage(), e);
        }

        return new ImportResult(inserted, updated, skipped);
    }

    private Map<String, Integer> buildHeaderIndexMap(Row header, DataFormatter formatter) {
        Map<String, Integer> columns = new LinkedHashMap<>();
        if (header == null) {
            return columns;
        }

        for (int i = header.getFirstCellNum(); i < header.getLastCellNum(); i++) {
            String normalized = normalize(readCell(header, i, formatter));
            if (!normalized.isEmpty()) {
                columns.put(normalized, i);
            }
        }

        return columns;
    }

    private boolean isRowEmpty(Row row, DataFormatter formatter) {
        if (row == null) {
            return true;
        }

        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            if (!readCell(row, i, formatter).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String readCell(Row row, int cellIndex, DataFormatter formatter) {
        if (row == null) {
            return "";
        }
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            return "";
        }
        return formatter.formatCellValue(cell).trim();
    }

    private String readCell(Row row, Map<String, Integer> columns, DataFormatter formatter, String... aliases) {
        Integer index = findColumn(columns, aliases);
        if (index == null) {
            return "";
        }
        return readCell(row, index, formatter);
    }

    private String readCell(Row row, Map<String, Integer> columns, DataFormatter formatter, int fallbackIndex,
            String... aliases) {
        Integer index = findColumn(columns, aliases);
        if (index != null) {
            return readCell(row, index, formatter);
        }
        return readCell(row, fallbackIndex, formatter);
    }

    private boolean isRowEmpty(Row row, DataFormatter formatter,
            org.apache.poi.ss.usermodel.FormulaEvaluator evaluator) {
        if (row == null) {
            return true;
        }

        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            if (!readCell(row, i, formatter, evaluator).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String readCell(Row row, int cellIndex, DataFormatter formatter,
            org.apache.poi.ss.usermodel.FormulaEvaluator evaluator) {
        if (row == null) {
            return "";
        }
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            return "";
        }
        try {
            return formatter.formatCellValue(cell, evaluator).trim();
        } catch (Exception e) {
            return formatter.formatCellValue(cell).trim();
        }
    }

    private String readCell(Row row, Map<String, Integer> columns, DataFormatter formatter,
            org.apache.poi.ss.usermodel.FormulaEvaluator evaluator, String... aliases) {
        Integer index = findColumn(columns, aliases);
        if (index == null) {
            return "";
        }
        return readCell(row, index, formatter, evaluator);
    }

    private String readCell(Row row, Map<String, Integer> columns, DataFormatter formatter, int fallbackIndex,
            org.apache.poi.ss.usermodel.FormulaEvaluator evaluator,
            String... aliases) {
        Integer index = findColumn(columns, aliases);
        if (index != null) {
            return readCell(row, index, formatter, evaluator);
        }
        return readCell(row, fallbackIndex, formatter, evaluator);
    }

    private Integer findColumn(Map<String, Integer> columns, String... aliases) {
        for (String alias : aliases) {
            Integer index = columns.get(normalize(alias));
            if (index != null) {
                return index;
            }
        }
        return null;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replace('Đ', 'D')
                .replace('đ', 'd')
                .toLowerCase(Locale.ROOT)
                .trim();
        return normalized
                .replace("`", "")
                .replace("\"", "")
                .replace("'", "")
                .replace("(", "")
                .replace(")", "")
                .replaceAll("\\s+", "")
                .replaceAll("[^a-z0-9_]", "");
    }

    private boolean isGocValue(String value) {
        return value != null && !value.trim().isEmpty() && normalize(value).contains("goc");
    }

    private Integer parseInteger(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            if (value.contains(".")) {
                return (int) Double.parseDouble(value.trim());
            }
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private Double parseDouble(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            return Double.parseDouble(value.trim().replace(",", "."));
        } catch (Exception e) {
            return null;
        }
    }

    private Double parseDoubleOrDefault(String value, Double defaultValue) {
        Double parsed = parseDouble(value);
        return parsed != null ? parsed : defaultValue;
    }

    private void mapSubjectToEntity(NganhTohop nt, String mon, Integer hs) {
        if (mon == null || mon.trim().isEmpty() || hs == null)
            return;
        String code = mon.replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ROOT);
        switch (code) {
            case "TO":
                nt.setTo(hs);
                break;
            case "VA":
                nt.setVa(hs);
                break;
            case "SI":
                nt.setSi(hs);
                break;
            case "LI":
                nt.setLi(hs);
                break;
            case "HO":
                nt.setHo(hs);
                break;
            case "SU":
                nt.setSu(hs);
                break;
            case "DI":
                nt.setDi(hs);
                break;
            case "TI":
                nt.setTi(hs);
                break;
            case "KTPL":
                nt.setKtpl(hs);
                break;
            case "N1":
                nt.setN1(hs);
                break;
            default:
                // Unknown subject code -> store in KHAC
                nt.setKhac(hs);
                break;
        }
    }

    /**
     * Inner class - chi tiết ngành
     */
    public static class NganhDetail {
        public Nganh nganh;
        public List<NganhTohop> nganhToHopList;

        public NganhDetail(Nganh nganh, List<NganhTohop> nganhToHopList) {
            this.nganh = nganh;
            this.nganhToHopList = nganhToHopList;
        }
    }
}
