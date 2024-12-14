package com.event.cia103g1springboot.example.ECPayDemo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.event.cia103g1springboot.ecpay.payment.integration.AllInOne;
import com.event.cia103g1springboot.ecpay.payment.integration.domain.AioCheckOutALL;
import com.event.cia103g1springboot.member.mem.model.MemVO;
import com.event.cia103g1springboot.product.product.model.CartVO;
import com.event.cia103g1springboot.product.productorder.model.ProductOrderVO;



@RestController
public class OrderController {

	@Autowired
	OrderService orderService;
	
	
//	@PostMapping("/ecpayCheckout")
//	public String ecpayCheckout() {
//		String aioCheckOutALLForm = orderService.ecpayCheckout();
//
//		return aioCheckOutALLForm;
//	}
	
	//生成綠界訂單
	@PostMapping("/shop_ecpayCheckout")
	public String shop_ecpayCheckout(HttpSession session) {
		String uuId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20);
		AllInOne all = new AllInOne("");
	
		AioCheckOutALL obj = new AioCheckOutALL();
		obj.setMerchantTradeNo(uuId);
		
		String currentTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
		obj.setMerchantTradeDate(currentTime);
		
		Integer total = (Integer) session.getAttribute("total");
		obj.setTotalAmount(String.valueOf(total));
		
		obj.setTradeDesc("test Description");
		List<CartVO> cart = (List<CartVO>) session.getAttribute("cart");
		
		// 從 cart 中提取商品名稱、數量、價格，並組合成格式
	    String itemNames = cart.stream()
	            .map(cartItem -> cartItem.getPdtName() + " x " + cartItem.getOrderQty() + " = " + cartItem.getPdtPrice() + "元")
	            .collect(Collectors.joining("\n"));  // 使用換行符號分隔每一項商品
	
		obj.setItemName(itemNames);
		obj.setReturnURL("<http://211.23.128.214:5000>");
		obj.setNeedExtraPaidInfo("N");
		obj.setChoosePayment("Credit");
		obj.setOrderResultURL("http://localhost:8080/shop/orderResult");
		
		//為了將這筆newPdtOrderId傳遞(超麻煩)
		Integer newPdtOrderId = (Integer) session.getAttribute("newPdtOrderId");
		obj.setCustomField1(String.valueOf(newPdtOrderId));
		
		String form = all.aioCheckOut(obj, null);
		
//		session.removeAttribute("total");
//		session.removeAttribute("cart");  //購物車移除
		
		return form;
	}
	
	
}