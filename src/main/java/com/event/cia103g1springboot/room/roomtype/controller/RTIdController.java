package com.event.cia103g1springboot.room.roomtype.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.event.cia103g1springboot.room.roomtype.model.RTService;
import com.event.cia103g1springboot.room.roomtype.model.RTVO;

@Controller
@Validated
@RequestMapping("/rt")
public class RTIdController {

	@Autowired
	RTService rtSvc;
	
	@PostMapping("getRT_For_Display")
	public String getRT_For_Display(
			@NotEmpty(message="房型編號:請勿空白")
			@Digits(integer = 2, fraction = 0, message = "房型編號: 請填數字-請勿超過{integer}位數")
			@Min(value = 1, message = "商品編號: 不能小於{value}")
			@Max(value = 99, message = "商品編號: 不能超過{value}")
			@RequestParam("roomTypeId") String roomTypeId,
					ModelMap model) {
		RTVO rtVO = rtSvc.getOneRT(Integer.valueOf(roomTypeId));
		List<RTVO> list = rtSvc.getAllRT();
		model.addAttribute("rtListData",list);
		
		if(rtVO == null) {
			model.addAttribute("errorMessage","查無資料");
			return "back-end/rt/select_page_RT";
		}
		model.addAttribute("rtVO",rtVO);
		List<RTVO> result = new ArrayList<>();
		result.add(rtVO);
		model.addAttribute("rtListData",result);
		return "back-end/rt/select_page_RT";
	}
	
	@ExceptionHandler(value = { ConstraintViolationException.class })
	public ModelAndView handleError(HttpServletRequest req,ConstraintViolationException e,Model model) {
	    Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
	    StringBuilder strBuilder = new StringBuilder();
	    for (ConstraintViolation<?> violation : violations ) {
	          strBuilder.append(violation.getMessage() + "<br>");
	    }
		List<RTVO> list = rtSvc.getAllRT();
		model.addAttribute("rtListData", list); 
		String message = strBuilder.toString();
	    return new ModelAndView("back-end/rt/select_page_RT", "errorMessage", "請修正以下錯誤:<br>"+message);
	}
	
}
