package com.event.cia103g1springboot.room.roomorder.model;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("roService")
public class ROService {

	@Autowired
	RORepository repository;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	public void addRO (ROVO roVO) {
		repository.save(roVO);
	}
	
	public void updateRO (ROVO roVO) {
		repository.save(roVO);
	}
	
	public void deleteRO(Integer roomOderId) {
		if( repository.existsById(roomOderId)) {
			repository.deleteByROId(roomOderId);;
		}
	}
	
	 public ROVO getOneRO(Integer roomOderId) {
		 Optional<ROVO> optional = repository.findById(roomOderId);
		 ROVO roVO = new ROVO();
		 return optional.orElse(roVO);
	 }
	 
	 public List<ROVO> getAllRO(){
		 return repository.findAll();
	 }
	 
	 public List<ROVO> getByPlan(Integer planOrderId){
		 return repository.getByPlan(planOrderId);
	 }
}