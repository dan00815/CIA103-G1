package com.event.cia103g1springboot.bb.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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

import com.event.cia103g1springboot.bb.model.BBService;
import com.event.cia103g1springboot.bb.model.BBVO;

@Controller
@RequestMapping("/bb")
public class BBController {
	
	@Autowired
	BBService bbSvc;

	@GetMapping("/select_page_bb")
	public String select_page_bb(Model model) {
		return "back-end/bb/select_page_bb";
	}
	
	@GetMapping("/listAllMsg")
	public String listAllMsg(Model model) {
		return "back-end/bb/listAllMsg";
	}
	
	@ModelAttribute("bbListData")
	protected List<BBVO> referenceListData(Model model) {
		List<BBVO> list = bbSvc.getAll();
		return list;
	}
	
	@GetMapping("addMsg")
	public String addMsg(ModelMap model) {
		BBVO bbVO = new BBVO();
		model.addAttribute("bbVO",bbVO);
		return "back-end/bb/addMsg"; //驗證失敗返回新增頁面
	}
	
	@PostMapping("/insert")
	 public String insert(@ModelAttribute("bbVO") @Valid BBVO bbVO,
	       BindingResult result,
	       @RequestParam("posttime") String posttimeStr,  // 添加這個參數
	       Model model) {
		
		  try {
			   // 轉換日期時間
			   LocalDateTime dateTime = LocalDateTime.parse(posttimeStr);
			   bbVO.setPosttime(Timestamp.valueOf(dateTime));

			   System.out.println("接收到的數據：");
			   System.out.println("msgtype: " + bbVO.getMsgtype());
			   System.out.println("msgtitle: " + bbVO.getMsgtitle());
			   System.out.println("msgcon: " + bbVO.getMsgcon());
			   System.out.println("poststat: " + bbVO.getPoststat());
			   System.out.println("posttime: " + bbVO.getPosttime());


			   bbSvc.addMsg(bbVO);
			   return "redirect:/bb/listAllMsg";
			  } catch (Exception e) {
			   System.out.println("處理失敗：" + e.getMessage());
			   e.printStackTrace();
			   model.addAttribute("errorMessage", "新增失敗:欄位不可空白!");
			   return "back-end/bb/addMsg";
			  }
	  }
	 
	 
	
	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("msgid") String msgid,ModelMap model) {
		BBVO bbVO = bbSvc.getOneMsg(Integer.valueOf(msgid));
		model.addAttribute("bbVO",bbVO);
		return "back-end/bb/update_bb_input";
	}
	
	@PostMapping("update")
	public String update(@ModelAttribute("bbVO")@Valid BBVO bbVO, BindingResult result, @RequestParam("posttime") String posttimeStr,Model model)throws IOException {
		
		 System.out.println("==========進入 update 方法==========");

		  try {
		   // 轉換日期時間
		   LocalDateTime dateTime = LocalDateTime.parse(posttimeStr);
		   bbVO.setPosttime(Timestamp.valueOf(dateTime));
		   System.out.println("接收到的數據：");
		   System.out.println("msgid: " + bbVO.getMsgid());
		   System.out.println("msgtype: " + bbVO.getMsgtype());
		   System.out.println("msgtitle: " + bbVO.getMsgtitle());
		   System.out.println("msgcon: " + bbVO.getMsgcon());
		   System.out.println("poststat: " + bbVO.getPoststat());
		   System.out.println("posttime: " + bbVO.getPosttime());
		
		if(!result.hasErrors()) {
			return "back-end/bb/update_bb_input";
		}
		bbSvc.updateMsg(bbVO);
		model.addAttribute("success","-(修改成功)");
		bbVO = bbSvc.getOneMsg(Integer.valueOf(bbVO.getMsgid()));
		model.addAttribute("bbVO",bbVO);
		return "back-end/bb/listOneMsg";
		  } catch (Exception e) {
			   e.printStackTrace();
			   model.addAttribute("errorMessage", "失敗:欄位不可空白!");
			   return "back-end/bb/addMsg";
			  }
	}
	
	
	@PostMapping("delete")
	public String delete(@RequestParam("msgid")Integer msgid,ModelMap model) {
		bbSvc.deleteMsg(msgid);
		List<BBVO> list = bbSvc.getAll();
		model.addAttribute("bbListData", list);
		model.addAttribute("success","-(刪除成功)");
		return "back-end/bb/listAllMsg";
	}
	
	public BindingResult removeFieldError(BBVO bbVO, BindingResult result, String removedFieldname) {
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
				.filter(fieldname -> !fieldname.getField().equals(removedFieldname))
				.collect(Collectors.toList());
		result = new BeanPropertyBindingResult(bbVO, "bbVO");
		for (FieldError fieldError : errorsListToKeep) {
			result.addError(fieldError);
		}
		return result;
	}
	
	@PostMapping("listMsg_ByCompositeQuery")
	public String listAllMsg(HttpServletRequest req,Model model) {
		try {
			Map<String, String[]> map = req.getParameterMap();
			List<BBVO> list = bbSvc.getAll(map);
			model.addAttribute("bbListData",list);
			return "back-end/bb/listAllMsg";
		}catch (Exception e) {
			   System.out.println("處理失敗：" + e.getMessage());
			   e.printStackTrace();
			   model.addAttribute("errorMessage", "失敗: " + e.getMessage());
			   return "back-end/bb/addMsg";	  
		}
	}
	
	@PostMapping("pinMsg")
	public String pinMsg(@RequestParam("msgid") String msgid,ModelMap model) {
		BBVO bbVO = bbSvc.getOneMsg(Integer.valueOf(msgid));
		bbVO.setIsPinned(true);
		model.addAttribute("bbVO",bbVO);
		List<BBVO> list = bbSvc.getAll();
		model.addAttribute("bbListData",list);
		return "back-end/bb/listAllMsg";
	}
	
	@PostMapping("unpinMsg")
	public String unpinMsg(@RequestParam("msgid") String msgid,ModelMap model) {
		BBVO bbVO = bbSvc.getOneMsg(Integer.valueOf(msgid));
		bbVO.setIsPinned(false);
		model.addAttribute("bbVO",bbVO);
		List<BBVO> list = bbSvc.getAll();
		model.addAttribute("bbListData",list);
		return "redirect:/bb/listAllMsg ";
	}
	
	
}
