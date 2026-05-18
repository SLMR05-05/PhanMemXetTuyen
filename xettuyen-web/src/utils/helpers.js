/**
 * Các hàm utility tiện ích
 */

// Format ngày theo định dạng Việt Nam
export const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('vi-VN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
    });
};

// Format điểm số
export const formatScore = (score) => {
    if (score === null || score === undefined) return '-';
    return parseFloat(score).toFixed(2);
};

// Kiểm tra điều kiện đấu tranh thứ tự
export const getStatusClass = (status) => {
    switch (status) {
        case 'Đậu':
            return 'bg-green-100 text-green-800';
        case 'Rớt':
            return 'bg-red-100 text-red-800';
        default:
            return 'bg-gray-100 text-gray-800';
    }
};

// Lưu token
export const saveToken = (token) => {
    localStorage.setItem('token', token);
};

// Lấy token
export const getToken = () => {
    return localStorage.getItem('token');
};

// Xóa token
export const removeToken = () => {
    localStorage.removeItem('token');
};

// Kiểm tra token có hợp lệ không
export const isValidToken = (token) => {
    if (!token) return false;
    // Kiểm tra format JWT (3 phần)
    return token.split('.').length === 3;
};
