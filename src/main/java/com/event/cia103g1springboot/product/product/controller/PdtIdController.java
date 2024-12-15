 package com.event.cia103g1springboot.product.product.controller;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.event.cia103g1springboot.product.product.model.PdtService;
import com.event.cia103g1springboot.product.product.model.PdtVO;
import com.event.cia103g1springboot.product.producttype.model.PdtTypeService;
import com.event.cia103g1springboot.product.producttype.model.PdtTypeVO;

@Controller
@Validated
@RequestMapping("/product")
public class PdtIdController {

	@Autowired
	PdtService pdtSvc;
	
	@Autowired
	PdtTypeService pdtTypeSvc;
	
	@PostMapping("getPdt_For_Display")
	public String getOne_For_Display(
		@NotEmpty(message="商品編號: 請勿空白")
		@Digits(integer = 4, fraction = 0, message = "商品編號: 請填數字-請勿超過{integer}位數")
		@Min(value = 1001, message = "商品編號: 不能小於{value}")
		@Max(value = 9999, message = "商品編號: 不能超過{value}")
		@RequestParam("pdtId") String pdtId,
		ModelMap model) {

		PdtVO pdtVO = pdtSvc.getOnePdt(Integer.valueOf(pdtId));
		
		List<PdtVO> list = pdtSvc.getAll();
		model.addAttribute("pdtListData", list);    
		model.addAttribute("pdtTypeVO", new PdtTypeVO());
		List<PdtTypeVO> list2 = pdtTypeSvc.getAll();
    	model.addAttribute("pdtTypeListData",list2); 
		
		if (pdtVO == null) {
			model.addAttribute("errorMessage", "查無資料");
			return "back-end/product/select_page_pdt";
		}
		model.addAttribute("pdtVO", pdtVO);
		return "back-end/product/select_page_pdt";
	}
	
	@ExceptionHandler(value = { ConstraintViolationException.class })
	public ModelAndView handleError(HttpServletRequest req,ConstraintViolationException e,Model model) {
	    Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
	    StringBuilder strBuilder = new StringBuilder();
	    for (ConstraintViolation<?> violation : violations ) {
	          strBuilder.append(violation.getMessage() + "<br>");
	    }
		List<PdtVO> list = pdtSvc.getAll();
		model.addAttribute("pdtListData", list); 
		model.addAttribute("pdtTypeVO", new PdtTypeVO()); 
		List<PdtTypeVO> list2 = pdtTypeSvc.getAll();
    	model.addAttribute("pdtTypeListData",list2); 
		String message = strBuilder.toString();
	    return new ModelAndView("back-end/product/select_page_pdt", "errorMessage", "請修正以下錯誤:<br>"+message);
	}
}
