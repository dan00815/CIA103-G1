package com.event.cia103g1springboot.event.evtcontroller;
import com.event.cia103g1springboot.event.evtmodel.EvtService;
import com.event.cia103g1springboot.event.evtmodel.EvtVO;
import com.event.cia103g1springboot.event.evtordermodel.EvtOrderService;
import com.event.cia103g1springboot.event.evtordermodel.EvtOrderVO;
import com.event.cia103g1springboot.member.mem.model.MemService;
import com.event.cia103g1springboot.member.mem.model.MemVO;
import com.event.cia103g1springboot.member.notify.model.MemberNotifyService;
import com.event.cia103g1springboot.member.notify.model.MemberNotifyVO;
import com.event.cia103g1springboot.plan.planorder.model.PlanOrder;
import com.event.cia103g1springboot.plan.planorder.model.PlanOrderRepository;
import com.event.cia103g1springboot.plan.planorder.model.PlanOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Controller
public class EvtOrderController {

    @Autowired
    EvtOrderService evtOrderService;
    @Autowired
    EvtService evtService;
    
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MemService memService;

    @Autowired
    private MemberNotifyService memberNotifyService;
    


    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private PlanOrderRepository planOrderRepository;
    @Autowired
    private PlanOrderService planOrderService;

    @GetMapping("/planevt/{planOrderId}/attend/{id}")
    public String attend(@PathVariable Integer planOrderId,
                         @PathVariable Integer id,
                         Model model,
                         HttpSession session) {

        EvtVO event = evtService.getOneEvt(id);
        MemVO memVO = (MemVO) session.getAttribute("auth");
        PlanOrder planOrder = planOrderService.findPlanOrderById(planOrderId);

        if (event == null || memVO == null || planOrder == null) {
            return "redirect:/error";
        }

        String captchaKey = "captcha:" + event.getEvtId();

        model.addAttribute("event", event);
        model.addAttribute("memVO", memVO);
        model.addAttribute("planOrder", planOrder);
        model.addAttribute("captchaKey", captchaKey);

        return "front-end/evtord/attendpage";
    }


    //拿所有活動訂單明細並分頁
    @GetMapping("/ordlistall")
    public String orderlistall(@RequestParam(defaultValue = "0") Integer page, Model model,Integer status) {
        if(status!=null){
            Page<EvtOrderVO>evtOrderVOStatus = evtOrderService.findAllByEvtOrderStat(status,page);
            model.addAttribute("ord", evtOrderVOStatus);
        }else {
        Page<EvtOrderVO> evtOrderPage = evtOrderService.getAllEvtorders(page);
        model.addAttribute("ord", evtOrderPage);}
        return "/back-end/evtord/orderlist";
    }

    @Transactional
    @PostMapping("/confirm/{id}")
    public String confirm(@PathVariable Integer id, EvtOrderVO evtOrderVO, Model model, HttpSession session) {

        PlanOrder planOrder = planOrderService.findPlanOrderById(1);
        evtOrderVO.setPlanOrder(planOrder);

        MemVO memVO = (MemVO) session.getAttribute("auth");
        evtOrderVO.setMemVO(memVO);

        // 處理報名
        evtService.attend(id, evtOrderVO);
        EvtVO event = evtService.getOneEvt(id);

        // 創通知
        MemberNotifyVO notification = new MemberNotifyVO();
        notification.setMember(memVO);
        notification.setNotifyType(2);  // 2活動
        notification.setNotifyCon("您已報名活動：" + event.getEvtName() +
                "，訂單編號：" + evtOrderVO.getEvtOrderId()+"，請等待系統審核訂單");
        notification.setBusinessKey("EVENT_ORDER_" + evtOrderVO.getEvtOrderId());

        // 存
        memberNotifyService.createNotification(notification);


        model.addAttribute("memVO", memVO);
        model.addAttribute("event", event);
        model.addAttribute("order", evtOrderVO);
        model.addAttribute("planOrder", planOrder);

        return "front-end/evtord/attendsucess";
    }

    //活動明細 有會員資訊 活動資訊 報名時間、備註....然後審核可以寄MAIL通知bla~~
    @GetMapping("/orderdetail")
    public String orderdetail(@PathVariable Integer id, Model model) {
        EvtOrderVO evtord = evtOrderService.getOneEvt(id);
        EvtVO evt = evtService.getOneEvt(evtord.getEvtVO().getEvtId());
        model.addAttribute("order", evtord);
        model.addAttribute("evt", evt);
        //之後拿會員~行程~~
        return "back-end/evtord/orderdetail";
    }

    @Transactional
    @GetMapping("/frontOrderDetail")
    public String frontOrderDetail(Model model, HttpSession session) {
        MemVO memVO = (MemVO) session.getAttribute("auth");
        Set<EvtOrderVO> evtords = memVO.getEvtOrders();

        // 歷史訂單、進行中
        List<EvtOrderVO> activeOrders = new ArrayList<>();
        List<EvtOrderVO> historyOrders = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        for (EvtOrderVO order : evtords) {
            // 判斷訂單狀態
//          依據活動日期判斷
            if (order.getEvtDate().isAfter(now) ) {
                activeOrders.add(order);
            } else {
                historyOrders.add(order);
            }
        }
        model.addAttribute("activeOrders", activeOrders);
        model.addAttribute("historyOrders", historyOrders);

        return "front-end/evtord/evtorddetail";
    }

    @Transactional
    @GetMapping("/confirmord")
    public String confirmord(@RequestParam Integer id, @RequestParam Integer status,Model model) throws MessagingException {
            evtOrderService.updateEvtStatus(id, status);
            EvtOrderVO order = evtOrderService.getOneEvt(id);
            EvtVO evt = order.getEvtVO();
            MemVO memVO = order.getMemVO();
            MemberNotifyVO notification = new MemberNotifyVO();

            if (status == 2) { // 取消訂單
            int updatedAttendance = order.getEvtVO().getEvtAttend() - order.getEvtAttend();
            evt.setEvtAttend(updatedAttendance);
            evtService.addEvt(evt);
                notification.setMember(memVO);
                notification.setNotifyType(2);  // 活動
                notification.setNotifyCon("親愛的"+" "+memVO.getName()+" "+"會員您好:"+"\n"+"很抱歉，您的活動報名失敗："+"活動名稱:" + order.getEvtName() +" "+
                        "，活動訂單編號：" + order.getEvtOrderId()+"，有任何問題歡迎致電或來信詢問");
                notification.setBusinessKey("Evt_ORDER_" + order.getEvtOrderId());
                memberNotifyService.createNotification(notification);
            }else {
                notification.setMember(memVO);
                notification.setNotifyType(2);  // 活動
                notification.setNotifyCon("親愛的"+" "+memVO.getName()+" "+"會員您好:"+"\n"+"您的活動報名成功，" +"活動名稱:" +order.getEvtName() +" "+
                        "，活動訂單編號：" + order.getEvtOrderId()+"，活動訂單成立通知");
                notification.setBusinessKey("Evt_ORDER_" + order.getEvtOrderId());
                memberNotifyService.createNotification(notification);
            }


            //thymeleaf上下文 ----->你信件裡面想要放的資料 才能從thymeleaf取出
            Context context = new Context();
            context.setVariable("memberName", order.getMemVO().getName());
            context.setVariable("memberID", order.getMemVO().getMemId());
            context.setVariable("orderId", order.getEvtOrderId());
            context.setVariable("eventName", order.getEvtName());
            context.setVariable("evtAttendDate", order.getEvtAttendDate());
            context.setVariable("status", status);

            //判斷訂單狀態 --->訂單狀態是1的話傳"back-end/email"這個thymeleaf 不為1傳"back-end/emailfail"
            String templatePath = (status == 1) ? "back-end/evtemail/email" : "back-end/evtemail/emailfail";
            //經過上面判斷後設定要去抓哪個網頁--->templatePath;信件內容:context
            String mailContent = templateEngine.process(templatePath, context);


            MimeMessage message = mailSender.createMimeMessage();
            //第二個參數 "multipart=true"表示可以內嵌圖片或副件
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            //要傳給誰的
            helper.setTo(order.getMemVO().getEmail());
            //判斷標題
            helper.setSubject(status == 1 ? "活動訂單確認通知" : "活動訂單處理失敗通知");

            //String "mailContent" = templateEngine.process(templatePath, context);
            //將 mailContent 作為電子郵件的內容 ,true是表示可以用html渲染~~
            helper.setText(mailContent, true);
            //1才加圖
            //ClassPathResource("static/email/emailsucess.png")---->自己想要在信件放的圖片或附件
            if(status == 1) {
                ClassPathResource footer = new ClassPathResource("static/email/emailsucess.png");
                helper.addInline("footer", footer);
            }



            mailSender.send(message);

            return "redirect:/ordlistall";

        }
//        -------------------------------------------------------------------------
    }








//debug專用
//System.out.println(evtService.getOneEvt(id));
//        System.out.println("確認方法");
//        System.out.println(evtOrderVO.getEvtAttend());
//        System.out.println(evtOrderVO.getEvtAttend());
//        System.out.println(evtOrderVO.getPlanOrderId());
//        System.out.println(evtOrderVO.getEvtDate());
//        System.out.println(evtOrderVO.getMemId());
//        System.out.println(evtVO.getEvtAttend());
//        System.out.println("EvtOrderStat:"+evtOrderVO.getEvtOrderStat());