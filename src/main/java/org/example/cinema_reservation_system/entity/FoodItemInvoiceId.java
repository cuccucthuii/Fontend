package org.example.cinema_reservation_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodItemInvoiceId {
    @Column(name = "id_hoa_don")
    private Integer idHoaDon;

    @Column(name = "id_mon")
    private Integer idMon;
}
