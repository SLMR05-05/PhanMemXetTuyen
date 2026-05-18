# 🚀 Hướng dẫn cài đặt Xét Tuyển Web

## 📋 Yêu cầu hệ thống

- **Node.js**: >= 16.0.0
- **npm**: >= 8.0.0 (hoặc yarn >= 1.22.0)
- **Backend**: Spring Boot API chạy trên `http://localhost:8080`

## ⚙️ Các bước cài đặt

### 1️⃣ Clone hoặc tải source code

```bash
cd xettuyen-web
```

### 2️⃣ Cài đặt dependencies

```bash
npm install
```

Nếu dùng yarn:
```bash
yarn install
```

### 3️⃣ Cấu hình Backend URL

Mở file `vite.config.js` và cập nhật URL backend (nếu khác localhost:8080):

```javascript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',  // 👈 Thay đổi ở đây nếu cần
      changeOrigin: true,
    }
  }
}
```

### 4️⃣ Chạy ứng dụng development

```bash
npm run dev
```

Ứng dụng sẽ mở tự động tại: **http://localhost:3000**

## 🔐 Đảm bảo Backend hỗ trợ CORS

Backend Spring Boot cần cấu hình CORS để cho phép requests từ frontend:

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:3000")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
            }
        };
    }
}
```

## 📦 Build cho Production

```bash
npm run build
```

Output sẽ ở trong thư mục `dist/`. Sau đó có thể deploy:

```bash
# Copy dist folder tới web server (Nginx, Apache, etc.)
# Hoặc serve với Node.js:
npm install -g serve
serve -s dist
```

## 🧪 Kiểm tra linting

```bash
npm run lint
```

Sửa lỗi tự động:
```bash
npm run lint:fix
```

## 📝 Tệp cấu hình quan trọng

| Tệp | Mục đích |
|-----|---------|
| `vite.config.js` | Cấu hình Vite (build, dev server, proxy) |
| `tailwind.config.js` | Cấu hình Tailwind CSS |
| `jsconfig.json` | Path aliases (ví dụ: `@/`) |
| `package.json` | Dependencies và scripts |

## 🔧 Cấu trúc thư mục

```
xettuyen-web/
├── src/
│   ├── components/        # React components
│   ├── pages/            # Pages
│   ├── services/         # API services
│   ├── context/          # Zustand stores
│   ├── styles/           # CSS
│   ├── utils/            # Utility functions
│   ├── App.jsx
│   └── main.jsx
├── public/               # Static assets
├── index.html
├── package.json
├── vite.config.js
├── tailwind.config.js
└── README.md
```

## 🐛 Troubleshooting

### ❌ Lỗi: "Cannot find module 'react'"
```bash
npm install react react-dom
```

### ❌ Lỗi: "CORS policy: No 'Access-Control-Allow-Origin'"
- Kiểm tra backend có CORS config đúng
- Kiểm tra URL backend trong proxy config

### ❌ Lỗi: "Failed to connect to backend"
```bash
# Kiểm tra backend có chạy không
curl http://localhost:8080/api/nganh

# Nếu không, hãy:
# 1. Chạy backend: mvn spring-boot:run
# 2. Chờ khoảng 30 giây
# 3. Refresh frontend
```

### ❌ Lỗi: "Module not found: @vitejs/plugin-react"
```bash
npm install @vitejs/plugin-react --save-dev
```

### ❌ Token hết hạn, không thể đăng nhập
- Kiểm tra backend có tạo JWT token đúng
- Kiểm tra token format là "Bearer {token}" trong header

## 📱 Responsive Design

Ứng dụng được thiết kế responsive cho:
- 📱 Mobile (< 640px)
- 💻 Tablet (640px - 1024px)  
- 🖥️ Desktop (> 1024px)

## 🎨 Theme Colors

```javascript
// tailwind.config.js
primary: '#3b82f6'      // Blue
secondary: '#10b981'   // Green
danger: '#ef4444'      // Red
warning: '#f59e0b'     // Orange
```

## 📚 Các công nghệ sử dụng

- **React 18.2** - UI library
- **Vite** - Build tool
- **React Router v6** - Routing
- **Zustand** - State management
- **Axios** - HTTP client
- **Tailwind CSS** - Styling
- **ESLint** - Code linting

## ✅ Kiểm tra cài đặt thành công

1. ✅ Mở http://localhost:3000 trên browser
2. ✅ Trang login hiện lên
3. ✅ Backend API có kết nối (thử login bằng CCCD test)
4. ✅ Có thể thấy danh sách ngành
5. ✅ Có thể thêm nguyện vọng

## 🚀 Deployment

### Deploy lên GitHub Pages (Static)
```bash
npm run build
# Upload `dist` folder
```

### Deploy lên Vercel
```bash
# Cài Vercel CLI
npm install -g vercel

# Deploy
vercel
```

### Deploy lên Netlify
```bash
npm run build
# Drag & drop `dist` folder to Netlify
```

## 📞 Support

Nếu gặp vấn đề, kiểm tra:
1. Phiên bản Node.js
2. Phiên bản npm
3. Backend API có chạy
4. Firewall/Proxy settings
5. Network connectivity

---

**Chúc bạn cài đặt thành công! 🎉**
