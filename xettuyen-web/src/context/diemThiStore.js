import { create } from 'zustand';
import { diemThiService } from '../services/diemThiService';

export const useDiemThiStore = create((set) => ({
    diemThiList: [],
    loading: false,
    error: null,

    fetchMyDiemThi: async () => {
        set({ loading: true, error: null });
        try {
            const data = await diemThiService.getMyDiemThi();
            set({ diemThiList: data, loading: false });
            return data;
        } catch (error) {
            set({
                error: error.message || 'Lỗi khi tải dữ liệu điểm thi',
                loading: false,
            });
        }
    },
}));