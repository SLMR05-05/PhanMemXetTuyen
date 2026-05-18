# ⚡ Quick Start - 5 Phút Để Chạy Ứng Dụng

## 1️⃣ Yêu cầu chuẩn bị

- ✅ Node.js 16+ installed
- ✅ Backend Spring Boot chạy trên `http://localhost:8080`

## 2️⃣ Cài đặt (1 dòng lệnh)

```bash
cd xettuyen-web && npm install && npm run dev
```

✨ **Done!** Ứng dụng sẽ mở tại: http://localhost:3000

---

## 🔍 Các bước chi tiết

### Step 1: Navigate to project
```bash
cd d:\Khanh\XayDungPhanMemTheoMoHinhPhanLop\xettuyen2026\xettuyen-web
```

### Step 2: Install dependencies
```bash
npm install
```

### Step 3: Start development server
```bash
npm run dev
```

### Step 4: Open browser
```
http://localhost:3000
```

---

## 🧪 Test Đăng Nhập

Dùng CCCD và password từ database của bạn:

```
CCCD: 032123456789
Password: password
```

---

## 📱 Các tính năng

| Tính năng | Mô tả |
|----------|--------|
| 🔐 **Login** | Đăng nhập bằng CCCD + Password |
| 📋 **View Preferences** | Xem danh sách nguyện vọng |
| ➕ **Add Preference** | Thêm nguyện vọng mới |
| ❌ **Delete Preference** | Xóa nguyện vọng |
| 📊 **View Scores** | Xem điểm xét tuyển |
| 📱 **Responsive** | Hoạt động trên desktop + mobile |

---

## 🛠️ Các lệnh hữu ích

```bash
# Dev mode
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Check code quality
npm run lint

# Fix linting issues
npm run lint:fix
```

---

## ❌ Nếu gặp lỗi

### Lỗi: "Cannot find module"
```bash
rm -rf node_modules package-lock.json
npm install
```

### Lỗi: "Cannot connect to API"
```bash
# Kiểm tra backend chạy đúng không
curl http://localhost:8080/api/nganh

# Nếu không, chạy backend từ root folder
cd ../
mvn clean spring-boot:run
```

### Lỗi: "Port 3000 already in use"
```bash
# Tìm process sử dụng port 3000
lsof -i :3000

# Hoặc chạy trên port khác
PORT=3001 npm run dev
```

---

## 📂 Cấu trúc nhanh

```
src/
├── pages/           # Login, Dashboard
├── components/      # UI components
├── services/        # API calls
├── context/         # State management (Zustand)
└── styles/          # CSS
```

---

## 🎯 Workflow thường ngày

```
1. npm run dev            # Chạy dev server
2. Edit src/...           # Sửa code
3. Vite auto-reload       # Tự refresh browser
4. npm run build          # Build khi ready
5. Deploy dist/ folder    # Deploy lên server
```

---

## 🚀 Deploy nhanh

```bash
# Build
npm run build

# Serve locally để test
npx serve -s dist
```

---

## 📞 Need Help?

- 📖 Xem file `README.md` - Hướng dẫn chi tiết
- 📖 Xem file `INSTALLATION.md` - Cài đặt chi tiết
- 📖 Xem file `API_INTEGRATION.md` - API integration
- 🐛 Check `vite.config.js` - Config

---

**Chúc bạn thành công! 🎉**
