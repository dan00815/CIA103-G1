package com.event.cia103g1springboot.event.evtcontroller;


import com.event.cia103g1springboot.event.evtimgmodel.EvtImgService;
import com.event.cia103g1springboot.event.evtimgmodel.EvtImgVO;
import com.event.cia103g1springboot.event.evtmodel.EvtService;
import com.event.cia103g1springboot.event.evtmodel.EvtVO;
import com.event.cia103g1springboot.member.mem.model.MemVO;
import com.event.cia103g1springboot.plan.planorder.model.PlanOrder;
import com.event.cia103g1springboot.plan.planorder.model.PlanOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RequestMapping("/front")
@Controller
public class EvtFrontEndController {
    @Autowired
    EvtService evtService;
    @Autowired
    EvtImgService evtImgService;
    @Autowired
    PlanOrderService planOrderService;



    //只拿上架活動
    @GetMapping("/list")
    public String listActiveEvents(Model model) {
        List<EvtVO> events = evtService.findByEvtStatOrderByEvtDateAsc2(1,3);
        model.addAttribute("events", events);
        return "front-end/evt/listpage";
    }

    //根據活動id拿照片跟活動內容
    @GetMapping("/detail/{id}")
    public String showEventDetail(@PathVariable Integer id, Model model, HttpSession session) {
        // 先取得活動相關資訊
        EvtVO event = evtService.getOneEvt(id);
        List<EvtImgVO> evtImgs = evtImgService.getImagesByEvtId(id);

        //先讓沒報名和沒登入都能看活動詳情
        model.addAttribute("evt", event);
        model.addAttribute("evtImgs", evtImgs);

        //檢查會員登入狀態及行程訂單
        //有登入直接抓他訂單,沒登入滾去報名行程
        MemVO memVO = (MemVO) session.getAttribute("auth");
        if (memVO != null) {
            List<PlanOrder> planOrders = planOrderService.findPlanOrdersByMemId(memVO.getMemId());
            boolean hasPlanOrder = !planOrders.isEmpty();

            if (hasPlanOrder) {
                model.addAttribute("planOrder", planOrders.get(0));
            }
            model.addAttribute("hasPlanOrder", hasPlanOrder);
        } else {
            model.addAttribute("hasPlanOrder", false);
        }

        return "front-end/evt/eventdetail";
    }
    
}