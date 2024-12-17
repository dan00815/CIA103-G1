package com.event.cia103g1springboot.plan.planorder.model;

import com.event.cia103g1springboot.plan.plan.model.PlanService;

import com.event.cia103g1springboot.plan.planorder.controller.planOrderController;
import com.event.cia103g1springboot.room.roomorder.model.RORepository;
import com.event.cia103g1springboot.room.roomorder.model.ROService;
import com.event.cia103g1springboot.room.roomorder.model.ROVO;
import com.event.cia103g1springboot.room.roomtype.model.RTRepository;
import com.event.cia103g1springboot.room.roomtype.model.RTService;
import com.event.cia103g1springboot.room.roomtype.model.RTVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PlanOrderService {
    @Autowired
    PlanService planService;
    @Autowired
    JavaMailSender mailSender;
    @Autowired
    TemplateEngine templateEngine;

    private final PlanOrderDTOService dtoService;
    private final PlanOrderRepository planOrderRepository;
    private final RORepository roRepository;
    private final RTRepository rtRepository;

    @Autowired
    public PlanOrderService(PlanOrderDTOService dtoService,
                            PlanOrderRepository planOrderRepository,
                            RORepository roRepository,
                            RTRepository rtRepository) {
        this.dtoService = dtoService;
        this.planOrderRepository = planOrderRepository;
        this.roRepository = roRepository;
        this.rtRepository = rtRepository;
    }

        @Transactional
        public PlanOrder addPlanOrder(PlanOrder planOrder) {
            return planOrderRepository.save(planOrder);  // 使用小寫的變量名
        }
        public List<PlanOrder> findAllPlanOrders() { return planOrderRepository.findAll(); }

        public PlanOrder findPlanOrderById(Integer id) {
            Optional<PlanOrder> planOrder = planOrderRepository.findById(id);
            return planOrder.orElse(new PlanOrder());
        };
        
        public PlanOrder getOnePlanOrder(Integer planOrderId) {
    		Optional<PlanOrder> optional = planOrderRepository.findById(planOrderId);
    		return optional.orElse(null);
    	}

        public PlanOrder cancelord(Integer planOrderId, Integer orderStatus){
            PlanOrder planOrder =planOrderRepository.findByPlanOrderIdAndOrderStat(planOrderId,orderStatus);
            planOrder.setOrderStat(orderStatus);
            return  planOrderRepository.save(planOrder);
        }

    public List<PlanOrder> findPlanOrdersByMemId(Integer memId) {
        return planOrderRepository.findByMemVO_MemId(memId);
    }
    // 進行中
    public List<PlanOrder> findActiveOrdersByMember(Integer memberId) {
        LocalDate now = LocalDate.now();
        return planOrderRepository.findActiveOrdersByMember(memberId, now);
    }
    // 歷史
    public List<PlanOrder> findHistoryOrdersByMember(Integer memberId) {
        LocalDate now = LocalDate.now();
        return planOrderRepository.findByMemVO_MemIdAndPlan_EndDateBefore(memberId, now);
    }


    public void sendCancelPlanOrdMail(PlanOrder order,Integer Status) throws MessagingException {
        Context context = new Context();
        context.setVariable("memberName", order.getMemVO().getName());
        context.setVariable("planName", order.getPlan().getPlanType().getPlanName());
        context.setVariable("orderDate", order.getOrderDate());
        context.setVariable("cancelDate", LocalDateTime.now());
        context.setVariable("orderStatus", order.getOrderStat());
        context.setVariable("totalAmount", order.getTotalPrice());
        String mailContent = templateEngine.process("plan/planfront/plancancelemail", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
        //別改
        helper.setTo(order.getMemVO().getEmail());
        helper.setSubject("鄰星嗨嗨:行程訂單取消通知");
        helper.setText(mailContent, true);

        mailSender.send(message);
    }

    public void sendPlanOrdMail(PlanOrder order, List<planOrderController.RoomSelection> rooms) throws MessagingException {
        Integer totalRoomPrice = rooms.stream()
                .mapToInt(room -> room.getRoomPrice() * room.getQuantity())
                .sum();

        Integer totalPrice = order.getPlanPrice() + totalRoomPrice;

        Context context = new Context();
        context.setVariable("memberName", order.getMemVO().getName());
        context.setVariable("planName", order.getPlan().getPlanType().getPlanName());
        context.setVariable("rooms", rooms);
        context.setVariable("tripTotal",order.getPlanPrice());  //
        context.setVariable("payMethod", order.getPayMethod());
        context.setVariable("PlanOrderId", order.getPlanOrderId());
        context.setVariable("totalPrice", totalPrice);
        context.setVariable("orderdate", order.getOrderDate());
        context.setVariable("startDate", order.getPlan().getStartDate());
        context.setVariable("endDate", order.getPlan().getEndDate());
        context.setVariable("totalRoomPrice", totalRoomPrice);

        String mailContent = templateEngine.process("plan/planfront/planemail", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(order.getMemVO().getEmail());
        helper.setSubject("鄰星嗨嗨:行程訂單成立通知");
        helper.setText(mailContent, true);

        ClassPathResource footer = new ClassPathResource("static/email/planemail.png");
        helper.addInline("footer", footer);

        mailSender.send(message);
    }

    public PlanOrder findByMemIdAndPlanOrderId(Integer memId, Integer planOrderId) {
        return planOrderRepository.findByMemVO_MemIdAndPlanOrderId(memId, planOrderId);
    }
    @Transactional(rollbackFor = Exception.class)
    public PlanOrder addPlanOrder(PlanOrder planOrder, List<RoomSelectionDTO> selectedRooms) {
        try {
            // 1. 儲存訂單
            PlanOrder savedOrder = planOrderRepository.save(planOrder);

            // 2. 處理房型訂單
            if (selectedRooms != null && !selectedRooms.isEmpty()) {
                for (RoomSelectionDTO roomSelections : selectedRooms) {
                    try {
                        // 使用 DTOService 轉換和儲存房型訂單
                        ROVO roomOrder = dtoService.convertToROVO(roomSelections, savedOrder);
                        roRepository.save(roomOrder);
                    } catch (Exception e) {
                        throw new RuntimeException("處理房型訂單時發生錯誤: " + e.getMessage());
                    }
                }
            }
            return savedOrder;
        } catch (Exception e) {
            // 記錄詳細錯誤
            e.printStackTrace();
            throw new RuntimeException("新增訂單失敗: " + e.getMessage());
        }
    }


    public ROVO convertToROVO(RoomSelectionDTO roomSelection, PlanOrder planOrder) {
        ROVO roomOrder = new ROVO();
        roomOrder.setRoomPrice(roomSelection.getRoomPrice());
        roomOrder.setOrderQty(roomSelection.getQuantity());
        roomOrder.setPlanOrder(planOrder);

        RTVO roomType = rtRepository.findByRoomTypeId(roomSelection.getRoomTypeId());
        if (roomType == null) {
            throw new RuntimeException("找不到房型ID: " + roomSelection.getRoomTypeId());
        }
        roomOrder.setRtVO(roomType);

        return roomOrder;
    }
}







//    public PlanOrderDTO findPlanOrderDTOById(Integer id) {
//        PlanOrder planOrder = planOrderRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//
//        // 轉換為 DTO
//        PlanOrderDTO dto = new PlanOrderDTO();
//        dto.setPlanOrderId(planOrder.getPlanOrderId());
//        dto.setPlanPrice(planOrder.getPlanPrice());
//        dto.setRoomPrice(planOrder.getRoomPrice());
//        dto.setTotalPrice(planOrder.getTotalPrice());
//        dto.setOrderDate(planOrder.getOrderDate());
//        dto.setOrderStat(planOrder.getOrderStat());
//        dto.setPayMethod(planOrder.getPayMethod());
//        dto.setRemAcct(planOrder.getRemAcct());
//        dto.setCardLast4(planOrder.getCardLast4());
//
//        // 設置會員名稱
//        if (planOrder.getMemVO() != null) {
//            dto.setMemberName(planOrder.getMemVO().getName());
//        }
//
//        // 設置行程名稱
//        if (planOrder.getPlan() != null) {
//            dto.setPlanName(planOrder.getPlan().getPlanType().getPlanName());
//        }
//
//        // 轉換房型資訊
//        List<OrderRoomDTO> roomDTOs = new ArrayList<>();
//        if (planOrder.getRoomOrders() != null) {
//            for (ROVO rovo : planOrder.getRoomOrders()) {
//                OrderRoomDTO roomDTO = new OrderRoomDTO();
//                roomDTO.setRoomTypeName(rovo.getRtVO().getRoomTypeName());
//                roomDTO.setRoomPrice(rovo.getRoomPrice());
//                roomDTOs.add(roomDTO);
//            }
//        }
//        dto.setRooms(roomDTOs);
//
//        return dto;
//    }



