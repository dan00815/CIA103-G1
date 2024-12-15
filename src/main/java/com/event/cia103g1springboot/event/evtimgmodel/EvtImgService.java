package com.event.cia103g1springboot.event.evtimgmodel;

import com.event.cia103g1springboot.event.evtmodel.EvtVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class EvtImgService {

    @Autowired
    EvtImgRepository evtImgRepository;


    @Transactional
    public EvtImgVO addEventImage(EvtImgVO evtImgVO) {
        return evtImgRepository.save(evtImgVO);
    }

    //根據活動id拿
    public List<EvtImgVO> getImagesByEvtId(Integer evtId) {
        return evtImgRepository.findByEvtVOEvtId(evtId);
    }

    //根據照片編號拿
    public EvtImgVO getImageById(Integer imgId) {
        Optional<EvtImgVO> evtImg = evtImgRepository.findById(imgId);
        if (evtImg.isPresent()) {
            return evtImg.get();
        } else {
            return null;
        }
    }

    //拿資料庫所有照片
    public List<EvtImgVO> findPublishevtImg(Integer evtStat1, Integer evtStat2) {
        return evtImgRepository.findPublishImg(Arrays.asList(evtStat1, evtStat2));
    }


    // 取得活動的所有圖片
    public List<EvtImgVO> getEventImages(EvtVO evtVO) {
        return evtImgRepository.findByEvtVO(evtVO);
    }

    public void deleteEventImage(Integer imgId) {
            evtImgRepository.deleteById(imgId);
        }
    }




