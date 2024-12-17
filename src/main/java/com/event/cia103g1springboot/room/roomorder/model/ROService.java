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

	public void updateRO(ROVO roVO) {
		try {
			System.out.println("====開始更新訂單====");
			System.out.println("訂單ID: " + roVO.getRoomOrderId());
			System.out.println("房型ID: " + (roVO.getRtVO() != null ? roVO.getRtVO().getRoomTypeId() : "null"));
			System.out.println("訂單數量: " + roVO.getOrderQty());
			System.out.println("訂單價格: " + roVO.getRoomPrice());

			if (!repository.existsById(roVO.getRoomOrderId())) {
				throw new RuntimeException("找不到訂單ID: " + roVO.getRoomOrderId());
			}

			ROVO saved = repository.save(roVO);

			System.out.println("====更新完成====");
			System.out.println("更新後ID: " + saved.getRoomOrderId());

		} catch (Exception e) {
			System.out.println("更新訂單失敗: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("更新訂單失敗: " + e.getMessage());
		}
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