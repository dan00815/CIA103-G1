package com.event.cia103g1springboot.room.roomorder.controller;

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

import com.event.cia103g1springboot.plan.planorder.model.PlanOrder;
import com.event.cia103g1springboot.plan.planorder.model.PlanOrderService;
import com.event.cia103g1springboot.room.roomorder.model.ROService;
import com.event.cia103g1springboot.room.roomorder.model.ROVO;
import com.event.cia103g1springboot.room.roomtype.model.RTService;
import com.event.cia103g1springboot.room.roomtype.model.RTVO;

@Controller
@Validated
@RequestMapping("/roomOrder")
public class ROIdController {
	
	@Autowired
	ROService roSvc;
	
	@Autowired
	RTService rtSvc;
	
	@Autowired
	PlanOrderService poSvc;
	
	@PostMapping("getRO_For_Display")
	public String getRO_For_Display(
			@NotEmpty(message="房型訂單編號:請勿空白")
			@Digits(integer = 4, fraction = 0, message = "房型訂單編號: 請填數字-請勿超過{integer}位數")
			@Min(value = 2001, message = "房型訂單編號: 不能小於{value}")
			@RequestParam("roomOrderId")String roomOrderId,ModelMap model) {
		
		ROVO roVO = roSvc.getOneRO(Integer.valueOf(roomOrderId));
		List<ROVO> list = roSvc.getAllRO();
		model.addAttribute("roListData",list);
		model.addAttribute("rtVO",new RTVO());
		List<RTVO> list2 = rtSvc.getAllRT();
		model.addAttribute("rtListData",list2);
		model.addAttribute("planOrder",new PlanOrder());
		List<PlanOrder> list3 = poSvc.findAllPlanOrders();
		model.addAttribute("poListData",list3);
		
		if(roVO==null) {
			model.addAttribute("errorMessage","查無資料");
			return "back-end/roomOrder/select_page_RO";
		}
		model.addAttribute("roVO",roVO);
		List<ROVO> list4 = new ArrayList<>();
		list4.add(roVO);
		model.addAttribute("roListData",list4);
		return "back-end/roomOrder/listAllRO";
	}
	

	@ExceptionHandler(value = { ConstraintViolationException.class })
	public ModelAndView handleError(HttpServletRequest req,ConstraintViolationException e,Model model) {
	    Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
	    StringBuilder strBuilder = new StringBuilder();
	    for (ConstraintViolation<?> violation : violations ) {
	          strBuilder.append(violation.getMessage() + "、");
	    }
		List<ROVO> list = roSvc.getAllRO();
		model.addAttribute("roListData", list); 
		model.addAttribute("rtVO", new RTVO()); 
		List<RTVO> list2 = rtSvc.getAllRT();
    	model.addAttribute("rtListData",list2);
    	model.addAttribute("planOrder",new PlanOrder());
		List<PlanOrder> list3 = poSvc.findAllPlanOrders();
		model.addAttribute("poListData",list3);
		String message = strBuilder.toString();
	    return new ModelAndView("back-end/roomOrder/select_page_RO", "errorMessage", "請修正以下錯誤: "+message);
	}
}
