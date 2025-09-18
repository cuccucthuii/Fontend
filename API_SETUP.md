# Hướng dẫn thiết lập API Frontend - Backend

## Cấu trúc đã thiết lập

### 1. File cấu hình API (`src/services/api.js`)
- Cấu hình axios với base URL: `http://localhost:8080`
- Interceptors để xử lý request/response
- Tự động thêm token vào header
- Xử lý lỗi 401 (token hết hạn)

### 2. Constants API (`src/constants/api.js`)
- Quản lý tất cả endpoints
- HTTP status codes
- User roles

### 3. User Service (`src/services/userService.js`)
- Các hàm gọi API cho user
- Sử dụng constants từ `api.js`

### 4. Vite Config (`vite.config.js`)
- Proxy configuration cho development
- Chuyển hướng `/api` requests đến `http://localhost:8080`

## Cách sử dụng

### 1. Gọi API trong component
```javascript
import { loginUser } from '../services/userService'

// Trong component
async function handleLogin() {
  try {
    const response = await loginUser({
      tenDangNhap: username.value,
      matKhau: password.value
    })
    
    // Xử lý response
    console.log(response.data)
  } catch (error) {
    // Xử lý lỗi
    console.error(error)
  }
}
```

### 2. Thêm API mới
1. Thêm endpoint vào `src/constants/api.js`
2. Thêm function vào service tương ứng
3. Import và sử dụng trong component

### 3. Xử lý Authentication
- Token được tự động thêm vào header
- Lỗi 401 sẽ tự động logout và redirect về login
- Thông tin user được lưu trong localStorage

## Các file đã sửa

1. `src/services/api.js` - File cấu hình API chung
2. `src/constants/api.js` - Constants cho endpoints
3. `src/services/userService.js` - Service cho user API
4. `src/pages/Login.vue` - Cải thiện xử lý login
5. `src/components/SidebarMenu.vue` - Cải thiện logout
6. `vite.config.js` - Proxy configuration

## Lưu ý

- Backend phải chạy trên port 8080
- CORS phải được cấu hình đúng ở backend
- API endpoints phải khớp với backend
- Token authentication (nếu có) sẽ được tự động xử lý

## Troubleshooting

### Lỗi CORS
- Kiểm tra backend có cấu hình CORS đúng không
- Đảm bảo backend chạy trên port 8080

### Lỗi kết nối
- Kiểm tra backend có đang chạy không
- Kiểm tra URL trong `api.js` có đúng không

### Lỗi 401
- Token hết hạn sẽ tự động logout
- Kiểm tra backend có trả về token đúng format không 