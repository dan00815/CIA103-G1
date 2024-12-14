package com.event.cia103g1springboot.member.emp.model;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.hutool.crypto.digest.BCrypt;

@Service
public class EmployeeService {
    
    @Autowired
    private EmployeeRepository employeeRepository;

    public EmployeeVO login(String empAcct, String empPwd) {
        EmployeeVO employee = employeeRepository.findByEmpAcct(empAcct);
        if (employee != null && BCrypt.checkpw(empPwd, employee.getEmpPwd())) {
            return employee;
        }
        return null;
    }

    public boolean register(EmployeeVO employee) {
        if (employeeRepository.findByEmpAcct(employee.getEmpAcct()) != null) {
            return false;
        }
        employee.setEmpPwd(BCrypt.hashpw(employee.getEmpPwd()));
        employee.setHireDate(new Date());
        employee.setEmpStat(1);
        return employeeRepository.save(employee) != null;
    }

    public EmployeeVO getEmployeeProfile(Integer empId) {
        return employeeRepository.findById(empId).orElse(null);
    }

    public boolean updateProfile(EmployeeVO employee) {
        EmployeeVO existingEmployee = employeeRepository.findById(employee.getEmpId()).orElse(null);
        if (existingEmployee != null) {
            existingEmployee.setEmpName(employee.getEmpName());
            existingEmployee.setEmpJobTitle(employee.getEmpJobTitle());
            existingEmployee.setEmpImg(employee.getEmpImg());
            employeeRepository.save(existingEmployee);
            return true;
        }
        return false;
    }

    public boolean deactivateEmployee(Integer empId) {
        return employeeRepository.updateStatus(empId, 0) > 0;
    }

    public boolean resetPassword(String empAcct, String newPassword) {
        return employeeRepository.updatePassword(empAcct, 
            BCrypt.hashpw(newPassword)) > 0;
    }

    public List<EmployeeVO> getAllEmployees() {
        return employeeRepository.findAll();
    }
} 