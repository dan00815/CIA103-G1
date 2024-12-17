package com.event.cia103g1springboot.plan.planorder.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomSelectionDTO {
    private Integer roomTypeId;  // 必須有這個欄位
    private String roomTypeName;
    private Integer roomPrice;
    private Integer quantity;
}

