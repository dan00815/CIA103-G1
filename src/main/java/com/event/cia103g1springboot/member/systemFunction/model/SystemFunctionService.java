package com.event.cia103g1springboot.member.systemFunction.model;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("systemFunctionService")
public class SystemFunctionService {

	@Autowired
	SystemFunctionRepository repository;

	public List<SystemFunctionVO> getAll() {
		return repository.findAll();
	}

	public void addSysFun(SystemFunctionVO sysFunVO) {
		repository.save(sysFunVO);
	}

	public SystemFunctionVO getOneSysFun(Integer id) {
		Optional<SystemFunctionVO> optional = repository.findById(id);
		return optional.orElse(new SystemFunctionVO());
	}
	
	public void deleteFun(Integer funId) {
		repository.deleteById(funId);
	}

}
