import axiosInstance from './api';

export const diemThiService = {
    getMyDiemThi: async () => {
        try {
            const response = await axiosInstance.get('/diemthi');
            return response.data;
        } catch (error) {
            throw error.response?.data || 'Failed to fetch điểm thi';
        }
    },
};