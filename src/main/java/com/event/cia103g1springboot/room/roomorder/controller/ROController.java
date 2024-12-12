package com.event.cia103g1springboot.room.roomorder.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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
	
	@PostMapping("insertRO")
	public String insertRO (@ModelAttribute("roVO")@Valid ROVO roVO ,BindingResult result , ModelMap model)throws IOException{
		try {
			if(!result.hasErrors()) {
				model.addAttribute("errorMessage","請檢察錯誤");
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
	
//	@PostMapping("insertROByPO")
//	public String insertROByPO (@Valid PlanOrder order, Map<Object, Object> roomData,BindingResult result,ModelMap model)throws IOException{ 
//		ROVO roVO = new ROVO();
//		model.addAttribute("roVO",roVO);
//		if(result.hasErrors()) {
//			model.addAttribute("errorMessage","房型訂單明細新增失敗");			
//			return "redirect:/planord/detail/";
//		}
//		
//    	PlanOrder newPO = poSvc.getOnePlanOrder(order.getPlanOrderId());
//    	RTVO rtVO = rtSvc.getOneRT(Integer.parseInt(roomData.get("roomTypeId").toString()));
//    	roVO.setPlanOrder(newPO);
//    	roVO.setOrderQty(Integer.parseInt(roomData.get("roomQty").toString()));
//    	roVO.setRtVO(rtVO);
//    	roVO.setRoomPrice(Integer.parseInt(roomData.get("roomPrice").toString()));
//    	roVO.setOrderQty(Integer.parseInt(roomData.get("roomQty").toString()));
//		roSvc.addRO(roVO);
//		List<ROVO> list = roSvc.getAllRO();
//		model.addAttribute("roListData",list);
//		model.addAttribute("success","-(新增成功)");
//		return "back-end/roomOrder/listAllRO";
//	}
	
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
