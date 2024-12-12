package com.event.cia103g1springboot.event.evtordermodel;

import com.event.cia103g1springboot.event.evtmodel.EvtVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvtOrderRepository extends JpaRepository<EvtOrderVO, Integer> {
    EvtOrderVO findByEvtVO(EvtVO evtVO);;
    Page<EvtOrderVO> findByEvtOrderStat(Integer evtOrderStat, PageRequest pageRequest);
}