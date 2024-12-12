package com.event.cia103g1springboot.plan.planorder.model;

import com.event.cia103g1springboot.plan.plan.model.PlanService;

import com.event.cia103g1springboot.plan.planorder.controller.planOrderController;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PlanOrderService {
    @Autowired
    private PlanOrderRepository planOrderRepository;

    @Autowired
    PlanService planService;
    @Autowired
    JavaMailSender mailSender;
    @Autowired
    TemplateEngine templateEngine;

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
        helper.setTo("mm950490@gmail.com");
        helper.setSubject("鄰星嗨嗨:行程訂單成立通知");
        helper.setText(mailContent, true);

        ClassPathResource footer = new ClassPathResource("static/email/planemail.png");
        helper.addInline("footer", footer);

        mailSender.send(message);
    }
}

