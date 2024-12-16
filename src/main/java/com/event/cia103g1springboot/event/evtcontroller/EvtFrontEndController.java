package com.event.cia103g1springboot.event.evtcontroller;


import com.event.cia103g1springboot.event.evtimgmodel.EvtImgService;
import com.event.cia103g1springboot.event.evtimgmodel.EvtImgVO;
import com.event.cia103g1springboot.event.evtmodel.EvtService;
import com.event.cia103g1springboot.event.evtmodel.EvtVO;
import com.event.cia103g1springboot.member.mem.model.MemVO;
import com.event.cia103g1springboot.plan.planorder.model.PlanOrder;
import com.event.cia103g1springboot.plan.planorder.model.PlanOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.HashMap;
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
    public String listEvents(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Integer minAttend,
            @RequestParam(required = false) Integer maxAttend,
            @RequestParam(defaultValue = "0") int page,  // 加入分頁參數
            Model model) {

        Page<EvtVO> events;

        // 有條件就直接查
        if (keyword != null || startDate != null || endDate != null ||
                minAttend != null || maxAttend != null) {

            Map<String, Object> criteria = new HashMap<>();
            criteria.put("keyword", keyword);
            criteria.put("startDate", startDate);
            criteria.put("endDate", endDate);
            criteria.put("minAttend", minAttend);
            criteria.put("maxAttend", maxAttend);

            events = evtService.findActiveEvents(criteria, page);
        } else {
            //原本的
            events = evtService.findByEvtStatOrderByEvtDateAsc2(1, 3, page);
        }

        model.addAttribute("events", events);
        model.addAttribute("keyword", keyword);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("minAttend", minAttend);
        model.addAttribute("maxAttend", maxAttend);
        model.addAttribute("currentPage", page);

        return "front-end/evt/listpage";
    }


    //根據活動id拿照片跟活動內容
    @GetMapping("/detail/{id}")
    public String showEventDetail(@PathVariable Integer id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
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
        //拿第一張單就好行程不會時間相衝
            if (hasPlanOrder) {
                model.addAttribute("planOrder", planOrders.get(0));
            }
            model.addAttribute("hasPlanOrder", hasPlanOrder);
        } else {
            model.addAttribute("hasPlanOrder", false);
        }
        //額滿送回家
        if (event.getEvtAttend() >= event.getEvtMax()) {
            redirectAttributes.addFlashAttribute("errorMessage", "該活動報名人數已額滿");
            return "redirect:/front/list";
        }

        return "front-end/evt/eventdetail";
    }
    
}