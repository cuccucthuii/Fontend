package org.example.cinema_reservation_system.service.movie;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.moviedto.MovieRequestDto;
import org.example.cinema_reservation_system.dto.moviedto.MovieResponseDto;
import org.example.cinema_reservation_system.entity.*;
import org.example.cinema_reservation_system.exception.ResourceNotFoundException;
import org.example.cinema_reservation_system.exception.BusinessException;
import org.example.cinema_reservation_system.mapper.MovieMapper;
import org.example.cinema_reservation_system.repository.actor.ActorRepository;
import org.example.cinema_reservation_system.repository.director.DirectorRepository;
import org.example.cinema_reservation_system.repository.genre.GenreRepository;
import org.example.cinema_reservation_system.repository.image.ImageNativeRepository;
import org.example.cinema_reservation_system.repository.movie.*;
import org.example.cinema_reservation_system.repository.trailer.TrailerRepository;
import org.example.cinema_reservation_system.service.movie.MovieService;
import org.example.cinema_reservation_system.service.storage.FileStorageService;
import org.example.cinema_reservation_system.utils.enums.LoaiHinhAnh;
import static org.example.cinema_reservation_system.validator.PhimValidator.validate;
import static org.example.cinema_reservation_system.validator.PhimValidator.validateImage;

import org.example.cinema_reservation_system.utils.enums.TrangThai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MovieServiceImpl implements MovieService {
    private final MovieRepository phimRepo;
    private final MovieMapper phimMapper;
    private final FileStorageService fileStorageService;
    private final DirectorRepository daoDienRepo;
    private final ActorRepository dienVienRepo;
    private final GenreRepository theLoaiRepo;
    private final ImageNativeRepository hinhAnhNativeRepo;
    private final EntityManager entityManager;
    private final TrailerRepository trailerRepository;

    @Override
    public List<MovieResponseDto> getAll() {
        return phimRepo.findAllFetchHinhAnh().stream()
                .map(phimMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public MovieResponseDto getById(Integer id) {
        Movie phim = phimRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim với ID: " + id));
        return phimMapper.toDto(phim);
    }

    @Override
    public MovieResponseDto create(MovieRequestDto dto, MultipartFile poster, MultipartFile banner) {
        validateRequest(dto, poster, banner, LocalDate.now());
        Movie phim = saveOrUpdateMovieEntity(null, dto, poster, banner);
        return phimMapper.toDto(phim);
    }

    @Override
    public MovieResponseDto update(Integer id, MovieRequestDto dto, MultipartFile poster, MultipartFile banner) {
        Movie phim = phimRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim với ID: " + id));
        validateRequest(dto, poster, banner, phim.getNgayTao());
        phim = saveOrUpdateMovieEntity(id, dto, poster, banner);
        return phimMapper.toDto(phim);
    }

    @Override
    public Map<String, Object> delete(Integer id) {
        Movie phim = phimRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim với ID: " + id));

        phimRepo.delete(phim);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Xóa phim thành công");
        result.put("idPhim", phim.getIdPhim());
        result.put("tenPhim", phim.getTenPhim());
        return result;
    }

    @Override
    public List<MovieResponseDto> filter(String theLoai, TrangThai trangThai) {
        return phimRepo.findAll().stream()
                .filter(phim -> {
                    boolean matchTrangThai = trangThai == null || phim.getTrangThai() == trangThai;
                    boolean matchTheLoai = true;
                    if (theLoai != null && !theLoai.isBlank()) {
                        matchTheLoai = phim.getTheLoaiList().stream()
                                .anyMatch(tl -> tl.getTenTheLoai().equalsIgnoreCase(theLoai));
                    }
                    return matchTrangThai && matchTheLoai;
                })
                .map(phimMapper::toDto)
                .collect(Collectors.toList());
    }

    private void validateRequest(MovieRequestDto dto, MultipartFile poster, MultipartFile banner, LocalDate ngayTao) {
        validate(dto, ngayTao);
        validateNames(dto);
        validateMedia(dto, poster, banner);
    }

    private void validateMedia(MovieRequestDto dto, MultipartFile poster, MultipartFile banner) {
        // Kiểm tra poster (file upload hoặc URL)
        boolean hasPosterFile = poster != null && !poster.isEmpty();
        boolean hasPosterUrl = dto.getPosterUrl() != null && !dto.getPosterUrl().trim().isEmpty();

        if (!hasPosterFile && !hasPosterUrl) {
            throw new BusinessException("Poster phim là bắt buộc (file upload hoặc URL)");
        }

        if (hasPosterFile) {
            validateImage(poster, "Ảnh poster");
        }

        if (hasPosterUrl && !fileStorageService.isValidImageUrl(dto.getPosterUrl())) {
            throw new BusinessException("URL poster không hợp lệ");
        }

        // Kiểm tra banner (file upload hoặc URL)
        boolean hasBannerFile = banner != null && !banner.isEmpty();
        boolean hasBannerUrl = dto.getBannerUrl() != null && !dto.getBannerUrl().trim().isEmpty();

        if (!hasBannerFile && !hasBannerUrl) {
            throw new BusinessException("Banner phim là bắt buộc (file upload hoặc URL)");
        }

        if (hasBannerFile) {
            validateImage(banner, "Ảnh banner");
        }

        if (hasBannerUrl && !fileStorageService.isValidImageUrl(dto.getBannerUrl())) {
            throw new BusinessException("URL banner không hợp lệ");
        }
    }

    private void validateNames(MovieRequestDto dto) {
        validateTextOnly(dto.getDaoDienMoi(), "Tên đạo diễn mới");
        validateTextOnly(dto.getDienVienMoi(), "Tên diễn viên mới");
        validateTextOnly(dto.getTheLoaiMoi(), "Tên thể loại mới");
    }

    private Movie saveOrUpdateMovieEntity(Integer id, MovieRequestDto dto, MultipartFile poster, MultipartFile banner) {
        if (dto.getTheLoaiIds() == null) {
            dto.setTheLoaiIds(new HashSet<>());
        }
        if (dto.getDaoDienIds() == null) {
            dto.setDaoDienIds(new HashSet<>());
        }
        if (dto.getDienVienIds() == null) {
            dto.setDienVienIds(new HashSet<>());
        }
        // Bước 1: Lưu hoặc thêm mới các entity liên quan nếu cần
        dto.getTheLoaiIds().addAll(saveIfNotExist(
                dto.getTheLoaiMoi(), theLoaiRepo,
                Genre::getTenTheLoai, name -> new Genre(null, name), Genre::getIdTheLoai
        ));
        dto.getDaoDienIds().addAll(saveIfNotExist(
                dto.getDaoDienMoi(), daoDienRepo,
                Director::getTenDaoDien, name -> new Director(null, name), Director::getIdDaoDien
        ));
        dto.getDienVienIds().addAll(saveIfNotExist(
                dto.getDienVienMoi(), dienVienRepo,
                Actor::getTenDienVien, name -> new Actor(null, name), Actor::getIdDienVien
        ));

        // Bước 2: Insert hoặc update phim
        LocalDate ngayTao = (id == null)
                ? LocalDate.now()
                : phimRepo.findById(id).orElseThrow().getNgayTao();

        if (id == null) {
            phimRepo.insertPhim(
                    dto.getTenPhim(), dto.getMoTa(), dto.getThoiLuong(),
                    dto.getNgayPhatHanh(), dto.getTrangThai().name(), dto.getDinhDang(), ngayTao
            );
        } else {
            phimRepo.updatePhim(
                    id, dto.getTenPhim(), dto.getMoTa(), dto.getThoiLuong(),
                    dto.getNgayPhatHanh(), dto.getTrangThai().name(), dto.getDinhDang(), ngayTao
            );
        }

        // Bước 3: Load phim từ DB
        Movie phim = phimRepo.findByTenPhim(dto.getTenPhim())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim sau khi lưu"));

        // Bước 4: Gán quan hệ
        phim.setTheLoaiList(new HashSet<>(theLoaiRepo.findAllById(dto.getTheLoaiIds())));
        phim.setDaoDienList(new HashSet<>(daoDienRepo.findAllById(dto.getDaoDienIds())));
        phim.setDienVienList(new HashSet<>(dienVienRepo.findAllById(dto.getDienVienIds())));

        // Bước 5: Trailer
        saveOrUpdateTrailer(dto.getTrailerUrl(), phim);

        // Bước 6: Ảnh poster/banner (hỗ trợ cả file upload và URL)
        saveImage(poster, LoaiHinhAnh.POSTER, phim);
        saveImage(banner, LoaiHinhAnh.BANNER, phim);

        // Nếu không có file upload, thử lấy từ URL
        if ((poster == null || poster.isEmpty()) && dto.getPosterUrl() != null) {
            saveImageFromUrl(dto.getPosterUrl(), LoaiHinhAnh.POSTER, phim);
        }
        if ((banner == null || banner.isEmpty()) && dto.getBannerUrl() != null) {
            saveImageFromUrl(dto.getBannerUrl(), LoaiHinhAnh.BANNER, phim);
        }

        entityManager.flush();
        entityManager.refresh(phim);

        return phim;
    }

    private void saveOrUpdateTrailer(String url, Movie phim) {
        if (url != null && !url.isBlank()) {
            Trailer trailer = Optional.ofNullable(phim.getTrailer()).orElse(new Trailer());
            trailer.setPhim(phim);
            trailer.setUrl(url);
            trailerRepository.save(trailer);
            phim.setTrailer(trailer);
        }
    }

    private void saveImage(MultipartFile file, LoaiHinhAnh loai, Movie phim) {
        if (file != null && !file.isEmpty()) {
            String folder = (loai == LoaiHinhAnh.POSTER) ? "posters" : "banners";
            String url = fileStorageService.saveFile(file, folder);

            hinhAnhNativeRepo.insertImage(
                    file.getOriginalFilename(),
                    loai.name(),
                    url,
                    phim.getIdPhim()
            );
        }
    }

    private void saveImageFromUrl(String imageUrl, LoaiHinhAnh loai, Movie phim) {
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            String folder = (loai == LoaiHinhAnh.POSTER) ? "posters" : "banners";
            String url = fileStorageService.saveFileFromUrl(imageUrl, folder);

            String fileName = "downloaded_" + System.currentTimeMillis() + fileStorageService.getFileExtension(imageUrl);
            hinhAnhNativeRepo.insertImage(
                    fileName,
                    loai.name(),
                    url,
                    phim.getIdPhim()
            );
        }
    }

    private <T, R> Set<R> saveIfNotExist(
            Set<String> tenMoi,
            JpaRepository<T, R> repo,
            Function<T, String> getTen,
            Function<String, T> createEntity,
            Function<T, R> getId
    ) {
        if (tenMoi == null || tenMoi.isEmpty()) return Collections.emptySet();

        List<T> all = repo.findAll();
        Set<R> ids = all.stream()
                .filter(e -> tenMoi.contains(getTen.apply(e)))
                .map(getId)
                .collect(Collectors.toSet());

        tenMoi.stream()
                .filter(name -> all.stream().noneMatch(e -> getTen.apply(e).equalsIgnoreCase(name)))
                .map(createEntity)
                .map(repo::save)
                .map(getId)
                .forEach(ids::add);

        return ids;
    }

    private void validateTextOnly(Set<String> values, String fieldName) {
        if (values == null) return;
        for (String value : values) {
            if (value != null && !value.matches("^[\\p{L}\\s]+$")) {
                throw new IllegalArgumentException(fieldName + " phải là chữ, không chứa số hoặc ký tự đặc biệt: " + value);
            }
        }
    }

    // ====================== NEW METHODS ==========================

    @Override
    public List<MovieResponseDto> searchMovies(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }

        String searchTerm = keyword.toLowerCase().trim();
        return phimRepo.findAll().stream()
                .filter(phim ->
                        phim.getTenPhim().toLowerCase().contains(searchTerm) ||
                                (phim.getMoTa() != null && phim.getMoTa().toLowerCase().contains(searchTerm)) ||
                                phim.getTheLoaiList().stream()
                                        .anyMatch(genre -> genre.getTenTheLoai().toLowerCase().contains(searchTerm)) ||
                                phim.getDaoDienList().stream()
                                        .anyMatch(director -> director.getTenDaoDien().toLowerCase().contains(searchTerm)) ||
                                phim.getDienVienList().stream()
                                        .anyMatch(actor -> actor.getTenDienVien().toLowerCase().contains(searchTerm))
                )
                .map(phimMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieResponseDto> getTrendingMovies() {
        LocalDate today = LocalDate.now();
        return phimRepo.findAll().stream()
                .filter(phim -> phim.getTrangThai() == TrangThai.DANG_CHIEU || phim.getNgayPhatHanh().isBefore(today.plusDays(7)))
                .sorted(Comparator.comparing(Movie::getLuotXem, Comparator.nullsLast(Integer::compareTo)).reversed())
                .limit(10)
                .map(phimMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieResponseDto> getUpcomingMovies() {
        LocalDate today = LocalDate.now();
        return phimRepo.findAll().stream()
                .filter(phim ->
                        phim.getTrangThai() == TrangThai.SAP_CHIEU &&
                                phim.getNgayPhatHanh() != null && phim.getNgayPhatHanh().isAfter(today)
                )
                .sorted(Comparator.comparing(Movie::getNgayPhatHanh))
                .limit(10)
                .map(phimMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieResponseDto> getSimilarMovies(Integer movieId) {
        Movie base = phimRepo.findById(movieId).orElse(null);
        if (base == null) return List.of();
        return phimRepo.findAll().stream()
                .filter(p -> !p.getIdPhim().equals(movieId))
                .sorted(Comparator.comparingInt(p -> -similarityScore(base, (Movie)p)))
                .limit(10)
                .map(phimMapper::toDto)
                .collect(Collectors.toList());
    }

    // New: search suggestions prioritizing DANG_CHIEU
    @Override
    public List<MovieResponseDto> searchSuggest(String keyword, int limit) {
        String term = keyword == null ? "" : keyword.trim().toLowerCase();
        if (term.isEmpty()) {
            return phimRepo.findAll().stream()
                    .sorted(Comparator.comparing((Movie p) -> p.getTrangThai() == TrangThai.DANG_CHIEU ? 0 : 1)
                            .thenComparing(Movie::getTenPhim))
                    .limit(Math.max(1, limit))
                    .map(phimMapper::toDto)
                    .collect(Collectors.toList());
        }
        return phimRepo.findAll().stream()
                .filter(p -> p.getTenPhim().toLowerCase().contains(term)
                        || (p.getMoTa() != null && p.getMoTa().toLowerCase().contains(term)))
                .sorted(Comparator
                        .comparing((Movie p) -> p.getTrangThai() == TrangThai.DANG_CHIEU ? 0 : 1)
                        .thenComparing((Movie p) -> matchScore(p.getTenPhim(), term))
                        .thenComparing(Movie::getTenPhim))
                .limit(Math.max(1, limit))
                .map(phimMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getStatistics() {
        return Map.of();
    }

    private int matchScore(String name, String term) {
        String n = name == null ? "" : name.toLowerCase();
        if (n.startsWith(term)) return 0;
        if (n.contains(term)) return 1;
        return 2;
    }

    // Compute similarity based on shared genres, directors, and actors
    private int similarityScore(Movie base, Movie other) {
        int score = 0;
        // Genres weight 3
        try {
            var g1 = base.getTheLoaiList();
            var g2 = other.getTheLoaiList();
            if (g1 != null && g2 != null) {
                var s1 = g1.stream().map(Genre::getTenTheLoai).filter(Objects::nonNull).map(String::toLowerCase).collect(Collectors.toSet());
                var s2 = g2.stream().map(Genre::getTenTheLoai).filter(Objects::nonNull).map(String::toLowerCase).collect(Collectors.toSet());
                s1.retainAll(s2);
                score += s1.size() * 3;
            }
        } catch (Exception ignored) {}
        // Directors weight 2
        try {
            var d1 = base.getDaoDienList();
            var d2 = other.getDaoDienList();
            if (d1 != null && d2 != null) {
                var s1 = d1.stream().map(Director::getTenDaoDien).filter(Objects::nonNull).map(String::toLowerCase).collect(Collectors.toSet());
                var s2 = d2.stream().map(Director::getTenDaoDien).filter(Objects::nonNull).map(String::toLowerCase).collect(Collectors.toSet());
                s1.retainAll(s2);
                score += s1.size() * 2;
            }
        } catch (Exception ignored) {}
        // Actors weight 1
        try {
            var a1 = base.getDienVienList();
            var a2 = other.getDienVienList();
            if (a1 != null && a2 != null) {
                var s1 = a1.stream().map(Actor::getTenDienVien).filter(Objects::nonNull).map(String::toLowerCase).collect(Collectors.toSet());
                var s2 = a2.stream().map(Actor::getTenDienVien).filter(Objects::nonNull).map(String::toLowerCase).collect(Collectors.toSet());
                s1.retainAll(s2);
                score += s1.size();
            }
        } catch (Exception ignored) {}
        return score;
    }

    @Override
    public List<MovieReview> getMovieReviews(Integer movieId) {
        Movie movie = phimRepo.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim với ID: " + movieId));

        // This would need to be implemented based on your review system
        // For now, returning empty list
        return new ArrayList<>();
    }

    @Override
    public List<ShowTime> getMovieShowtimes(Integer movieId) {
        Movie movie = phimRepo.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim với ID: " + movieId));

        // This would need to be implemented based on your showtime system
        // For now, returning empty list
        return new ArrayList<>();
    }

    //thêm mới
//    @Override
//    public List<MovieResponseDto> getAllDeleted() {
//        try {
//            List<Movie> deletedMovies = phimRepo.findAllDeleted();
//            return deletedMovies.stream()
//                    .map(phimMapper::toDto)
//                    .collect(Collectors.toList());
//        } catch (Exception e) {
//            throw new BusinessException("Lỗi khi lấy danh sách phim đã xóa: " + e.getMessage());
//        }
//    }

    @Override
    public Map<String, Object> softDelete(Integer id) {
        try {
            // Kiểm tra phim có tồn tại và chưa bị xóa không
            Movie phim = phimRepo.findActiveById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim với ID: " + id));

            // Thực hiện soft delete
            phim.softDelete();
            phimRepo.save(phim);

            // Tạo response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Phim đã được xóa mềm thành công");
            response.put("movieId", id);
            response.put("tenPhim", phim.getTenPhim());
            response.put("deletedAt", phim.getNgayXoa());
            response.put("status", "SOFT_DELETED");

            return response;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Lỗi khi xóa mềm phim: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> restore(Integer id) {
        try {
            // Kiểm tra phim có tồn tại và đã bị xóa không
            Movie phim = phimRepo.findDeletedById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim đã xóa với ID: " + id));

            // Thực hiện restore
            phim.restore();
            phimRepo.save(phim);

            // Tạo response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Phim đã được khôi phục thành công");
            response.put("movieId", id);
            response.put("tenPhim", phim.getTenPhim());
            response.put("restoredAt", phim.getNgayCapNhat());
            response.put("status", "RESTORED");

            return response;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Lỗi khi khôi phục phim: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> hardDelete(Integer id) {
        try {
            // Kiểm tra phim có tồn tại không (có thể đã bị xóa mềm hoặc chưa)
            Movie phim = phimRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim với ID: " + id));

            // Lưu thông tin trước khi xóa
            String tenPhim = phim.getTenPhim();
            LocalDateTime deletedAt = LocalDateTime.now();

            // Thực hiện hard delete
            phimRepo.delete(phim);

            // Tạo response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Phim đã được xóa vĩnh viễn khỏi hệ thống");
            response.put("movieId", id);
            response.put("tenPhim", tenPhim);
            response.put("deletedAt", deletedAt);
            response.put("status", "HARD_DELETED");
            response.put("warning", "Phim đã bị xóa vĩnh viễn và không thể khôi phục");

            return response;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Lỗi khi xóa vĩnh viễn phim: " + e.getMessage());
        }
    }
}

