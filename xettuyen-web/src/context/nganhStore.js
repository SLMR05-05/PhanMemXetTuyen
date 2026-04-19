import { create } from 'zustand';
import { nganhService } from '../services/nganhService';

export const useNganhStore = create((set) => ({
    nganhList: [],
    loading: false,
    error: null,

    fetchAllNganh: async () => {
        set({ loading: true, error: null });
        try {
            const data = await nganhService.getAllNganh();
            set({ nganhList: data, loading: false });
            return data;
        } catch (error) {
            set({
                error: error.message || 'Lấy danh sách ngành thất bại',
                loading: false,
            });
            throw error;
        }
    },

    clearError: () => set({ error: null }),
}));
