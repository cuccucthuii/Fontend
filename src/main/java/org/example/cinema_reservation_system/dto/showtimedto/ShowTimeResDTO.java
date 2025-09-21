package org.example.cinema_reservation_system.dto.showtimedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowTimeResDTO {

    private boolean success;
    private String message;
    private DataContent data;

    @Data
    public static class DataContent {
        private MovieInfo movie;
        private CinemaInfo cinema;
        private List<AvailableDate> availableDates;
        private List<ShowtimeInfo> showtimes;
    }

    @Data
    public static class MovieInfo {
        private Long id;
        private String name;
        private String posterUrl;
    }

    @Data
    public static class CinemaInfo {
        private Long id;
        private String name;
    }

    @Data
    public static class AvailableDate {
        private String value;
        private int day;
        private String month;
        private String weekday;
    }

    @Data
    public static class ShowtimeInfo {
        private Long id;
        private String date;
        private String time;
        private String endTime;
        private int availableSeats;
        private int totalSeats;
        private boolean soldOut;
        private String format;
        // Bổ sung thêm các field lấy từ query
        private Long movieId;
        private String movieName;
        private String posterUrl;
        private Long cinemaId;
        private String cinemaName;
        // Thông tin phòng chiếu
        private Long roomId;
        private String roomName;
        
        // Mapping từ JSON database
        public void setIdSuatChieu(Long idSuatChieu) {
            this.id = idSuatChieu;
        }
        
        public void setNgayChieu(String ngayChieu) {
            this.date = ngayChieu;
        }
        
        public void setStartTime(String startTime) {
            this.time = startTime;
        }
        
        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
        
        public void setAvailableSeats(int availableSeats) {
            this.availableSeats = availableSeats;
        }
        
        public void setTotalSeats(int totalSeats) {
            this.totalSeats = totalSeats;
        }
        
        public void setSoldOut(boolean soldOut) {
            this.soldOut = soldOut;
        }
        
        public void setFormat(String format) {
            this.format = format;
        }
        
        public void setMovieId(Long movieId) {
            this.movieId = movieId;
        }
        
        public void setTenPhim(String tenPhim) {
            this.movieName = tenPhim;
        }
        
        public void setPosterUrl(String posterUrl) {
            this.posterUrl = posterUrl;
        }
        
        public void setCinemaId(Long cinemaId) {
            this.cinemaId = cinemaId;
        }
        
        public void setCinemaName(String cinemaName) {
            this.cinemaName = cinemaName;
        }
        
        public void setRoomId(Long roomId) {
            this.roomId = roomId;
        }
        
        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }
    }
}