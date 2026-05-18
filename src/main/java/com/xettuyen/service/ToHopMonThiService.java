package com.xettuyen.service;

import java.util.List;

import com.xettuyen.dao.DAOFactory;
import com.xettuyen.dao.NganhToHopDAO;
import com.xettuyen.dao.ToHopMonDAO;
import com.xettuyen.entity.TohopMonthi;

public class ToHopMonThiService {

    public static class SearchResult {
        public final List<TohopMonthi> data;
        public final int currentPage;
        public final int totalPages;
        public final long totalItems;

        public SearchResult(List<TohopMonthi> data, int currentPage, int totalPages, long totalItems) {
            this.data = data;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.totalItems = totalItems;
        }
    }

    public static class DeleteResult {
        public final boolean success;
        public final String message;

        public DeleteResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    private ToHopMonDAO dao = DAOFactory.getToHopMonDAO();
    private NganhToHopDAO nganhToHopDAO = DAOFactory.getNganhToHopDAO();

    public List<TohopMonthi> getAll(int page, int size) {
        return dao.getAll(page, size);
    }

    public SearchResult search(String keyword, int page, int size) {
        int safePage = Math.max(1, page);
        int safeSize = Math.max(1, size);
        String safeKeyword = keyword == null ? "" : keyword.trim();

        long totalItems = safeKeyword.isEmpty()
                ? dao.countAll(TohopMonthi.class)
                : dao.countSearchResult(safeKeyword);
        int totalPages = (int) Math.max(1, Math.ceil(totalItems / (double) safeSize));
        int currentPage = Math.min(safePage, totalPages);
        int offset = (currentPage - 1) * safeSize;

        List<TohopMonthi> data = safeKeyword.isEmpty()
                ? dao.findAllWithPagination(TohopMonthi.class, offset, safeSize)
                : dao.searchByKeyword(safeKeyword, offset, safeSize);

        return new SearchResult(data, currentPage, totalPages, totalItems);
    }

    public TohopMonthi getById(int id) {
        return dao.findById(TohopMonthi.class, id);
    }

    public TohopMonthi findByMaTohop(String maTohop) {
        return dao.findByMaTohop(maTohop);
    }

    public void add(TohopMonthi entity) {
        dao.save(entity);
    }

    public void update(TohopMonthi entity) {
        dao.update(entity);
    }

    public void deleteById(int id) {
        TohopMonthi entity = getById(id);
        if (entity != null) {
            dao.delete(entity);
        }
    }

    public DeleteResult deleteWithUsageCheck(int id) {
        TohopMonthi entity = getById(id);
        if (entity == null) {
            return new DeleteResult(false, "Không tìm thấy tổ hợp cần xóa.");
        }

        long mappingCount = nganhToHopDAO.countByMaTohop(entity.getMaTohop());
        if (mappingCount > 0) {
            return new DeleteResult(false,
                    "Tổ hợp đang được sử dụng trong bảng ngành - tổ hợp (" + mappingCount
                            + " bản ghi), không thể xóa.");
        }

        dao.delete(entity);
        return new DeleteResult(true, "Xóa tổ hợp thành công.");
    }
}