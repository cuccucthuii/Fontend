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
@Table(name = "hoa_don_combo")
public class ComboInvoice {
    
    @EmbeddedId
    private ComboInvoiceId id;
    
    @ManyToOne
    @MapsId("idHoaDon")
    @JoinColumn(name = "id_hoa_don", nullable = false)
    private Invoice hoaDon;
    
    @ManyToOne
    @MapsId("idCombo")
    @JoinColumn(name = "id_combo", nullable = false)
    private FoodCombo combo;
    
    @Column(name = "so_luong", nullable = false)
    private Integer soLuong = 1;
    
    @Column(name = "gia_combo", nullable = false, precision = 10, scale = 2)
    private BigDecimal giaCombo;
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComboInvoiceId {
        @Column(name = "id_hoa_don")
        private Integer idHoaDon;
        
        @Column(name = "id_combo")
        private Integer idCombo;
    }
}


