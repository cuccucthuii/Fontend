# Debug Login - Hướng dẫn kiểm tra

## Cách debug:

### 1. Mở Developer Tools
- Nhấn `F12` hoặc `Ctrl+Shift+I`
- Vào tab **Console**

### 2. Đăng nhập với tài khoản admin
- Vào trang login
- Nhập username và password admin
- Nhấn "Đăng nhập"

### 3. Xem logs trong Console
Bạn sẽ thấy các log sau:

```
Login response: [Response object]
Response data: [Data object]
Role from backend: [Role value]
Full response data: [JSON string]
Role before redirect: [Role value]
Is admin?: true/false
Normalized role: [Normalized role]
Redirecting to /admin hoặc /home
```

### 4. Kiểm tra localStorage
- Vào tab **Application** (Chrome) hoặc **Storage** (Firefox)
- Chọn **Local Storage**
- Xem key `userInfo`

## Các trường hợp có thể xảy ra:

### Trường hợp 1: Backend trả về role = "admin"
```
Role from backend: admin
Is admin?: true
Redirecting to /admin
```

### Trường hợp 2: Backend trả về role = "ADMIN" (uppercase)
```
Role from backend: ADMIN
Is admin?: false (vì so sánh với "admin")
Normalized role: admin
Redirecting to /admin
```

### Trường hợp 3: Backend trả về role = "Administrator"
```
Role from backend: Administrator
Is admin?: false
Normalized role: administrator
Redirecting to /admin
```

### Trường hợp 4: Backend không trả về role
```
Role from backend: undefined
Is admin?: false
Normalized role: user
Redirecting to /home
```

## Cách sửa:

### Nếu backend trả về role khác "admin":
1. Xem log `Role from backend:` để biết role thực tế
2. Thêm role đó vào điều kiện trong Login.vue:

```javascript
if (role === 'admin' || role === 'ADMIN' || role === 'your_role_here') {
    router.push('/admin')
}
```

### Nếu backend không trả về role:
1. Kiểm tra API response structure
2. Có thể role nằm ở `data.user.role` thay vì `data.role`
3. Sửa code để lấy role đúng vị trí

## Test cases:

### Test 1: Tài khoản admin
- Username: admin
- Password: admin
- Expected: Chuyển đến `/admin`

### Test 2: Tài khoản user
- Username: user
- Password: user  
- Expected: Chuyển đến `/home`

## Lưu ý:
- Đảm bảo backend đang chạy trên port 8080
- Kiểm tra CORS nếu có lỗi kết nối
- Xem Network tab để kiểm tra API call 