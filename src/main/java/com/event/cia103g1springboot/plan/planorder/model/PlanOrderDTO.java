package com.event.cia103g1springboot.plan.planorder.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanOrderDTO {
    private Integer planOrderId;
    private String memberName;
    private String planName;
    private LocalDateTime orderDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer orderStat;
    private Integer payMethod;
    private String remAcct;
    private String cardLast4;

    // 價格相關
    private Integer planPrice;      // 單人行程價格
    private Integer attendeeCount;  // 報名人數
    private Integer tripTotal;      // 行程總價
    private Integer roomTotal;      // 房間總價
    private Integer totalPrice;     // 總價格

    // 房型資訊
    private List<OrderRoomDTO> rooms;
}


