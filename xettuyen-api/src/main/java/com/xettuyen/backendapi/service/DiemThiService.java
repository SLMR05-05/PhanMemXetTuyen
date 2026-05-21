package com.xettuyen.backendapi.service;

import com.xettuyen.backendapi.dto.DiemThiDTO;
import com.xettuyen.backendapi.repository.DiemThiRepository;
import com.xettuyen.entity.DiemThiXettuyen;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DiemThiService {

    private final DiemThiRepository diemThiRepository;

    public DiemThiService(DiemThiRepository diemThiRepository) {
        this.diemThiRepository = diemThiRepository;
    }

    public List<DiemThiDTO> getMyDiemThi(Authentication authentication) {
        String cccd = authentication.getName(); // Lấy CCCD từ Token
        Optional<DiemThiXettuyen> diemThiOpt = diemThiRepository.findByCccd(cccd);

        List<DiemThiDTO> danhSachDiem = new ArrayList<>();

        if (diemThiOpt.isPresent()) {
            DiemThiXettuyen dt = diemThiOpt.get();

            // Nhóm điểm THPT Quốc Gia
            addDiemToList(danhSachDiem, "Toán học", dt.getTo(), "THPT Quốc gia");
            addDiemToList(danhSachDiem, "Ngữ văn", dt.getVa(), "THPT Quốc gia");
            addDiemToList(danhSachDiem, "Ngoại ngữ", dt.getN1Cc(), "THPT Quốc gia"); // Lấy điểm cao nhất giữa thi và quy đổi
            addDiemToList(danhSachDiem, "Vật lý", dt.getLi(), "THPT Quốc gia");
            addDiemToList(danhSachDiem, "Hóa học", dt.getHo(), "THPT Quốc gia");
            addDiemToList(danhSachDiem, "Sinh học", dt.getSi(), "THPT Quốc gia");
            addDiemToList(danhSachDiem, "Lịch sử", dt.getSu(), "THPT Quốc gia");
            addDiemToList(danhSachDiem, "Địa lý", dt.getDi(), "THPT Quốc gia");
            addDiemToList(danhSachDiem, "GDCD / KTPL", dt.getKtpl(), "THPT Quốc gia");
            addDiemToList(danhSachDiem, "Tin học", dt.getTi(), "THPT Quốc gia");
            addDiemToList(danhSachDiem, "Công nghệ Công nghiệp", dt.getCncn(), "THPT Quốc gia");
            addDiemToList(danhSachDiem, "Công nghệ Nông nghiệp", dt.getCnnn(), "THPT Quốc gia");

            // Nhóm điểm Đánh giá năng lực
            addDiemToList(danhSachDiem, "Bài thi tổng hợp ĐGNL", dt.getNl1(), "ĐGNL ĐHQG");

            // Nhóm điểm Năng khiếu
            addDiemToList(danhSachDiem, "Năng khiếu 1", dt.getNk1(), "Năng khiếu");
            addDiemToList(danhSachDiem, "Năng khiếu 2", dt.getNk2(), "Năng khiếu");
        }

        return danhSachDiem;
    }

    // Hàm hỗ trợ: Chỉ add vào List nếu sinh viên có thi môn đó (điểm != null)
    private void addDiemToList(List<DiemThiDTO> list, String monThi, Double diem, String kyThi) {
        if (diem != null) {
            list.add(new DiemThiDTO(monThi, diem, kyThi));
        }
    }
}