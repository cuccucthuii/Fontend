package org.example.cinema_reservation_system.controller;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.entity.Customer;
import org.example.cinema_reservation_system.entity.Notification;
import org.example.cinema_reservation_system.repository.NotificationRepository;
import org.example.cinema_reservation_system.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @PostMapping
    public ResponseEntity<Notification> create(@RequestParam(required = false) Integer customerId,
                                               @RequestParam String tieuDe,
                                               @RequestParam String noiDung,
                                               @RequestParam(required = false) String loai,
                                               @RequestParam(defaultValue = "IN_APP") String kenh,
                                               @RequestParam(required = false) String metadata) {
        Customer customer = null;
        if (customerId != null) {
            customer = new Customer();
            customer.setIdKhachHang(customerId);
        }
        Notification n = notificationService.createForCustomer(customer, tieuDe, noiDung, loai, kenh, metadata);
        return ResponseEntity.ok(n);
    }

    @GetMapping("/by-customer/{customerId}")
    public ResponseEntity<List<Notification>> listByCustomer(@PathVariable Integer customerId) {
        return ResponseEntity.ok(notificationService.findByCustomerId(customerId));
    }
}




















