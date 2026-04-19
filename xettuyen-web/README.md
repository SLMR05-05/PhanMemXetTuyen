# Xét Tuyển Web - React + Vite

Ứng dụng web React cho hệ thống quản lý nguyện vọng xét tuyển sinh viên năm 2026.

## 🚀 Tính năng

- ✅ **Đăng nhập**: Sinh viên đăng nhập bằng CCCD và mật khẩu
- ✅ **Quản lý nguyện vọng**: Xem danh sách, thêm, xóa nguyện vọng
- ✅ **Chọn ngành**: Lựa chọn từ danh sách ngành có sẵn
- ✅ **Xem điểm**: Hiển thị điểm xét tuyển và kết quả
- ✅ **Responsive**: Giao diện thân thiện trên mobile và desktop

## 📋 Yêu cầu

- Node.js >= 16
- npm hoặc yarn

## 🛠️ Cài đặt

### 1. Cài đặt dependencies
```bash
npm install
```

### 2. Cấu hình API

Sửa file `vite.config.js` để trỏ đến backend API:
```javascript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',  // Đổi URL backend ở đây
      changeOrigin: true,
    }
  }
}
```

### 3. Chạy ứng dụng development
```bash
npm run dev
```

Ứng dụng sẽ chạy trên `http://localhost:3000`

## 📦 Build cho production

```bash
npm run build
```

Các file build sẽ nằm trong thư mục `dist/`

## 🏗️ Cấu trúc dự án

```
xettuyen-web/
├── src/
│   ├── components/          # React components
│   │   ├── Header.jsx
│   │   ├── NguyenVongForm.jsx
│   │   ├── NguyenVongList.jsx
│   │   └── ProtectedRoute.jsx
│   ├── context/            # Zustand stores
│   │   ├── authStore.js
│   │   ├── nguyenVongStore.js
│   │   └── nganhStore.js
│   ├── pages/              # Trang chính
│   │   ├── LoginPage.jsx
│   │   └── DashboardPage.jsx
│   ├── services/           # API services
│   │   ├── api.js
│   │   ├── authService.js
│   │   ├── nguyenVongService.js
│   │   └── nganhService.js
│   ├── styles/             # CSS files
│   │   └── index.css
│   ├── App.jsx
│   └── main.jsx
├── public/                 # Static files
├── index.html
├── vite.config.js
├── tailwind.config.js
├── postcss.config.js
└── package.json
```

## 🔌 API Endpoints

Ứng dụng tương tác với các endpoints sau từ backend:

### Authentication
- `POST /api/auth/login` - Đăng nhập

### Ngành
- `GET /api/nganh` - Lấy danh sách tất cả ngành

### Nguyện vọng
- `GET /api/nguyenvong` - Lấy danh sách nguyện vọng của sinh viên
- `POST /api/nguyenvong` - Tạo nguyện vọng mới
- `DELETE /api/nguyenvong/{id}` - Xóa nguyện vọng

## 🔐 Authentication

Token JWT được lưu trong `localStorage` với key `token` và được gửi trong mọi request:

```javascript
Authorization: Bearer {token}
```

## 🎨 Giao diện

Ứng dụng sử dụng **Tailwind CSS** để tạo giao diện responsive và hiện đại.

### Các màu chính
- Primary: Blue (#3b82f6)
- Success: Green (#10b981)
- Danger: Red (#ef4444)
- Warning: Orange (#f59e0b)

## 📝 Lưu ý

1. Đảm bảo backend API đang chạy trước khi khởi động ứng dụng
2. CORS phải được cấu hình đúng trên backend
3. Token JWT hết hạn sẽ tự động xóa khỏi localStorage

## 🐛 Troubleshooting

### Lỗi: "Cannot find module '@vitejs/plugin-react'"
```bash
npm install @vitejs/plugin-react
```

### Lỗi: CORS
Thêm CORS configuration vào backend Spring Boot:
```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:3000")
                    .allowedMethods("GET", "POST", "PUT", "DELETE")
                    .allowedHeaders("*");
            }
        };
    }
}
```

### Lỗi: API không kết nối
Kiểm tra:
1. Backend có chạy trên `http://localhost:8080` không?
2. Proxy URL trong `vite.config.js` có đúng không?
3. CORS có được bật không?

## 📚 Tài liệu tham khảo

- [React Documentation](https://react.dev)
- [Vite Documentation](https://vitejs.dev)
- [Tailwind CSS](https://tailwindcss.com)
- [Zustand](https://github.com/pmndrs/zustand)
- [Axios](https://axios-http.com)
- [React Router](https://reactrouter.com)

## 📄 License

MIT
