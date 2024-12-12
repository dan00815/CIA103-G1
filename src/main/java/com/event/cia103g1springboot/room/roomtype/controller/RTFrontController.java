package com.event.cia103g1springboot.room.roomtype.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.event.cia103g1springboot.room.roomImg.model.RImgService;
import com.event.cia103g1springboot.room.roomImg.model.RImgVO;
import com.event.cia103g1springboot.room.roomtype.model.RTService;
import com.event.cia103g1springboot.room.roomtype.model.RTVO;

@Controller
@RequestMapping("/roomtype")
public class RTFrontController {

	@Autowired
	RTService rtSvc;
	
	@Autowired
	RImgService rImgSvc;
	
	@GetMapping("/roomTypeList")
	public String roomTypeList(Model model) {
		return "front-end/roomtype/roomTypeList";
	}
	
	@ModelAttribute("rtListData")
	protected List<RTVO> referenceListData_RT(Model model) {
		List<RTVO> list = rtSvc.getAllRT();
		return list;
	}
	
	@ModelAttribute("rImgListData")
	protected List<RImgVO> referenceListData_RImg(){
		return rImgSvc.getAllRImg();
	}
	
	@PostMapping("listRT_ByCompositeQuery")
	public String listAllRT(HttpServletRequest req , Model model) {
		Map<String,String[]> map = req.getParameterMap();
		List<RTVO> list = rtSvc.getAllRT(map);
		model.addAttribute("rtListData",list);
		return "front-end/roomtype/roomTypeList";
	}
	
	@GetMapping("roomTypeDetail")
	public String rtDetail(
			@RequestParam("roomTypeId") String roomTypeId,
			ModelMap model) {
		RTVO rtVO = rtSvc.getOneRT(Integer.valueOf(roomTypeId));
		List<RTVO> list = rtSvc.getAllRT();
		model.addAttribute("rtListData",list);
		List<RImgVO> list3 = rImgSvc.getByroomTypeId(rtVO.getRoomTypeId());
		model.addAttribute("rImgListData",list3);
		model.addAttribute("rtVO",rtVO);
		return "front-end/roomtype/roomTypeDetail";
	}

}
