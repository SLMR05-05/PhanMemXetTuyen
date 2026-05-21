import axiosInstance from './api';

export const nguyenVongService = {
    // Lấy danh sách nguyện vọng của sinh viên hiện tại
    getMyNguyenVong: async () => {
        try {
            const response = await axiosInstance.get('/nguyenvong');
            return response.data;
        } catch (error) {
            throw error.response?.data || 'Failed to fetch nguyện vọng';
        }
    },

    // Tra cứu nguyện vọng theo CCCD
    lookupNguyenVongByCccd: async (cccd) => {
        try {
            const response = await axiosInstance.get('/nguyenvong/lookup', {
                params: { cccd },
            });
            return response.data;
        } catch (error) {
            throw error.response?.data || 'Failed to lookup nguyện vọng';
        }
    },

    // Tạo nguyện vọng mới
    createNguyenVong: async (maNganh, thuTuNguyenVong, ttPhuongThuc = null, ttThm = null) => {
        try {
            const response = await axiosInstance.post('/nguyenvong', {
                maNganh,
                thuTuNguyenVong,
                ttPhuongThuc,
                ttThm,
            });
            return response.data;
        } catch (error) {
            throw error.response?.data || 'Failed to create nguyện vọng';
        }
    },

    // Cập nhật nguyện vọng
    updateNguyenVong: async (idNv, maNganh, thuTuNguyenVong) => {
        try {
            const response = await axiosInstance.put(`/nguyenvong/${idNv}`, {
                maNganh,
                thuTuNguyenVong,
            });
            return response.data;
        } catch (error) {
            throw error.response?.data || 'Failed to update nguyện vọng';
        }
    },

    // Xóa nguyện vọng
    deleteNguyenVong: async (idNv) => {
        try {
            await axiosInstance.delete(`/nguyenvong/${idNv}`);
            return true;
        } catch (error) {
            throw error.response?.data || 'Failed to delete nguyện vọng';
        }
    },
};
