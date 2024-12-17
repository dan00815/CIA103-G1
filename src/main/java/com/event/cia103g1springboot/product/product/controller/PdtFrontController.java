package com.event.cia103g1springboot.product.product.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

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

import com.event.cia103g1springboot.product.pdtimg.model.PdtImgService;
import com.event.cia103g1springboot.product.pdtimg.model.PdtImgVO;
import com.event.cia103g1springboot.product.product.model.PdtService;
import com.event.cia103g1springboot.product.product.model.PdtVO;
import com.event.cia103g1springboot.product.producttype.model.PdtTypeService;
import com.event.cia103g1springboot.product.producttype.model.PdtTypeVO;



@Controller
@RequestMapping("/pdt")
public class PdtFrontController {

	@Autowired
	PdtService pdtSvc;
	
	@Autowired
	PdtTypeService pdtTypeSvc;
	
	@Autowired
	PdtImgService pdtImgSvc;
	
	@GetMapping("/productlist")
	public String listSalePdt(Model model) {
		return "front-end/product/productlist";
	}
	
	@ModelAttribute("pdtTypeListData")
	protected List<PdtTypeVO> referenceListData() {
		List<PdtTypeVO> list = pdtTypeSvc.getAll();
		return list;
	}
	@ModelAttribute("pdtImgListData")
	protected List<PdtImgVO> referenceListData_PdtImg(){
		List<PdtImgVO> list = pdtImgSvc.getAllPdtImg();
		return list;
	}

	@ModelAttribute("salePdtList")
	protected List<PdtVO> referenceListData_salePdt(Model model){
		List<PdtVO> list = pdtSvc.getSalePdt();
		return list;
	}
	
	@PostMapping("listSalePdt_ByCompositeQuery")
	public String listAllPdt(HttpServletRequest req, Model model) {
		Map<String, String[]> map = req.getParameterMap();
		List<PdtVO> list = pdtSvc.getAllFront(map);
		model.addAttribute("salePdtList", list);
		model.addAttribute("pdtTypeVO",new PdtTypeVO());
		List<PdtTypeVO> pdtTypeListData = pdtTypeSvc.getAll();
		model.addAttribute("pdtTypeListData", pdtTypeListData);
		if (list.isEmpty()) {
			model.addAttribute("errorMessage", "查無資料");
			return "front-end/product/productlist";
		}
		return "front-end/product/productlist";
	}
	
	@GetMapping("productDetail")
	public String productDetail(
			@NotEmpty(message="商品編號:請勿空白")
			@Digits(integer = 4, fraction = 0, message = "商品編號: 請填數字-請勿超過{integer}位數")
			@Min(value = 1001, message = "商品編號: 不能小於{value}")
			@Max(value = 9999, message = "商品編號: 不能超過{value}")
			@RequestParam("pdtId") String pdtId,
			ModelMap model) {
		PdtVO pdtVO = pdtSvc.getOnePdt(Integer.valueOf(pdtId));
		List<PdtVO> list = pdtSvc.getSalePdt();
		model.addAttribute("salePdtList",list);
		model.addAttribute("pdtTypeVO",new PdtTypeVO());
		List<PdtTypeVO> list2 = pdtTypeSvc.getAll();
		model.addAttribute("pdtTypeListData",list2);
		model.addAttribute("pdtImgVO", new PdtImgVO());
		List<PdtImgVO> list3 = pdtImgSvc.getByPdtId(pdtVO.getPdtId());
		model.addAttribute("pdtImgList",list3);
		model.addAttribute("pdtVO",pdtVO);
		return "front-end/product/productDetail";
	}
}
