package com.event.cia103g1springboot.event.evtordermodel;

import com.event.cia103g1springboot.event.evtmodel.EvtVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EvtOrderService {



    @Autowired
    EvtOrderRepository evtOrderRepository;

    public EvtOrderVO addEvtOrder(EvtOrderVO evtOrderVO) {
        return evtOrderRepository.save(evtOrderVO);
    }

    //之後會用到ㄉ
    public List<EvtOrderVO> findAllEvtOrder() {
        return evtOrderRepository.findAll();
    }

    public EvtOrderVO getOneEvt(Integer evtordId) {
        Optional<EvtOrderVO> optional = evtOrderRepository.findById(evtordId);
        return optional.orElse(null);
    }

    public EvtOrderVO findByEvtVO(EvtVO evtVO){
        return evtOrderRepository.findByEvtVO(evtVO);
    }

    public Page<EvtOrderVO> getAllEvtorders(int page) {
        PageRequest pageRequest = PageRequest.of(page, 5, Sort.by("evtOrderId").descending());
        return evtOrderRepository.findAll(pageRequest);
    }


    @Transactional
    public void updateEvtStatus(Integer evtOrdId, Integer evtStatus) {
        Optional<EvtOrderVO> optional = evtOrderRepository.findById(evtOrdId);
        EvtOrderVO evtOrderVO = optional.get();
        evtOrderVO.setEvtOrderStat(evtStatus);
        evtOrderRepository.save(evtOrderVO);
    }


    public Page<EvtOrderVO> findAllByEvtOrderStat(Integer status, int page) {
        PageRequest pageRequest = PageRequest.of(page, 5, Sort.by("evtOrderId").descending());
        return evtOrderRepository.findByEvtOrderStat(status, pageRequest);
    }


}