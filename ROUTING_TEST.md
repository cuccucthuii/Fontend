# Test Logic Routing

## Cấu trúc Routes hiện tại:

### 1. Trang Khách hàng (Public)
- `/` → `HomePage` (trang chủ khách hàng)
- `/booking` → `BookingPage`
- `/news` → `NewsPage`
- `/prices` → `TicketPricePage`
- `/showtimes` → `ShowtimesPage`
- `/promotions` → `PromotionsPage`
- `/terms` → `TermsPage`
- `/privacy` → `PrivacyPage`
- `/contact` → `ContactPage`
- `/feedback` → `FeedbackPage`
- `/about` → `AboutPage`

### 2. Trang Admin
- `/admin` → `AdminLayout` với các children:
  - `/admin` → `DashboardPage`
  - `/admin/account` → `AdminAccount`
  - `/admin/movies` → `MoviePage`
  - `/admin/rooms` → `RoomPage`
  - `/admin/branches` → `BranchPage`
  - `/admin/schedule` → `SchedulePage`
  - `/admin/bills` → `BillPage`
  - `/admin/seats` → `SeatPage`
  - `/admin/invoices` → `InvoicePage`

### 3. Trang Dashboard (Cần auth)
- `/dashboard` → `MainLayout` với các children:
  - `/dashboard` → `DashboardPage`
  - `/dashboard/account` → `AccountPage`
  - `/dashboard/movies` → `MoviePage`
  - `/dashboard/rooms` → `RoomPage`
  - `/dashboard/branches` → `BranchPage`
  - `/dashboard/schedule` → `SchedulePage`
  - `/dashboard/bills` → `BillPage`
  - `/dashboard/seats` → `SeatPage`
  - `/dashboard/invoices` → `InvoicePage`

### 4. Trang POS
- `/pos` → `POSLayout` với các children:
  - `/pos` → `POSHomePage`
  - `/pos/showtimes` → `ShowtimeSelectPage`
  - `/pos/seats/:scheduleId` → `SeatSelectPage`
  - `/pos/payment` → `PaymentPage`

## Logic Chuyển hướng:

### Khi đăng nhập:
- **Admin** → `/admin`
- **User** → `/` (trang khách hàng)

### Khi đã đăng nhập và vào `/login`:
- **Admin** → `/admin`
- **User** → `/` (trang khách hàng)

## Test Cases:

### 1. Đăng nhập với role "admin":
- Input: username, password với role="admin"
- Expected: Chuyển đến `/admin`
- Actual: ?

### 2. Đăng nhập với role "user":
- Input: username, password với role="user"
- Expected: Chuyển đến `/` (HomePage)
- Actual: ?

### 3. Đăng nhập với role khác:
- Input: username, password với role="staff"
- Expected: Chuyển đến `/` (HomePage)
- Actual: ?

## Debug Steps:

1. Mở Developer Tools (F12)
2. Vào tab Console
3. Đăng nhập và xem log:
   - `Login response:` - Response từ backend
   - `Login error:` - Lỗi nếu có
4. Kiểm tra localStorage:
   - `isLoggedIn`
   - `userInfo`
5. Kiểm tra URL sau khi đăng nhập

## Các file đã sửa:

1. `src/router/index.js` - Sửa route `/` thành `/dashboard`
2. `src/pages/Login.vue` - Logic chuyển hướng dựa vào role
3. `src/components/SidebarMenu.vue` - Logout function

## Lưu ý:

- Backend phải trả về `role` trong response
- Role phải khớp với logic frontend
- Kiểm tra CORS nếu có lỗi kết nối 