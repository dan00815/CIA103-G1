package com.event.cia103g1springboot.event.evtimgmodel;

import com.event.cia103g1springboot.event.evtmodel.EvtVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EvtImgRepository extends JpaRepository<EvtImgVO,Integer> {
    List<EvtImgVO> findByEvtVO(EvtVO evtVO);
    List<EvtImgVO> findByEvtVOEvtId(Integer evtId);
    Optional<EvtImgVO> findById(Integer imgId);

    @Modifying
    @Query("SELECT ei FROM EvtImgVO ei JOIN ei.evtVO e WHERE e.evtStat IN :evtStats")
    List<EvtImgVO> findPublishImg(@Param("evtStats") List<Integer> evtStats);
}
