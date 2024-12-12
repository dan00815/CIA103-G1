
package com.event.cia103g1springboot.bb.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.event.cia103g1springboot.hibernate.util.CompositeQuery.HibernateUtil_CompositeQuery_BB;
import com.event.cia103g1springboot.hibernate.util.CompositeQuery.HibernateUtil_CompositeQuery_BB_Front;



@Service("bbService")
public class BBService{

	@Autowired
	BBRepository repository;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	public void addMsg(BBVO bbVO) {
		repository.save(bbVO);
	}
	
	public void updateMsg(BBVO bbVO) {
		repository.save(bbVO);
	}
	
	public void deleteMsg(Integer msgid) {
		if(repository.existsById(msgid)){
			repository.deleteByMsgId(msgid);
		}
	}
	
	public BBVO getOneMsg(Integer msgid) {
		Optional<BBVO> optional = repository.findById(msgid);
		return optional.orElse(null);
	}
	
	public List<BBVO> getAll(){
		return repository.getAllMsg();
	}
	

	public List<BBVO> getAll(Map<String, String[]> map){
		return HibernateUtil_CompositeQuery_BB.getAllC(map,sessionFactory.openSession());
	}
	
	public List<BBVO> getAllFront(Map<String, String[]> map){
		return HibernateUtil_CompositeQuery_BB_Front.getAllC(map,sessionFactory.openSession());
	}

	
	public List<BBVO> getPostMsg(){
		return repository.getPostMsg();
	}
	
	
	
}
