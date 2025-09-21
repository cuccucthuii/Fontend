package org.example.cinema_reservation_system.service.ai;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.entity.Movie;
import org.example.cinema_reservation_system.repository.movie.MovieRepository;
import org.example.cinema_reservation_system.utils.enums.TrangThaiPhim;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieRecommendationService {

    private final MovieRepository movieRepository;

    /**
     * Get movie recommendations based on user preferences
     */
    public List<Movie> getRecommendations(String genre, String ageRating, String format, String timePreference) {
        List<Movie> allMovies = movieRepository.findByTrangThai(TrangThaiPhim.DANG_CHIEU);
        
        return allMovies.stream()
                .filter(movie -> filterByGenre(movie, genre))
                .filter(movie -> filterByAgeRating(movie, ageRating))
                .filter(movie -> filterByFormat(movie, format))
                .sorted((m1, m2) -> sortByTimePreference(m1, m2, timePreference))
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * Get top movies by criteria
     */
    public List<Movie> getTopMovies(String criteria, int limit) {
        List<Movie> movies = movieRepository.findByTrangThai(TrangThaiPhim.DANG_CHIEU);
        
        switch (criteria.toLowerCase()) {
            case "newest":
                return movies.stream()
                        .sorted((m1, m2) -> m2.getNgayPhatHanh().compareTo(m1.getNgayPhatHanh()))
                        .limit(limit)
                        .collect(Collectors.toList());
                        
            case "popular":
                // Sort by release date (newer = more popular for now)
                return movies.stream()
                        .sorted((m1, m2) -> m2.getNgayPhatHanh().compareTo(m1.getNgayPhatHanh()))
                        .limit(limit)
                        .collect(Collectors.toList());
                        
            case "family":
                return movies.stream()
                        .filter(movie -> "P".equals(movie.getTuoiGioiHan()) || "T13".equals(movie.getTuoiGioiHan()))
                        .limit(limit)
                        .collect(Collectors.toList());
                        
            default:
                return movies.stream().limit(limit).collect(Collectors.toList());
        }
    }

    /**
     * Compare two movies
     */
    public Map<String, Object> compareMovies(String movie1Name, String movie2Name) {
        Optional<Movie> movie1Opt = movieRepository.findByTenPhim(movie1Name);
        Optional<Movie> movie2Opt = movieRepository.findByTenPhim(movie2Name);
        
        Map<String, Object> comparison = new HashMap<>();
        
        if (movie1Opt.isPresent() && movie2Opt.isPresent()) {
            Movie movie1 = movie1Opt.get();
            Movie movie2 = movie2Opt.get();
            
            comparison.put("movie1", createMovieSummary(movie1));
            comparison.put("movie2", createMovieSummary(movie2));
            comparison.put("similarities", findSimilarities(movie1, movie2));
            comparison.put("differences", findDifferences(movie1, movie2));
            comparison.put("recommendation", generateComparisonRecommendation(movie1, movie2));
        }
        
        return comparison;
    }

    /**
     * Get movies suitable for specific audience
     */
    public List<Movie> getMoviesForAudience(String audience) {
        List<Movie> movies = movieRepository.findByTrangThai(TrangThaiPhim.DANG_CHIEU);
        
        switch (audience.toLowerCase()) {
            case "family":
            case "gia đình":
                return movies.stream()
                        .filter(movie -> "P".equals(movie.getTuoiGioiHan()))
                        .collect(Collectors.toList());
                        
            case "children":
            case "trẻ em":
                return movies.stream()
                        .filter(movie -> "P".equals(movie.getTuoiGioiHan()))
                        .collect(Collectors.toList());
                        
            case "teenagers":
            case "thiếu niên":
                return movies.stream()
                        .filter(movie -> "P".equals(movie.getTuoiGioiHan()) || "T13".equals(movie.getTuoiGioiHan()))
                        .collect(Collectors.toList());
                        
            case "adults":
            case "người lớn":
                return movies.stream()
                        .filter(movie -> !"P".equals(movie.getTuoiGioiHan()))
                        .collect(Collectors.toList());
                        
            default:
                return movies;
        }
    }

    // Helper methods
    private boolean filterByGenre(Movie movie, String genre) {
        if (genre == null || genre.trim().isEmpty()) return true;
        
        // This is a simplified filter - in real implementation, you'd check movie.getTheLoais()
        return true; // For now, return all movies
    }

    private boolean filterByAgeRating(Movie movie, String ageRating) {
        if (ageRating == null || ageRating.trim().isEmpty()) return true;
        
        String movieAgeRating = movie.getTuoiGioiHan();
        if (movieAgeRating == null) return true;
        
        switch (ageRating.toLowerCase()) {
            case "p":
            case "mọi lứa tuổi":
                return "P".equals(movieAgeRating);
            case "t13":
            case "13+":
                return "P".equals(movieAgeRating) || "T13".equals(movieAgeRating);
            case "t16":
            case "16+":
                return "P".equals(movieAgeRating) || "T13".equals(movieAgeRating) || "T16".equals(movieAgeRating);
            case "t18":
            case "18+":
                return true; // All movies
            default:
                return true;
        }
    }

    private boolean filterByFormat(Movie movie, String format) {
        if (format == null || format.trim().isEmpty()) return true;
        
        String movieFormat = movie.getDinhDang();
        if (movieFormat == null) return true;
        
        return movieFormat.equalsIgnoreCase(format);
    }

    private int sortByTimePreference(Movie m1, Movie m2, String timePreference) {
        if (timePreference == null || timePreference.trim().isEmpty()) {
            return m2.getNgayPhatHanh().compareTo(m1.getNgayPhatHanh()); // Newer first
        }
        
        // For now, just sort by release date
        return m2.getNgayPhatHanh().compareTo(m1.getNgayPhatHanh());
    }

    private Map<String, Object> createMovieSummary(Movie movie) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("name", movie.getTenPhim());
        summary.put("description", movie.getMoTa());
        summary.put("duration", movie.getThoiLuong());
        summary.put("ageRating", movie.getTuoiGioiHan());
        summary.put("format", movie.getDinhDang());
        summary.put("releaseDate", movie.getNgayPhatHanh());
        summary.put("year", movie.getNamSanXuat());
        return summary;
    }

    private List<String> findSimilarities(Movie movie1, Movie movie2) {
        List<String> similarities = new ArrayList<>();
        
        if (Objects.equals(movie1.getDinhDang(), movie2.getDinhDang())) {
            similarities.add("Cùng định dạng: " + movie1.getDinhDang());
        }
        
        if (Objects.equals(movie1.getTuoiGioiHan(), movie2.getTuoiGioiHan())) {
            similarities.add("Cùng độ tuổi: " + movie1.getTuoiGioiHan());
        }
        
        if (Objects.equals(movie1.getNamSanXuat(), movie2.getNamSanXuat())) {
            similarities.add("Cùng năm sản xuất: " + movie1.getNamSanXuat());
        }
        
        return similarities;
    }

    private List<String> findDifferences(Movie movie1, Movie movie2) {
        List<String> differences = new ArrayList<>();
        
        if (!Objects.equals(movie1.getDinhDang(), movie2.getDinhDang())) {
            differences.add("Định dạng: " + movie1.getDinhDang() + " vs " + movie2.getDinhDang());
        }
        
        if (!Objects.equals(movie1.getTuoiGioiHan(), movie2.getTuoiGioiHan())) {
            differences.add("Độ tuổi: " + movie1.getTuoiGioiHan() + " vs " + movie2.getTuoiGioiHan());
        }
        
        if (!Objects.equals(movie1.getThoiLuong(), movie2.getThoiLuong())) {
            differences.add("Thời lượng: " + movie1.getThoiLuong() + " phút vs " + movie2.getThoiLuong() + " phút");
        }
        
        return differences;
    }

    private String generateComparisonRecommendation(Movie movie1, Movie movie2) {
        StringBuilder recommendation = new StringBuilder();
        
        // Simple recommendation logic
        if ("P".equals(movie1.getTuoiGioiHan()) && !"P".equals(movie2.getTuoiGioiHan())) {
            recommendation.append("Nếu đi cùng gia đình, nên chọn ").append(movie1.getTenPhim());
        } else if (!"P".equals(movie1.getTuoiGioiHan()) && "P".equals(movie2.getTuoiGioiHan())) {
            recommendation.append("Nếu đi cùng gia đình, nên chọn ").append(movie2.getTenPhim());
        } else {
            recommendation.append("Cả hai phim đều phù hợp, tùy thuộc vào sở thích cá nhân");
        }
        
        return recommendation.toString();
    }
}

