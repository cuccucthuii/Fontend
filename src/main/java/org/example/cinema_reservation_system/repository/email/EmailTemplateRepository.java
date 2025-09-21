package org.example.cinema_reservation_system.repository.email;

import org.example.cinema_reservation_system.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Integer> {
    
    // Tìm template theo loại
    Optional<EmailTemplate> findByLoaiTemplateAndTrangThaiTrue(String loaiTemplate);
    
    // Tìm template theo tên
    Optional<EmailTemplate> findByTenTemplateAndTrangThaiTrue(String tenTemplate);
    
    // Lấy tất cả template đang hoạt động
    List<EmailTemplate> findByTrangThaiTrue();
    
    // Lấy template theo loại
    List<EmailTemplate> findByLoaiTemplateAndTrangThaiTrueOrderByNgayTaoDesc(String loaiTemplate);
}


















































