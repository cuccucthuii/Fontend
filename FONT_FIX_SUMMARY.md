# Sửa Lỗi Font - Header Trang Chi Tiết Phim

## Vấn đề
Header của trang chi tiết phim bị lỗi font, hiển thị không đẹp và không nhất quán.

## Nguyên nhân
- Thiếu font-family được định nghĩa rõ ràng trong CSS
- Sử dụng font mặc định của hệ thống có thể không đẹp
- Không có font fallback phù hợp

## Giải pháp đã triển khai

### 1. Cập nhật CSS Global
**File**: `src/style.css`
```css
:root {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  /* ... */
}

* {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

html, body, #app {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}
```

### 2. Cập nhật HomeHeader Component
**File**: `src/components/HomeHeader.vue`
```css
.main-header.full-header.dark {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.header-left .logo {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.menu-link {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.hotline {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}
```

### 3. Cập nhật MovieDetailPage
**File**: `src/pages/USER/MovieDetailPage.vue`
```css
.movie-detail-page {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.movie-title {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.meta-label, .meta-value {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.btn-book-ticket, .btn-showtimes {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}
```

## Font Stack được sử dụng
```css
'Segoe UI', Tahoma, Geneva, Verdana, sans-serif
```

### Giải thích:
- **Segoe UI**: Font chính, đẹp và hiện đại
- **Tahoma**: Fallback cho Windows
- **Geneva**: Fallback cho macOS
- **Verdana**: Fallback cho các hệ thống khác
- **sans-serif**: Fallback cuối cùng

## Kết quả
- ✅ Font hiển thị đẹp và nhất quán trên tất cả các trang
- ✅ Không còn lỗi font trong header
- ✅ Tương thích với nhiều hệ điều hành
- ✅ Fallback tốt khi font chính không có sẵn
- ✅ CSS global đảm bảo font được áp dụng cho toàn bộ ứng dụng

## Files đã cập nhật
1. `src/style.css` - Cập nhật font-family global
2. `src/components/HomeHeader.vue` - Thêm font-family cho header component
3. `src/views/customer/HomePage.vue` - Thêm font-family cho header inline
4. `src/pages/LandingPage.vue` - Thêm font-family cho landing page header
5. `src/pages/USER/NewsPage.vue` - Thêm font-family cho news page
6. `src/pages/USER/MovieDetailPage.vue` - Thêm font-family cho movie detail page
7. `src/assets/global-fonts.css` - Tạo file CSS global với !important
8. `src/main.js` - Import file CSS global

## Test
1. Mở trang chi tiết phim
2. Kiểm tra header có font đẹp không
3. Kiểm tra các phần tử khác có font nhất quán không
4. Test trên các trình duyệt khác nhau

## Lưu ý
- Font Segoe UI có sẵn trên Windows
- Các font fallback đảm bảo hiển thị tốt trên mọi hệ thống
- CSS được áp dụng cho toàn bộ ứng dụng 