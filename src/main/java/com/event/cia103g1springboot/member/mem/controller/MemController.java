package com.event.cia103g1springboot.member.mem.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.event.cia103g1springboot.member.mem.model.MemService;
import com.event.cia103g1springboot.member.mem.model.MemVO;

@Controller
@RequestMapping("/mem")
public class MemController {

	@Autowired
	MemService memSvc;
	@Autowired
	HttpSession session;

	@GetMapping("/register")
	public String register(ModelMap model) {
		if (session.getAttribute("auth") != null) {
			return "front-end/mem/prevent";
		}

		MemVO memVO = new MemVO();
		memVO.setSex(1);
		memVO.setMemType(1);
		model.addAttribute("memVO", memVO);
		return "front-end/mem/register";
	}

	@PostMapping("/register")
	public String registerNewMem(@Valid @Validated(MemVO.ModPwd.class) MemVO memVO, BindingResult result,
			@RequestParam Map<String, String> params, @RequestParam("memImg") MultipartFile part, ModelMap model)
			throws IOException {
		String city = params.get("city");
		String area = params.get("area");
		String road = params.get("road");
		String addr = params.get("addr");
		String realAddr = city + area + road + addr;
		String reg_code = params.get("reg_code");

		String code = (String) session.getAttribute("code");

		boolean isRepeat = memSvc.checkAcct(memVO.getMemAcct());
		if (isRepeat) {
			// 加一個訊息到BindingResult告知帳號重複
			result.rejectValue("memAcct", "repeatError", "此帳號已被註冊");
		}

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		// 去除BindingResult中upFiles欄位的FieldError紀錄 --> 見第172行
		result = removeFieldError(memVO, result, "memImg");

		if (!part.isEmpty()) {
			byte[] buf = part.getBytes(); // getBytes會丟IOException
			memVO.setMemImg(buf);
		}

		if (result.hasErrors()) { // 如果result內有任何錯，或part圖片本身是空的就回去原頁面
			return "front-end/mem/register";
		}

		if (!reg_code.equals(code)) {
			model.addAttribute("reg_code_error", "信箱驗證碼輸入錯誤，請再次確認");
			return "front-end/mem/register";
		} else {
			session.removeAttribute("code");
		}

		/*************************** 2.開始新增資料 *****************************************/
		memVO.setAddr(realAddr);
		memSvc.registerOneMem(memVO);

		/*************************** 3.新增完成,準備轉交 **************/
		model.addAttribute("msg", "帳號註冊成功");
		return "front-end/mem/login";
	}

	@GetMapping("/login")
	public String loginPage(ModelMap model) {
		if (session.getAttribute("auth") != null) {
			return "front-end/mem/prevent";
		}

		MemVO memVO = new MemVO();
		model.addAttribute("memVO", memVO);
		return "front-end/mem/login";
	}

	@PostMapping("/login")
	public String login(@Validated(MemVO.LoginGroup.class) MemVO memVO, BindingResult result,
			@RequestParam("memAcct") String acct, @RequestParam("memPwd") String pwd,
			@RequestParam("captcha") String captchaInput, ModelMap model) {
		// 驗證資料
		List<String> errorMsgs = new ArrayList<String>();
		String captcha = (String) session.getAttribute("captcha");
		captchaInput = captchaInput.trim();

		if (result.hasErrors()) {
			result.getFieldErrors().forEach(err -> errorMsgs.add(err.getDefaultMessage()));
			model.addAttribute("errorMsgs", errorMsgs);
			return "front-end/mem/login";
		}

		String checkRes = memSvc.checkUser(acct, pwd);
		if (!checkRes.equals("帳密無誤")) {
			errorMsgs.add(checkRes);
		}

		// 處理驗證碼是否正確
		if (captchaInput.trim().isEmpty()) {
			errorMsgs.add("驗證碼請勿空白");
			model.addAttribute("errorMsgs", errorMsgs);
			return "front-end/mem/login";
		}
		if (captchaInput == null || !captcha.equalsIgnoreCase(captchaInput)) {
			errorMsgs.add("驗證碼輸入錯誤");
		}

		if (!errorMsgs.isEmpty()) {
			model.addAttribute("errorMsgs", errorMsgs);
			return "front-end/mem/login";
		}

		// 驗證無誤重導向，找出會員資料及設定auth
		MemVO mem = memSvc.findOneMem(acct); // 找到該會員的資料

		session.removeAttribute("captcha");
		session.setAttribute("auth", mem);
		session.setAttribute("sex", mem.getGenderText());
		String location = (String) session.getAttribute("location");
		if (location != null) {
			session.removeAttribute("location");
			return "redirect:" + location;
		}

		return "front-end/mem/profile";
	}

	@GetMapping("/profile")
	public String profile(ModelMap model) {

		@SuppressWarnings("unchecked")
		MemVO mem = (MemVO) session.getAttribute("auth");
		return "front-end/mem/profile";
	}

//	@GetMapping("/modify_profile")
//	public String getModifyProfile(ModelMap model) {
//		model.addAttribute("modify", true);
//
//		MemVO mem = (MemVO) session.getAttribute("auth");
//		model.addAttribute("memVO", mem);
//		return "front-end/mem/profile";
//	}

	@PostMapping("/modify_profile")
	public String modifyProfile(ModelMap model) {
		model.addAttribute("modify", true);

		MemVO mem = (MemVO) session.getAttribute("auth");
		model.addAttribute("memVO", mem);

		return "front-end/mem/profile";
	}

	@PostMapping("/update")
	public String update(@Valid MemVO memVO, BindingResult result, @RequestParam("memImg") MultipartFile part,
			ModelMap model) throws IOException {

		result = removeFieldError(memVO, result, "memImg");
		MemVO oneMem = (MemVO) session.getAttribute("auth");

		memVO.setMemId(oneMem.getMemId());
		memVO.setMemAcct(oneMem.getMemAcct());
		memVO.setMemPwd(oneMem.getMemPwd());
		memVO.setMemType(oneMem.getMemType());

		if (!part.isEmpty()) {
			byte[] buf = part.getBytes();
			memVO.setMemImg(buf);
		} else { // 沒上傳圖片就拿舊的
			memVO.setMemImg(oneMem.getMemImg());
		}

		if (result.hasErrors()) {
			model.addAttribute("modify", true);
			return "front-end/mem/profile";
		}

		memSvc.update(memVO);
		session.setAttribute("auth", memVO);
		session.setAttribute("sex", memVO.getGenderText());

		return "front-end/mem/profile";
	}

	@GetMapping("/logout")

	public String logout(ModelMap model, RedirectAttributes redirectAttributes) {

		session.removeAttribute("auth");
		redirectAttributes.addFlashAttribute("msg", "登出成功");
		return "redirect:/";
	}

	@GetMapping("/forgetpwd")
	public String getforgetPwd() {
		return "front-end/mem/forgetpwd";
	}

	@PostMapping("forgetPwd")
	public String forgetPwdHandler(@RequestParam("memAcct") String memAcct, HttpServletRequest request,
			ModelMap model) {
		memAcct = memAcct.trim();

		boolean hasUser = memSvc.checkAcct(memAcct.trim());
		if (!hasUser) {
			model.addAttribute("noAcctError", "查無使用者");
			return "front-end/mem/forgetpwd";
		}

		MemVO mem = memSvc.findOneMem(memAcct);
		String email = mem.getEmail(); // 用使用者輸入的帳號去找到資料庫的email
		request.setAttribute("email", email);
		// 存id找我要改的會員
		session.setAttribute("modify_id", mem.getMemId());
		return "forward:/mem/modifyPwdMail";
	}

	@GetMapping("/modifyPwd")
	public String getModifyPwd() {
		return "front-end/mem/modifyPwd";
	}

	@PostMapping("/modifyPwd")
	public String modofyPwdHandler(@Validated(MemVO.ModPwd.class) MemVO memVO, BindingResult result,
			@RequestParam("memPwd") String memPwd, ModelMap model) {
		List<String> errorMsgs = new ArrayList<String>();

		if (result.hasErrors()) {
			result.getFieldErrors().forEach(err -> errorMsgs.add(err.getDefaultMessage()));

			model.addAttribute("errorMsgs", errorMsgs);
			return "front-end/mem/modifyPwd";
		}

//		拿session裡面的id找到要改密碼的會員
		Integer modify_id = (Integer) session.getAttribute("modify_id");
		if (modify_id == null) {
			errorMsgs.add("流程有誤，請返回忘記密碼頁面進行收信");
			model.addAttribute("errorMsgs", errorMsgs);
			return "front-end/mem/modifyPwd";
		}
		MemVO mem = (MemVO) memSvc.getMem(modify_id);
		mem.setMemPwd(memPwd);
		memSvc.update(mem);
		session.removeAttribute("modify_id");

		return "front-end/mem/login";
	}

	@ModelAttribute("memVO") // 用於補充未填充的數據
	MemVO getMemVO() {
		MemVO mem = new MemVO();
		return mem;
	}

	// 去除BindingResult中某個欄位的FieldError紀錄
	public BindingResult removeFieldError(MemVO memVO, BindingResult result, String removedFieldname) {
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream() // getFieldErrors拿到所有錯誤
				.filter(fieldname -> !fieldname.getField().equals(removedFieldname)).collect(Collectors.toList());
		result = new BeanPropertyBindingResult(memVO, "memVO"); // 準備一個新的bindingResult來裝保留下來的錯誤
		for (FieldError fieldError : errorsListToKeep) {
			result.addError(fieldError);
		}
		return result;
	}
}
