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
	
//	public void updateROsql (String roomOrderId,String roomTypeId, String roomTypeName, String planOrderId , String roomPrice, String roomQty) {
//		repository.updateRO(Integer.valueOf(roomOrderId), Integer.valueOf(roomTypeId), roomTypeName, Integer.valueOf(planOrderId), Integer.valueOf(roomPrice), Integer.valueOf(roomQty));
//	}
	
	public void updateRO (ROVO roVO) {
		repository.save(roVO);
	}
	
	public void deleteRO(Integer roomOrderId) {
		if( repository.existsById(roomOrderId)) {
			repository.deleteByROId(roomOrderId);;
		}
	}
	
	 public ROVO getOneRO(Integer roomOrderId) {
		 Optional<ROVO> optional = repository.findById(roomOrderId);
		 return optional.orElse(null);
	 }
	 
	 public List<ROVO> getAllRO(){
		 return repository.findAll();
	 }
	 
	 public List<ROVO> getByPlan(Integer planOrderId){
		 return repository.getByPlan(planOrderId);
	 }
	 
	 public List<ROVO> getByMemId(Integer memId){
		 return repository.getByMemId(memId);
	}
}