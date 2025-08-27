package org.example.cinema_reservation_system.controller.employee;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.employee.AddEmployeeDto;
import org.example.cinema_reservation_system.dto.employee.EmployeeDto;
import org.example.cinema_reservation_system.repository.employee.EmployeeRepository;
import org.example.cinema_reservation_system.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;

    // Tạo mới nhân viên
    @PostMapping("/addEmployee")
    public EmployeeDto createEmployee(@RequestBody @Valid AddEmployeeDto dto) {
        return employeeService.createEmployee(dto);
    }

    // Lấy chi tiết nhân viên theo ID
    @GetMapping("/{id}")
    public EmployeeDto getEmployeeById(@PathVariable Integer id) {
        return employeeService.getEmployeeById(id);
    }

    // Lấy tất cả nhân viên
    @GetMapping
    public List<EmployeeDto> getAllNhanVien() {
        return employeeService.getAllEmployees();
    }

    // Lấy nhân viên theo trạng thái
    @GetMapping("/trang-thai/{trangThai}")
    public List<EmployeeDto> getEmployeesByStatus(@PathVariable String trangThai) {
        return employeeService.getEmployeesByStatus(trangThai);
    }

    // Xử lý lỗi validate trả về message rõ ràng
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(errorMsg);
    }

    // Kiểm tra trùng email
    @GetMapping("/check-email")
    public boolean findByEmail(@RequestParam String email) {
        return employeeRepository.findByEmail(email) != null;
    }

    // Kiểm tra trùng CCCD
    @GetMapping("/check-cccd")
    public boolean findByCccd(@RequestParam String cccd) {
        return employeeRepository.findByCccd(cccd) != null;
    }

    // Kiểm tra trùng số điện thoại
    @GetMapping("/check-sdt")
    public boolean findBySoDienThoai(@RequestParam String soDienThoai) {
        return employeeRepository.findBySoDienThoai(soDienThoai) != null;
    }

}
