package com.event.cia103g1springboot.plan.planorder.model;


import com.event.cia103g1springboot.plan.planorder.controller.planOrderController;
import com.event.cia103g1springboot.room.roomorder.model.ROVO;
import com.event.cia103g1springboot.room.roomtype.model.RTRepository;
import com.event.cia103g1springboot.room.roomtype.model.RTService;
import com.event.cia103g1springboot.room.roomtype.model.RTVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanOrderDTOService {

    @Autowired
    private RTRepository rtRepository;


    public PlanOrderDTO convertToDTO(PlanOrder planOrder, List<RoomSelectionDTO> selectedRooms, int attendeeCount) {
        PlanOrderDTO dto = new PlanOrderDTO();

        // 基本訂單資訊設置
        dto.setPlanOrderId(planOrder.getPlanOrderId());
        dto.setMemberName(planOrder.getMemVO().getName());
        dto.setPlanName(planOrder.getPlan().getPlanType().getPlanName());
        dto.setStartDate(planOrder.getPlan().getStartDate());
        dto.setEndDate(planOrder.getPlan().getEndDate());
        dto.setOrderStat(planOrder.getOrderStat());
        dto.setPayMethod(planOrder.getPayMethod());
        dto.setOrderDate(planOrder.getOrderDate());

        // 價格相關
        dto.setPlanPrice(planOrder.getPlan().getPlanPrice());
        dto.setAttendeeCount(attendeeCount);
        Integer tripTotal = planOrder.getPlan().getPlanPrice() * attendeeCount;
        dto.setTripTotal(tripTotal);

        // 房型資訊
        List<OrderRoomDTO> roomDTOs = new ArrayList<>();
        if (selectedRooms != null && !selectedRooms.isEmpty()) {
            for (RoomSelectionDTO room : selectedRooms) {
                OrderRoomDTO roomDTO = new OrderRoomDTO();
                roomDTO.setRoomTypeName(room.getRoomTypeName());
                roomDTO.setRoomPrice(room.getRoomPrice());
                roomDTO.setQuantity(room.getQuantity());
                roomDTO.setSubtotal(room.getRoomPrice() * room.getQuantity());
                roomDTOs.add(roomDTO);
            }
        }
        dto.setRooms(roomDTOs);

        // 計算總價
        Integer roomTotal = planOrder.getRoomPrice();
        dto.setRoomTotal(roomTotal);
        dto.setTotalPrice(tripTotal + roomTotal);

        return dto;
    }


    public ROVO convertToROVO(RoomSelectionDTO roomSelectionDTO, PlanOrder planOrder) {
        ROVO roomOrder = new ROVO();

        // 1. 設置基本資訊
        roomOrder.setRoomPrice(roomSelectionDTO.getRoomPrice());
        roomOrder.setOrderQty(roomSelectionDTO.getQuantity());
        roomOrder.setPlanOrder(planOrder);

        // 2. 查詢並設置完整的房型資訊
        RTVO roomType = rtRepository.findByRoomTypeId(roomSelectionDTO.getRoomTypeId());
        if (roomType == null) {
            throw new RuntimeException("找不到房型ID: " + roomSelectionDTO.getRoomTypeId());
        }
        roomOrder.setRtVO(roomType);

        return roomOrder;
    }
    }


