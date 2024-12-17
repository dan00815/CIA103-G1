package com.event.cia103g1springboot.onlinecustomerservice.controller;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.event.cia103g1springboot.member.mem.model.MemService;
import com.event.cia103g1springboot.member.mem.model.MemVO;

@RestController
public class ChatController {

	@Autowired
	MemService memSvc;

	@GetMapping("/member/{userName}/avatar")
	public ResponseEntity<String> getMemberAvatar(@PathVariable String userName) {
		System.out.println(userName);
	    MemVO member = memSvc.getuserName(userName);
	    
	    if(member != null) {
	    	System.out.println(member);
	    }
	    byte[] memImg = member.getMemImg();
	    
	    if(memImg == null) {
	    	System.out.println("memImg is null");
	    }
//	    if (member != null && member.getMemImg() != null) {
//	        // 將二進制圖片數據轉換為 Base64 字符串
//	        String base64Image = Base64.getEncoder().encodeToString(member.getMemImg());
//	        return ResponseEntity.ok("data:image/png;base64," + base64Image);  // 返回 base64 字符串
//	    }
	    // 返回默認圖片 URL
	    return ResponseEntity.ok("/static/images/default-avatar.png");
	}
	
	
    
    
    
}
