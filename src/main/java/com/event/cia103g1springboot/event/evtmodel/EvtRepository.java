package com.event.cia103g1springboot.event.evtmodel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EvtRepository extends JpaRepository<EvtVO, Integer> , JpaSpecificationExecutor<EvtVO> {

    Page<EvtVO> findByEvtStatOrEvtStatOrderByEvtDateAsc(Integer stat1, Integer stat2, Pageable page);


    Optional<EvtVO> findByEvtId(Integer evtId);

    // 獲取指定範圍
    @Query("SELECT e FROM EvtVO e WHERE e.evtDate BETWEEN :startDate AND :endDate AND e.evtStat = :status")
    List<EvtVO> findByDateRangeAndStatus(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") Integer status
    );

    // 查找指定週
    @Query("SELECT e FROM EvtVO e WHERE " +
            "e.evtDate BETWEEN :startDate AND :endDate " + "AND e.evtStat <> 0 " + "ORDER BY e.evtDate ASC") // 只排除已下架的活動

    List<EvtVO> findEventsForWeek(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    //查已上架
    @Query("SELECT e FROM EvtVO e WHERE e.evtStat = :status ORDER BY e.evtDate ASC")
    List<EvtVO> findByEvtStatOrderByEvtDateAsc(@Param("status") Integer status1);
}


