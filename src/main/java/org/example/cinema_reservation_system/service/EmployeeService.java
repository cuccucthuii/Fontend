package org.example.cinema_reservation_system.service;

import org.example.cinema_reservation_system.dto.employee.AddEmployeeDto;
import org.example.cinema_reservation_system.dto.employee.EmployeeDto;

import java.util.List;

public interface EmployeeService {
    EmployeeDto createEmployee(AddEmployeeDto dto);
    List<EmployeeDto> getAllEmployees();
    List<EmployeeDto> getEmployeesByStatus(String status);
    EmployeeDto getEmployeeById(Integer id);
}
