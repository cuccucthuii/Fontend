package org.example.cinema_reservation_system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "khach_hang_voucher")
@IdClass(CustomerVoucher.CustomerVoucherId.class)
public class CustomerVoucher {
    
    @Id
    @ManyToOne
    @JoinColumn(name = "id_khach_hang")
    private Customer khachHang;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_voucher")
    private Voucher voucher;

    @Column(name = "ngay_nhan")
    private LocalDate ngayNhan = LocalDate.now();
    
    @Column(name = "trang_thai")
    private String trangThai = "CHUA_SU_DUNG";
    
    // Composite key class
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CustomerVoucherId implements java.io.Serializable {
        private Integer khachHang;
        private Integer voucher;
    }
    
    // Convenience methods
    public Customer getCustomer() {
        return khachHang;
    }
    
    public void setCustomer(Customer customer) {
        this.khachHang = customer;
    }
}
