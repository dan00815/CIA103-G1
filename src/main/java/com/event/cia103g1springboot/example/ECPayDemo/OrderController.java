package com.event.cia103g1springboot.example.ECPayDemo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.event.cia103g1springboot.ecpay.payment.integration.AllInOne;
import com.event.cia103g1springboot.ecpay.payment.integration.domain.AioCheckOutALL;
import com.event.cia103g1springboot.product.product.model.CartVO;



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
		
		//從 cart 中提取商品名稱、數量、價格，並組合成格式
	    String itemNames = cart.stream()
	            .map(cartItem -> cartItem.getPdtName() + " x " + cartItem.getOrderQty() + " = " + cartItem.getPdtPrice() + "元")
	            .collect(Collectors.joining("#"));  // 使用換行符號分隔每一項商品
		
	    // 若 itemNames 長度超過 400 字元，進行截斷
	    if (itemNames.length() > 400) {
	        itemNames = itemNames.substring(0, 400);
	    }
		
		obj.setItemName(itemNames);
		obj.setReturnURL("<http://211.23.128.214:5000>");
		obj.setNeedExtraPaidInfo("N");
		obj.setChoosePayment("Credit");
		//Client端返回商店 支付完成後自動跳轉
		obj.setOrderResultURL("http://localhost:8080/shop/orderResult");
		//點擊「返回商店」按鈕時使用
//		obj.setClientBackURL("http://localhost:8080/product/productlist");
		//後端伺服器用來接收交易完成的通知
//		obj.setReturnURL("https://yourdomain.com/api/payment/notify");
		
		//為了將這筆newPdtOrderId傳遞(超麻煩)
		Integer newPdtOrderId = (Integer) session.getAttribute("newPdtOrderId");
		obj.setCustomField1(String.valueOf(newPdtOrderId));
		
		String form = all.aioCheckOut(obj, null);
		
		return form;
	}
	
	//生成綠界訂單
		@PostMapping("/myPdtOrderItem_ecpayCheckout")
		public String myPdtOrderItem_ecpayCheckout(@RequestBody Map<String, String> requestData) {
			String uuId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20);
			AllInOne all = new AllInOne("");
		
			AioCheckOutALL obj = new AioCheckOutALL();
			obj.setMerchantTradeNo(uuId);
			
			String currentTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
			obj.setMerchantTradeDate(currentTime);
			
			// 獲取 totalAmount 值
	        String totalAmount = requestData.get("totalAmount");
			obj.setTotalAmount(totalAmount);
			
			obj.setTradeDesc("test Description");
			
			String itemNames = (String) requestData.get("itemNames");
		    // 若 itemNames 長度超過 400 字元，進行截斷
		    if (itemNames.length() > 400) {
		        itemNames = itemNames.substring(0, 400);
		    }
			
			obj.setItemName(itemNames);
			obj.setReturnURL("<http://211.23.128.214:5000>");
			obj.setNeedExtraPaidInfo("N");
			obj.setChoosePayment("Credit");
			//Client端返回商店 支付完成後自動跳轉
			obj.setOrderResultURL("http://localhost:8080/shop/orderResult");
			//點擊「返回商店」按鈕時使用
//			obj.setClientBackURL("http://localhost:8080/product/productlist");
			//後端伺服器用來接收交易完成的通知
//			obj.setReturnURL("https://yourdomain.com/api/payment/notify");
			
			//為了將這筆newPdtOrderId傳遞(超麻煩)
			String pdtOrderId = requestData.get("pdtOrderId");
			obj.setCustomField1(pdtOrderId);
			
			String form = all.aioCheckOut(obj, null);

			
			return form;
		}
	
	
}