package org.example.cinema_reservation_system.utils;

import java.security.SecureRandom;

public final class PasswordGenerator {
    private static final String UPPER = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijkmnopqrstuvwxyz";
    private static final String DIGIT = "23456789";
    private static final String ALL = UPPER + LOWER + DIGIT;
    private static final SecureRandom RNG = new SecureRandom();

    // Sinh mật khẩu chỉ gồm chữ + số, độ dài 8–10
    public static String generate(int length) {
        // Ép dài về khoảng 8–10
        if (length < 8) length = 8;
        if (length > 10) length = 10;

        StringBuilder sb = new StringBuilder(length);
        // Bảo đảm có đủ nhóm ký tự tối thiểu
        sb.append(pick(UPPER))
                .append(pick(LOWER))
                .append(pick(DIGIT));

        // Bổ sung phần còn lại bằng tập ALL (chỉ chữ + số)
        for (int i = sb.length(); i < length; i++) {
            sb.append(pick(ALL));
        }
        return sb.toString();
    }

    private static char pick(String s) {
        return s.charAt(RNG.nextInt(s.length()));
    }

    private PasswordGenerator() {}
}