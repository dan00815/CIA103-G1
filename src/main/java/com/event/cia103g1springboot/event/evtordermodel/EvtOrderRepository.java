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
    Page<EvtOrderVO> findByEvtOrderStat(Integer evtOrderStat, PageRequest pageRequest);

    @Query("SELECT e FROM EvtOrderVO e WHERE 1=1 " +
            "AND (:keyword IS NULL OR (CONCAT(e.evtOrderId, '') LIKE %:keyword% OR e.evtName LIKE %:keyword%)) " +
            "AND (:status IS NULL OR e.evtOrderStat = :status)")
    Page<EvtOrderVO> findWithDynamicQuery(
            @Param("keyword") String keyword,
            @Param("status") String status,
            Pageable pageable);
}
