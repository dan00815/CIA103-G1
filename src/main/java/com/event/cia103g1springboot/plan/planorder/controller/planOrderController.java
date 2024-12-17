package com.event.cia103g1springboot.plan.planorder.controller;
import com.event.cia103g1springboot.event.evtimgmodel.EvtImgService;
import com.event.cia103g1springboot.event.evtimgmodel.EvtImgVO;
import com.event.cia103g1springboot.member.notify.model.MemberNotifyService;
import com.event.cia103g1springboot.member.notify.model.MemberNotifyVO;
import com.event.cia103g1springboot.plan.planorder.model.PlanOrder;
import com.event.cia103g1springboot.plan.planorder.model.PlanOrderDTO;
import com.event.cia103g1springboot.plan.planorder.model.PlanOrderDTOService;
import com.event.cia103g1springboot.plan.planroom.model.PlanRoom;
import com.event.cia103g1springboot.room.roomorder.model.ROVO;
import com.event.cia103g1springboot.room.roomtype.model.RTVO;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.event.cia103g1springboot.plan.planorder.model.RoomSelectionDTO;
import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    @Autowired
    MemberNotifyService memberNotifyService;

    @Autowired
    EvtImgService evtImgService;

    @Autowired
    PlanOrderDTOService planOrderDTOService;




    @GetMapping("/detail/{id}")
    public String planDetail(@PathVariable Integer id, Model model) {
        Plan plan = planService.findPlanById(id);

        //拿上架、額滿
        List<EvtImgVO> allImages = evtImgService.findPublishevtImg(1, 3);
        List<EvtImgVO> randomImages;

        if (allImages != null && !allImages.isEmpty()) {
            // 如果圖片少於5張直接全拿
            if (allImages.size() <= 5) {
                randomImages = allImages;
            } else {
                // 隨機拿5張
                Collections.shuffle(allImages);
                randomImages = allImages.subList(0, 5);
            }
            model.addAttribute("evtImgVO", randomImages);
        }


        // 拿房型
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

        // 數據轉 JSON
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
            //傑克森的轉型工具
            ObjectMapper mapper = new ObjectMapper();
            //rooms物件轉json寫入
            cartData.put("rooms", mapper.writeValueAsString(request.getRooms()));

            cartData.put("totalPrice", String.valueOf(request.getTotalPrice()));

            cartData.put("attendeeCount", String.valueOf(request.getAttendeeCount()));

            cartData.put("planId", String.valueOf(request.getPlanId()));

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
            // 從 Redis 拿
            Map<Object, Object> cartData = redisTemplate.opsForHash().entries(cartKey);

            if (cartData.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            ObjectMapper mapper = new ObjectMapper();

            //用JAKSON 從JSON轉回來
            String roomsJson = (String) cartData.get("rooms");
            List<RoomSelection> rooms = mapper.readValue(roomsJson,
                    new TypeReference<List<RoomSelection>>() {});

            // 報名人數 預設1
            int attendeeCount = 1;
            if (cartData.containsKey("attendeeCount")) {
                attendeeCount = Integer.parseInt(cartData.get("attendeeCount").toString());
            }

            // 取得總價
            Object totalPrice = cartData.get("totalPrice");

            // 重設過期時間
            redisTemplate.expire(cartKey, 15, TimeUnit.MINUTES);

            // 打回前端
            return ResponseEntity.ok(Map.of(
                    "rooms", rooms,
                    "attendeeCount", attendeeCount,
                    "totalPrice", totalPrice
            ));
            //錯誤打狀態回去跟訊息
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    //清車 好像沒用到= = ???
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
    public String attend(@PathVariable Integer id, Model model, HttpSession session) {
        Plan plan = planService.findPlanById(id);
        MemVO mem = (MemVO) session.getAttribute("auth");
        String cartKey = "plan:cart:" + id;
        model.addAttribute("mem", mem);
        model.addAttribute("plan", plan);

        try {
            Map<Object, Object> cartData = redisTemplate.opsForHash().entries(cartKey);

            if (cartData.isEmpty()) {
                return "redirect:/planord/detail/" + id;
            }

            ObjectMapper mapper = new ObjectMapper();


            //用JAKSON 從JSON轉回來
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

            // 計算房間總價 過濾 但好像不用??
            int roomTotal = rooms.stream()
                    .mapToInt(room ->
                            (room.getRoomPrice() != null ? room.getRoomPrice() : 0) *
                                    (room.getQuantity() != null ? room.getQuantity() : 0))
                    .sum();

            // 計算總價
          int totalPrice = tripTotal + roomTotal;

            // 過期時間
            redisTemplate.expire(cartKey, 15, TimeUnit.MINUTES);


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



    @Transactional
    @PostMapping("/confirm/{id}")
    public String confirm(@PathVariable Integer id, @Valid PlanOrder planOrder,
                          @RequestParam("rooms") String roomsJson, Model model,HttpSession session) {
        try {

            ObjectMapper mapper = new ObjectMapper();
            List<RoomSelection> selectedRooms = mapper.readValue(roomsJson,
                    new TypeReference<List<RoomSelection>>() {});

            MemVO memVO = (MemVO) session.getAttribute("auth");
            Plan plan = planService.findPlanById(id);

            // 拿報名人數
            String cartKey = "plan:cart:" + id;
            Map<Object, Object> cartData = redisTemplate.opsForHash().entries(cartKey);

            Set<ROVO> roomOrders = selectedRooms.stream().map(room -> {
                ROVO rovo = new ROVO();
                rovo.setOrderQty(room.getQuantity());       // 訂房數量
                rovo.setRoomPrice(room.getRoomPrice());     // 房價

                // 設置關聯的 RTVO（房型）
                RTVO rtvo = new RTVO();
                rtvo.setRoomTypeId(room.getRoomTypeId());
                rtvo.setRoomTypeName(room.getRoomTypeName());
                rovo.setRtVO(rtvo);

                // 關聯 PlanOrder（稍後設置）
                rovo.setPlanOrder(planOrder);
                return rovo;
            }).collect(Collectors.toSet());


            // 報名人數，預設1
            int attendeeCount = 1;
            if (cartData.containsKey("attendeeCount") && cartData.get("attendeeCount") != null) {
                attendeeCount = Integer.parseInt(cartData.get("attendeeCount").toString());
            }
            // 更新行程報名人數
            plan.setAttEnd(plan.getAttEnd() + attendeeCount);

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
            planOrder.setRoomOrders(roomOrders);
        // 設置訂單關聯和人數
        planOrder.setPlanPrice(tripTotal);
        planOrder.setRoomPrice(totalRoomPrice);
        planOrder.setPlan(plan);
        planOrder.setMemVO(memVO);

        // 保存訂單
        PlanOrder savedOrder = planOrderService.addPlanOrder(planOrder);

            //通知=============================================
            MemberNotifyVO notification = new MemberNotifyVO();
            notification.setMember(memVO);
            notification.setNotifyType(6);  // 行程
            notification.setNotifyCon("您已報名行程：" + plan.getPlanType().getPlanName() +
                    "，行程訂單編號：" + planOrder.getPlanOrderId()+"，行程報名成功!");
            notification.setBusinessKey("Plan_ORDER_" + planOrder.getPlanOrderId());
            memberNotifyService.createNotification(notification);
            //====================================================
            // 發mail
            try {
                planOrderService.sendPlanOrdMail(savedOrder, selectedRooms);
            } catch (MessagingException e) {
                e.printStackTrace();
                model.addAttribute("error", "郵件發送失敗");
            }

            // 清車
            redisTemplate.delete(cartKey);

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

//    @Transactional
//    @PostMapping("/confirm/{id}")
//    public String confirm(@PathVariable Integer id, @Valid PlanOrder planOrder,
//                          @RequestParam("rooms") String roomsJson, Model model, HttpSession session) {
//        try {
//
//            ObjectMapper mapper = new ObjectMapper();
//            List<RoomSelectionDTO> selectedRooms = mapper.readValue(roomsJson,
//                    new TypeReference<List<RoomSelectionDTO>>() {});
//
//            MemVO memVO = (MemVO) session.getAttribute("auth");
//            Plan plan = planService.findPlanById(id);
//
//            // 拿報名人數
//            String cartKey = "plan:cart:" + id;
//            Map<Object, Object> cartData = redisTemplate.opsForHash().entries(cartKey);
//
//            // 報名人數，預設1
//            int attendeeCount = 1;
//            if (cartData.containsKey("attendeeCount") && cartData.get("attendeeCount") != null) {
//                attendeeCount = Integer.parseInt(cartData.get("attendeeCount").toString());
//            }
//            // 更新行程報名人數
//            plan.setAttEnd(plan.getAttEnd() + attendeeCount);
//
//            // 處理付款
//            if (planOrder.getPayMethod() == 0) {
//                planOrder.setRemAcct(null);
//            } else if (planOrder.getPayMethod() == 1) {
//                planOrder.setCardLast4(null);
//            }
//
//            // 計算房間總價
//            int totalRoomPrice = selectedRooms.stream()
//                    .mapToInt(room -> room.getRoomPrice() * room.getQuantity())
//                    .sum();
//
//            planOrder.setRoomPrice(totalRoomPrice);
//
//            // 計算行程總價（人數 × 單價）
//            int tripTotal = plan.getPlanPrice() * attendeeCount;
//
//            // 計算總價（行程總價 + 房間總價）
//            int totalPrice = tripTotal + totalRoomPrice;
//
//            // 更新每個房型的庫存
//            for (RoomSelectionDTO roomSelection : selectedRooms) {
//                PlanRoom planRoom = planRoomService.findByRmTypeIdAndPlanId(
//                        roomSelection.getRoomTypeId(), plan.getPlanId());
//
//                if (planRoom.getRoomQty() < roomSelection.getQuantity()) {
//                    throw new RuntimeException("房間數量不足");
//                }
//                planRoom.setRoomQty(planRoom.getRoomQty() - roomSelection.getQuantity());
//                planRoom.setReservedRoom(planRoom.getReservedRoom() + roomSelection.getQuantity());
//                planRoomService.save(planRoom);
//            }
//            // 設置訂單關聯和人數
//            planOrder.setPlanPrice(tripTotal);
//            planOrder.setRoomPrice(totalRoomPrice);
//            planOrder.setPlan(plan);
//            planOrder.setMemVO(memVO);
//
//            // 保存訂單
//            PlanOrder savedOrder = planOrderService.addPlanOrder(planOrder,selectedRooms);
//
//            // 轉換為 DTO
//            PlanOrderDTO orderDTO = planOrderDTOService.convertToDTO(savedOrder,selectedRooms, attendeeCount);
//            model.addAttribute("orderDTO", orderDTO);            // ... 通知和郵件邏輯 ...
//            //通知=============================================
//            MemberNotifyVO notification = new MemberNotifyVO();
//            notification.setMember(memVO);
//            notification.setNotifyType(6);  // 行程
//            notification.setNotifyCon("您已報名行程：" + plan.getPlanType().getPlanName() +
//                    "，行程訂單編號：" + planOrder.getPlanOrderId()+"，行程報名成功!");
//            notification.setBusinessKey("Plan_ORDER_" + planOrder.getPlanOrderId());
//            memberNotifyService.createNotification(notification);
//            //====================================================
//            // 發mail
////            try {
////                planOrderService.sendPlanOrdMail(savedOrder,selectedRooms);
////            } catch (MessagingException e) {
////                e.printStackTrace();
////                model.addAttribute("error", "郵件發送失敗");
////            }
//
//            // 清車
//            redisTemplate.delete(cartKey);
//
//            model.addAttribute("orderDTO", orderDTO);
//            return "plan/planfront/attendsucess";
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            model.addAttribute("error", "訂單提交失敗：" + e.getMessage());
//            return "redirect:/planord/detail/" + id;
//        }
//        }




    @GetMapping("/planorddetail")
    @Transactional
    public String frontPlanOrderDetail(Model model, HttpSession session) {
        MemVO memVO = (MemVO) session.getAttribute("auth");
        Set<PlanOrder> planOrders = memVO.getPlanOrder();

        List<PlanOrder> activeOrders = new ArrayList<>();
        List<PlanOrder> historyOrders = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (PlanOrder order : planOrders) {
            //狀態沒寫很細只能用時間判斷= =
            if (order.getPlan().getStartDate().isAfter(now)){
                activeOrders.add(order);
            } else {
                historyOrders.add(order);
            }
        }
        model.addAttribute("activeOrders", activeOrders);
        model.addAttribute("historyOrders", historyOrders);

        return "plan/planorder/planorddetail";
    }
   // 因該跟上面那個大同小異
    @GetMapping("/memorder/{memId}")
    public String viewOrdersByMember(@PathVariable Long memId, Model model, HttpSession session) {
        // 從 Session 中取得會員資訊
        MemVO memVO = (MemVO) session.getAttribute("auth");

        // 檢查會員登入狀態
        if (memVO == null) {
            return "redirect:/login"; // 如果未登入，跳轉至登入頁面
        }


        // 查詢會員的所有行程訂單
        List<PlanOrder> planOrders = planOrderService.findPlanOrdersByMemId(memVO.getMemId());
        if (planOrders == null || planOrders.isEmpty()) {
            model.addAttribute("errorMessage", "目前沒有任何行程訂單！");
            model.addAttribute("mem", memVO);
            return "plan/planorder/planmemlist"; // 返回無訂單頁面
        }

        // 將資料傳遞至頁面
        model.addAttribute("name",memVO.getName());
        model.addAttribute("mem", memVO);
        model.addAttribute("orders", planOrders);

        return "plan/planorder/planmemlist"; // 返回訂單列表頁面
    }

    //會員行程訂單明細
    @GetMapping("/memview/{id}")
    public String memviewOrder(@PathVariable Integer id, Model model, HttpSession session,
                               @RequestParam(required = false) Integer index) {
        // 從 Session 中取得會員資訊
        MemVO memVO = (MemVO) session.getAttribute("auth");

        // 檢查會員登入狀態
        if (memVO == null) {
            return "redirect:/login"; // 如果未登入，跳轉至登入頁面
        }

        // 查詢訂單
        PlanOrder order = planOrderService.findByMemIdAndPlanOrderId(memVO.getMemId(), id);


        // 確保訂單屬於當前會員
        if (!order.getMemVO().getMemId().equals(memVO.getMemId())) {
            return "redirect:/planord/memorder/" + memVO.getMemId(); // 防止訪問其他會員的訂單
        }
        model.addAttribute("index",index);
        model.addAttribute("name",memVO.getName());
        model.addAttribute("order", order);
        return "plan/planorder/memplanview";
    }

//  會員行程明細  ( 三個按鈕 )
@GetMapping("/planord/filter")
public String filterOrders(@RequestParam("type") String type, Model model, HttpSession session) {
    MemVO memVO = (MemVO) session.getAttribute("auth");
    List<PlanOrder> orders;

    // 檢查會員登入狀態
    if (memVO == null) {
        return "redirect:/login"; // 如果未登入，跳轉至登入頁面
    }

    switch (type) {
        case "active": // 進行中
            orders = planOrderService.findActiveOrdersByMember(memVO.getMemId());
            break;
        case "history": // 歷史
            orders = planOrderService.findHistoryOrdersByMember(memVO.getMemId());
            break;
        default: // 所有
            orders = planOrderService.findPlanOrdersByMemId(memVO.getMemId());

    }


    model.addAttribute("name",memVO.getName());
    model.addAttribute("orders", orders);
    model.addAttribute("currentFilter", type);
    return "plan/planorder/planmemlist"; // 返回 Thymeleaf 頁面
}



    //    後台------------------------------------------------------
    //列所有訂單
    @GetMapping("/listall")
    public String listAll(Model model) {
        List<PlanOrder> orders = planOrderService.findAllPlanOrders();
        model.addAttribute("list", orders);
        return "plan/planorder/planordlist";
    }

    //行程訂單明細
    @GetMapping("/view/{id}")
    public String viewOrder(@PathVariable Integer id, Model model) {
        PlanOrder order = planOrderService.findPlanOrderById(id);
        Plan plan = order.getPlan();
        Set<PlanRoom> planRooms = plan.getPlanRoom(); // 假設返回類型是 Set<PlanRoom>

        // 將訂單和房型資訊添加到模型中
        model.addAttribute("order", order);
        model.addAttribute("planRooms", planRooms);

        return "plan/planorder/view";
    }

    //取消訂單
    @Transactional
    @PostMapping("/cancel/{id}")
    public String cancel(@PathVariable Integer id, Model model) throws MessagingException {
        PlanOrder order = planOrderService.findPlanOrderById(id);
        if (order != null) {
            order.setOrderStat(4);  // 設置狀態為取消
            planOrderService.addPlanOrder(order);  // 保存更改
            planOrderService.sendCancelPlanOrdMail(order, 4);  // 調用 service 的郵件發送方法
        }

        return "redirect:/planord/listall";
    }


    //    DTO 之後再丟出去------------------------------------------------------
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



