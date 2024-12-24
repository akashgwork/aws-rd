package org.example.controller;

import org.example.entity.Employee;
import org.example.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // Endpoint to register a new user
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Employee registerEmployee(@RequestBody Employee employee) {
        return employeeService.registerUser(employee);
    }
}
