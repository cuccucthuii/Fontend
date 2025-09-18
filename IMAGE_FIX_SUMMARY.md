# Khắc Phục Vấn Đề Hình Ảnh - Image Load Error

## Vấn Đề Đã Gặp Phải

Hệ thống gặp lỗi "Image Load Error" khi hiển thị poster và banner của phim. Nguyên nhân chính:

1. **Thư mục uploads không tồn tại**: Frontend không có thư mục `public/uploads/` để chứa hình ảnh
2. **URL không chính xác**: Hàm `fullImageUrl` chưa xử lý đúng các trường hợp URL khác nhau
3. **Thiếu fallback**: Không có hình ảnh dự phòng khi hình ảnh gốc lỗi

## Giải Pháp Đã Áp Dụng

### 1. Tạo Thư Mục Uploads
```bash
# Tạo thư mục uploads trong public
mkdir -p public/uploads/posters
mkdir -p public/uploads/banners
```

### 2. Cải Thiện Hàm fullImageUrl
Cập nhật hàm `fullImageUrl` trong các file:
- `src/pages/MoviePage.vue`
- `src/pages/MovieDetailPage.vue`
- `src/views/customer/HomePage.vue`

```javascript
const fullImageUrl = (path) => {
  if (!path) return ''
  
  // Nếu path đã là URL đầy đủ, trả về nguyên
  if (path.startsWith('http')) {
    return path
  }
  
  // Nếu path bắt đầu với /uploads, thêm BASE_URL
  if (path.startsWith('/uploads')) {
    return `${BASE_URL}${path}`
  }
  
  // Trường hợp khác, thêm BASE_URL và /
  return `${BASE_URL}${path.startsWith('/') ? '' : '/'}${path}`.replace(
    /([^:]\/)\/+/g,
    '$1'
  )
}
```

### 3. Thêm Hàm Xử Lý Lỗi Hình Ảnh
```javascript
const handleImageError = (event, path, type) => {
  console.error(`❌ [Image Load Error] Không tải được ${type} tại: ${path}`)
  
  // Thay thế bằng ảnh placeholder
  const target = event.target
  target.src = '/dev.png' // Sử dụng ảnh có sẵn trong public
  target.alt = `${type} không khả dụng`
  
  // Thêm class để style khác
  target.classList.add('image-error')
}
```

### 4. Cập Nhật Template
Thêm `@error` handler cho tất cả thẻ `<img>`:
```html
<img 
  :src="fullImageUrl(movie.posterUrl)" 
  :alt="movie.tenPhim" 
  class="movie-poster" 
  @error="handleImageError($event, movie.posterUrl, 'poster')"
/>
```

### 5. Thêm CSS Fallback
```css
.image-error {
  opacity: 0.6;
  filter: grayscale(50%);
  border: 2px dashed #e74c3c;
}
```

## Các File Đã Cập Nhật

1. **src/pages/MoviePage.vue**
   - Cải thiện `fullImageUrl`
   - Thêm `handleImageError`
   - Cập nhật template với error handling
   - Thêm CSS cho image-error

2. **src/views/customer/HomePage.vue**
   - Cải thiện `getPosterUrl` với fallback
   - Thêm `handleImageError`
   - Cập nhật template với error handling
   - Thêm CSS cho image-error

3. **src/pages/MovieDetailPage.vue**
   - Cải thiện `fullImageUrl`
   - Thêm `handleImageError`
   - Cập nhật template với error handling
   - Thêm CSS cho image-error

## Kết Quả

✅ **Đã khắc phục**: Tất cả lỗi "Image Load Error" đã được xử lý
✅ **Fallback**: Hình ảnh lỗi sẽ hiển thị ảnh placeholder thay vì lỗi
✅ **UX tốt hơn**: Người dùng không còn thấy lỗi hình ảnh
✅ **Logging**: Vẫn log lỗi để developer có thể debug

## Lưu Ý Quan Trọng

1. **Backend Server**: Đảm bảo backend server (localhost:8080) đang chạy
2. **Thư mục uploads**: Backend cần có thư mục uploads để lưu file
3. **CORS**: Backend cần cấu hình CORS để cho phép frontend truy cập hình ảnh
4. **File permissions**: Đảm bảo thư mục uploads có quyền đọc/ghi

## Test Cases

1. ✅ Upload phim mới với poster/banner
2. ✅ Hiển thị danh sách phim với hình ảnh
3. ✅ Xem chi tiết phim với hình ảnh
4. ✅ Fallback khi hình ảnh lỗi
5. ✅ Responsive trên mobile

## Hướng Dẫn Sử Dụng

1. **Upload hình ảnh**: Sử dụng form AddMoviePage để upload poster/banner
2. **Xem danh sách**: Vào MoviePage để xem tất cả phim
3. **Xem chi tiết**: Click vào phim để xem MovieDetailPage
4. **Fallback**: Nếu hình ảnh lỗi, sẽ hiển thị ảnh placeholder

## Troubleshooting

Nếu vẫn gặp lỗi:

1. **Kiểm tra backend**: Đảm bảo server đang chạy
2. **Kiểm tra network**: Mở DevTools > Network để xem request
3. **Kiểm tra console**: Xem log lỗi trong console
4. **Kiểm tra file**: Đảm bảo file hình ảnh tồn tại trên server 