package com.event.cia103g1springboot.member.emp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.event.cia103g1springboot.member.emp.model.EmployeeService;
import com.event.cia103g1springboot.member.emp.model.EmployeeVO;


@Controller
@RequestMapping("/emp")
public class EmployeeViewController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/login")
    public String login() {
        return "backindexlogin"; // 正確的視圖名稱
    }

    @GetMapping("/edit/{id}")
    public String editEmployee(@PathVariable("id") Integer id, Model model) {
        EmployeeVO employee = employeeService.getEmployeeProfile(id);
        model.addAttribute("employee", employee);
        return "emp/edit";
    }
}


//@Controller
//@RequestMapping("/emp")
//public class EmployeeViewController {
//
//    @Autowired
//    private EmployeeService employeeService;
//
//    @GetMapping("/login")
//    public String login() {
//        return "/backindexlogin";
//    }
//
//    @GetMapping("/edit/{id}")
//    public String editEmployee(@PathVariable("id") Integer id, Model model) {
//        EmployeeVO employee = employeeService.getEmployeeProfile(id);
//        model.addAttribute("employee", employee);
//        return "emp/edit";
//    }
//} 