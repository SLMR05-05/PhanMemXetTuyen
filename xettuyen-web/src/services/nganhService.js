import axiosInstance from './api';

export const nganhService = {
    // Lấy danh sách tất cả các ngành
    getAllNganh: async () => {
        try {
            const response = await axiosInstance.get('/nganh');
            return response.data;
        } catch (error) {
            throw error.response?.data || 'Failed to fetch ngành';
        }
    },
};
