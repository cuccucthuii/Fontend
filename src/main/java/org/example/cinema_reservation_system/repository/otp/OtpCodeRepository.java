package org.example.cinema_reservation_system.repository.otp;

import org.example.cinema_reservation_system.entity.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findTopByPhoneAndOtpCodeAndIsUsedFalseAndExpiresAtAfter(
            String phone, String otpCode, LocalDateTime now);
    long countByPhoneAndCreatedAtAfter(String phone, LocalDateTime after);
}