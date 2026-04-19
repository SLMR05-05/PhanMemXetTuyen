import { create } from 'zustand';
import { authService } from '../services/authService';

export const useAuthStore = create((set) => ({
    isAuthenticated: authService.isAuthenticated(),
    token: authService.getToken(),
    user: null,
    loading: false,
    error: null,

    login: async (cccd, password) => {
        set({ loading: true, error: null });
        try {
            const token = await authService.login(cccd, password);
            set({
                isAuthenticated: true,
                token,
                loading: false,
                error: null,
            });
            return token;
        } catch (error) {
            set({
                error: error.message || 'Đăng nhập thất bại',
                loading: false,
            });
            throw error;
        }
    },

    logout: () => {
        authService.logout();
        set({
            isAuthenticated: false,
            token: null,
            user: null,
        });
    },

    clearError: () => set({ error: null }),
}));
