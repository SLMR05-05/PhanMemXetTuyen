# API Integration Guide

## 🔌 Backend API Endpoints

Ứng dụng frontend tương tác với các endpoints sau:

### 1. Authentication

#### POST `/api/auth/login`
Đăng nhập sinh viên

**Request:**
```json
{
  "cccd": "032123456789",
  "password": "password123"
}
```

**Response:**
```
jwt_token_string_here
```

**Status codes:**
- 200: Đăng nhập thành công
- 400: CCCD hoặc mật khẩu sai
- 401: Unauthorized

---

### 2. Ngành (Major)

#### GET `/api/nganh`
Lấy danh sách tất cả các ngành

**Response:**
```json
[
  {
    "maNganh": "CNTT",
    "tenNganh": "Công Nghệ Thông Tin"
  },
  {
    "maNganh": "KTPM",
    "tenNganh": "Kỹ Thuật Phần Mềm"
  }
]
```

**Status codes:**
- 200: OK
- 401: Token hết hạn

---

### 3. Nguyện Vọng (Preferences)

#### GET `/api/nguyenvong`
Lấy danh sách nguyện vọng của sinh viên hiện tại

**Headers:**
```
Authorization: Bearer {token}
```

**Response:**
```json
[
  {
    "idNv": 1,
    "nnCccd": "032123456789",
    "nvMaNganh": "CNTT",
    "nvTt": 1,
    "diemThxt": 27.5,
    "diemUtqd": 0.0,
    "diemCong": 25.0,
    "diemXettuyen": 27.5,
    "nvKetqua": null,
    "nvKeys": "key_1"
  }
]
```

---

#### POST `/api/nguyenvong`
Tạo nguyện vọng mới

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request:**
```json
{
  "maNganh": "CNTT",
  "thuTuNguyenVong": 1,
  "ttPhuongThuc": "Xét tuyển điểm thi",
  "ttThm": "A00"
}
```

**Response:**
```json
{
  "idNv": 1,
  "nnCccd": "032123456789",
  "nvMaNganh": "CNTT",
  "nvTt": 1,
  "diemThxt": null,
  "diemUtqd": null,
  "diemCong": null,
  "diemXettuyen": null,
  "nvKetqua": null,
  "nvKeys": "key_1"
}
```

**Status codes:**
- 201: Created
- 400: Dữ liệu không hợp lệ
- 401: Unauthorized

---

#### DELETE `/api/nguyenvong/{id}`
Xóa nguyện vọng

**Headers:**
```
Authorization: Bearer {token}
```

**Parameters:**
- `id` (path): ID nguyện vọng

**Response:**
```
204 No Content
```

**Status codes:**
- 204: Deleted successfully
- 401: Unauthorized
- 404: Not found

---

## 🔑 Authentication Flow

### 1. Login
```
Client -> POST /api/auth/login (cccd, password)
Server <- JWT Token
Client saves token to localStorage
```

### 2. Subsequent Requests
```
Client -> GET /api/nguyenvong (with Authorization header)
Server <- Data
```

### 3. Logout
```
Client removes token from localStorage
Client redirects to login page
```

---

## 📡 Error Handling

### Common Error Responses

**401 Unauthorized:**
```json
{
  "error": "Token expired",
  "message": "Please login again"
}
```

**400 Bad Request:**
```json
{
  "error": "Validation error",
  "message": "maNganh is required"
}
```

**500 Internal Server Error:**
```json
{
  "error": "Server error",
  "message": "Something went wrong"
}
```

---

## 🧪 Testing with Curl

### Test Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"cccd":"032123456789","password":"password123"}'
```

### Test Get Majors
```bash
curl http://localhost:8080/api/nganh
```

### Test Get Preferences (with token)
```bash
curl -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  http://localhost:8080/api/nguyenvong
```

### Test Create Preference
```bash
curl -X POST http://localhost:8080/api/nguyenvong \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "maNganh":"CNTT",
    "thuTuNguyenVong":1,
    "ttPhuongThuc":"Xét tuyển",
    "ttThm":"A00"
  }'
```

---

## 🔐 Security Considerations

1. **Token Storage**: Token được lưu trong `localStorage`
   - Vulnerability: XSS có thể đánh cắp token
   - Mitigation: Sử dụng HttpOnly cookies nếu possible

2. **CORS**: Frontend chỉ gọi API từ domain được phép
   - Backend phải cấu hình CORS đúng

3. **HTTPS**: Luôn sử dụng HTTPS trên production
   - Ensure token transmission an toàn

4. **Token Expiry**: Backend nên set expiry time
   - Frontend tự động login lại khi hết hạn

---

## 📊 API Integration in Frontend

### Using Services Layer

```javascript
// src/services/authService.js
import axiosInstance from './api';

export const authService = {
  login: async (cccd, password) => {
    const response = await axiosInstance.post('/auth/login', {
      cccd,
      password,
    });
    localStorage.setItem('token', response.data);
    return response.data;
  },
};
```

### Using Zustand Store

```javascript
// src/context/authStore.js
import { create } from 'zustand';
import { authService } from '../services/authService';

export const useAuthStore = create((set) => ({
  login: async (cccd, password) => {
    const token = await authService.login(cccd, password);
    set({ isAuthenticated: true, token });
  },
}));
```

### Using in Component

```javascript
import { useAuthStore } from '../context/authStore';

export default function LoginPage() {
  const { login } = useAuthStore();
  
  const handleSubmit = async (cccd, password) => {
    await login(cccd, password);
  };
}
```

---

## 🚀 Deployment Checklist

- [ ] Backend API URL updated for production
- [ ] CORS configured for production domain
- [ ] Token expiry set appropriately
- [ ] Error handling for network failures
- [ ] Loading states for all async operations
- [ ] Security headers configured
- [ ] API rate limiting enabled
- [ ] Logging/monitoring setup

---

## 📚 Resources

- [JWT Authentication](https://jwt.io)
- [REST API Best Practices](https://restfulapi.net)
- [CORS Explained](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)
- [Axios Documentation](https://axios-http.com)
