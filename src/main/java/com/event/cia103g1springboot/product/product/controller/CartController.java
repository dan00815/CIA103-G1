package com.event.cia103g1springboot.product.product.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.event.cia103g1springboot.example.ECPayDemo.OrderService;
import com.event.cia103g1springboot.member.mem.model.MemVO;
import com.event.cia103g1springboot.member.notify.model.MemberNotifyService;
import com.event.cia103g1springboot.member.notify.model.MemberNotifyVO;
import com.event.cia103g1springboot.product.pdtorderitem.model.ProductOrderItemService;
import com.event.cia103g1springboot.product.pdtorderitem.model.ProductOrderItemVO;
import com.event.cia103g1springboot.product.product.model.CartVO;
import com.event.cia103g1springboot.product.product.model.PdtService;
import com.event.cia103g1springboot.product.productorder.model.ProductOrderService;
import com.event.cia103g1springboot.product.productorder.model.ProductOrderVO;

@Controller
@RequestMapping("/shop")
public class CartController {

	@Autowired
	ProductOrderService pdtOrderSvc;

	@Autowired
	ProductOrderItemService pdtOrderItemSvc;

	@Autowired
	PdtService productSvc;

	// 綠界
	@Autowired
	OrderService orderService;
	
	@Autowired
	MemberNotifyService memberNotifyService;

	// ============================================== shoppingCart ==============================================

	@GetMapping("/shoppingCart")
	public String shoppingCart(Model model, HttpSession session) {
		// 檢查 total 是否存在，若不存在則初始化為 0
		if (session.getAttribute("total") == null) {
			session.setAttribute("total", 0);
		}
		return "front-end/shop/shoppingCart";
	}

	@ModelAttribute("cartListData")
	protected List<CartVO> cartListData(HttpSession session) {
		List<CartVO> cartList = (List<CartVO>) session.getAttribute("cart");
		return cartList;
	}

	//商品頁面加入購物車(+1)
	@SuppressWarnings("unchecked")
	@PostMapping("/addToCart")
	@ResponseBody
	public ResponseEntity<String> addToCart(@RequestParam("pdtId") Integer pdtId,
			@RequestParam("pdtName") String pdtName, @RequestParam("pdtPrice") Integer pdtPrice,
			@RequestParam(value = "orderQty", required = false, defaultValue = "1") Integer orderQty,
			HttpSession session) {

		// 確保數量不為 null，並設置預設值
		if (orderQty == null || orderQty <= 0) {
			orderQty = 1;
		}

		// 獲取購物車
		List<CartVO> cart = (List<CartVO>) session.getAttribute("cart");
		if (cart == null) {
			cart = new ArrayList<>();
		}

		// 檢查商品是否已存在於購物車
		CartVO existingItem = cart.stream().filter(item -> item.getPdtId().equals(pdtId)).findFirst().orElse(null);

		if (existingItem != null) {
			// 如果商品已存在，累加數量並更新小計
			existingItem.setOrderQty(existingItem.getOrderQty() + orderQty);
			existingItem.setSubtotal(existingItem.getPdtPrice() * existingItem.getOrderQty());
		} else {
			// 如果商品不存在，新增到購物車
			CartVO newItem = new CartVO(pdtId, pdtName, pdtPrice, orderQty);
			cart.add(newItem);
		}

		// 更新 session
		session.setAttribute("cart", cart);
		System.out.println(session.getAttribute("cart"));

		// 計算總金額
		Integer total = productSvc.calculateTotal(cart);
		System.out.println("購物車總金額: " + total);
		session.setAttribute("total", total);

		return ResponseEntity.ok("商品已加入購物車");
	}

	@PostMapping("/updateCart")
	@ResponseBody
	public ResponseEntity<String> updateCart(@RequestParam("pdtId") Integer pdtId,
			@RequestParam("pdtName") String pdtName, @RequestParam("pdtPrice") Integer pdtPrice,
			@RequestParam("orderQty") Integer orderQty, HttpSession session) {

		// 獲取購物車
		List<CartVO> cart = (List<CartVO>) session.getAttribute("cart");

		// 檢查購物車是否存在
		if (cart == null) {
			return ResponseEntity.badRequest().body("購物車為空，無法更新");
		}

		// 查找對應商品
		CartVO targetItem = cart.stream().filter(item -> item.getPdtId().equals(pdtId)).findFirst().orElse(null);

		if (targetItem != null) {
			if (orderQty > 0) {
				// 修改購物車內容
				targetItem.setOrderQty(orderQty);
				targetItem.setSubtotal(pdtPrice * orderQty); // 更新小計
			} else {
				// 移除商品
				cart.remove(targetItem);

			}
		} else {
			return ResponseEntity.badRequest().body("未找到該商品，無法更新");
		}

		// 將購物車更新回 Session
		session.setAttribute("cart", cart);
		System.out.println(session.getAttribute("cart"));

		// 計算總金額
		Integer total = productSvc.calculateTotal(cart);
		System.out.println("購物車總金額: " + total);
		session.setAttribute("total", total);

		return ResponseEntity.ok("購物車更新成功");

	}

	@PostMapping("/deleteCart")
	@ResponseBody
	public ResponseEntity<String> deleteCart(@RequestParam("pdtId") Integer pdtId, HttpSession session) {
		// 從 Session 中獲取購物車
		List<CartVO> cart = (List<CartVO>) session.getAttribute("cart");
		if (cart == null) {
			ResponseEntity.badRequest().body("購物車為空，無法更新");
		}
		// 查找並移除指定商品
		cart.removeIf(item -> item.getPdtId().equals(pdtId));

		// 更新 Session 中的購物車
		session.setAttribute("cart", cart);
		System.out.println(session.getAttribute("cart"));

		// 計算總金額
		Integer total = productSvc.calculateTotal(cart);
		System.out.println("購物車總金額: " + total);
		session.setAttribute("total", total);

		return ResponseEntity.ok("購物車已刪除");

	}

	//============================================== checkOut ==============================================
	@GetMapping("/checkOut")
	public String checkOut(Model model, HttpSession session) {

		// 確認 session 中是否有會員資訊
		MemVO memVO = (MemVO) session.getAttribute("auth");
		if (memVO == null) {
			// 如果用戶未登錄，重定向到登錄頁面
			return "redirect:mem/login";
		}

		ProductOrderVO productOrderVO = new ProductOrderVO();
		// 綁定會員資料
		productOrderVO.setMemVO(memVO);

//		System.out.println("Member Name: " + productOrderVO.getMemVO().getName());
		// 設定初始值
		productOrderVO.setRecName(memVO.getName());
		productOrderVO.setRecTel(memVO.getTel());
		productOrderVO.setRecAddr(memVO.getAddr());

		model.addAttribute("productOrderVO", productOrderVO);
		return "front-end/shop/checkOut";
	}


	@PostMapping("insert")
	public String insert(@Valid ProductOrderVO productOrderVO, BindingResult result, ModelMap model,
			HttpSession session) throws IOException {
		// @Valid 和 BindingResult 必須出現在相同的方法參數列表中，且 BindingResult 必須緊跟在 @Valid
		// 參數後面，這樣才能正確接收和處理錯誤。
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		if (result.hasErrors()) {
			return "front-end/shop/checkOut";
		}
		/*************************** 2.開始新增資料 *****************************************/
		Integer total = (Integer) session.getAttribute("total");
		productOrderVO.setOrderAmt(total);
		productOrderVO.setOrderStat(2); // 設定狀態:2訂單成立

		// 新增訂單並獲得pdtOrderId!!!
		ProductOrderVO newProductOrderVO = pdtOrderSvc.addProductOrder(productOrderVO);
		Integer newPdtOrderId = newProductOrderVO.getPdtOrderId();
		session.setAttribute("newPdtOrderId", newPdtOrderId);
		System.out.println("自增的訂單 ID: " + newPdtOrderId);
		
		/*************************** 3.新增訂單明細 *****************************************/
		List<CartVO> cart = (List<CartVO>) session.getAttribute("cart");
		if (cart != null && !cart.isEmpty()) {
			for (CartVO item : cart) {
				ProductOrderItemVO PdtOrderItem = new ProductOrderItemVO();
				PdtOrderItem.setPdtOrderId(newPdtOrderId);
				PdtOrderItem.setPdtId(item.getPdtId());
				PdtOrderItem.setPdtPrice(item.getPdtPrice());
				PdtOrderItem.setPdtName(item.getPdtName());
				PdtOrderItem.setOrderQty(item.getOrderQty());
				pdtOrderItemSvc.addProductOrderItem(PdtOrderItem);
				
			}
			System.out.println("訂單明細新增成功");
		}
		
//		session.removeAttribute("total");
//		session.removeAttribute("cart");
		
		return "redirect:orderSuccessPage";

	}
	
	@GetMapping("/orderSuccessPage")
	public String orderSuccessPage(ModelMap model, HttpSession session) {
		
		// 創通知
		Integer newPdtOrderId = (Integer) session.getAttribute("newPdtOrderId");
		ProductOrderVO productOrderVO= pdtOrderSvc.getOneProductOrder(newPdtOrderId);  //用訂單編號找memVO
		MemVO memVO = productOrderVO.getMemVO();
		
		//寄送mail
		if(productOrderVO.getOrderStat().equals(2)) {
			try {
				pdtOrderSvc.sendSuccessPdtOrdMail(productOrderVO);
			}catch(Exception e) {
					e.printStackTrace();
					model.addAttribute("errorMessage","成立信件寄送失敗");
			}
		}
		
		MemberNotifyVO notification = new MemberNotifyVO();
        notification.setMember(memVO);
        notification.setNotifyType(3);  // 3商品訂單
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = LocalDateTime.now().format(formatter);
        System.out.println("當前時間: " + formattedDate);
        
        notification.setNotifyCon("親愛的顧客您好，感謝您的訂購，您的訂單已成功成立！" + "您的訂單編號：" + newPdtOrderId +
                "，訂購日期：" + formattedDate + "<br>" + "歡迎至會員中心查看您的訂單詳情，祝您購物愉快。");
        notification.setBusinessKey("PRODUCT_ORDER_" + newPdtOrderId);
        // 存通知
        memberNotifyService.createNotification(notification);
		return "front-end/shop/orderSuccessPage";
	}

	// ============================================== 我的訂單 ==============================================
	@GetMapping("/noThankPage")
	public String noThankPage(Model model) {
		return "front-end/shop/noThankPage";
	}
	
	@GetMapping("/thankPage")
	public String thankPage(Model model) {
		return "front-end/shop/thankPage";
	}


	//綠界-訂單狀態修改(付款成功or失敗)
	@PostMapping("/orderResult")
	public String orderResult(@RequestParam Map<String, String> params, Model model, HttpSession session) {
		// 1. 獲取所有回傳參數
		System.out.println("ECPay 回傳資料：" + params);

		// 2. 驗證 CheckMacValue(省略...超麻煩)

		// 3. 確認交易結果
		String rtnCode = params.get("RtnCode");
		if ("1".equals(rtnCode)) {
			// 交易成功，處理後續業務邏輯
			String merchantTradeNo = params.get("MerchantTradeNo");
			String tradeAmt = params.get("TradeAmt");
			String paymentDate = params.get("PaymentDate");
			System.out.printf("訂單 %s 支付成功，金額: %s, 支付時間: %s%n", merchantTradeNo, tradeAmt, paymentDate);
			

//			Integer newPdtOrderId = (Integer) session.getAttribute("newPdtOrderId"); 這個娶不到
			
			//取出用CustomField1儲存的newPdtOrderId
			String CustomField1 = params.get("CustomField1");
			Integer newPdtOrderId = Integer.valueOf(CustomField1);
			if (newPdtOrderId == null) {
				System.out.println("newPdtOrderId還是找不到阿TT");
				return "redirect:product/productlist";
			}
			// 修改訂單狀態:1已付款
			pdtOrderSvc.getNewOrderStat(1, newPdtOrderId); 
			
			// 創通知
			ProductOrderVO ProductOrderVO= pdtOrderSvc.getOneProductOrder(newPdtOrderId);  //用訂單編號找memVO
			MemVO memVO = ProductOrderVO.getMemVO();
			
			MemberNotifyVO notification = new MemberNotifyVO();
	        notification.setMember(memVO);
	        notification.setNotifyType(3);  // 3商品訂單
	        
	        notification.setNotifyCon("親愛的顧客您好，感謝您的購買！我們已經成功收到您的信用卡付款。<br>" +
	                "您的訂單編號：" + newPdtOrderId + "，付款日期：" + paymentDate + "，付款金額：" + tradeAmt + "。<br>" +
	                "我們已經開始處理您的訂單，並會儘快為您發送商品。<br>" +
	                "歡迎至會員中心查看您的訂單詳情，祝您購物愉快！");
	        
	        notification.setBusinessKey("PRODUCT_ORDER_" + newPdtOrderId);
	        // 存通知
	        memberNotifyService.createNotification(notification);
			
		} else {
			// 交易失敗，記錄錯誤訊息
			String rtnMsg = params.get("RtnMsg");
			System.out.printf("交易失敗：%s%n", rtnMsg);
			
			//取出用Remark儲存的newPdtOrderId
			String CustomField1 = params.get("CustomField1");
			Integer newPdtOrderId = Integer.valueOf(CustomField1);
			
			if (newPdtOrderId == null) {
				System.out.println("newPdtOrderId還是找不到阿TT");
				return "redirect:product/productlist";
			}
			
			// 修改訂單狀態:0未付款
			pdtOrderSvc.getNewOrderStat(0, newPdtOrderId); 
			
			// 創通知
			ProductOrderVO ProductOrderVO= pdtOrderSvc.getOneProductOrder(newPdtOrderId);  //用訂單編號找memVO
			MemVO memVO = ProductOrderVO.getMemVO();
			
			MemberNotifyVO notification = new MemberNotifyVO();
	        notification.setMember(memVO);
	        notification.setNotifyType(3);  // 3商品訂單
	        
	        notification.setNotifyCon("親愛的顧客您好，您於訂單編號："+ newPdtOrderId + " 的支付未成功。<br>"+
	        				"如需重新支付，請前往會員中心查看訂單並重新操作。如有疑問，請聯繫客服，謝謝！敬祝順安。");
	        notification.setBusinessKey("PRODUCT_ORDER_" + newPdtOrderId);
	        // 存通知
	        memberNotifyService.createNotification(notification);
	        return "redirect:noThankPage"; // 訂購失敗頁面
		}

		return "redirect:thankPage";
	}
	

	@GetMapping("/get_myPdtOrder")
	public String get_myPdtOrder(ModelMap model, HttpSession session) {

		// 確認 session 中是否有會員資訊
		MemVO memVO = (MemVO) session.getAttribute("auth");
		if (memVO == null) {
			// 如果用戶未登錄，重定向到登錄頁面
			return "redirect:mem/login";
		}

		Integer memId = memVO.getMemId();
		List<ProductOrderVO> list = pdtOrderSvc.getProductOrderByMemId(memId);
		model.addAttribute("orderListData", list);
		return "front-end/shop/myPdtOrder";
	}
	
	@ModelAttribute("pdtOrderListData")
	protected List<ProductOrderVO> referenceListData(HttpSession session) {

		List<ProductOrderVO> list = pdtOrderSvc.getAll();

		// 定義狀態對應關係
		Map<Integer, String> orderStatMap = Map.of(0, "未付款", 1, "已付款", 2, "訂單成立", 3, "配送中", 4, "商品已到達", 5, "訂單完成", 6,
				"訂單取消", 7, "未出貨", 8, "退款中", 9, "退款完成");

		Map<Integer, String> payMethodMap = Map.of(0, "轉帳", 1, "信用卡", 2, "貨到付款");

		Map<Integer, String> delMethodMap = Map.of(0, "宅配", 1, "船上取貨");

		// 將 Map 存入 Session
		session.setAttribute("orderStatMap", orderStatMap);
		session.setAttribute("payMethodMap", payMethodMap);
		session.setAttribute("delMethodMap", delMethodMap);

		return list;
	}

	@GetMapping("/get_myPdtOrderItem")
	public String get_myPdtOrderItem(
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
	    
	    //新修正未確認過
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
	    
	    // 拼接商品名稱，符合綠界規則
        String itemNames = list.stream()
                .map(ProductOrderItemVO::getPdtName) // 取得商品名稱
                .collect(Collectors.joining("#")); // 用 # 分隔
        model.addAttribute("itemNames", itemNames);
	    
		// 返回訂單明細頁面
		return "front-end/shop/myPdtOrderItem";
	}

	
	
	
	// 綠界備用不用開
//	@PostMapping("/ecpayCheckout")
//	public String ecpayCheckout(Model model, HttpSession session) {
//		String uuId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20);
//		AllInOne all = new AllInOne("");
//	
//		AioCheckOutALL obj = new AioCheckOutALL();
//		obj.setMerchantTradeNo(uuId);
//		
//		String currentTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
//		obj.setMerchantTradeDate(currentTime);
//		
//		Integer total = (Integer) session.getAttribute("total");
//		obj.setTotalAmount(String.valueOf(total));
//		
//		obj.setTradeDesc("test Description");
//		List<CartVO> cart = (List<CartVO>) session.getAttribute("cart");
//		
//		// 從 cart 中提取商品名稱、數量、價格，並組合成格式
//	    String itemNames = cart.stream()
//	            .map(cartItem -> cartItem.getPdtName() + " x " + cartItem.getOrderQty() + " = " + cartItem.getPdtPrice() + "元")
//	            .collect(Collectors.joining("\n"));  // 使用換行符號分隔每一項商品
//	
//		obj.setItemName(itemNames);
//		obj.setReturnURL("<http://211.23.128.214:5000>");
//		obj.setNeedExtraPaidInfo("N");
//		String form = all.aioCheckOut(obj, null);
//		
////		session.removeAttribute("total");
////		session.removeAttribute("cart");  //購物車移除
//		
//		// 將表單存入模型
//        model.addAttribute("ecpayForm", form);
//
//        // 返回對應的視圖名稱
//        return form; 
//	}

}
