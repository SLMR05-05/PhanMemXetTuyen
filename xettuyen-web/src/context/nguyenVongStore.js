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

    clearError: () => set({ error: null }),
}));
