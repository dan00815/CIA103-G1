package com.event.cia103g1springboot.member.emp.controller;
import javax.servlet.http.HttpSession;

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
        return "emp/login";
    }

    @GetMapping("/show")
    public String showEmployeeInfo(HttpSession session, Model model) {
        EmployeeVO loginUser = (EmployeeVO) session.getAttribute("loginUser");
        if (loginUser != null) {
            model.addAttribute("employee", loginUser);
            return "back-end/emp/show";
        }
        return "redirect:/emp/login";
    }

    @GetMapping("/edit/{id}")
    public String editEmployee(@PathVariable("id") Integer id, Model model) {
        EmployeeVO employee = employeeService.getEmployeeProfile(id);
        model.addAttribute("employee", employee);
        return "emp/edit";
    }
}




//package com.example.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import com.example.model.EmployeeService;
//import com.example.model.EmployeeVO;
//
//@Controller
//@RequestMapping("/emp")
//public class EmployeeViewController {
//
//    @Autowired
//    private EmployeeService employeeService;
//
//    @GetMapping("/login")
//    public String login() {
//        return "emp/login";
//    }
//
//    @GetMapping("/edit/{id}")
//    public String editEmployee(@PathVariable("id") Integer id, Model model) {
//        EmployeeVO employee = employeeService.getEmployeeProfile(id);
//        model.addAttribute("employee", employee);
//        return "emp/edit";
//    }
//} 