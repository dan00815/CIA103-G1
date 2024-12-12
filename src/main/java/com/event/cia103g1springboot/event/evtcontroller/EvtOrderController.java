package com.event.cia103g1springboot.event.evtcontroller;
import com.event.cia103g1springboot.event.evtmodel.EvtService;
import com.event.cia103g1springboot.event.evtmodel.EvtVO;
import com.event.cia103g1springboot.event.evtordermodel.EvtOrderService;
import com.event.cia103g1springboot.event.evtordermodel.EvtOrderVO;
import com.event.cia103g1springboot.member.mem.model.MemService;
import com.event.cia103g1springboot.member.mem.model.MemVO;
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
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private PlanOrderRepository planOrderRepository;
    @Autowired
    private PlanOrderService planOrderService;


//    @InitBinder
//    public void initBinder(WebDataBinder binder) {
//        binder.registerCustomEditor(Timestamp.class, new PropertyEditorSupport() {
//            @Override
//            public void setAsText(String text) {
//                try {
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                    LocalDateTime dateTime = LocalDateTime.parse(text, formatter);
//                    setValue(Timestamp.valueOf(dateTime));
//                } catch (DateTimeParseException e) {
//                    setValue(null);
//                }
//            }
//        });
//    }

    @GetMapping("/attend/{id}")
    public String attend(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        EvtVO event = evtService.getOneEvt(id);
        MemVO memVO = memService.findOneMem("benson000");

        //額滿直接把人送回家 addFlashAttribute好用= =
        if (event.getEvtAttend() >= event.getEvtMax()) {
            redirectAttributes.addFlashAttribute("errorMessage", "該活動報名人數已額滿");
            return "redirect:/front/list";
        }
//        EvtOrderVO evtOrderVO = evtOrderService.getOrderByEvtId(id);
        //只生成key

        String captchaKey = "captcha:" + event.getEvtId();
        model.addAttribute("memVO", memVO);
        model.addAttribute("event", event);
        model.addAttribute("captchaKey", captchaKey);
        return "front-end/evtord/attendpage";
    }


    //拿所有活動訂單明細並分頁
    @GetMapping("/ordlistall")
    public String orderlistall(@RequestParam(defaultValue = "0") Integer page, Model model) {
        Page<EvtOrderVO> evtOrderPage = evtOrderService.getAllEvtorders(page);
        model.addAttribute("ord", evtOrderPage);
        return "/back-end/evtord/orderlist";
    }

    @Transactional
    @PostMapping("/confirm/{id}")
    public String confirm(@PathVariable Integer id, EvtOrderVO evtOrderVO, Model model) {

        //先用這兩行測@@
        PlanOrder planOrder = planOrderService.findPlanOrderById(1);
        evtOrderVO.setPlanOrder(planOrder);

        MemVO memVO = memService.findOneMem("benson000");
        evtOrderVO.setMemVO(memVO);

        evtService.attend(id, evtOrderVO);
        EvtVO event = evtService.getOneEvt(id);
        model.addAttribute("memVO", memVO);
        model.addAttribute("event", event);
        model.addAttribute("order", evtOrderVO);
        model.addAttribute("planOrder", planOrder);

        return "front-end/evtord/attendsucess";
    }

    //活動明細 有會員資訊 活動資訊 報名時間、備註....然後審核可以寄MAIL通知bla~~
    @GetMapping("/orderdetail/{id}")
    public String orderdetail(@PathVariable Integer id, Model model) {
        EvtOrderVO evtord = evtOrderService.getOneEvt(id);
        EvtVO evt = evtService.getOneEvt(evtord.getEvtVO().getEvtId());
        model.addAttribute("order", evtord);
        model.addAttribute("evt", evt);
        //之後拿會員~行程~~
        return "back-end/evtord/orderdetail";
    }

    @Transactional
    @GetMapping("/confirmord")
    public String confirmord(@RequestParam Integer id, @RequestParam Integer status,Model model) throws MessagingException {
            evtOrderService.updateEvtStatus(id, status);
            EvtOrderVO order = evtOrderService.getOneEvt(id);
            EvtVO evt = order.getEvtVO();

            if (status == 2) { // 取消訂單
            int updatedAttendance = order.getEvtVO().getEvtAttend() - order.getEvtAttend();
            evt.setEvtAttend(updatedAttendance);
            evtService.addEvt(evt); // 假設有這個更新方法
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