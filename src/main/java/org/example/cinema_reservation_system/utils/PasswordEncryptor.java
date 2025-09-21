package org.example.cinema_reservation_system.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cinema_reservation_system.entity.UserAccount;
import org.example.cinema_reservation_system.repository.useraccount.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PasswordEncryptor implements CommandLineRunner {

    private final UserAccountRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        List<UserAccount> users = userRepo.findAll();
        int count = 0;

        for (UserAccount user : users) {
            String rawPassword = user.getMatKhau();

            // Nếu chưa mã hoá (BCrypt luôn bắt đầu bằng $2a$, $2b$...)
            if (rawPassword != null && !rawPassword.startsWith("$2a$")) {
                String encoded = passwordEncoder.encode(rawPassword);
                user.setMatKhau(encoded);
                count++;
                log.info("✅ Mã hoá mật khẩu cho user: {}", user.getTenDangNhap());
            }
        }

        if (count > 0) {
            userRepo.saveAll(users);
            log.info(" Đã mã hoá xong {} tài khoản!", count);
        } else {
            log.info("⚠️ Không có mật khẩu nào cần mã hoá.");
        }
    }
}