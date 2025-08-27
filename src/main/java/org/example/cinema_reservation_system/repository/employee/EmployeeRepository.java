package org.example.cinema_reservation_system.repository.employee;

import org.example.cinema_reservation_system.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Employee findByEmail(String email);
    Employee findByCccd(String cccd);
    Employee findBySoDienThoai(String soDienThoai);
    List<Employee> findByTrangThai(String trangThai);
    Employee findByIdNhanVien(Long idNhanVien);
}
