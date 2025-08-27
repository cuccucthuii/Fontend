package org.example.cinema_reservation_system.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.cinema_reservation_system.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmployeeIdEmail(String toEmail, Integer idNhanVien) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Thông báo ID nhân viên DEV CINEMA");
        message.setText("Chào bạn,\nBạn vừa được thêm vào hệ thống DEV CINEMA với vai trò nhân viên.\nID nhân viên của bạn là: " + idNhanVien + "\nHãy dùng ID này để đăng ký tài khoản trên hệ thống.");
        mailSender.send(message);
    }
}