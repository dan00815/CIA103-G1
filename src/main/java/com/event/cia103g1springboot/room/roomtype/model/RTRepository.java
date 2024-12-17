package com.event.cia103g1springboot.room.roomtype.model;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface RTRepository extends JpaRepository<RTVO,Integer>{

	@Transactional
	@Modifying
	@Query(value = "UPDATE roomType SET roomQty = -1 WHERE roomTypeId = ?1", nativeQuery = true)
	 void deleteByRoomTypeId(int roomTypeId);
	
	@Transactional
	@Modifying
	@Query(value = "SELECT * FROM roomType WHERE roomQty >0", nativeQuery = true)
	List<RTVO> getAllRT();
	
	 @Modifying
	 @Query("UPDATE RTVO rt SET rt.roomQty = :qty WHERE rt.roomTypeId = :roomTypeId")
	 void updateRoomQty(@Param("roomTypeId") Integer roomTypeId, @Param("qty") Integer qty);


	 RTVO findByRoomTypeId(Integer roomTypeId);
}

