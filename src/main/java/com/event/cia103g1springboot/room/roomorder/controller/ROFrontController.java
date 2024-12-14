package com.event.cia103g1springboot.room.roomorder.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.event.cia103g1springboot.member.mem.model.MemVO;
import com.event.cia103g1springboot.plan.planorder.model.PlanOrder;
import com.event.cia103g1springboot.plan.planorder.model.PlanOrderService;
import com.event.cia103g1springboot.room.roomorder.model.ROService;
import com.event.cia103g1springboot.room.roomorder.model.ROVO;
import com.event.cia103g1springboot.room.roomtype.model.RTService;
import com.event.cia103g1springboot.room.roomtype.model.RTVO;

@Controller
@RequestMapping("/roomOrder")
public class ROFrontController {

	@Autowired
	ROService roSvc;
	
	@Autowired
	RTService rtSvc;
	
	@Autowired
	PlanOrderService poSvc;
	
	@Autowired
	private HttpSession session;
	
	@PostMapping("roomOrderList")
	public String roomOrderList (@RequestParam(value ="planOrderId") String planOrderId,ModelMap model )throws IOException  {
		if (planOrderId == null || planOrderId.trim().isEmpty()) {
	        model.addAttribute("errorMessage2", "行程訂單編號不可為空白");
	        return "front-end/roomOrder/selectRO";
	    }

	    // 確保轉換為數字時不報錯
	    Integer planOrderIdInt;
	    try {
	        planOrderIdInt = Integer.valueOf(planOrderId);
	    } catch (NumberFormatException e) {
	        model.addAttribute("errorMessage2", "行程訂單編號格式不正確");
	        return "front-end/roomOrder/selectRO";
	    }

	    // 查詢計劃訂單
	    PlanOrder newPO = poSvc.getOnePlanOrder(planOrderIdInt);
	    if (newPO == null) {
	        model.addAttribute("errorMessage2", "查無房型訂單資料");
	        return "front-end/roomOrder/selectRO";
	    }
	    // 查詢房型訂單
	    List<ROVO> roListByPOId = roSvc.getByPlan(newPO.getPlanOrderId());
	    if (roListByPOId == null || roListByPOId.isEmpty()) {
	        model.addAttribute("planOrder", newPO);
	        model.addAttribute("errorMessage2", "查無房型訂單資料");
	        return "front-end/roomOrder/selectRO";
	    }

	    // 確保非空後處理
	    ROVO firstRO = roListByPOId.get(0);
	    RTVO newRT = rtSvc.getOneRT(firstRO.getRtVO().getRoomTypeId());
	    MemVO memVO = (MemVO)session.getAttribute("auth");
	    if (newRT == null) {
	        model.addAttribute("errorMessage2", "查無房型訂單資料");
	        return "front-end/roomOrder/selectRO";
	    }
//	    判斷是否該登入會員的訂單
	    else if(! newPO.getMemVO().getMemId().equals(memVO.getMemId())) {
	    	model.addAttribute("errorMessage2", "查無房型訂單資料");
	    	return "front-end/roomOrder/selectRO";
	    }

	    // 將查詢結果放入 model
	    model.addAttribute("planOrder", newPO);
	    model.addAttribute("roListByPOId", roListByPOId);
	    model.addAttribute("rtVO", newRT);
		return "front-end/roomOrder/roomOrderList";
	}
	
	@GetMapping("/selectRO")
	public String selectRO(ModelMap mode) {
		return "front-end/roomOrder/selectRO";
	}
	
}
