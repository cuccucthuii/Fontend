package org.example.cinema_reservation_system.repository.movie;

import org.example.cinema_reservation_system.entity.MovieReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieReviewRepository extends JpaRepository<MovieReview, Integer> {
    
    // Tìm đánh giá theo phim
    List<MovieReview> findByPhim_IdPhim(Integer idPhim);
    
    // Tìm đánh giá theo khách hàng
    List<MovieReview> findByKhachHang_IdKhachHang(Integer idKhachHang);
    
    // Tìm đánh giá theo khách hàng và phim
    List<MovieReview> findByKhachHang_IdKhachHangAndPhim_IdPhim(Integer idKhachHang, Integer idPhim);
    
    // Tính điểm trung bình của phim
    @Query("SELECT AVG(mr.diemDanhGia) FROM MovieReview mr WHERE mr.phim.idPhim = :idPhim")
    Double getAverageRatingByMovie(@Param("idPhim") Integer idPhim);
    
    // Đếm số lượng đánh giá của phim
    @Query("SELECT COUNT(mr) FROM MovieReview mr WHERE mr.phim.idPhim = :idPhim")
    Long countReviewsByMovie(@Param("idPhim") Integer idPhim);
    
    // Lấy đánh giá theo điểm số
    @Query("SELECT mr FROM MovieReview mr WHERE mr.phim.idPhim = :idPhim AND mr.diemDanhGia = :rating")
    List<MovieReview> findByMovieAndRating(@Param("idPhim") Integer idPhim, @Param("rating") Integer rating);
    
    // Kiểm tra khách hàng đã đánh giá phim chưa
    boolean existsByKhachHang_IdKhachHangAndPhim_IdPhim(Integer idKhachHang, Integer idPhim);
}
