package com.event.cia103g1springboot.product.product.controller;

import java.io.IOException;
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
import org.springframework.web.multipart.MultipartFile;

import com.event.cia103g1springboot.product.pdtimg.model.PdtImgService;
import com.event.cia103g1springboot.product.pdtimg.model.PdtImgVO;
import com.event.cia103g1springboot.product.product.model.PdtService;
import com.event.cia103g1springboot.product.product.model.PdtVO;
import com.event.cia103g1springboot.product.producttype.model.PdtTypeService;
import com.event.cia103g1springboot.product.producttype.model.PdtTypeVO;

@Controller
@RequestMapping("/product")
public class PdtController {

	@Autowired
	PdtService pdtSvc;
	
	@Autowired
	PdtTypeService pdtTypeSvc;
	
	@Autowired
	PdtImgService pdtImgSvc;
	
	@GetMapping("/select_page_pdt")
	public String select_page_pdt(Model model) {
		return "back-end/product/select_page_pdt";
	}
	@GetMapping("/listAllPdt")
	public String listAllPdt(Model model) {
		return "back-end/product/select_page_pdt";
	}
	
	@ModelAttribute("pdtListData")
	protected List<PdtVO> referenceListData_pdt(Model model) {
		List<PdtVO> list = pdtSvc.getAll();
		return list;
	}
	
	@PostMapping("addPdtAndImgs")
	public String addPdtAndImgs(@Valid PdtVO pdtVO, BindingResult result,
	                            @RequestParam("pdtImg") MultipartFile[] parts,
	                            ModelMap model) throws IOException {
	    
		result = removeFieldError(pdtVO, result, "pdtImg");
		
		// 驗證商品資料
	    if (result.hasErrors()) {
	        model.addAttribute("errorMessage", "請檢查商品資料是否完整！");
	        return "back-end/product/addPdt";
	    }
		
	    // 驗證圖片
	    if (parts == null || parts.length == 0 || parts[0].isEmpty()) {
	        model.addAttribute("errorMessage", "請至少上傳一張商品圖片！");
	        return "back-end/product/addPdt";
	    }

	    // 新增商品
	    pdtSvc.addPdt(pdtVO);

	    for (MultipartFile multipartFile : parts) {
	        if (!multipartFile.isEmpty()) {
	            PdtImgVO newPdtImgVO = new PdtImgVO();
	            newPdtImgVO.setPdtImg(multipartFile.getBytes());
	            newPdtImgVO.setPdtImgName(multipartFile.getOriginalFilename());
	            newPdtImgVO.setPdtVO(pdtVO);
	            pdtImgSvc.addPdtImg(newPdtImgVO);
	        }
	    }
	    
 		List<PdtImgVO> list = pdtImgSvc.getByPdtId(pdtVO.getPdtId());
 		model.addAttribute("pdtImgListByPdtId",list);
 		PdtImgVO firstPdtImgVO = list.get(0);
 		PdtTypeVO newPdtTypeVO = pdtTypeSvc.getOnePdtType(firstPdtImgVO.getPdtVO().getPdtTypeVO().getPdtTypeId());
 		PdtVO newPdtVO = pdtSvc.getOnePdt(firstPdtImgVO.getPdtVO().getPdtId());
 		newPdtVO.setPdtTypeVO(newPdtTypeVO);
 		model.addAttribute("pdtVO",newPdtVO);
 		return "back-end/product/listOnePdt";
	}

	
	
	@GetMapping("addPdt")
	public String addPdt(ModelMap model) {
		PdtVO pdtVO = new PdtVO();
		model.addAttribute("pdtVO", pdtVO);
		return "back-end/product/addPdt";
	}
		
	@PostMapping("getPdt_For_Update")
	public String getPdt_For_Update(@RequestParam("pdtId") String pdtId, ModelMap model) {
		PdtVO pdtVO = pdtSvc.getOnePdt(Integer.valueOf(pdtId));
		model.addAttribute("pdtVO", pdtVO);
		List<PdtImgVO> list = pdtImgSvc.getByPdtId(pdtVO.getPdtId());
		model.addAttribute("pdtImgListByPdtId",list);
		return "back-end/product/update_pdt_input"; 
	}
	
	@PostMapping("updatePdt")
	public String updatePdt(@Valid PdtVO pdtVO, BindingResult result, ModelMap model) throws IOException {
		if (result.hasErrors()) {
			return "back-end/product/update_pdt_input";
		}
		pdtSvc.updatePdt(pdtVO);
		model.addAttribute("success", "- (修改成功)");
		pdtVO = pdtSvc.getOnePdt(Integer.valueOf(pdtVO.getPdtId()));
		model.addAttribute("pdtVO", pdtVO);
		List<PdtImgVO> list = pdtImgSvc.getByPdtId(Integer.valueOf(pdtVO.getPdtId()));
		model.addAttribute("pdtImgListByPdtId",list);
		return "back-end/product/listOnePdt";
	}
	
	@PostMapping("deletePdt")
	public String deletePdt(@RequestParam("pdtId") String pdtId, ModelMap model) {
		pdtSvc.deletePdt(Integer.valueOf(pdtId));
		/*************************** 3.刪除完成,準備轉交(Send the Success view) **************/
		List<PdtVO> list = pdtSvc.getAll();
		model.addAttribute("pdtListData", list);
		model.addAttribute("success", "- (刪除成功)");
		return "back-end/product/select_page_pdt"; // 刪除完成後轉交listAllEmp.html
	}
	
	@ModelAttribute("pdtTypeListData")
	protected List<PdtTypeVO> referenceListData() {
		return pdtTypeSvc.getAll();
	}
	@ModelAttribute("pdtImgListData")
	protected List<PdtImgVO> referenceListData_PdtImg(){
		return pdtImgSvc.getAllPdtImg();
	}
	
	
	public BindingResult removeFieldError(PdtVO pdtVO, BindingResult result, String removedFieldname) {
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
				.filter(fieldname -> !fieldname.getField().equals(removedFieldname))
				.collect(Collectors.toList());
		result = new BeanPropertyBindingResult(pdtVO, "pdtVO");
		for (FieldError fieldError : errorsListToKeep) {
			result.addError(fieldError);
		}
		return result;
	}
	
	@PostMapping("listPdt_ByCompositeQuery")
	public String listAllPdt(HttpServletRequest req, Model model) {
		Map<String, String[]> map = req.getParameterMap();
		List<PdtVO> list = pdtSvc.getAll(map);
		model.addAttribute("pdtListData", list);
		return "back-end/product/select_page_pdt";
	}
		
}