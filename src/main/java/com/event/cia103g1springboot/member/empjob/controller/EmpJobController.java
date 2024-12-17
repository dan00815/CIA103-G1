package com.event.cia103g1springboot.member.empjob.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.event.cia103g1springboot.member.emp.model.EmployeeService;
import com.event.cia103g1springboot.member.emp.model.EmployeeVO;
import com.event.cia103g1springboot.member.empjob.model.EmpJobService;
import com.event.cia103g1springboot.member.empjob.model.EmpJobVO;
import com.event.cia103g1springboot.member.systemFunction.model.SystemFunctionService;
import com.event.cia103g1springboot.member.systemFunction.model.SystemFunctionVO;

@Controller
@RequestMapping("/auth")
public class EmpJobController {

	@Autowired
	EmpJobService empJobSvc;

	@Autowired
	EmployeeService empSvc;

	@Autowired
	SystemFunctionService sysFunSvc;


	@GetMapping("listAllAuth")
	public String listAllAuth(HttpSession session, ModelMap model) {
		// 判斷訪問的人他的session中有沒有包含超管的權限編號
		@SuppressWarnings("unchecked")
		Set<EmpJobVO> auths = (Set<EmpJobVO>) session.getAttribute("auths");

		// 先提取權限Id的集合
		Set<Integer> authIds = auths.stream().map(EmpJobVO -> EmpJobVO.getSystemFunctionVO().getFunId())
				.collect(Collectors.toSet());
		if (authIds == null || !authIds.contains(101)) {
			return "back-end/auth/noPermission";
		}

		List<EmployeeVO> allEmps = empSvc.getAllEmployees();
		model.addAttribute("empListData", allEmps);

		return "back-end/auth/listAllAuths";
	}

	@GetMapping("update")
	public ResponseEntity<Map<String, Object>> getOneEmpAuth(@RequestParam("empId") String empId, Model model) {
//		要資料驗證，然後傳到修改的表單
		Integer empIdInt = Integer.valueOf(empId);

		// 找到該管理員的權限
		List<Integer> empAuths = empJobSvc.findAuthByEmpId(empIdInt);

		List<SystemFunctionVO> allAuths = sysFunSvc.getAll();
		// 我只拿權限名稱跟id，做成Map的List
		List<String> allAuthsName = allAuths.stream().map(SystemFunctionVO::getFunName).collect(Collectors.toList());
		List<Map<String, Object>> allAuthaaa = allAuths.stream().map(sysFun -> {
			Map<String, Object> map = new HashMap<>();
			map.put("authId", sysFun.getFunId());
			map.put("authName", sysFun.getFunName());
			return map;
		}).collect(Collectors.toList());

		Map<String, Object> response = new HashMap<>();
		response.put("auth", empAuths);
		response.put("empId", empIdInt);
		response.put("allAuthName", allAuthaaa);

		return ResponseEntity.ok(response);
	}

	// 權限更新
	@PostMapping("update")
	public String update(
			@RequestParam(value = "authTypes", required = false, defaultValue = "") List<Integer> authTypes,
			@RequestParam("empId") String empId, HttpSession session, ModelMap model) {
		// 還需要有empId
		Integer empIdInt = Integer.valueOf(empId);
		List<Integer> existAuths = empJobSvc.findAuthByEmpId(empIdInt);

		for (Integer auth : authTypes) {
			if (!existAuths.contains(auth)) {
				EmpJobVO empJobVO = new EmpJobVO();
				empJobVO.setEmpId(empIdInt);
				empJobVO.setFunId(auth);
				empJobSvc.addAuth(empJobVO);
			}
		}

		for (Integer exist : existAuths) {
			if (!authTypes.contains(exist)) {
				empJobSvc.deleteOldAuth(empIdInt, exist); // 刪除該權限
			}
		}

		return "redirect:/auth/listAllAuth";
	}

	@ModelAttribute("empJobData")
	public List<EmpJobVO> referenceListData(Model model) {
		List<EmpJobVO> list = empJobSvc.getAll();
		return list;
	}

	@ModelAttribute("sysFunListData")
	protected List<SystemFunctionVO> referencesysFunListData(Model model) {
		List<SystemFunctionVO> list = sysFunSvc.getAll();
		return list;
	}

}
