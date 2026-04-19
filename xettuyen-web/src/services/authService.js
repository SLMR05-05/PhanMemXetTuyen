import axiosInstance from './api';

export const authService = {
    // Đăng nhập
    login: async (cccd, password) => {
        try {
            const response = await axiosInstance.post('/auth/login', {
                cccd,
                password,
            });
            // Lưu token
            if (response.data) {
                localStorage.setItem('token', response.data);
            }
            return response.data;
        } catch (error) {
            throw error.response?.data || 'Login failed';
        }
    },

    // Đăng xuất
    logout: () => {
        localStorage.removeItem('token');
    },

    // Kiểm tra token còn hợp lệ không
    isAuthenticated: () => {
        return !!localStorage.getItem('token');
    },

    // Lấy token
    getToken: () => {
        return localStorage.getItem('token');
    },
};
