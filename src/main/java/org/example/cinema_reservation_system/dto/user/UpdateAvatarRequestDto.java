package org.example.cinema_reservation_system.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAvatarRequestDto {
    
    @NotNull(message = "ID avatar không được để trống")
    private Long avatarId;
}

