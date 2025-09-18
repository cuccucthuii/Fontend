# Test Trang Chi Tiết Phim

## Mô tả tính năng
Thay thế modal nhỏ bằng trang chi tiết phim riêng biệt khi click vào phim, tương tự như các website tham khảo.

## Các thay đổi đã thực hiện

### 1. Tạo trang chi tiết phim mới
- **File**: `src/pages/USER/MovieDetailPage.vue`
- **Route**: `/movie/:id`
- **Tính năng**:
  - Hiển thị thông tin chi tiết phim với giao diện đẹp
  - Hero section với background blur từ poster
  - Thông tin đầy đủ: tên, thể loại, thời lượng, đạo diễn, diễn viên
  - Nút xem trailer và đặt vé
  - Responsive design

### 2. Cập nhật Router
- **File**: `src/router/index.js`
- Thêm route `/movie/:id` cho trang chi tiết phim
- Sử dụng props để truyền movie ID

### 3. Cập nhật trang chủ
- **File**: `src/views/customer/HomePage.vue`
- Thay đổi từ `openMovieModal()` sang `goToMovieDetail()`
- Loại bỏ MovieDetailModal component
- Click vào poster phim sẽ chuyển đến trang chi tiết

### 4. Cập nhật trang lịch chiếu
- **File**: `src/pages/USER/ShowtimesPage.vue`
- Thêm khả năng click vào poster và tên phim
- Thêm hiệu ứng hover cho các phần tử có thể click

## Cách test

### Bước 1: Test từ trang chủ
1. Mở trang chủ (`/home`)
2. Click vào poster của bất kỳ phim nào
3. Kiểm tra xem có chuyển đến trang chi tiết phim không
4. URL sẽ có dạng `/movie/{id}`

### Bước 2: Test từ trang lịch chiếu
1. Mở trang lịch chiếu (`/showtimes`)
2. Click vào poster hoặc tên phim
3. Kiểm tra xem có chuyển đến trang chi tiết phim không

### Bước 3: Test trang chi tiết phim
1. Kiểm tra giao diện trang chi tiết:
   - Hero section với background blur
   - Poster phim với hiệu ứng hover
   - Thông tin chi tiết phim
   - Nút "Xem trailer" và "Đặt vé ngay"
   - Phần thông tin kỹ thuật

### Bước 4: Test chức năng trailer
1. Click nút "Xem trailer" trên poster
2. Kiểm tra xem modal trailer có hiển thị không
3. Kiểm tra xem video có phát không

### Bước 5: Test responsive
1. Thay đổi kích thước màn hình
2. Kiểm tra xem giao diện có responsive không
3. Test trên mobile view

## Các trường hợp test

### Trường hợp 1: Phim có đầy đủ thông tin
- ✅ Hiển thị đầy đủ thông tin
- ✅ Có trailer
- ✅ Có poster và banner

### Trường hợp 2: Phim thiếu thông tin
- ✅ Hiển thị "Chưa cập nhật" cho các trường trống
- ✅ Không bị lỗi khi thiếu dữ liệu

### Trường hợp 3: Phim không tồn tại
- ✅ Hiển thị trang lỗi
- ✅ Có nút "Quay về trang chủ"

### Trường hợp 4: URL không hợp lệ
- ✅ Xử lý lỗi gracefully
- ✅ Hiển thị thông báo lỗi phù hợp

## So sánh với website tham khảo

### IQ.com
- ✅ Trang chi tiết riêng biệt thay vì modal
- ✅ Hero section với background
- ✅ Thông tin chi tiết phim
- ✅ Nút xem trailer và đặt vé

### Chieuphimquocgia.com.vn
- ✅ Layout tương tự
- ✅ Thông tin kỹ thuật đầy đủ
- ✅ Giao diện responsive

## Lưu ý
- Đảm bảo backend API `/api/phim/{id}` hoạt động
- Kiểm tra xem có phim nào trong database không
- Test với các loại URL YouTube khác nhau cho trailer 