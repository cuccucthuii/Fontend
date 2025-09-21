package org.example.cinema_reservation_system.service.utility;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.auth.SocialLoginRequestDto;
import org.example.cinema_reservation_system.dto.auth.SocialLoginResponseDto;
import org.example.cinema_reservation_system.entity.Customer;
import org.example.cinema_reservation_system.entity.Role;
import org.example.cinema_reservation_system.entity.SocialAccount;
import org.example.cinema_reservation_system.entity.UserAccount;
import org.example.cinema_reservation_system.exception.BadRequestException;
import org.example.cinema_reservation_system.exception.ResourceNotFoundException;
import org.example.cinema_reservation_system.repository.customer.CustomerRepository;
import org.example.cinema_reservation_system.repository.role.RoleRepository;
import org.example.cinema_reservation_system.repository.social.SocialAccountRepository;
import org.example.cinema_reservation_system.repository.useraccount.UserAccountRepository;
import org.example.cinema_reservation_system.security.JwtService;
import org.example.cinema_reservation_system.service.utility.SocialLoginService;
import org.example.cinema_reservation_system.utils.enums.TrangThaiUserAccount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SocialLoginServiceImpl implements SocialLoginService {

    private final UserAccountRepository userAccountRepository;
    private final CustomerRepository customerRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.facebook.client-id}")
    private String facebookClientId;

    @Value("${spring.security.oauth2.client.registration.facebook.client-secret}")
    private String facebookClientSecret;

    @Override
    public SocialLoginResponseDto loginWithGoogle(SocialLoginRequestDto request) {
        // Xác thực Google ID token
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        try {
            GoogleIdToken idToken = verifier.verify(request.getAccessToken());
            if (idToken != null) {
                Payload payload = idToken.getPayload();

                // Lấy thông tin user từ Google
                String googleUserId = payload.getSubject();
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String picture = (String) payload.get("picture");

                return processSocialLoginInternal("GOOGLE", googleUserId, email, name, picture, request);
            } else {
                throw new BadRequestException("Token Google không hợp lệ");
            }
        } catch (Exception e) {
            throw new BadRequestException("Lỗi xác thực Google: " + e.getMessage());
        }
    }

    @Override
    public SocialLoginResponseDto loginWithFacebook(SocialLoginRequestDto request) {
        try {
            // Xác thực Facebook token bằng cách gọi Facebook Graph API
            String graphApiUrl = String.format(
                    "https://graph.facebook.com/me?fields=id,name,email,picture&access_token=%s",
                    request.getAccessToken()
            );

            // Trong thực tế, bạn sẽ sử dụng RestTemplate hoặc WebClient để gọi API
            // Đây là mock data cho demo
            String facebookUserId = request.getSocialId();
            String email = request.getEmail();
            String name = request.getName();
            String picture = request.getPicture();

            return processSocialLoginInternal("FACEBOOK", facebookUserId, email, name, picture, request);
        } catch (Exception e) {
            throw new BadRequestException("Lỗi xác thực Facebook: " + e.getMessage());
        }
    }

    @Override
    public SocialLoginResponseDto processSocialLogin(SocialLoginRequestDto request) {
        switch (request.getProvider().toUpperCase()) {
            case "GOOGLE":
                return loginWithGoogle(request);
            case "FACEBOOK":
                return loginWithFacebook(request);
            default:
                throw new BadRequestException("Provider không được hỗ trợ: " + request.getProvider());
        }
    }

    private SocialLoginResponseDto processSocialLoginInternal(String provider, String socialUserId,
                                                              String email, String name, String picture,
                                                              SocialLoginRequestDto request) {

        // 1. Kiểm tra xem social account đã tồn tại chưa
        Optional<SocialAccount> existingSocialAccount = socialAccountRepository
                .findByProviderAndProviderUserId(provider, socialUserId);

        if (existingSocialAccount.isPresent()) {
            // User đã đăng nhập bằng social account này trước đó
            SocialAccount socialAccount = existingSocialAccount.get();
            UserAccount userAccount = socialAccount.getUserAccount();

            // Cập nhật thông tin
            updateUserAccount(userAccount, name, picture);
            updateSocialAccount(socialAccount, request.getAccessToken());

            return createLoginResponse(userAccount, socialAccount, false);
        }

        // 2. Kiểm tra xem email đã tồn tại trong hệ thống chưa
        Optional<UserAccount> existingUser = userAccountRepository.findByTenDangNhap(email);

        if (existingUser.isPresent()) {
            // Email đã tồn tại, liên kết social account với tài khoản hiện có
            UserAccount userAccount = existingUser.get();
            SocialAccount newSocialAccount = createSocialAccount(userAccount, provider, socialUserId,
                    email, name, picture, request.getAccessToken());

            return createLoginResponse(userAccount, newSocialAccount, false);
        }

        // 3. Tạo tài khoản mới
        UserAccount newUserAccount = createNewUserAccount(email, name, picture, provider, socialUserId);
        SocialAccount newSocialAccount = createSocialAccount(newUserAccount, provider, socialUserId,
                email, name, picture, request.getAccessToken());

        return createLoginResponse(newUserAccount, newSocialAccount, true);
    }

    private UserAccount createNewUserAccount(String email, String name, String picture, String provider, String socialUserId) {
        // Tạo customer mới
        Customer customer = new Customer();
        customer.setTenKhachHang(name);
        customer.setEmail(email);
        customer.setSoDienThoaiKhachHang(""); // Có thể để trống hoặc yêu cầu cập nhật sau
        customer.setNgaySinh(null);
        customer.setGioiTinh(null);
        customer.setDiaChi("");
        customer.setNgayTao(LocalDate.now());

        Customer savedCustomer = customerRepository.save(customer);

        // Lấy role CLIENT
        Role clientRole = roleRepository.findByTenVaiTroIgnoreCase("CLIENT")
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy role CLIENT"));

        // Tạo user account
        UserAccount userAccount = new UserAccount();
        userAccount.setTenDangNhap(email);
        userAccount.setMatKhau(null); // Không cần password cho social login
        userAccount.setLastLogin(LocalDateTime.now());
        userAccount.setVaiTro(clientRole);
        userAccount.setKhachHang(savedCustomer);
        userAccount.setTrangThai(TrangThaiUserAccount.HOAT_DONG);
        userAccount.setSocialId(socialUserId);
        userAccount.setSocialProvider(provider);
        userAccount.setAvatarUrl(picture);
        userAccount.setEmailVerified(true);
        userAccount.setSocialVerified(true);

        return userAccountRepository.save(userAccount);
    }

    private SocialAccount createSocialAccount(UserAccount userAccount, String provider, String socialUserId,
                                              String email, String name, String picture, String accessToken) {
        SocialAccount socialAccount = new SocialAccount();
        socialAccount.setUserAccount(userAccount);
        socialAccount.setProvider(provider);
        socialAccount.setProviderUserId(socialUserId);
        socialAccount.setProviderUserEmail(email);
        socialAccount.setProviderUserName(name);
        socialAccount.setProviderUserPicture(picture);
        socialAccount.setAccessToken(accessToken);
        socialAccount.setIsActive(true);

        return socialAccountRepository.save(socialAccount);
    }

    private void updateUserAccount(UserAccount userAccount, String name, String picture) {
        userAccount.setLastLogin(LocalDateTime.now());
        if (picture != null && !picture.isEmpty()) {
            userAccount.setAvatarUrl(picture);
        }
        userAccountRepository.save(userAccount);
    }

    private void updateSocialAccount(SocialAccount socialAccount, String accessToken) {
        socialAccount.setAccessToken(accessToken);
        socialAccount.setUpdatedAt(LocalDateTime.now());
        socialAccountRepository.save(socialAccount);
    }

    private SocialLoginResponseDto createLoginResponse(UserAccount userAccount, SocialAccount socialAccount, boolean isNewUser) {
        String token = jwtService.generateToken(userAccount);

        SocialLoginResponseDto response = new SocialLoginResponseDto();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(86400000L); // 24 hours
        response.setUserId(userAccount.getIdTaiKhoan());
        response.setUsername(userAccount.getTenDangNhap());
        response.setEmail(userAccount.getKhachHang() != null ? userAccount.getKhachHang().getEmail() : null);
        response.setRole(userAccount.getVaiTro().getTenVaiTro());
        response.setAvatarUrl(userAccount.getAvatarUrl());
        response.setProvider(socialAccount.getProvider());
        response.setIsNewUser(isNewUser);
        response.setMessage(isNewUser ? "Tài khoản mới được tạo thành công!" : "Đăng nhập thành công!");

        return response;
    }

    @Override
    public SocialLoginResponseDto linkSocialAccount(Integer userId, SocialLoginRequestDto request) {
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));

        // Kiểm tra xem đã liên kết với provider này chưa
        if (socialAccountRepository.existsByUserAccountIdAndProvider(userId, request.getProvider())) {
            throw new BadRequestException("Tài khoản đã được liên kết với " + request.getProvider());
        }

        // Xử lý social login và liên kết
        return processSocialLoginInternal(request.getProvider(), request.getSocialId(),
                request.getEmail(), request.getName(), request.getPicture(), request);
    }

    @Override
    public void unlinkSocialAccount(Integer userId, String provider) {
        Optional<SocialAccount> socialAccount = socialAccountRepository.findByUserAccountIdAndProvider(userId, provider);
        if (socialAccount.isPresent()) {
            socialAccountRepository.deactivateSocialAccount(socialAccount.get().getIdSocial());
        }
    }

    @Override
    public SocialLoginResponseDto getLinkedAccounts(Integer userId) {
        // Implementation để lấy danh sách social account đã liên kết
        return new SocialLoginResponseDto();
    }

    @Override
    public boolean validateSocialToken(String token, String provider) {
        try {
            switch (provider.toUpperCase()) {
                case "GOOGLE":
                    return validateGoogleToken(token);
                case "FACEBOOK":
                    return validateFacebookToken(token);
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validateGoogleToken(String token) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(token);
            return idToken != null;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validateFacebookToken(String token) {
        // Implementation để validate Facebook token
        // Trong thực tế, bạn sẽ gọi Facebook Graph API để validate
        return true; // Mock implementation
    }
}
