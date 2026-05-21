/**
 * Constants cho ứng dụng
 */

export const HTTP_STATUS = {
    OK: 200,
    CREATED: 201,
    BAD_REQUEST: 400,
    UNAUTHORIZED: 401,
    FORBIDDEN: 403,
    NOT_FOUND: 404,
    INTERNAL_SERVER_ERROR: 500,
};

export const ERROR_MESSAGES = {
    NETWORK_ERROR: 'Lỗi kết nối mạng',
    LOGIN_FAILED: 'Đăng nhập thất bại. Vui lòng kiểm tra CCCD và mật khẩu',
    FETCH_FAILED: 'Lấy dữ liệu thất bại',
    CREATE_FAILED: 'Tạo dữ liệu thất bại',
    UPDATE_FAILED: 'Cập nhật dữ liệu thất bại',
    DELETE_FAILED: 'Xóa dữ liệu thất bại',
    UNAUTHORIZED: 'Phiên làm việc hết hạn. Vui lòng đăng nhập lại',
};

export const ROUTES = {
    LOGIN: '/login',
    DASHBOARD: '/dashboard',
    TRACUU: '/tracuu',
    NGUYEN_VONG: '/dashboard?tab=nguyenvong',
};

export const STORAGE_KEYS = {
    TOKEN: 'token',
    USER: 'user',
};

export const API_ENDPOINTS = {
    AUTH_LOGIN: '/auth/login',
    NGANH_LIST: '/nganh',
    NGUYEN_VONG_LIST: '/nguyenvong',
    NGUYEN_VONG_CREATE: '/nguyenvong',
    NGUYEN_VONG_UPDATE: (id) => `/nguyenvong/${id}`,
    NGUYEN_VONG_DELETE: (id) => `/nguyenvong/${id}`,
};
