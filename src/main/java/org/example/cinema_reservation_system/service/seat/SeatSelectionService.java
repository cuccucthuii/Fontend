package org.example.cinema_reservation_system.service.seat;

import org.example.cinema_reservation_system.dto.SeatSelectionDTO;

import java.util.Set;

public interface SeatSelectionService {
    SeatSelectionDTO.AvailabilityResponse getAvailability(Long showTimeId);

    SeatSelectionDTO.HoldResponse holdSeats(SeatSelectionDTO.HoldRequest request);

    void releaseSeats(SeatSelectionDTO.ReleaseRequest request);

    boolean confirmSeats(SeatSelectionDTO.ConfirmRequest request);

    boolean areSeatsAvailable(Long showTimeId, Set<Long> seatIds);
}












































