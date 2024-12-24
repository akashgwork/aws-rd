package org.example.service;

import org.example.entity.Employee;
import org.example.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee registerUser(Employee employee) {

        if (employeeRepository.findByUsername(employee.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
        }

        return employeeRepository.save(employee);
    }


}
