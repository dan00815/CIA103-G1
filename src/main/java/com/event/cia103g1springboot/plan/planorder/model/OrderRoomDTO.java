package com.event.cia103g1springboot.plan.planorder.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRoomDTO {
    private String roomTypeName;   // 房型名稱
    private Integer roomPrice;     // 房型單價
    private Integer quantity;      // 數量
    private Integer subtotal;      // 小計（單價 × 數量）
}
