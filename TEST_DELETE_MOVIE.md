# Test Chức Năng Xóa Phim

## Mô tả vấn đề
Khi xóa phim ở trang admin, phim đó vẫn hiển thị ở trang chủ user.

## Giải pháp đã triển khai

### 1. Sửa API Service
- Cập nhật `src/services/movieService.js` để sử dụng instance `api` thay vì `axios` trực tiếp
- Đảm bảo tất cả các hàm CRUD đều sử dụng cùng một instance API

### 2. Cập nhật hàm xóa phim
- Sửa hàm `confirmDeleteMovie()` trong `src/pages/MoviePage.vue` để gọi API thực sự
- Thêm cơ chế thông báo thay đổi dữ liệu qua localStorage và CustomEvent

### 3. Thêm cơ chế đồng bộ dữ liệu
- Các trang hiển thị phim sẽ lắng nghe sự kiện `moviesUpdated`
- Tự động refresh dữ liệu khi có thay đổi từ admin
- Sử dụng localStorage để track thời gian cập nhật

### 4. Cập nhật các trang liên quan
- `src/views/customer/HomePage.vue`: Lắng nghe sự kiện cập nhật
- `src/pages/MovieList.vue`: Lắng nghe sự kiện cập nhật  
- `src/pages/USER/ShowtimesPage.vue`: Chuyển từ mock data sang API thực sự

## Cách test

### Bước 1: Mở trang admin
1. Đăng nhập vào trang admin
2. Vào trang quản lý phim (`/admin/movies`)

### Bước 2: Xóa một phim
1. Tìm phim muốn xóa
2. Click nút "❌" để xóa
3. Xác nhận xóa trong modal
4. Kiểm tra xem phim đã biến mất khỏi danh sách admin

### Bước 3: Kiểm tra trang user
1. Mở trang chủ user (`/`)
2. Kiểm tra xem phim đã xóa có còn hiển thị không
3. Nếu vẫn hiển thị, refresh trang và kiểm tra lại

### Bước 4: Kiểm tra console
1. Mở Developer Tools (F12)
2. Chuyển sang tab Console
3. Xem có log "🔄 Phát hiện thay đổi phim, đang refresh dữ liệu..." không

## Các trường hợp test

### Trường hợp 1: Xóa phim thành công
- ✅ Phim biến mất khỏi trang admin
- ✅ Phim biến mất khỏi trang chủ user
- ✅ Hiển thị toast "🗑️ Xóa phim thành công!"

### Trường hợp 2: Xóa phim thất bại
- ✅ Hiển thị toast "❌ Lỗi khi xóa phim!"
- ✅ Phim vẫn còn trong danh sách

### Trường hợp 3: Refresh trang
- ✅ Dữ liệu vẫn đồng bộ sau khi refresh
- ✅ Không có phim đã xóa xuất hiện lại

## Lưu ý
- Đảm bảo backend API hoạt động bình thường
- Kiểm tra network tab trong Developer Tools để xem API calls
- Nếu có lỗi 500, kiểm tra backend logs 