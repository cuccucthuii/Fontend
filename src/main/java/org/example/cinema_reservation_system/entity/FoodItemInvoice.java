package org.example.cinema_reservation_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hoa_don_mon")
public class FoodItemInvoice {
    
    @EmbeddedId
    private FoodItemInvoiceId id;
    
    @ManyToOne
    @MapsId("idHoaDon")
    @JoinColumn(name = "id_hoa_don", nullable = false)
    private Invoice hoaDon;
    
    @ManyToOne
    @MapsId("idMon")
    @JoinColumn(name = "id_mon", nullable = false)
    private FoodItem monAn;
    
    @Column(name = "so_luong", nullable = false)
    private Integer soLuong = 1;
    
    @Column(name = "gia_mon", nullable = false, precision = 10, scale = 2)
    private BigDecimal giaMon;
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FoodItemInvoiceId {
        @Column(name = "id_hoa_don")
        private Integer idHoaDon;
        
        @Column(name = "id_mon")
        private Integer idMon;
    }
}
