package com.event.cia103g1springboot.room.roomorder.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
@RequestMapping("/roomOrder")
public class ROController {
	
	@Autowired
	ROService roSvc;
	
	@Autowired
	RTService rtSvc;
	
	@Autowired
	PlanOrderService poSvc;
	
	@Autowired
	MemService memSvc;
	
	@ModelAttribute("roListData")
	protected List<ROVO> referenceListData_RO(){
		List<ROVO> list = roSvc.getAllRO();
		return list;
	}
	
	@GetMapping("addRO")
	public String addRO (ModelMap model) {
		ROVO roVO = new ROVO();
		model.addAttribute("roVO",roVO);
		return "back-end/roomOrder/addRO";
	}
	
	@GetMapping("/listAllRO")
	public String listAllRO(Model model) {
		List<ROVO> list = roSvc.getAllRO();
		model.addAttribute("roListData",list);
		return "back-end/roomOrder/listAllRO";
	}
	@GetMapping("/select_page_RO")
	public String select_page_RO(Model model) {
		return "back-end/roomOrder/select_page_RO";
	}
	
	
	@PostMapping("roomOrderListBack")
	public String roomOrderList(@RequestParam(value = "planOrderId", required = false) String planOrderId, ModelMap model) {
	    if (planOrderId == null || planOrderId.trim().isEmpty()) {
	        model.addAttribute("errorMessage2", "行程訂單編號不可為空白");
	        return "back-end/roomOrder/select_page_RO";
	    }

	    // 確保轉換為數字時不報錯
	    Integer planOrderIdInt;
	    try {
	        planOrderIdInt = Integer.valueOf(planOrderId);
	    } catch (NumberFormatException e) {
	        model.addAttribute("errorMessage2", "行程訂單編號格式不正確");
	        return "back-end/roomOrder/select_page_RO";
	    }

	    // 查詢計劃訂單
	    PlanOrder newPO = poSvc.getOnePlanOrder(planOrderIdInt);
	    if (newPO == null) {
	        model.addAttribute("errorMessage2", "查無此行程訂單資料");
	        return "back-end/roomOrder/select_page_RO";
	    }
	    // 查詢房型訂單
	    List<ROVO> roListByPOId = roSvc.getByPlan(newPO.getPlanOrderId());
	    if (roListByPOId == null || roListByPOId.isEmpty()) {
	        model.addAttribute("planOrder", newPO);
	        model.addAttribute("errorMessage2", "該行程訂單下無任何房型訂單資料");
	        return "back-end/roomOrder/select_page_RO";
	    }

	    // 確保非空後處理
	    ROVO firstRO = roListByPOId.get(0);
	    RTVO newRT = rtSvc.getOneRT(firstRO.getRtVO().getRoomTypeId());
	    if (newRT == null) {
	        model.addAttribute("errorMessage2", "無法查詢到對應的房型資訊");
	        return "back-end/roomOrder/select_page_RO";
	    }

	    // 將查詢結果放入 model
	    model.addAttribute("planOrder", newPO);
	    model.addAttribute("roListData", roListByPOId);
	    model.addAttribute("rtVO", newRT);

	    return "back-end/roomOrder/listAllRO";
	}

	
	@PostMapping("insertRO")
	public String insertRO (@ModelAttribute("roVO")@Valid ROVO roVO ,BindingResult result , ModelMap model)throws IOException{
		try {
			if(result.hasErrors()) {
				model.addAttribute("errorMessage","請檢查錯誤");
				return "back-end/roomOrder/addRO";
			}
			roSvc.addRO(roVO);
			List<ROVO> list = roSvc.getAllRO();
			model.addAttribute("roListData",list);
			model.addAttribute("success","-(新增成功)");
			return "back-end/roomOrder/listAllRO";
		}catch(Exception e) {
			 System.out.println("處理失敗：" + e.getMessage());
			   e.printStackTrace();
			   model.addAttribute("errorMessage", "新增失敗:欄位不可空白!");
			   return "back-end/roomOrder/addRO";
		}
	}
	
	
	@PostMapping("getRO_For_Update")
	public String getRO_For_Update (@RequestParam("roomOrderId")String roomOrderId,ModelMap model) {
		ROVO roVO = roSvc.getOneRO(Integer.valueOf(roomOrderId));
		model.addAttribute("roVO",roVO);
		return "back-end/roomOrder/update_RO_input";
	}
	
	@PostMapping("updateRO")
	public String updateRO(@Valid ROVO roVO, ModelMap model, BindingResult result)throws IOException {
		if(result.hasErrors()) {
			return "back-end/roomOrder/update_RO_input";
		}
		roSvc.updateRO(roVO);
		model.addAttribute("success", "- (修改成功)");
		roVO = roSvc.getOneRO(Integer.valueOf(roVO.getRoomOrderId()));
		model.addAttribute("roVO",roVO);
		return "back-end/roomOrder/listOneRO";
	}
	
	@PostMapping("deleteRO")
	public String deleteRO(@RequestParam("roomOrderId") String roomOrderId,ModelMap model) {
		roSvc.deleteRO(Integer.valueOf(roomOrderId));
		List<ROVO> list = roSvc.getAllRO();
		model.addAttribute("ROListData",list);
		model.addAttribute("success", "- (刪除成功)");
		return "back-end/roomOrder/listAllRO";
	}
	
	@PostMapping("getByMemId")
	public String getByMemId(@RequestParam("memId") String memId,ModelMap model) {
		if(memId.isEmpty() || memId.trim().length() == 0 || memId == null) {
			model.addAttribute("errorMessage3","會員編號:請勿空白");
			return "back-end/roomOrder/select_page_RO";
		}
		
		if (!memId.matches("\\d+")) {  // Regex to check if it's a numeric string
	        model.addAttribute("errorMessage3", "會員編號:請輸入有效的數字");
	        return "back-end/roomOrder/select_page_RO";
	    }
		MemVO mem = memSvc.getMem(Integer.valueOf(memId));
		List<PlanOrder> historyPO = poSvc.findPlanOrdersByMemId(mem.getMemId());
		model.addAttribute("planOrderList",historyPO);
		List<ROVO> list = roSvc.getByMemId(mem.getMemId());
		if(list.isEmpty() || list == null) {
			model.addAttribute("errorMessage3","查無訂房明細");
			return "back-end/roomOrder/select_page_RO";
		}
		
		Integer memIdInt;
	    try {
	    	memIdInt = Integer.valueOf(memId);
	    } catch (NumberFormatException e) {
	        model.addAttribute("errorMessage3", "會員編號不正確");
	        return "back-end/roomOrder/select_page_RO";
	    }
		
		model.addAttribute("roListData",list);
		return "back-end/roomOrder/listAllRO";
	}
	
	@ModelAttribute("rtListData")
	protected List<RTVO> referenceListData_RT(){
		List<RTVO> list = rtSvc.getAllRT();
		return list;
	}
	
	@ModelAttribute("poListData")
	protected List<PlanOrder> referenceListData_PO(){
		List<PlanOrder> list = poSvc.findAllPlanOrders();
		return list;
	}
	
	public BindingResult removeFieldError(ROVO roVO, BindingResult result, String removedFieldname) {
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
				.filter(fieldname -> !fieldname.getField().equals(removedFieldname))
				.collect(Collectors.toList());
		result = new BeanPropertyBindingResult(roVO, "roVO");
		for (FieldError fieldError : errorsListToKeep) {
			result.addError(fieldError);
		}
		return result;
	}
}
