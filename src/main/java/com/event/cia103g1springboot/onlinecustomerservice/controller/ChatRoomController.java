package com.event.cia103g1springboot.onlinecustomerservice.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.event.cia103g1springboot.member.mem.model.MemVO;

@Controller
public class ChatRoomController {
	
	//=========== chatRoom ===========
			@GetMapping("/fakeLogin")
			public String fakeLogin(Model mode) {
				return "front-end/onlinecustomerservice/fakeLogin";
			}
			
			
			@GetMapping("/chatRoom")
			public String chatRoom(Model model, HttpSession session) {
				MemVO memVO = (MemVO) session.getAttribute("auth");
				String userName = memVO.getMemAcct(); 
				if (userName != null) {
					model.addAttribute("userName", userName);
				} else {
				    throw new IllegalStateException("Session attribute 'auth' is null.");
				}
				return "front-end/onlinecustomerservice/chatRoom";
			}
			
			@GetMapping("/backChatRoom")
			public String backChatRoom(Model mode) {
				return "back-end/onlinecustomerservice/backChatRoom";
			}
	
}
