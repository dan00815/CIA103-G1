package com.event.cia103g1springboot.member.emp.controller;

import java.util.Base64;

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
		return "backindexlogin";
	}

	@GetMapping("/show")
	public String showEmployeeInfo(HttpSession session, Model model) {
		EmployeeVO loginUser = (EmployeeVO) session.getAttribute("loginUser");
		if (loginUser != null) {
			model.addAttribute("employee", loginUser);

			// 取得員工圖片
			byte[] empImg = loginUser.getEmpImg();
			if (empImg != null && empImg.length > 0) { // 確保圖片數據存在且不為空
				String base64Image = Base64.getEncoder().encodeToString(empImg);
				model.addAttribute("base64Image", base64Image);
//                System.out.println("圖片已轉換為 base64，長度: " + base64Image.length()); // 除錯用
			} else {
//                System.out.println("找不到員工圖片數據"); // 除錯用
			}

			String not_allow = (String) session.getAttribute("not_allow");
			if (not_allow != null) {
				model.addAttribute("not_allow", not_allow);
				session.removeAttribute("not_allow");
			}

			return "back-end/emp/show";
		}
		return "redirect:/emp/login";
	}

	@GetMapping("/edit/{id}")
	public String editEmployee(@PathVariable("id") Integer id, Model model) {
		EmployeeVO employee = employeeService.getEmployeeProfile(id);
		model.addAttribute("employee", employee);
		return "back-end/emp/edit";
	}

	@GetMapping("/list")
	public String listEmployees(Model model, HttpSession session) {
		// 添加你的業務邏輯
		EmployeeVO employee = (EmployeeVO) session.getAttribute("loginUser");
		if (employee.getEmpId() != 7001) {
			session.setAttribute("not_allow", "僅有超級管理員才能查看");
			return "redirect:/emp/show";
		}
		return "back-end/emp/list"; // 更新視圖名稱
	}

	@GetMapping("/register")

	public String registerEmployee() {

		return "back-end/emp/register"; // 更新視圖名稱
	}

	@GetMapping("/reset-password")

	public String resetPassword() {
		return "back-end/emp/reset_password"; // 更新視圖名稱
	}

}


