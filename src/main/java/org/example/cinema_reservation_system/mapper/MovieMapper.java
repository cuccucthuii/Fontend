package org.example.cinema_reservation_system.mapper;

import org.example.cinema_reservation_system.dto.moviedto.MovieRequestDto;
import org.example.cinema_reservation_system.dto.moviedto.MovieResponseDto;
import org.example.cinema_reservation_system.entity.*;
import org.example.cinema_reservation_system.utils.enums.LoaiHinhAnh;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MovieMapper {

    public Movie toEntity(MovieRequestDto dto) {
        Movie phim = new Movie();
        phim.setTenPhim(dto.getTenPhim());
        phim.setMoTa(dto.getMoTa());
        phim.setThoiLuong(dto.getThoiLuong());
        phim.setNgayPhatHanh(dto.getNgayPhatHanh());
        phim.setTrangThai(dto.getTrangThai());
        phim.setDinhDang(dto.getDinhDang());
        return phim;
    }

    public MovieResponseDto toDto(Movie phim) {
        MovieResponseDto dto = new MovieResponseDto();
        dto.setIdPhim(phim.getIdPhim());
        dto.setTenPhim(phim.getTenPhim());
        dto.setMoTa(phim.getMoTa());
        dto.setThoiLuong(phim.getThoiLuong());
        dto.setNgayPhatHanh(phim.getNgayPhatHanh());
        dto.setDinhDang(phim.getDinhDang());
        dto.setTrangThai(phim.getTrangThai());
        dto.setNgayTao(phim.getNgayTao() != null ? phim.getNgayTao() : null);
        dto.setTuoiGioiHan(phim.getTuoiGioiHan());
        dto.setNamSanXuat(phim.getNamSanXuat());
        dto.setDoPhoBien(phim.getDoPhoBien());
        dto.setGiaVeCoBan(phim.getGiaVeCoBan());
        dto.setPosterUrl(getImageUrl(phim, LoaiHinhAnh.POSTER));
        dto.setBannerUrl(getImageUrl(phim, LoaiHinhAnh.BANNER));
        dto.setTrailerUrl(phim.getTrailer() != null ? phim.getTrailer().getUrl() : null);

        dto.setDaoDien(phim.getDaoDienList().stream().map(Director::getTenDaoDien).collect(Collectors.toList()));
        dto.setDienVien(phim.getDienVienList().stream().map(Actor::getTenDienVien).collect(Collectors.toList()));
        dto.setTheLoai(phim.getTheLoaiList().stream().map(Genre::getTenTheLoai).collect(Collectors.toList()));

        return dto;
    }

    private String getImageUrl(Movie phim, LoaiHinhAnh loai) {
        Set<Image> hinhAnhs = phim.getHinhAnhs();
        if (hinhAnhs == null || hinhAnhs.isEmpty()) return null;

        return hinhAnhs.stream()
                .filter(img -> loai.equals(img.getLoai()))
                .findFirst()
                .map(Image::getUrl)
                .map(this::normalizeImageUrl)
                .orElse(null);
    }

    private String normalizeImageUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) return null;

        String url = rawUrl.trim();
        // Map legacy /images/* to /uploads/*
        if (url.startsWith("/images/")) {
            url = url.replaceFirst("^/images/", "/uploads/");
        }
        // If URL ends with '/', it's a directory, FE cannot load → return null to let FE fallback
        if (url.endsWith("/")) {
            return null;
        }
        // Build absolute URL if not already absolute
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        try {
            return org.springframework.web.servlet.support.ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path(url.startsWith("/") ? url : "/" + url)
                    .toUriString();
        } catch (Exception ignored) {
            // Fallback to relative if no request context
            return url;
        }
    }

}
