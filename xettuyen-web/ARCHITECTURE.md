# 📚 Project Architecture & Features

## 🏗️ Kiến trúc ứng dụng

```
┌─────────────────────────────────────────┐
│         Frontend (React + Vite)         │
├─────────────────────────────────────────┤
│                                         │
│  Pages (LoginPage, DashboardPage)       │
│  ├── Components (UI)                    │
│  │   ├── Header                         │
│  │   ├── NguyenVongForm                 │
│  │   ├── NguyenVongList                 │
│  │   └── ProtectedRoute                 │
│  │                                      │
│  └── Services (API Calls)               │
│      ├── authService                    │
│      ├── nguyenVongService              │
│      └── nganhService                   │
│                                         │
│  Context/State (Zustand)                │
│  ├── authStore                          │
│  ├── nguyenVongStore                    │
│  └── nganhStore                         │
│                                         │
└─────────────────────────────────────────┘
         ↓ HTTP (Axios) ↓
┌─────────────────────────────────────────┐
│  Backend (Spring Boot REST API)         │
├─────────────────────────────────────────┤
│                                         │
│  AuthController      → login            │
│  NganhController     → getAllNganh       │
│  NguyenVongController→ CRUD operations   │
│                                         │
│  Services (Business Logic)              │
│  Repositories (JPA)                     │
│  Entities (Database Models)             │
│                                         │
└─────────────────────────────────────────┘
         ↓ SQL ↓
┌─────────────────────────────────────────┐
│  MySQL Database                         │
├─────────────────────────────────────────┤
│  - users                                │
│  - xt_thisinhxettuyen25                 │
│  - xt_nguyenvongxettuyen                │
│  - xt_nganh                             │
│  - (other tables)                       │
└─────────────────────────────────────────┘
```

---

## 📋 Các tính năng chi tiết

### 1. 🔐 Authentication (Đăng nhập)

**Quy trình:**
```
1. User nhập CCCD + Password
2. Frontend gửi POST /api/auth/login
3. Backend xác minh (hash password)
4. Trả về JWT token
5. Frontend lưu token vào localStorage
6. Token được gửi kèm mỗi request (Authorization header)
```

**Files liên quan:**
- `src/pages/LoginPage.jsx` - UI form
- `src/services/authService.js` - API call
- `src/context/authStore.js` - State management

---

### 2. 📋 Nguyện Vọng Management

**Tính năng:**
- ✅ **View**: Xem danh sách nguyện vọng của sinh viên
- ✅ **Create**: Thêm nguyện vọng mới (chọn ngành + thứ tự)
- ✅ **Delete**: Xóa nguyện vọng
- ✅ **View Score**: Xem điểm xét tuyển & kết quả

**Data Structure:**
```javascript
{
  idNv: 1,              // ID nguyện vọng
  nnCccd: "032123...",  // CCCD sinh viên
  nvMaNganh: "CNTT",    // Mã ngành
  nvTt: 1,              // Thứ tự nguyện vọng (1-5)
  diemThxt: 27.5,       // Điểm xét tuyển
  diemUtqd: 0.0,        // Điểm ưu tiên
  diemCong: 25.0,       // Điểm cộng
  diemXettuyen: 27.5,   // Điểm xét tuyển cuối
  nvKetqua: "Đậu"       // Kết quả (Đậu/Rớt)
}
```

**Files liên quan:**
- `src/pages/DashboardPage.jsx` - Main page
- `src/components/NguyenVongForm.jsx` - Form thêm
- `src/components/NguyenVongList.jsx` - Hiển thị danh sách
- `src/services/nguyenVongService.js` - API calls
- `src/context/nguyenVongStore.js` - State management

---

### 3. 🎓 Ngành (Major) Selection

**Tính năng:**
- ✅ Lấy danh sách tất cả ngành từ server
- ✅ Cache trong Zustand store
- ✅ Hiển thị trong dropdown khi thêm nguyện vọng

**Data Structure:**
```javascript
{
  maNganh: "CNTT",
  tenNganh: "Công Nghệ Thông Tin"
}
```

**Files liên quan:**
- `src/services/nganhService.js` - API calls
- `src/context/nganhStore.js` - State management

---

## 🔄 Data Flow

### Flow 1: Đăng nhập
```
LoginPage
  ├── User enters CCCD + Password
  ├── onClick → authStore.login()
  ├── → authService.login() [HTTP POST]
  ├── → API trả JWT token
  ├── → lưu token vào localStorage
  ├── → set isAuthenticated = true
  └── → redirect to /dashboard
```

### Flow 2: Xem danh sách nguyện vọng
```
DashboardPage (mount)
  ├── useEffect → fetch data
  ├── nguyenVongStore.fetchNguyenVongs()
  ├── nganhStore.fetchAllNganh()
  ├── → API calls [HTTP GET]
  ├── → data returned
  ├── → store updates
  └── → NguyenVongList re-renders with new data
```

### Flow 3: Thêm nguyện vọng
```
NguyenVongForm
  ├── User selects ngành + thứ tự
  ├── onClick → nguyenVongStore.addNguyenVong()
  ├── → nguyenVongService.createNguyenVong() [HTTP POST]
  ├── → API creates & returns new record
  ├── → store adds to list
  ├── → NguyenVongList re-renders
  └── → show success message
```

---

## 🎨 UI Components Structure

```
App
├── LoginPage
│   └── (Login form)
│
└── ProtectedRoute
    └── DashboardPage
        ├── Header
        │   └── (Logo + Logout button)
        │
        ├── Tab: Danh sách
        │   └── NguyenVongList
        │       ├── (Table)
        │       ├── (Rows)
        │       └── (Delete buttons)
        │
        ├── Tab: Thêm
        │   └── NguyenVongForm
        │       ├── (Ngành select)
        │       ├── (Thứ tự select)
        │       └── (Submit button)
        │
        └── Stats boxes (Summary)
```

---

## 🔌 API Endpoints Integration

| Method | Endpoint | Service | Status |
|--------|----------|---------|--------|
| POST | `/api/auth/login` | authService | ✅ |
| GET | `/api/nganh` | nganhService | ✅ |
| GET | `/api/nguyenvong` | nguyenVongService | ✅ |
| POST | `/api/nguyenvong` | nguyenVongService | ✅ |
| DELETE | `/api/nguyenvong/{id}` | nguyenVongService | ✅ |

---

## 🛠️ State Management (Zustand)

### AuthStore
```javascript
{
  isAuthenticated: boolean,
  token: string,
  loading: boolean,
  error: string,
  
  login(cccd, password),
  logout(),
  clearError()
}
```

### NguyenVongStore
```javascript
{
  nguyenVongs: array,
  loading: boolean,
  error: string,
  
  fetchNguyenVongs(),
  addNguyenVong(maNganh, thuTuNguyenVong),
  removeNguyenVong(idNv),
  clearError()
}
```

### NganhStore
```javascript
{
  nganhList: array,
  loading: boolean,
  error: string,
  
  fetchAllNganh(),
  clearError()
}
```

---

## 🔒 Security Features

1. **JWT Authentication**
   - Token stored in localStorage
   - Sent in Authorization header
   - Validated by backend

2. **Protected Routes**
   - ProtectedRoute component
   - Checks isAuthenticated
   - Redirects to login if needed

3. **CORS**
   - Backend configured for localhost:3000
   - Production domain should be updated

4. **Input Validation**
   - Frontend form validation
   - Backend DTOs validate input

---

## 📊 Performance Optimizations

1. **Code Splitting**
   - Vite automatically chunks vendor libraries
   - React, ReactDOM, React Router in separate chunk
   - Axios, Zustand in utility chunk

2. **Caching**
   - Zustand stores cache API data
   - Avoid redundant API calls

3. **Lazy Loading**
   - Routes lazy loaded (potential future improvement)

4. **Responsive Images**
   - Tailwind CSS for responsive design

---

## 🧪 Testing Strategy

### Unit Testing (Future)
```javascript
// authService.test.js
describe('authService', () => {
  test('login should return token', async () => {
    // Test implementation
  });
});
```

### Integration Testing (Future)
- Test full login flow
- Test CRUD operations
- Test error handling

---

## 📈 Scalability Considerations

### Current Limitations
- No pagination for large lists
- No search/filter functionality
- No caching of API responses

### Future Improvements
- Add pagination for nguyện vọng list
- Add search/filter by ngành
- Implement React Query for caching
- Add more sophisticated error handling
- Add user profile page
- Add edit functionality for nguyện vọng

---

## 🌍 i18n (Internationalization)

Currently: Vietnamese only

### Future Implementation
```javascript
import i18next from 'i18next';

// src/i18n/config.js
i18next.init({
  resources: {
    en: { ... },
    vi: { ... }
  }
});
```

---

## 📱 Responsive Design

Tailwind CSS breakpoints used:
- **sm**: 640px - Phone
- **md**: 768px - Tablet
- **lg**: 1024px - Desktop
- **xl**: 1280px - Large Desktop

Example usage:
```jsx
<div className="grid grid-cols-1 md:grid-cols-3 gap-4">
  {/* 1 col on mobile, 3 cols on medium screens */}
</div>
```

---

## 🚀 Deployment Architecture

```
┌─────────────────────┐
│ Git Repository      │
└──────────┬──────────┘
           │ push
┌──────────▼──────────┐
│ CI/CD Pipeline      │
│ - Build            │
│ - Test             │
│ - Deploy           │
└──────────┬──────────┘
           │
┌──────────▼──────────┐
│ Production Server   │
│ - Nginx/Apache      │
│ - Static files      │
│ (dist/ folder)      │
└─────────────────────┘
```

---

## 📚 Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| UI Framework | React | 18.2 |
| Build Tool | Vite | 5.0 |
| Routing | React Router | 6.20 |
| State Mgmt | Zustand | 4.4 |
| HTTP | Axios | 1.6 |
| Styling | Tailwind CSS | 3.4 |
| Linting | ESLint | 8.54 |

---

## 🎓 Learning Resources

- [React Official Docs](https://react.dev)
- [Vite Documentation](https://vitejs.dev)
- [Zustand GitHub](https://github.com/pmndrs/zustand)
- [Tailwind CSS Docs](https://tailwindcss.com)
- [Axios Guide](https://axios-http.com)

---

**Chúc bạn học tốt! 🚀**
