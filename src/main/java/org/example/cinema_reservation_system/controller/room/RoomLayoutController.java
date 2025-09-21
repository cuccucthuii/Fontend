package org.example.cinema_reservation_system.controller.room;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.roomdto.RoomLayoutDto;
import org.example.cinema_reservation_system.dto.roomdto.RoomLayoutRequestDto;
import org.example.cinema_reservation_system.service.room.RoomLayoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/phong-layout")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class RoomLayoutController {

    private final RoomLayoutService roomLayoutService;

    @GetMapping
    public ResponseEntity<List<RoomLayoutDto>> getAllRoomLayouts() {
        return ResponseEntity.ok(roomLayoutService.getAllRoomLayouts());
    }

    @GetMapping("/{idPhong}")
    public ResponseEntity<RoomLayoutDto> getRoomLayout(@PathVariable Integer idPhong) {
        return ResponseEntity.ok(roomLayoutService.getRoomLayout(idPhong));
    }

    @PostMapping
    public ResponseEntity<RoomLayoutDto> createRoomLayout(@Valid @RequestBody RoomLayoutRequestDto request) {
        return ResponseEntity.ok(roomLayoutService.createRoomLayout(request));
    }

    @PutMapping("/{idPhong}")
    public ResponseEntity<RoomLayoutDto> updateRoomLayout(@PathVariable Integer idPhong, 
                                                         @Valid @RequestBody RoomLayoutRequestDto request) {
        return ResponseEntity.ok(roomLayoutService.updateRoomLayout(idPhong, request));
    }

    @DeleteMapping("/{idPhong}")
    public ResponseEntity<Map<String, Object>> deleteRoomLayout(@PathVariable Integer idPhong) {
        return ResponseEntity.ok(roomLayoutService.deleteRoomLayout(idPhong));
    }

    @GetMapping("/calculate-seats")
    public ResponseEntity<Map<String, Object>> calculateSeatsFromArea(@RequestParam Double dienTich) {
        return ResponseEntity.ok(roomLayoutService.calculateSeatsFromArea(dienTich));
    }

    @GetMapping("/generate-seats")
    public ResponseEntity<List<RoomLayoutDto.SeatPositionDto>> generateSeatsFromGrid(
            @RequestParam Integer chieuRong, 
            @RequestParam Integer chieuDai) {
        return ResponseEntity.ok(roomLayoutService.generateSeatsFromGrid(chieuRong, chieuDai));
    }
} 