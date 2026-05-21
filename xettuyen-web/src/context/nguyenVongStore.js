import { create } from 'zustand';
import { nguyenVongService } from '../services/nguyenVongService';

export const useNguyenVongStore = create((set) => ({
    nguyenVongs: [],
    loading: false,
    error: null,

    fetchNguyenVongs: async () => {
        set({ loading: true, error: null });
        try {
            const data = await nguyenVongService.getMyNguyenVong();
            set({ nguyenVongs: data, loading: false });
            return data;
        } catch (error) {
            set({
                error: error.message || 'Lấy danh sách nguyện vọng thất bại',
                loading: false,
            });
            throw error;
        }
    },

    addNguyenVong: async (maNganh, thuTuNguyenVong) => {
        set({ loading: true, error: null });
        try {
            const newNguyenVong = await nguyenVongService.createNguyenVong(
                maNganh,
                thuTuNguyenVong
            );
            set((state) => ({
                nguyenVongs: [...state.nguyenVongs, newNguyenVong],
                loading: false,
            }));
            return newNguyenVong;
        } catch (error) {
            set({
                error: error.message || 'Tạo nguyện vọng thất bại',
                loading: false,
            });
            throw error;
        }
    },

    removeNguyenVong: async (idNv) => {
        set({ loading: true, error: null });
        try {
            await nguyenVongService.deleteNguyenVong(idNv);
            set((state) => ({
                nguyenVongs: state.nguyenVongs.filter((nv) => nv.idNv !== idNv),
                loading: false,
            }));
            return true;
        } catch (error) {
            set({
                error: error.message || 'Xóa nguyện vọng thất bại',
                loading: false,
            });
            throw error;
        }
    },

    swapNguyenVong: async (id1, id2) => {
        set({ loading: true, error: null });
        try {
            // API trả về danh sách mới nhất đã được sắp xếp
            const updatedList = await nguyenVongService.swapNguyenVong(id1, id2);
            set({
                nguyenVongs: updatedList,
                loading: false,
            });
            return true;
        } catch (error) {
            set({
                error: error.message || 'Hoán đổi nguyện vọng thất bại',
                loading: false,
            });
            throw error;
        }
    },

    removeNguyenVong: async (idNv) => {
        set({ loading: true, error: null });
        try {
            // 1. Gọi API xóa
            await nguyenVongService.deleteNguyenVong(idNv);
            
            // 2. Fetch lại danh sách nguyện vọng để cập nhật số thứ tự (Thứ tự 1, 2, 3...) mới nhất
            const updatedData = await nguyenVongService.getMyNguyenVong();
            
            set({
                nguyenVongs: updatedData,
                loading: false,
            });
            return true;
        } catch (error) {
            set({
                error: error.message || 'Xóa nguyện vọng thất bại',
                loading: false,
            });
            throw error;
        }
    },

    clearError: () => set({ error: null }),
}));
