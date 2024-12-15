package com.event.cia103g1springboot.product.productorder.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.event.cia103g1springboot.product.productorder.model.ProductOrderService;
import com.event.cia103g1springboot.product.productorder.model.ProductOrderVO;


@Controller

@RequestMapping("/pdtorder")
public class PdtOrderController {
	
	@Autowired
	ProductOrderService pdtOrderSvc;
		
	//===================== 修改訂單 ======================
	@GetMapping("getOne_For_Update")
	public String getOne_For_Update(
			@RequestParam("pdtOrderId") String pdtOrderId, 
			ModelMap model) {
		ProductOrderVO productOrderVO = pdtOrderSvc.getOneProductOrder(Integer.valueOf(pdtOrderId));
		model.addAttribute("productOrderVO", productOrderVO);
		return "back-end/pdtorder/updatePdtOrder";
	} 
	
	@PostMapping("update")
	public String update
	(@Valid ProductOrderVO productOrderVO, BindingResult result, 
			ModelMap model) throws IOException {
		//驗證
		if (result.hasErrors()) {
			return "back-end/pdtorder/updatePdtOrder";
		}
		//為了確保orderDate不遺失
		ProductOrderVO existingOrder = pdtOrderSvc.getOneProductOrder(productOrderVO.getPdtOrderId());
		productOrderVO.setOrderDate(existingOrder.getOrderDate());
		
		//更新後渲染
		pdtOrderSvc.updateProductOrder(productOrderVO);
		model.addAttribute("productOrderVO", productOrderVO);
		
		if(productOrderVO.getOrderStat().equals(6)) {
			try {
				pdtOrderSvc.sendCancelPdtOrdMail(productOrderVO);
			}catch(Exception e) {
					e.printStackTrace();
					model.addAttribute("errorMessage","取消信件寄送失敗");
			}
			
		}else if(productOrderVO.getOrderStat().equals(2)) {
			try {
				pdtOrderSvc.sendSuccessPdtOrdMail(productOrderVO);
			}catch(Exception e) {
					e.printStackTrace();
					model.addAttribute("errorMessage","成立信件寄送失敗");
			}
		}
		
		return "back-end/pdtorder/listPdtOrder";
	}

		
	

}
