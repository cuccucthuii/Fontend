package org.example.cinema_reservation_system.service.utility;

import org.example.cinema_reservation_system.dto.auth.SocialLoginRequestDto;
import org.example.cinema_reservation_system.dto.auth.SocialLoginResponseDto;

public interface SocialLoginService {
    
    // Xử lý đăng nhập bằng Google
    SocialLoginResponseDto loginWithGoogle(SocialLoginRequestDto request);
    
    // Xử lý đăng nhập bằng Facebook
    SocialLoginResponseDto loginWithFacebook(SocialLoginRequestDto request);
    
    // Xử lý đăng nhập chung cho tất cả provider
    SocialLoginResponseDto processSocialLogin(SocialLoginRequestDto request);
    
    // Liên kết tài khoản hiện tại với social account
    SocialLoginResponseDto linkSocialAccount(Integer userId, SocialLoginRequestDto request);
    
    // Hủy liên kết social account
    void unlinkSocialAccount(Integer userId, String provider);
    
    // Lấy danh sách social account của user
    SocialLoginResponseDto getLinkedAccounts(Integer userId);
    
    // Xác thực token từ social provider
    boolean validateSocialToken(String token, String provider);
}
