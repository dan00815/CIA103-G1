package com.event.cia103g1springboot.member.systemFunction.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.event.cia103g1springboot.member.systemFunction.model.SystemFunctionService;
import com.event.cia103g1springboot.member.systemFunction.model.SystemFunctionVO;

@Controller
@RequestMapping("/sysFun")
public class SystemFunctionController {

	@Autowired
	SystemFunctionService sysFunSvc;

	@GetMapping("/listAll")
	public String listAllSysFun(ModelMap model) {

		return "back-end/sysFun/listAllSysFun";
	}

//	@GetMapping("/addSysFun")
//	public String addSysFun(ModelMap model) {
//		SystemFunctionVO sysFunVO = new SystemFunctionVO();
//		model.addAttribute("sysFunVO", sysFunVO);
//		return "back-end/sysFun/addSysFun";
//	}

//	@PostMapping("/addSysFun")
//	public String insert(@Valid SystemFunctionVO sysFunVO, BindingResult result, ModelMap model) {
//		sysFunSvc.addSysFun(sysFunVO);
//		List<SystemFunctionVO> sysFuns = sysFunSvc.getAll();
//		model.addAttribute("sysFunListData", sysFuns);
//		return "back-end/sysFun/listAllSysFun";
//	}

//	@PostMapping("/delFun")
//	public String delete(@RequestParam("funId") String funId, ModelMap model) {
//		System.out.println(Integer.valueOf(funId));
//		sysFunSvc.deleteFun(Integer.valueOf(funId));
//		return "redirect:/sysFun/listAll";
//	}

	@ModelAttribute("sysFunListData")
	protected List<SystemFunctionVO> referenceListData(Model model) {
		List<SystemFunctionVO> list = sysFunSvc.getAll();
		return list;
	}

}
