package com.event.cia103g1springboot.product.productorder.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.event.cia103g1springboot.product.productorder.model.ProductOrderService;
import com.event.cia103g1springboot.product.productorder.model.ProductOrderVO;


@Controller
@Validated
@RequestMapping("/pdtorder")
public class PdtOrderIdController {
	
	@Autowired
	ProductOrderService pdtOrderSvc;
	
	@GetMapping("/allPdtOrder")
	public String allPdtOrder(Model model) {
		return "back-end/pdtorder/allPdtOrder";
	}
	
	@GetMapping("/addPdtOrder")
	public String addPdtOrder(Model model) {
		return "back-end/pdtorder/addPdtOrder";
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
	
	//訂單編號查詢
	@PostMapping("getOne_For_Display")
	public String getOne_For_Display(
		/***************************1.接收請求參數 - 輸入格式的錯誤處理*************************/
		@NotEmpty(message="訂單編號: 請勿空白")
		@Digits(integer = 5, fraction = 0, message = "訂單編號：請填數字，請勿超過{integer}位數")
		@RequestParam("pdtOrderId") String pdtOrderId,
		ModelMap model) {

		/***************************2.開始查詢資料*********************************************/
		ProductOrderVO productOrderVO = pdtOrderSvc.getOneProductOrder(Integer.valueOf(pdtOrderId));		
//		List<ProductOrderVO> list = pdtOrderSvc.getAll();
//		model.addAttribute("empListData", list);  
		
		if (productOrderVO == null) {
			model.addAttribute("errorMessage", "查無此訂單");
//			model.addAttribute("list_isEmpty", "true");
			return "back-end/pdtorder/allPdtOrder";
		}		
		/***************************3.查詢完成,準備轉交(Send the Success view)*****************/
		model.addAttribute("productOrderVO", productOrderVO);
		return "back-end/pdtorder/listPdtOrder";
	}
	
		//會員編號查詢
		@PostMapping("getSome_For_Display")
		public String getSome_For_Display(
			/***************************1.接收請求參數 - 輸入格式的錯誤處理*************************/
			@NotEmpty(message="會員編號: 請勿空白")
			@Digits(integer = 3, fraction = 0, message = "會員編號：請填數字，請勿超過{integer}位數")
			@RequestParam("memId") String memId,
			ModelMap model) {

			/***************************2.開始查詢資料*********************************************/		
			List<ProductOrderVO> list = pdtOrderSvc.getProductOrderByMemId(Integer.valueOf(memId));
			model.addAttribute("orderListData", list);  
			if (list.isEmpty()) {
				model.addAttribute("errorMessage", "查無此會員");
//				model.addAttribute("list_isEmpty", "true");
				return "back-end/pdtorder/allPdtOrder";
			}		
			/***************************3.查詢完成,準備轉交(Send the Success view)*****************/
			return "back-end/pdtorder/listPdtOrder2";
		}
		
		//訂單狀態查詢
		@PostMapping("getOrderStat_For_Display")
		public String getOrderStat_For_Display(
			/***************************1.接收請求參數 - 輸入格式的錯誤處理*************************/
			@NotEmpty(message="請選擇訂單狀態")
			@RequestParam("orderStat") String orderStat,
			ModelMap model) {

			/***************************2.開始查詢資料*********************************************/		
			List<ProductOrderVO> list = pdtOrderSvc.getOneProductOrderByOrderStat(Integer.valueOf(orderStat));
			model.addAttribute("orderListData", list);  
			if (list.isEmpty()) {
				model.addAttribute("errorMessage", "查無資料");
				model.addAttribute("list_isEmpty", "true");
				return "back-end/pdtorder/allPdtOrder";
			}		
			/***************************3.查詢完成,準備轉交(Send the Success view)*****************/
			return "back-end/pdtorder/listPdtOrder2";
		}
		
		

		@ExceptionHandler(value = { ConstraintViolationException.class })
		public String handleError(HttpServletRequest req, ConstraintViolationException e, RedirectAttributes redirectAttributes) {
		    // 獲取所有的約束違規
		    Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
		    StringBuilder strBuilder = new StringBuilder();
		    for (ConstraintViolation<?> violation : violations) {
		        strBuilder.append(violation.getMessage()).append("<br>");
		    }
		    String message = strBuilder.toString();

		    // 使用 RedirectAttributes 傳遞錯誤信息
		    redirectAttributes.addFlashAttribute("errorMessage", "請修正以下錯誤:<br>" + message);

		    // 重定向到目標控制器方法
		    return "redirect:/pdtorder/allPdtOrder";
		}
		

	

}
