package com.event.cia103g1springboot.plan.planorder.controller;
import com.event.cia103g1springboot.plan.planorder.model.PlanOrder;
import com.event.cia103g1springboot.plan.planroom.model.PlanRoom;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.event.cia103g1springboot.member.mem.model.MemService;
import com.event.cia103g1springboot.member.mem.model.MemVO;
import com.event.cia103g1springboot.plan.plan.model.Plan;
import com.event.cia103g1springboot.plan.plan.model.PlanService;
import com.event.cia103g1springboot.plan.planorder.model.PlanOrderService;
import com.event.cia103g1springboot.plan.planroom.model.PlanRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequestMapping("/planord")
@Controller
public class planOrderController {

    @Autowired
    PlanOrderService planOrderService;

    @Autowired
    PlanService planService;

    @Autowired
    PlanRoomService planRoomService;

    @Autowired
    MemService memService;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @GetMapping("/detail/{id}")
    public String planDetail(@PathVariable Integer id, Model model) {
        Plan plan = planService.findPlanById(id);

        if (plan == null) {
            throw new RuntimeException("Plan not found with ID: " + id); // 防止空值問題
        }

        // 為前端準備房型數據
        List<Map<String, Object>> roomDataList = new ArrayList<>();
        for (PlanRoom planRoom : plan.getPlanRoom()) {
            Map<String, Object> roomData = new HashMap<>();
            roomData.put("roomTypeId", planRoom.getRoomTypeId());
            roomData.put("roomTypeName", planRoom.getRtvo().getRoomTypeName());
            roomData.put("roomPrice", planRoom.getRtvo().getRoomPrice());
            roomData.put("roomQty", planRoom.getRoomQty());
            roomData.put("description", planRoom.getRtvo().getRoomTypeDesc());
            roomData.put("occupancy", planRoom.getRtvo().getOccupancy());
            roomDataList.add(roomData);
        }

        // 將數據轉換為 JSON 字符串
        try {
            ObjectMapper mapper = new ObjectMapper();
            String roomDataJson = mapper.writeValueAsString(roomDataList);
            model.addAttribute("roomDataJson", roomDataJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        model.addAttribute("plan", plan);
        return "plan/planfront/detail";
    }

    @PostMapping("/api/setRooms")
    @ResponseBody
    public ResponseEntity<?> setRooms(@RequestBody RoomSelectionRequest request) {
        try {
            String cartKey = "plan:cart:" + request.getPlanId();

            Map<String, String> cartData = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();

            // 存入房間資訊
            cartData.put("rooms", mapper.writeValueAsString(request.getRooms()));

            // 存入價格資訊
            cartData.put("totalPrice", String.valueOf(request.getTotalPrice()));

            // 存入人數資訊
            cartData.put("attendeeCount", String.valueOf(request.getAttendeeCount()));

            // 存入行程ID
            cartData.put("planId", String.valueOf(request.getPlanId()));

            // 寫入 Redis 並設置過期時間
            redisTemplate.opsForHash().putAll(cartKey, cartData);
            redisTemplate.expire(cartKey, 30, TimeUnit.MINUTES);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @ResponseBody
    @GetMapping("/cart/{planId}")
    public ResponseEntity<?> getCart(@PathVariable Integer planId) {
        System.out.println("收到請求，planId: " + planId);
        String cartKey = "plan:cart:" + planId;

        try {
            // 從 Redis 獲取購物車資料
            Map<Object, Object> cartData = redisTemplate.opsForHash().entries(cartKey);

            if (cartData.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            ObjectMapper mapper = new ObjectMapper();

            // 解析房間資料
            String roomsJson = (String) cartData.get("rooms");
            List<RoomSelection> rooms = mapper.readValue(roomsJson,
                    new TypeReference<List<RoomSelection>>() {});

            // 解析人數資料（如果沒有則預設為1）
            int attendeeCount = 1;
            if (cartData.containsKey("attendeeCount")) {
                attendeeCount = Integer.parseInt(cartData.get("attendeeCount").toString());
            }

            // 取得總價
            Object totalPrice = cartData.get("totalPrice");

            // 重設過期時間
            redisTemplate.expire(cartKey, 15, TimeUnit.MINUTES);

            // 返回所有資料
            return ResponseEntity.ok(Map.of(
                    "rooms", rooms,
                    "attendeeCount", attendeeCount,
                    "totalPrice", totalPrice
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/cart/{planId}")
    public ResponseEntity<?> clearCart(@PathVariable Integer planId) {
        String cartKey = "plan:cart:" + planId;

        try {
            redisTemplate.delete(cartKey);
            return ResponseEntity.ok(Map.of("message", "購物車已清空"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }



    @GetMapping("/attend/{id}")
    public String attend(@PathVariable Integer id, Model model) {
        Plan plan = planService.findPlanById(id);
        MemVO mem = memService.findOneMem("benson000");
        String cartKey = "plan:cart:" + id;
        model.addAttribute("mem", mem);
        model.addAttribute("plan", plan);

        try {
            Map<Object, Object> cartData = redisTemplate.opsForHash().entries(cartKey);

            if (cartData.isEmpty()) {
                return "redirect:/planord/detail/" + id;
            }

            ObjectMapper mapper = new ObjectMapper();
//            mapper.configure(DeserializationFeature.USE_LONG_FOR_INTS, true);

            // 解析房間數據
            List<RoomSelection> rooms = mapper.readValue(
                    (String) cartData.get("rooms"),
                    new TypeReference<List<RoomSelection>>() {}
            );

            // 取得報名人數，預設為1
            int attendeeCount = 1;
            if (cartData.get("attendeeCount") != null) {
                attendeeCount = Integer.parseInt(cartData.get("attendeeCount").toString());
            }

            // 計算行程費用（人數 × 單價）
            int tripTotal =  plan.getPlanPrice() * attendeeCount;

            // 計算房間總價
            int roomTotal = rooms.stream()
                    .mapToInt(room ->
                            (room.getRoomPrice() != null ? room.getRoomPrice() : 0) *
                                    (room.getQuantity() != null ? room.getQuantity() : 0))
                    .sum();

            // 計算總價（行程費用 + 房間費用）
          int totalPrice = tripTotal + roomTotal;

            // 更新 Redis 過期時間
            redisTemplate.expire(cartKey, 15, TimeUnit.MINUTES);

            // 添加所有需要的數據到模型
            model.addAttribute("selectedRooms", rooms);
            model.addAttribute("attendeeCount", attendeeCount);
            model.addAttribute("tripTotal", tripTotal);
            model.addAttribute("roomTotal", roomTotal);
            model.addAttribute("totalPrice", totalPrice);

            return "plan/planfront/attendpage";
        } catch (Exception e) {
            System.out.println("Exception caught: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/planord/detail/" + id;
        }
    }




    @PostMapping("/confirm/{id}")
    public String confirm(@PathVariable Integer id, @Valid PlanOrder planOrder,
                          @RequestParam("rooms") String roomsJson, Model model) {
        try {
            System.out.println("接收到的 roomsJson: " + roomsJson);

            ObjectMapper mapper = new ObjectMapper();
            List<RoomSelection> selectedRooms = mapper.readValue(roomsJson,
                    new TypeReference<List<RoomSelection>>() {});


            // 拿會員跟行程先寫死
            MemVO memVO = memService.findOneMem("benson000");
            Plan plan = planService.findPlanById(id);

            // 從 Redis 獲取報名人數
            String cartKey = "plan:cart:" + id;
            Map<Object, Object> cartData = redisTemplate.opsForHash().entries(cartKey);

            // 取得報名人數，預設為1
            int attendeeCount = 1;
            if (cartData.containsKey("attendeeCount") && cartData.get("attendeeCount") != null) {
                attendeeCount = Integer.parseInt(cartData.get("attendeeCount").toString());
            }
            // 更新行程報名人數
            plan.setAttEnd(plan.getAttEnd() + attendeeCount);
            //改錢
//            plan.setPlanPrice(plan.getPlanPrice() * attendeeCount);

//            System.out.println("價格:"+plan.getPlanPrice() * attendeeCount);
            // 處理付款
            if (planOrder.getPayMethod() == 0) {
                planOrder.setRemAcct(null);
            } else if (planOrder.getPayMethod() == 1) {
                planOrder.setCardLast4(null);
            }

            // 計算房間總價
            int totalRoomPrice = selectedRooms.stream()
                    .mapToInt(room -> room.getRoomPrice() * room.getQuantity())
                    .sum();

            planOrder.setRoomPrice(totalRoomPrice);
//            planOrder.setTotalPrice(plan.getPlanPrice() * attendeeCount+totalRoomPrice);
            // 計算行程總價（人數 × 單價）
            int tripTotal = plan.getPlanPrice() * attendeeCount;

            // 計算總價（行程總價 + 房間總價）
            int totalPrice = tripTotal + totalRoomPrice;

            // 更新每個房型的庫存
            for (RoomSelection roomSelection : selectedRooms) {
                PlanRoom planRoom = planRoomService.findByRmTypeIdAndPlanId(
                        roomSelection.getRoomTypeId(), plan.getPlanId());

                if (planRoom.getRoomQty() < roomSelection.getQuantity()) {
                    throw new RuntimeException("房間數量不足");
                }
                planRoom.setRoomQty(planRoom.getRoomQty() - roomSelection.getQuantity());
                planRoom.setReservedRoom(planRoom.getReservedRoom() + roomSelection.getQuantity());
                planRoomService.save(planRoom);
            }
            // 設置訂單關聯和人數
            planOrder.setPlanPrice(tripTotal);
            planOrder.setRoomPrice(totalRoomPrice);
            planOrder.setPlan(plan);
            planOrder.setMemVO(memVO);

            // 保存訂單
            PlanOrder savedOrder = planOrderService.addPlanOrder(planOrder);

            // 發mail
            try {
                planOrderService.sendPlanOrdMail(savedOrder, selectedRooms);
            } catch (MessagingException e) {
                e.printStackTrace();
                model.addAttribute("error", "郵件發送失敗");
            }

            // 清除 Redis 購物車數據
            redisTemplate.delete(cartKey);

            // 添加所有需要的資料到 model

            model.addAttribute("totalprice", totalPrice);
            model.addAttribute("tripTotal", tripTotal);
            model.addAttribute("roomTotal", totalRoomPrice);
            model.addAttribute("attendeeCount", attendeeCount);
            model.addAttribute("planord", savedOrder);
            model.addAttribute("plan", plan);
            model.addAttribute("mem", memVO);
            model.addAttribute("selectedRooms", selectedRooms);

            return "plan/planfront/attendsucess";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "訂單提交失敗：" + e.getMessage());
            return "redirect:/planord/detail/" + id;
        }
    }

    //    後端------------------------------------------------------
    @GetMapping("/listall")
    public String listAll(Model model) {
        List<PlanOrder> orders = planOrderService.findAllPlanOrders();
        model.addAttribute("list", orders);
        return "plan/planorder/planordlist";
    }


    @GetMapping("/view/{id}")
    public String viewOrder(@PathVariable Integer id, Model model) {
        PlanOrder order = planOrderService.findPlanOrderById(id);
        if (order == null) {
            return "redirect:/planorder/listall";
        }
        model.addAttribute("order", order);
        return "plan/planorder/view";
    }



    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class RoomSelectionRequest {
        private Integer planId;
        private List<RoomSelection> rooms;
        private Integer totalPrice;
        private Integer attendeeCount;
    }
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class RoomSelection {
        private Long id;
        private Integer roomTypeId;
        private String roomTypeName;
        private Integer roomPrice;
        private Integer quantity;
        private Integer maxQuantity;
    }

}



