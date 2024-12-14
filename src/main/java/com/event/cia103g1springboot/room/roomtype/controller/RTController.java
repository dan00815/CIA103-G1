package com.event.cia103g1springboot.room.roomtype.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.multipart.MultipartFile;

import com.event.cia103g1springboot.room.roomImg.model.RImgService;
import com.event.cia103g1springboot.room.roomImg.model.RImgVO;
import com.event.cia103g1springboot.room.roomtype.model.RTService;
import com.event.cia103g1springboot.room.roomtype.model.RTVO;

@Controller
@RequestMapping("/rt")
public class RTController {
	
	@Autowired
	RTService rtSvc;
	
	@Autowired
	RImgService rImgSvc;
	
	@GetMapping("/select_page_RT")
	public String select_page_RT(Model model) {
		return "back-end/rt/select_page_RT";
	}
	
	@GetMapping("/listAllRT")
	public String listAllRT(Model model) {
		return "back-end/rt/listAllRT";
	}
	
	@ModelAttribute("rtListData")
	protected List<RTVO> referenceListData_RT(Model model) {
		List<RTVO> list = rtSvc.getAllRT();
		return list;
	}
	
	@PostMapping("addRTAndRImgs")
	public String addRTAndRImgs(@Valid RTVO rtVO, BindingResult result,
								@RequestParam("rImg") MultipartFile[] parts,
								ModelMap model) throws IOException {
		result = removeFieldError(rtVO, result, "rImg");
		
		if(result.hasErrors()) {
			model.addAttribute("errorMessage","請檢查房型資訊是否完整");
			return "back-end/rt/addRT";
		}
		
		if(parts == null || parts.length == 0 ||parts[0].isEmpty()) {
			model.addAttribute("errorMessage","請至少上傳一張房型圖片!");
			return "back-end/rt/addRT";
		}
		
		rtSvc.addRT(rtVO);
		
		for(MultipartFile multipartFile : parts) {
			if(!multipartFile.isEmpty()) {
				RImgVO rImgVO = new RImgVO();
				rImgVO.setRoomImg(multipartFile.getBytes());
				rImgVO.setRoomImgName(multipartFile.getOriginalFilename());
				rImgVO.setRtVO(rtVO);
				rImgSvc.addRImg(rImgVO);
			}
		}
		
		List<RImgVO> list = rImgSvc.getByroomTypeId(rtVO.getRoomTypeId());
		model.addAttribute("rImgListByRTId",list);
		RImgVO firstRImg = list.get(0);
		RTVO newRTVO = rtSvc.getOneRT(firstRImg.getRtVO().getRoomTypeId());
		model.addAttribute("rtVO",newRTVO);
		return "back-end/rt/ListOneRT";
	}
	
	@GetMapping("addRT")
	public String addRT(ModelMap model) {
		RTVO rtVO = new RTVO();
		model.addAttribute("rtVO",rtVO);
		return "back-end/rt/addRT";
	}
	
	@PostMapping("getRT_For_Update")
	public String getRT_For_Update(@RequestParam("roomTypeId") String roomTypeId, ModelMap model){
		RTVO rtVO = rtSvc.getOneRT(Integer.valueOf(roomTypeId));
		model.addAttribute("rtVO",rtVO);
		List<RImgVO> list = rImgSvc.getByroomTypeId(rtVO.getRoomTypeId());
		model.addAttribute("rImgListByRTId",list);
		return "back-end/rt/update_RT_input";
	} 
	
	@PostMapping("updateRT")
	public String updateRT(@Valid RTVO rtVO,BindingResult result,ModelMap model ) throws IOException{
		if(result.hasErrors()) {
			return "back-end/rt/update_RT_input";
		}
		rtSvc.updateRT(rtVO);
		model.addAttribute("success","-(修改成功)");
		rtVO = rtSvc.getOneRT(Integer.valueOf(rtVO.getRoomTypeId()));
		model.addAttribute("rtVO",rtVO);
		List<RImgVO> list = rImgSvc.getByroomTypeId(rtVO.getRoomTypeId());
		model.addAttribute("rImgListByRTId",list);
		return "back-end/rt/listOneRT";
	}
	
	@PostMapping("deleteRT")
	public String deleteRT(@RequestParam("roomTypeId") String roomTypeId,ModelMap model) {
		rtSvc.deleteRT(Integer.valueOf(roomTypeId));
		List<RTVO> list = rtSvc.getAllRT();
		model.addAttribute("rtListData",list);
		model.addAttribute("success","-(刪除成功)");
		return "back-end/rt/listAllRT";
	}
	
	@ModelAttribute("rImgListData")
	protected List<RImgVO> referenceListData_RImg(){
		return rImgSvc.getAllRImg();
	}
	
	public BindingResult removeFieldError(RTVO rtVO,BindingResult result,String removedFieldname) {
		List<FieldError> errorListToKeep = result.getFieldErrors().stream()
				.filter(fieldName -> !fieldName.getField().equals(removedFieldname))
				.collect(Collectors.toList());
		result = new BeanPropertyBindingResult(rtVO,"rtVO");
		for(FieldError fieldError : errorListToKeep) {
			result.addError(fieldError);
		}
		return result;
	}
	
	@PostMapping("listRT_ByCompositeQuery")
	public String listAllRT(HttpServletRequest req , Model model) {
		Map<String,String[]> map = req.getParameterMap();
		List<RTVO> list = rtSvc.getAllRT(map);
		model.addAttribute("rtListData",list);
		return "back-end/rt/select_page_RT";
	}
		
	
}
