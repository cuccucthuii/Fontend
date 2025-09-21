package org.example.cinema_reservation_system.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cinema_reservation_system.dto.user.*;
import org.example.cinema_reservation_system.dto.userdto.UserRequestDto;
import org.example.cinema_reservation_system.dto.userdto.UserResponseDto;
import org.example.cinema_reservation_system.dto.auth.LoginDto;
import org.example.cinema_reservation_system.dto.auth.ChangePasswordDto;
import org.example.cinema_reservation_system.entity.*;
import org.example.cinema_reservation_system.repository.customer.CustomerRepository;
import org.example.cinema_reservation_system.repository.employee.EmployeeRepository;
import org.example.cinema_reservation_system.repository.role.RoleRepository;
import org.example.cinema_reservation_system.repository.staff.StaffRepository;
import org.example.cinema_reservation_system.repository.theater.TheaterRepository;
import org.example.cinema_reservation_system.service.utility.EmailService;
import org.example.cinema_reservation_system.utils.PasswordGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.example.cinema_reservation_system.repository.useraccount.UserAccountRepository;
import org.example.cinema_reservation_system.repository.avatar.AvatarRepository;
import org.example.cinema_reservation_system.utils.enums.TrangThaiUserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserAccountRepository userAccountRepository;
    private final AvatarRepository avatarRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_AVATAR_URL = "/images/avatars/default.png";
    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final TheaterRepository theaterRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        List<UserAccount> userAccounts = userAccountRepository.findAll();
        return userAccounts.stream()
                .map(this::convertToUserResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDto> findById(Integer id) {
        return userAccountRepository.findById(id)
                .map(this::convertToUserResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDto> findByEmail(String email) {
        return userAccountRepository.findByEmail(email)
                .map(this::convertToUserResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDto> findByUsername(String username) {
        return userAccountRepository.findByTenDangNhap(username)
                .map(this::convertToUserResponseDto);
    }

    @Override
    public UserResponseDto createUser(UserRequestDto userRequest) {
        // Kiểm tra email đã tồn tại
        if (userAccountRepository.existsByEmail(userRequest.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        // Kiểm tra username đã tồn tại
        if (userAccountRepository.existsByTenDangNhap(userRequest.getUsername())) {
            throw new RuntimeException("Username đã được sử dụng");
        }

        UserAccount userAccount = new UserAccount();
        userAccount.setTenDangNhap(userRequest.getUsername());
        userAccount.setEmail(userRequest.getEmail());
        userAccount.setMatKhau(passwordEncoder.encode(userRequest.getPassword()));
        userAccount.setTrangThai(TrangThaiUserAccount.HOAT_DONG);
        userAccount.setNgayTao(LocalDateTime.now());
        userAccount.setNgayCapNhat(LocalDateTime.now());
        userAccount.setAvatarUrl(DEFAULT_AVATAR_URL);

        UserAccount savedUser = userAccountRepository.save(userAccount);
        return convertToUserResponseDto(savedUser);
    }

    @Override
    public UserResponseDto updateUser(Integer id, UserRequestDto userRequest) {
        UserAccount userAccount = userAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));

        // Kiểm tra email trùng lặp (trừ chính user này)
        if (!userAccount.getEmail().equals(userRequest.getEmail()) &&
                userAccountRepository.existsByEmail(userRequest.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        // Kiểm tra username trùng lặp (trừ chính user này)
        if (!userAccount.getTenDangNhap().equals(userRequest.getUsername()) &&
                userAccountRepository.existsByTenDangNhap(userRequest.getUsername())) {
            throw new RuntimeException("Username đã được sử dụng");
        }

        userAccount.setTenDangNhap(userRequest.getUsername());
        userAccount.setEmail(userRequest.getEmail());
        userAccount.setNgayCapNhat(LocalDateTime.now());

        // Chỉ cập nhật mật khẩu nếu được cung cấp
        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
            userAccount.setMatKhau(passwordEncoder.encode(userRequest.getPassword()));
        }

        UserAccount updatedUser = userAccountRepository.save(userAccount);
        return convertToUserResponseDto(updatedUser);
    }

    @Override
    public void deleteUser(Integer id) {
        UserAccount userAccount = userAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));

        userAccountRepository.delete(userAccount);
    }

    @Override
    public UserResponseDto changeUserStatus(Integer id, String status) {
        UserAccount userAccount = userAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));

        userAccount.setTrangThai(TrangThaiUserAccount.valueOf(status));
        userAccount.setNgayCapNhat(LocalDateTime.now());

        UserAccount updatedUser = userAccountRepository.save(userAccount);
        return convertToUserResponseDto(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDto> findAllWithPagination(Pageable pageable) {
        Page<UserAccount> userPage = userAccountRepository.findAll(pageable);
        return userPage.map(this::convertToUserResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> searchUsers(String keyword) {
        // Tạm thời return empty list vì UserAccountRepository chưa có search method
        // Có thể implement sau nếu cần
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> findByRole(String role) {
        List<UserAccount> userAccounts = userAccountRepository.findByRole(role);
        return userAccounts.stream()
                .map(this::convertToUserResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void updatePassword(Integer id, String newPassword) {
        UserAccount userAccount = userAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));

        userAccount.setMatKhau(passwordEncoder.encode(newPassword));
        userAccount.setNgayCapNhat(LocalDateTime.now());

        userAccountRepository.save(userAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean authenticateUser(String email, String password) {
        Optional<UserAccount> userOpt = userAccountRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            UserAccount userAccount = userOpt.get();
            return passwordEncoder.matches(password, userAccount.getMatKhau()) &&
                    userAccount.getTrangThai() == TrangThaiUserAccount.HOAT_DONG;
        }
        return false;
    }

    private UserResponseDto convertToUserResponseDto(UserAccount userAccount) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(userAccount.getIdTaiKhoan());
        dto.setUsername(userAccount.getTenDangNhap());
        dto.setEmail(userAccount.getEmail());
        dto.setHoTen(userAccount.getKhachHang() != null ? userAccount.getKhachHang().getTenKhachHang() : null);
        dto.setSoDienThoai(userAccount.getKhachHang() != null ? userAccount.getKhachHang().getSoDienThoaiKhachHang() : null);
        dto.setDiaChi(userAccount.getKhachHang() != null ? userAccount.getKhachHang().getDiaChi() : null);
        dto.setNgaySinh(userAccount.getKhachHang() != null ? userAccount.getKhachHang().getNgaySinh() : null);
        dto.setGioiTinh(userAccount.getKhachHang() != null ? userAccount.getKhachHang().getGioiTinh() : null);
        dto.setRole(userAccount.getVaiTro() != null ? userAccount.getVaiTro().getTenVaiTro() : null);
        dto.setTrangThai(userAccount.getTrangThai() != null ? userAccount.getTrangThai().name() : null);
        dto.setCreatedAt(userAccount.getNgayTao());
        dto.setUpdatedAt(userAccount.getNgayCapNhat());
        dto.setLastLogin(userAccount.getLastLogin());
        return dto;
    }

    // ========== AUTH METHODS ==========

    @Override
    @Transactional
    public void registerCustomer(CustomerRegisterDto dto) {
        // Kiểm tra email đã tồn tại
        if (userAccountRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        // Kiểm tra username đã tồn tại
        if (userAccountRepository.existsByTenDangNhap(dto.getTenDangNhap())) {
            throw new RuntimeException("Username đã được sử dụng");
        }

        // Tạo khách hàng
        Customer customer = new Customer();
        customer.setTenKhachHang(dto.getTenKhachHang());
        customer.setSoDienThoaiKhachHang(dto.getSoDienThoai());
        customer.setEmail(dto.getEmail());
        customer.setNgayTao(LocalDate.from(LocalDateTime.now()));
        customer.setNgayCapNhat(LocalDateTime.now());
        customer = customerRepository.save(customer);

        // Tạo user account
        UserAccount userAccount = new UserAccount();
        userAccount.setTenDangNhap(dto.getTenDangNhap());
        userAccount.setEmail(dto.getEmail());
        userAccount.setMatKhau(passwordEncoder.encode(dto.getMatKhau()));
        userAccount.setTrangThai(TrangThaiUserAccount.HOAT_DONG);
        userAccount.setNgayTao(LocalDateTime.now());
        userAccount.setNgayCapNhat(LocalDateTime.now());
        userAccount.setAvatarUrl(DEFAULT_AVATAR_URL);

        // Set role cho customer
        Role customerRole = new Role();
        customerRole.setIdRole(3);
        userAccount.setVaiTro(customerRole);

        userAccount.setKhachHang(customer);


        userAccountRepository.save(userAccount);
    }

    @Override
    @Transactional
    public String registerEmployee(EmployeeRegisterDto dto) {

        // Kiểm tra email đã tồn tại
        if (userAccountRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        // Kiểm tra username đã tồn tại
        if (userAccountRepository.existsByTenDangNhap(dto.getTenDangNhap())) {
            throw new RuntimeException("Tên đăng nhập đã được sử dụng");
        }

        // 1. Tạo nhân viên mới (Staff)
        Staff staff = new Staff();
        staff.setTenNhanVien(dto.getTenNhanVien());
        staff.setSoDienThoaiNhanVien(dto.getSoDienThoai());
        staff.setNgaySinh(dto.getNgaySinh() != null ? LocalDate.parse(dto.getNgaySinh(),
                DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null);
        staff.setGioiTinh(dto.getGioiTinh());
        staff.setCccd(dto.getCccd());
        staff.setNgayVaoLam(dto.getNgayVaoLam() != null ? LocalDate.parse(dto.getNgayVaoLam(),
                DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null);
        staff.setChucVu(dto.getChucVu());
        staff.setEmail(dto.getEmail());

        // Nếu có idRapChieu thì gán rạp chiếu
        if (dto.getIdRapChieu() != null) {
            Theater cinema = theaterRepository.findById(dto.getIdRapChieu())
                    .orElseThrow(() -> new RuntimeException("Rạp chiếu không tồn tại"));
            staff.setRapChieu(cinema);
        }

        staff = staffRepository.save(staff); // DB tự sinh idNhanVien

        // 2. Tạo tài khoản cho nhân viên
        UserAccount userAccount = new UserAccount();
        userAccount.setTenDangNhap(dto.getTenDangNhap());
        userAccount.setMatKhau(passwordEncoder.encode(dto.getMatKhau()));
        userAccount.setEmail(dto.getEmail());
        userAccount.setTrangThai(TrangThaiUserAccount.HOAT_DONG);
        userAccount.setNgayTao(LocalDateTime.now());
        userAccount.setNgayCapNhat(LocalDateTime.now());
        userAccount.setAvatarUrl(DEFAULT_AVATAR_URL);

        // Set role mặc định cho nhân viên (ví dụ EMPLOYEE = 2)
        Role employeeRole = new Role();
        employeeRole.setIdRole(2);
        userAccount.setVaiTro(employeeRole);

        // Gán với nhân viên mới tạo
        userAccount.setNhanVien(staff);

        userAccountRepository.save(userAccount);

        return "Đăng ký nhân viên thành công với ID: " + staff.getIdNhanVien();
    }


    @Override
    @Transactional(readOnly = true)
    public UserAccount login(LoginDto dto) {
        UserAccount userAccount;
        boolean hasUsername = dto.getTenDangNhap() != null && !dto.getTenDangNhap().trim().isEmpty();
        boolean hasEmail = dto.getEmail() != null && !dto.getEmail().trim().isEmpty();

        if (hasUsername) {
            userAccount = userAccountRepository.findByTenDangNhap(dto.getTenDangNhap().trim())
                    .orElseThrow(() -> new RuntimeException("Tên đăng nhập hoặc mật khẩu không đúng"));
        } else if (hasEmail) {
            userAccount = userAccountRepository.findByEmail(dto.getEmail().trim())
                    .orElseThrow(() -> new RuntimeException("Email hoặc mật khẩu không đúng"));
        } else {
            throw new RuntimeException("Vui lòng nhập tên đăng nhập hoặc email");
        }

        String rawPassword = dto.getMatKhau();
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new RuntimeException("Mật khẩu không được để trống");
        }

        if (!passwordEncoder.matches(rawPassword, userAccount.getMatKhau())) {
            throw new RuntimeException("Email hoặc mật khẩu không đúng");
        }

        if (userAccount.getTrangThai() != TrangThaiUserAccount.HOAT_DONG) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }

        // Cập nhật thời gian đăng nhập cuối
        userAccount.setLastLogin(LocalDateTime.now());
        // Nếu thiếu avatar thì tự gán mặc định
        if (userAccount.getAvatarUrl() == null || userAccount.getAvatarUrl().isEmpty()) {
            userAccount.setAvatarUrl(DEFAULT_AVATAR_URL);
        }
        userAccountRepository.save(userAccount);

        return userAccount;
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        // Validate token và lấy user
        // Placeholder implementation
        if (!token.startsWith("reset_token_")) {
            throw new RuntimeException("Token không hợp lệ");
        }

        // Tìm user theo token (cần implement logic lưu token)
        // UserAccount userAccount = getUserByToken(token);
        // userAccount.setMatKhau(passwordEncoder.encode(newPassword));
        // userAccountRepository.save(userAccount);

        throw new RuntimeException("Chức năng đặt lại mật khẩu chưa được implement đầy đủ");
    }

    @Override
    public void changePassword(Integer userId, String currentPassword, String newPassword) {
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (!passwordEncoder.matches(currentPassword, userAccount.getMatKhau())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng");
        }

        userAccount.setMatKhau(passwordEncoder.encode(newPassword));
        userAccount.setNgayCapNhat(LocalDateTime.now());
        userAccountRepository.save(userAccount);
    }

    @Override
    public void changePassword(ChangePasswordDto dto) {
        // Lấy user hiện tại từ security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("Không thể xác định người dùng hiện tại");
        }

        //  SỬA: Lấy username từ Spring User principal
        String username;
        if (auth.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            username = ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername();
        } else {
            username = auth.getName(); // Fallback
        }

        // Tìm UserAccount entity bằng username
        UserAccount currentUser = userAccountRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(dto.getCurrentPassword(), currentUser.getMatKhau())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng");
        }

        // Cập nhật mật khẩu mới
        currentUser.setMatKhau(passwordEncoder.encode(dto.getNewPassword()));
        currentUser.setNgayCapNhat(LocalDateTime.now());

        userAccountRepository.save(currentUser);
    }

    // ========== ADDITIONAL METHODS FOR USERCONTROLLER ==========

    @Override
    public List<UserResponseDto> getAllUsers() {
        return findAll(); // Delegate to existing method
    }

    @Override
    @Transactional
    public UserResponseDto updateCurrentUser(UserUpdateDto dto) {
        // Lấy user hiện tại từ security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("Không thể xác định người dùng hiện tại");
        }

        // SỬA: Lấy username từ Spring User principal
        String username;
        if (auth.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            username = ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername();
        } else {
            username = auth.getName(); // Fallback
        }

        // Tìm UserAccount entity bằng username
        UserAccount userAccount = userAccountRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Cập nhật thông tin
        if (dto.getEmail() != null) {
            userAccount.setEmail(dto.getEmail());
        }
        userAccount.setNgayCapNhat(LocalDateTime.now());

        UserAccount updatedUser = userAccountRepository.save(userAccount);
        return convertToUserResponseDto(updatedUser);
    }

    @Override
    public void deleteByTenDangNhap(String tenDangNhap) {
        // Tìm UserAccount theo tên đăng nhập
        UserAccount userAccount = userAccountRepository.findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với tên đăng nhập: " + tenDangNhap));

        // Xóa UserAccount
        userAccountRepository.delete(userAccount);
    }

    @Override
    public UserResponseDto updateAvatar(UpdateAvatarRequestDto dto) {
        // Lấy user hiện tại từ security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserAccount)) {
            throw new RuntimeException("Không thể xác định người dùng hiện tại");
        }

        UserAccount currentUser = (UserAccount) auth.getPrincipal();
        Integer userId = currentUser.getIdTaiKhoan();

        // Tìm UserAccount entity
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Tìm Avatar entity theo ID
        Avatar avatar = avatarRepository.findById(dto.getAvatarId())
                .orElseThrow(() -> new RuntimeException("Avatar không tồn tại"));

        // Kiểm tra avatar có đang hoạt động không
        if (!avatar.getIsActive()) {
            throw new RuntimeException("Avatar đã bị vô hiệu hóa");
        }

        // Cập nhật avatar URL
        userAccount.setAvatarUrl(avatar.getAvatarUrl());
        userAccount.setNgayCapNhat(LocalDateTime.now());

        UserAccount updatedUser = userAccountRepository.save(userAccount);
        return convertToUserResponseDto(updatedUser);
    }

    //thêm mới
    @Transactional
    @Override
    public void adminResetPassword(String username) {
        UserAccount user = userAccountRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản"));

        if (user.getEmail() == null || user.getEmail().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tài khoản chưa có email.");

        String plain = PasswordGenerator.generate(12);
        user.setMatKhau(passwordEncoder.encode(plain));
        userAccountRepository.saveAndFlush(user);

        try {
            emailService.sendPasswordResetEmail(user.getEmail(), username, plain);
        } catch (org.springframework.mail.MailAuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "SMTP authentication failed");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Gửi email thất bại: " + e.getMessage());
        }
    }

    @Transactional
    public void lockUser(String username) {
        UserAccount user = userAccountRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Khóa tài khoản
        user.setTrangThai(TrangThaiUserAccount.KHOA);
        user.setNgayCapNhat(LocalDateTime.now());
        userAccountRepository.save(user);

        // Log hành động
        log.info("Admin {} đã khóa tài khoản {}", getCurrentAdmin(), username);
    }

    @Transactional
    public void unlockUser(String username) {
        UserAccount user = userAccountRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Mở khóa tài khoản
        user.setTrangThai(TrangThaiUserAccount.HOAT_DONG);
        user.setNgayCapNhat(LocalDateTime.now());
        userAccountRepository.save(user);

        // Log hành động
        log.info("Admin {} đã mở khóa tài khoản {}", getCurrentAdmin(), username);
    }

    // Helper method để lấy admin hiện tại
    private String getCurrentAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            return ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername();
        }
        return "Unknown";
    }

    // Helper method để kiểm tra có dữ liệu liên quan không
    private boolean hasRelatedData(UserAccount user) {
        // Kiểm tra có booking, invoice, etc. không
        // Implement logic kiểm tra dữ liệu liên quan
        return false; // Tạm thời return false
    }


    @Transactional
    @Override
    public UserResponseDto adminUpdateUser(String username, AdminUpdateUserDto dto) {
        UserAccount u = userAccountRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy: " + username));

        // Các field ở UserAccount
        if (dto.getEmail() != null)       u.setEmail(dto.getEmail());
        if (dto.getTrangThai() != null)   u.setTrangThai(dto.getTrangThai());
        if (dto.getVaiTro() != null) {
            Role r = roleRepository.findByTenVaiTroIgnoreCase(dto.getVaiTro())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vai trò không tồn tại"));
            u.setVaiTro(r);
        }

        // Các field thuộc khách hàng → chỉ cập nhật trên khach_hang
        Customer kh = u.getKhachHang();
        if (kh == null) {
            kh = new Customer();
            // nếu có khóa ngoại 1-1: kh.setUserAccount(u);
            u.setKhachHang(kh);
        }
        if (dto.getHoTen() != null)       kh.setTenKhachHang(dto.getHoTen());
        if (dto.getSoDienThoai() != null) kh.setSoDienThoaiKhachHang(dto.getSoDienThoai());
        if (dto.getNgaySinh() != null)    kh.setNgaySinh(dto.getNgaySinh());
        if (dto.getGioiTinh() != null)    kh.setGioiTinh(dto.getGioiTinh());
        if (dto.getDiaChi() != null)      kh.setDiaChi(dto.getDiaChi());
        kh.setNgayCapNhat(LocalDateTime.now());
        customerRepository.save(kh);

        u.setNgayCapNhat(LocalDateTime.now());
        userAccountRepository.saveAndFlush(u);
        return UserResponseDto.from(u);
    }

    private boolean isCustomer(UserAccount u) {
        Role role = u.getVaiTro();
        String code = role == null ? "" : role.getTenVaiTro(); // ví dụ: CUSTOMER, EMPLOYEE, ADMIN
        return "CUSTOMER".equalsIgnoreCase(code) || "USER".equalsIgnoreCase(code);
    }

    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        // Không tiết lộ tồn tại/không tồn tại
        userAccountRepository.findByEmail(email).ifPresent(user -> {
            String temp = PasswordGenerator.generate(12);           // <- đúng
            String encoded = passwordEncoder.encode(temp);
            user.setMatKhau(encoded);
            // Nếu chưa có cột, đừng gọi setRequireChangePassword()
            // try { user.setRequireChangePassword(true); } catch (Exception ignored) {}

            userAccountRepository.save(user);

            String username = user.getTenDangNhap();
            emailService.sendTempPassword(email, username, temp);
        });
    }

    @Override
    public List<UserResponseDto> getTrashUsers() {
        return userAccountRepository.findAllTrashWithJoins()
                .stream().map(UserResponseDto::from).toList();
    }

    @Transactional
    public void softDeleteUser(String username) {
        UserAccount u = userAccountRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy: " + username));
        u.setDaXoa(true);                                    // QUAN TRỌNG
        u.setTrangThai(TrangThaiUserAccount.KHOA);           // tùy chính sách
        u.setNgayCapNhat(LocalDateTime.now());
        userAccountRepository.save(u);
    }

    @Transactional
    public void restoreUser(String username) {
        UserAccount u = userAccountRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy: " + username));

        u.setDaXoa(false);
        // Cho chính sách “khôi phục = hoạt động”
        u.setTrangThai(TrangThaiUserAccount.HOAT_DONG); // <- thêm dòng này
        u.setNgayCapNhat(LocalDateTime.now());
        userAccountRepository.save(u);
    }

    @Transactional
    @Override
    public void hardDeleteUser(String username) {
        UserAccount u = userAccountRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy: " + username));

        if (hasRelatedData(u)) {
            throw new IllegalStateException("Không thể xóa vì có dữ liệu liên quan");
        }
        userAccountRepository.delete(u);
        log.info("Admin {} đã HARD DELETE {}", getCurrentAdmin(), username);
    }

    public String registerEmployeeSelf(EmployeeRegisterDto dto) {
        // Check tồn tại
        if (userAccountRepository.findByTenDangNhap(dto.getTenDangNhap()).isPresent()) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }
        if (userAccountRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        // Tạo tài khoản
        UserAccount account = new UserAccount();
        account.setTenDangNhap(dto.getTenDangNhap());
        account.setMatKhau(passwordEncoder.encode(dto.getMatKhau()));
        account.setEmail(dto.getEmail());
        Role defaultRole = roleRepository.findByTenVaiTroIgnoreCase("THU_NGAN")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy role EMPLOYEE"));
        account.setVaiTro(defaultRole);
        userAccountRepository.save(account);

        // Tạo nhân viên
        Employee nv = new Employee();
        nv.setTenNhanVien(dto.getTenNhanVien());
        nv.setSoDienThoai(dto.getSoDienThoai());
        nv.setNgaySinh(LocalDate.parse(dto.getNgaySinh()));
        nv.setGioiTinh(dto.getGioiTinh());
        nv.setCccd(dto.getCccd());
        nv.setChucVu(dto.getChucVu());

        // Lấy rạp chiếu từ DB
        Theater rap = theaterRepository.findById(dto.getIdRapChieu())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy rạp chiếu với id = " + dto.getIdRapChieu()));
        nv.setRapChieu(rap);

        // Gán tài khoản cho nhân viên
        //nv.setUserAccount(account);

        employeeRepository.save(nv);


        return "Đăng ký nhân viên thành công";
    }
}
