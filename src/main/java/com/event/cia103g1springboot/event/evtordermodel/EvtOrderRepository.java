package com.event.cia103g1springboot.event.evtordermodel;

import com.event.cia103g1springboot.event.evtmodel.EvtVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvtOrderRepository extends JpaRepository<EvtOrderVO, Integer> {
    EvtOrderVO findByEvtVO(EvtVO evtVO);;
    //分頁用
    Page<EvtOrderVO> findByEvtOrderStat(Integer evtOrderStat, PageRequest pageRequest);

    //模糊查詢訂單
    @Query("SELECT e FROM EvtOrderVO e WHERE 1=1 " +
            "AND (:keyword IS NULL OR (CONCAT(e.evtOrderId, '') LIKE %:keyword% OR e.evtName LIKE %:keyword%)) " +
            "AND (:status IS NULL OR e.evtOrderStat = :status)")
    //複合查詢ㄉ
    Page<EvtOrderVO> findWithDynamicQuery(
            @Param("keyword") String keyword,
            @Param("status") String status,
            Pageable pageable);

    //檢查是不是有重複報名= =
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EvtOrderVO e WHERE e.memVO.memId = :memId AND e.evtVO.evtId = :evtId")
    boolean existsByMemIdAndEvtId(@Param("memId") Integer memId, @Param("evtId") Integer evtId);
}

