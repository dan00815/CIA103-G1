package com.event.cia103g1springboot.room.roomorder.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;




public interface RORepository extends JpaRepository<ROVO,Integer> {

	@Transactional
	@Modifying
	@Query(value = "delete RoomOder WHERE roomOrderId =?1", nativeQuery = true)
	void deleteByROId(int roomOrderId);
	
	@Transactional
	@Modifying
	@Query(value = "SELECT * FROM roomorder WHERE planOrderId =?1", nativeQuery = true)
	List<ROVO> getByPlan(int planOrderId);
	
	@Transactional
	@Modifying
	@Query(value = "SELECT * FROM roomorder ro JOIN planOrder po ON ro.planOrderId = po.planOrderId WHERE po.memId =?1", nativeQuery = true)
	List<ROVO> getByMemId(int memId);
	
}
