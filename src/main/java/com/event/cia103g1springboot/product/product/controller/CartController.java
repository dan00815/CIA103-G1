package com.event.cia103g1springboot.product.product.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import com.event.cia103g1springboot.member.mem.model.MemVO;
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
	
	//========================== shoppingPage ==========================
	@GetMapping("/shoppingPage")
	public String shoppingPage(Model model) {
		return "front-end/shop/shoppingPage"; 
	}

	@PostMapping("get_myPdtOrder")
	public String get_MyPdtOrder(@RequestParam("memId") String memId, ModelMap model) {
		List<ProductOrderVO>list = pdtOrderSvc.getProductOrderByMemId(Integer.valueOf(memId));
		model.addAttribute("orderListData", list);
		return "front-end/shop/myPdtOrder";
	}
	
//	@GetMapping("/get_myPdtOrderItem")
//	public String get_myPdtOrderItem(
//			@RequestParam("pdtOrderId") String pdtOrderId, 
//			@RequestParam("orderStat") String orderStat, 
//			@RequestParam("orderDate") String orderDate, 
//			Model model, HttpSession session) {
//		
//	    // 日期解析和格式化器
//	    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
//	    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//	    // 解析和格式化
//	    String formattedDate = LocalDateTime.parse(orderDate, inputFormatter).format(outputFormatter);
//	    // 傳遞格式化後的日期
//	    model.addAttribute("orderDate", formattedDate);
//		
//		// 顯式轉換 orderStat
//	    Integer pdtOrderId2= Integer.valueOf(pdtOrderId);
//	    model.addAttribute("pdtOrderId", pdtOrderId2);
//	    model.addAttribute("orderStat", Integer.valueOf(orderStat));
//	    
//	    
//	    List<ProductOrderItemVO> list = pdtOrderItemSvc.getOrderItemByPdtOrderId(pdtOrderId2);
//	    //計算總金額
//	    Integer totalAmount = 0; // 定義總金額變數
//	    
//	    //計算每個項目的小計並累加總金額
//	    for (ProductOrderItemVO item : list) {
//	    	Integer subtotal = item.getPdtPrice() * item.getOrderQty();
//	        totalAmount += subtotal;    // 累加小計
//	    }
//
//	    // 將結果加入模型
//	    model.addAttribute("pdtOrderItemListData", list); // 傳遞訂單項目列表
//	    model.addAttribute("totalAmount", totalAmount); // 傳遞總金額
//	    
//	    
//		// 返回訂單明細頁面
//		return "front-end/shop/myPdtOrderItem";
//	}
	
	//========================== shoppingCart ==========================

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

	//shoppingPage.html加入購物車
	@SuppressWarnings("unchecked")
	@PostMapping("/addToCart")
	@ResponseBody
	public ResponseEntity<String> addToCart(
			@RequestParam("pdtId") Integer pdtId,
			@RequestParam("pdtName") String pdtName, 
			@RequestParam("pdtPrice") Integer pdtPrice, 
			@RequestParam(value = "orderQty", required = false, defaultValue = "1") Integer orderQty, HttpSession session) {
			
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
	    CartVO existingItem = cart.stream()
	            .filter(item -> item.getPdtId().equals(pdtId))
	            .findFirst()
	            .orElse(null);

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
	public ResponseEntity<String> updateCart(
			@RequestParam("pdtId") Integer pdtId,
			@RequestParam("pdtName") String pdtName, 
			@RequestParam("pdtPrice") Integer pdtPrice, 
			@RequestParam("orderQty") Integer orderQty, HttpSession session) {
		
			// 獲取購物車
		    List<CartVO> cart = (List<CartVO>) session.getAttribute("cart");
	
		    // 檢查購物車是否存在
		    if (cart == null) {
		        return ResponseEntity.badRequest().body("購物車為空，無法更新");
		    }
	
		    // 查找對應商品
		    CartVO targetItem = cart.stream()
		            .filter(item -> item.getPdtId().equals(pdtId))
		            .findFirst()
		            .orElse(null);
	
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
	public ResponseEntity<String> deleteCart(
			@RequestParam("pdtId") Integer pdtId,
			HttpSession session) {
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
	

//	//========================== checkOut ==========================
	@GetMapping("/checkOut")
	public String checkOut(Model model, HttpSession session) {
		
		// 確認 session 中是否有會員資訊
		MemVO memVO = (MemVO) session.getAttribute("auth");
		if (memVO == null) {
		    // 如果用戶未登錄，重定向到登錄頁面
		    return "redirect:mem/login";
		}
		
		ProductOrderVO productOrderVO = new ProductOrderVO();
		//綁定會員資料
		productOrderVO.setMemVO(memVO);
		
//		System.out.println("Member Name: " + productOrderVO.getMemVO().getName());
		//設定初始值
		productOrderVO.setRecName(memVO.getName()); 
		productOrderVO.setRecTel(memVO.getTel());
		productOrderVO.setRecAddr(memVO.getAddr());
		
		model.addAttribute("productOrderVO", productOrderVO);
		return "front-end/shop/checkOut"; 
	}
	
	@PostMapping("insert")
	public String insert
		(@Valid ProductOrderVO productOrderVO, BindingResult result, 
				ModelMap model, 
				HttpSession session
			) throws IOException {
		//@Valid 和 BindingResult 必須出現在相同的方法參數列表中，且 BindingResult 必須緊跟在 @Valid 參數後面，這樣才能正確接收和處理錯誤。
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		if (result.hasErrors()) {
			return "front-end/shop/checkOut";
		}
		/*************************** 2.開始新增資料 *****************************************/
		Integer total = (Integer) session.getAttribute("total");
		productOrderVO.setOrderAmt(total);
		productOrderVO.setOrderStat(2);  //設定狀態:訂單成立
		Integer pdtOrderId = pdtOrderSvc.addProductOrder(productOrderVO);  //新增訂單並獲得pdtOrderId
	    System.out.println("自增的訂單 ID: " + pdtOrderId);
	    
	    /*************************** 3.新增訂單明細 *****************************************/
//		ProductOrderItemVO ProductOrderItem = new ProductOrderItemVO();
		List<CartVO> cart = (List<CartVO>) session.getAttribute("cart");
		if (cart != null && !cart.isEmpty()) {
		    for (CartVO item : cart) {
		    	ProductOrderItemVO PdtOrderItem = new ProductOrderItemVO();
		    	PdtOrderItem.setPdtOrderId(pdtOrderId);
		    	PdtOrderItem.setPdtId(item.getPdtId());
		    	PdtOrderItem.setPdtPrice(item.getPdtPrice());
		    	PdtOrderItem.setPdtName(item.getPdtName());
		    	PdtOrderItem.setOrderQty(item.getOrderQty());
		    	pdtOrderItemSvc.addProductOrderItem(PdtOrderItem);
		    	System.out.println("訂單明細新增成功");
		    }
		    session.removeAttribute("cart");  //購物車移除
		    session.removeAttribute("total");
		}
		return "redirect:/product/productlist";  //新增完成重導至購物頁面
	}
	
}
