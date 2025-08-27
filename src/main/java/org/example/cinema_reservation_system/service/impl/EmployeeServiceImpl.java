package org.example.cinema_reservation_system.service.impl;

import org.example.cinema_reservation_system.dto.employee.AddEmployeeDto;
import org.example.cinema_reservation_system.dto.employee.EmployeeDto;
import org.example.cinema_reservation_system.entity.Employee;
import org.example.cinema_reservation_system.entity.Theater;
import org.example.cinema_reservation_system.repository.employee.EmployeeRepository;
import org.example.cinema_reservation_system.repository.theater.TheaterRepository;
import org.example.cinema_reservation_system.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private TheaterRepository theaterRepository;

    @Override
    public EmployeeDto createEmployee(AddEmployeeDto dto) {
        // Check trùng email
        if (employeeRepository.findByEmail(dto.getEmail()) != null) {
            throw new RuntimeException("Email đã tồn tại!");
        }
        // Check trùng CCCD
        if (employeeRepository.findByCccd(dto.getCccd()) != null) {
            throw new RuntimeException("CCCD đã tồn tại!");
        }
        // Check trùng số điện thoại (nếu muốn)
        if (employeeRepository.findBySoDienThoai(dto.getSoDienThoai()) != null) {
            throw new RuntimeException("Số điện thoại đã tồn tại!");
        }

        Employee nv = new Employee();
        nv.setTenNhanVien(dto.getTenNhanVien());
        nv.setEmail(dto.getEmail());
        nv.setSoDienThoai(dto.getSoDienThoai());
        nv.setNgaySinh(dto.getNgaySinh());
        nv.setGioiTinh(dto.getGioiTinh());
        nv.setCccd(dto.getCccd());
        nv.setNgayVaoLam(dto.getNgayVaoLam());
        nv.setTrangThai("HOAT_DONG");
        Theater theater = theaterRepository.findById(dto.getIdRapChieu())
                .orElseThrow(() -> new RuntimeException("Rạp chiếu không tồn tại"));
        nv.setRapChieu(theater);
        Employee saved = employeeRepository.save(nv);
        return toDTO(saved);
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDto> getEmployeesByStatus(String status) {
        return employeeRepository.findByTrangThai(status).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public EmployeeDto getEmployeeById(Integer id) {
        return employeeRepository.findById(id).map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại"));
    }

    // Helper chuyển entity sang DTO
    private EmployeeDto toDTO(Employee nv) {
        EmployeeDto dto = new EmployeeDto();
        dto.setIdNhanVien(Long.valueOf(nv.getIdNhanVien()));
        dto.setTenNhanVien(nv.getTenNhanVien());
        dto.setEmail(nv.getEmail());
        dto.setSoDienThoai(nv.getSoDienThoai());
        dto.setNgaySinh(nv.getNgaySinh());
        dto.setGioiTinh(nv.getGioiTinh());
        dto.setCccd(nv.getCccd());
        dto.setTrangThai(nv.getTrangThai());
        dto.setNgayVaoLam(nv.getNgayVaoLam());
        if (nv.getRapChieu() != null) {
            dto.setIdRapChieu(nv.getRapChieu().getIdRapChieu());
//            dto.setTenRap(nv.getRapChieu().getTenRap());
        }
        return dto;
    }
}
