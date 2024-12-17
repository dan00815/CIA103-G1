package com.event.cia103g1springboot.room.roomorder.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.event.cia103g1springboot.member.mem.model.MemService;
import com.event.cia103g1springboot.member.mem.model.MemVO;
import com.event.cia103g1springboot.plan.planorder.model.PlanOrder;
import com.event.cia103g1springboot.plan.planorder.model.PlanOrderService;
import com.event.cia103g1springboot.room.roomorder.model.ROService;
import com.event.cia103g1springboot.room.roomorder.model.ROVO;
import com.event.cia103g1springboot.room.roomtype.model.RTService;
import com.event.cia103g1springboot.room.roomtype.model.RTVO;

@Controller
@RequestMapping("/roomorder")
public class ROFrontController {

	@Autowired
	ROService roSvc;
	
	@Autowired
	RTService rtSvc;
	
	@Autowired
	PlanOrderService poSvc;
	
	@Autowired
	private HttpSession session;
	
	@Autowired
	MemService memSvc;

	@GetMapping("/historyRO")
	public String historyRO(ModelMap model, HttpSession session) {

		MemVO memVO = (MemVO)session.getAttribute("auth");
		if (memVO == null) {
			// 如果用戶未登錄，重定向到登錄頁面
			return "redirect:mem/login";
		}
		MemVO theMem = memSvc.getMem(memVO.getMemId());
		List<PlanOrder> historyPO = poSvc.findPlanOrdersByMemId(theMem.getMemId());
		model.addAttribute("planOrderList",historyPO);
		List<ROVO> historyRO = roSvc.getByMemId(theMem.getMemId());
		
		if(historyRO.isEmpty() || historyRO == null) {
			model.addAttribute("errorMessage","查無歷史訂房紀錄");
		}
		
		model.addAttribute("roByMemId",historyRO);
		return "front-end/roomorder/roomOrderList";
		
	}
	
	
}
