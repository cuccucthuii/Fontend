package org.example.cinema_reservation_system.service.user;

import jakarta.validation.Valid;
import org.example.cinema_reservation_system.dto.user.*;
import org.example.cinema_reservation_system.dto.userdto.UserRequestDto;
import org.example.cinema_reservation_system.dto.userdto.UserResponseDto;
import org.example.cinema_reservation_system.dto.auth.LoginDto;
import org.example.cinema_reservation_system.dto.auth.ChangePasswordDto;
import org.example.cinema_reservation_system.entity.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserService {

    /**
     * Lấy tất cả người dùng
     */
    List<UserResponseDto> findAll();

    /**
     * Lấy người dùng theo ID
     */
    Optional<UserResponseDto> findById(Integer id);

    /**
     * Lấy người dùng theo email
     */
    Optional<UserResponseDto> findByEmail(String email);

    /**
     * Lấy người dùng theo username
     */
    Optional<UserResponseDto> findByUsername(String username);

    /**
     * Tạo người dùng mới
     */
    UserResponseDto createUser(UserRequestDto userRequest);

    /**
     * Cập nhật thông tin người dùng
     */
    UserResponseDto updateUser(Integer id, UserRequestDto userRequest);

    /**
     * Xóa người dùng
     */
    void deleteUser(Integer id);

    /**
     * Thay đổi trạng thái người dùng
     */
    UserResponseDto changeUserStatus(Integer id, String status);

    /**
     * Lấy danh sách người dùng với phân trang
     */
    Page<UserResponseDto> findAllWithPagination(Pageable pageable);

    /**
     * Tìm kiếm người dùng
     */
    List<UserResponseDto> searchUsers(String keyword);

    /**
     * Lấy người dùng theo vai trò
     */
    List<UserResponseDto> findByRole(String role);

    /**
     * Cập nhật mật khẩu
     */
    void updatePassword(Integer id, String newPassword);

    /**
     * Xác thực người dùng
     */
    boolean authenticateUser(String email, String password);

    // ========== AUTH METHODS ==========

    /**
     * Đăng ký khách hàng
     */
    void registerCustomer(CustomerRegisterDto dto);

    /**
     * Đăng ký nhân viên (Admin tạo tài khoản cho nhân viên đã có)
     */
    String registerEmployee(EmployeeRegisterDto dto);

    /**
     * Đăng nhập
     */
    UserAccount login(LoginDto dto);

    /**
     * Yêu cầu đặt lại mật khẩu
     */
    void requestPasswordReset(String email);

    /**
     * Đặt lại mật khẩu
     */
    void resetPassword(String token, String newPassword);

    /**
     * Thay đổi mật khẩu
     */
    void changePassword(Integer userId, String currentPassword, String newPassword);

    /**
     * Thay đổi mật khẩu (overloaded method với DTO)
     */
    void changePassword(ChangePasswordDto dto);

    // ========== ADDITIONAL METHODS FOR USERCONTROLLER ==========

    /**
     * Lấy tất cả người dùng (alias cho findAll)
     */
    List<UserResponseDto> getAllUsers();

    /**
     * Cập nhật thông tin người dùng hiện tại
     */
    UserResponseDto updateCurrentUser(UserUpdateDto dto);

    /**
     * Xóa người dùng theo tên đăng nhập
     */
    void deleteByTenDangNhap(String tenDangNhap);

    /**
     * Cập nhật avatar người dùng
     */
    UserResponseDto updateAvatar(UpdateAvatarRequestDto dto);

    // ✅ THÊM CÁC METHOD MỚI:
    void lockUser(String username);
    void unlockUser(String username);
    void softDeleteUser(String username);

    @Transactional
    void restoreUser(String username);

    void hardDeleteUser(String username);

    void adminResetPassword(String username); // shorthand: tự sinh
    UserResponseDto adminUpdateUser(String username, AdminUpdateUserDto dto);

    List<UserResponseDto> getTrashUsers();

    String registerEmployeeSelf(@Valid EmployeeRegisterDto employeeRegisterDto);
}
