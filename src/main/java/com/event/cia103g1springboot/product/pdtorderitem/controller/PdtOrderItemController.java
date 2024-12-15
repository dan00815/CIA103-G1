package com.event.cia103g1springboot.product.pdtorderitem.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.event.cia103g1springboot.product.pdtorderitem.model.ProductOrderItemService;
import com.event.cia103g1springboot.product.pdtorderitem.model.ProductOrderItemVO;


@Controller
@Validated
@RequestMapping("/pdtorderitem")
public class PdtOrderItemController {
	
	@Autowired
	ProductOrderItemService pdtOrderItemSvc;
	
	@GetMapping("/getOrderItemDetails")
	public String getOrderItemDetails(
			@RequestParam("pdtOrderId") String pdtOrderId, 
			@RequestParam("orderStat") String orderStat, 
			@RequestParam("orderDate") String orderDate, 
			Model model) {
		
	    // 日期解析和格式化器
	    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
	    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	    // 解析和格式化
	    String formattedDate = LocalDateTime.parse(orderDate, inputFormatter).format(outputFormatter);
	    // 傳遞格式化後的日期
	    model.addAttribute("orderDate", formattedDate);
		
		// 顯式轉換 orderStat
	    Integer pdtOrderId2= Integer.valueOf(pdtOrderId);
	    model.addAttribute("pdtOrderId", pdtOrderId2);
	    model.addAttribute("orderStat", Integer.valueOf(orderStat));
	    
	    
//	    List<ProductOrderItemVO> list = pdtOrderItemSvc.getOrderItemByPdtOrderId(pdtOrderId2);
	    List<ProductOrderItemVO> list = pdtOrderItemSvc.getOrderItemsByPdtOrderId(pdtOrderId2);
	    //計算總金額
	    Integer totalAmount = 0; // 定義總金額變數
	    
	    //計算每個項目的小計並累加總金額
	    for (ProductOrderItemVO item : list) {
	    	Integer subtotal = item.getPdtPrice() * item.getOrderQty();
	        totalAmount += subtotal;    // 累加小計
	    }

	    // 將結果加入模型
	    model.addAttribute("pdtOrderItemListData", list); // 傳遞訂單項目列表
	    model.addAttribute("totalAmount", totalAmount); // 傳遞總金額
	    
	    
		// 返回訂單明細頁面
		return "back-end/pdtorderitem/pdtOrderItem";
	}
	
	
}
