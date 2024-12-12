package com.event.cia103g1springboot.bb.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.event.cia103g1springboot.bb.model.BBService;
import com.event.cia103g1springboot.bb.model.BBVO;


@Controller
@RequestMapping("/bulletinBoard")
public class BBFrontController {
	
	@ModelAttribute("bbPostList")
	protected List<BBVO> referenceListData_bbPost(Model model){
		List<BBVO> list = bbSvc.getPostMsg();
		return list;
	}
	
	@Autowired
	BBService bbSvc;
	@GetMapping("/bbList")
	public String bbList(Model model) {
		return "front-end/bulletinBoard/bbList";
	}
	
	
	
	@PostMapping("listPostMsg_ByCompositeQuery")
	public String listPostMsg(HttpServletRequest req, Model model) {
		try{Map<String, String[]> map = req.getParameterMap();
		List<BBVO> list = bbSvc.getAllFront(map);
		model.addAttribute("bbPostList", list);
		return "front-end/bulletinBoard/bbList";
	}catch(Exception e) {
			System.out.println("處理失敗：" + e.getMessage());
			   e.printStackTrace();
			   model.addAttribute("errorMessage", "失敗: " + e.getMessage());
		return "front-end/bulletinBoard/bbList";
	}
	}
	
	@PostMapping("/pinMsg")
	public String pinMsg(@RequestParam("msgid") String msgId,ModelMap model) {
		BBVO bbVO = bbSvc.getOneMsg(Integer.valueOf(msgId));
		bbVO.setIsPinned(true);
		model.addAttribute("bbVO",bbVO);
		List<BBVO> list = bbSvc.getPostMsg();
		model.addAttribute("bbPostList",list);
		return "front-end/bulletinBoard/bbList";
	}
	
	@PostMapping("/unpinMsg")
	public String unpinMsg(@RequestParam("msgid") String msgId,ModelMap model) {
		BBVO bbVO = bbSvc.getOneMsg(Integer.valueOf(msgId));
		bbVO.setIsPinned(false);
		model.addAttribute("bbVO",bbVO);
		List<BBVO> list = bbSvc.getPostMsg();
		model.addAttribute("bbPostList",list);
		return "front-end/bulletinBoard/bbList";
	}
	
//	@RequestMapping(value = "/saveToSession", method = RequestMethod.POST)
//	public String myMsg(HttpServletRequest req,@RequestParam("msgid") String msgId) {
//		HttpSession session = req.getSession();
//		@SuppressWarnings("unchecked")
//		List<String> myMsg = (List<String>) session.getAttribute("myMsg");
//		if(myMsg == null) {
//			myMsg = new ArrayList<>();
//		}
//		myMsg.add(msgId);
//		session.setAttribute("myMsg", myMsg);
//		return "redirect:/myMsg";
//	}
	
}
