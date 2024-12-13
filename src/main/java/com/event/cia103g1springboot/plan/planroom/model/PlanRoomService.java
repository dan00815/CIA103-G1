package com.event.cia103g1springboot.plan.planroom.model;

import com.event.cia103g1springboot.plan.plan.model.Plan;
import com.event.cia103g1springboot.room.roomtype.model.RTVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PlanRoomService {
    @Autowired
    private PlanRoomRepository planRoomRepository;

    public PlanRoom save(PlanRoom planRoom) {return planRoomRepository.save(planRoom);}

    public List<PlanRoom> findAll() {return planRoomRepository.findAll();}

    public PlanRoom findByRmTypeIdAndPlanId(Integer roomTypeID,Integer planId) {
        return planRoomRepository.findByRoomTypeIdAndPlanId(roomTypeID,planId);
    }

    public PlanRoom findById(Integer id) {
        return planRoomRepository.findById(id).orElse(null);
    }

    public void  addPlanRooms(List<PlanRoom> planRooms) {
        planRoomRepository.saveAll(planRooms);
    }

    public int extractCapacityFromRoomTypeName (String roomTypeName) {
        Map <String , Integer> roomCapacityMap = Map.of(
                "單人" ,1,
                "雙人",2,
                "四人",4
        );
        for (Map.Entry<String, Integer> entry : roomCapacityMap.entrySet()) {
            if (roomTypeName.contains(entry.getKey())) {
                return entry.getValue(); // 返回對應人數
            }
        }
        throw new IllegalArgumentException("未知的房型名稱：" + roomTypeName);
    }
}
