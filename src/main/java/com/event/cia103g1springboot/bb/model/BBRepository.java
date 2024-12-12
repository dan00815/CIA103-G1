package com.event.cia103g1springboot.bb.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;



public interface BBRepository extends JpaRepository<BBVO, Integer>{

	@Transactional
	@Modifying
	@Query(value = "UPDATE bulletinboard SET poststat = 2 WHERE msgid = ?1", nativeQuery = true)
	void deleteByMsgId(int msgid);

	@Transactional
	@Modifying
	@Query(value = "SELECT * FROM bulletinboard WHERE postStat <2 ORDER BY isPinned, msgId", nativeQuery = true)
	List<BBVO> getAllMsg();

	@Query(value = "from BBVO where msgid=?1 and msgtype like?2 and msgtitle like?3 and msgcon like ?4 and poststat =?5 and posttime between ?6 and ?7 ORDER BY isPinned, msgId")
	List<BBVO> findByOthers(int msgid , Byte msgtype ,String msgtitle,String msgcon, Byte poststat, java.sql.Timestamp posttime);
//			java.sql.Timestamp startPosttime,java.sql.Timestamp endPosttime);


	@Transactional
	@Modifying
	@Query(value = "SELECT * FROM bulletinboard WHERE postStat =1 ORDER BY isPinned, msgId", nativeQuery = true)
	List<BBVO> getPostMsg();

}
