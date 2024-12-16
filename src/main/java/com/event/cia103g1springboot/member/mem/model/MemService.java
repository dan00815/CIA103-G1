package com.event.cia103g1springboot.member.mem.model;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("memService")
public class MemService {

	@Autowired
	MemRepository memRepository;

	public void registerOneMem(MemVO memVO) {
		memRepository.save(memVO);
	}

	// 檢查帳號是否重複
	public boolean checkAcct(String acct) {
		List<String> allAccts = memRepository.getAllMemAccts();
		boolean compareRes = false;
		for (String existAcct : allAccts) {
			if (acct.equals(existAcct)) {
				compareRes = true;
			}
		}
		return compareRes;
	}

	// 登入檢查
	public String checkUser(String acct, String pwd) {
		List<String> allAccts = memRepository.getAllMemAccts();
		boolean userExist = false;
		for (String existAcct : allAccts) {
			if (acct.equals(existAcct)) {
				userExist = true;
			}
		}

		if (!userExist) {
			return "使用者不存在";
		}

		String memPwd = memRepository.getMemPwd(acct);
		if (!pwd.equals(memPwd)) {
			return "密碼錯誤";
		}

		return "帳密無誤";
	}

	public MemVO findOneMem(String acct) {
		return memRepository.findMemByAcct(acct);
	}

	public MemVO getMem(Integer memId) {
		Optional<MemVO> optional = memRepository.findById(memId);
		return optional.orElse(new MemVO());
	}

	public void update(MemVO memVO) {
		memRepository.save(memVO);
	}
	
	//聊天室取userName
	public MemVO getuserName(String userName) {
		Optional<MemVO> optional = memRepository.findMemByName(userName);
		return optional.orElse(new MemVO());
	}

}
