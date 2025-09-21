package org.example.cinema_reservation_system.service;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.entity.Customer;
import org.example.cinema_reservation_system.entity.Notification;
import org.example.cinema_reservation_system.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification createForCustomer(Customer customer, String tieuDe, String noiDung, String loai, String kenh, String metadata) {
        Notification n = new Notification();
        n.setKhachHang(customer);
        n.setTieuDe(tieuDe);
        n.setNoiDung(noiDung);
        n.setLoai(loai);
        n.setKenh(kenh);
        n.setTrangThai("PENDING");
        n.setNgayTao(LocalDateTime.now());
        n.setThoiGianGui(LocalDateTime.now());
        n.setMetadata(metadata);
        return notificationRepository.save(n);
    }

    public List<Notification> findByCustomerId(Integer customerId) {
        // đơn giản: lọc tại memory cho nhanh, có thể đổi sang query method
        return notificationRepository.findAll().stream()
                .filter(n -> n.getKhachHang() != null && n.getKhachHang().getIdKhachHang().equals(customerId))
                .toList();
    }
}




















