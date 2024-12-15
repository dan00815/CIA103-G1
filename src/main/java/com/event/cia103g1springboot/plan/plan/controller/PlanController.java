package com.event.cia103g1springboot.plan.plan.controller;


import com.event.cia103g1springboot.plan.plan.model.Plan;
import com.event.cia103g1springboot.plan.plan.model.PlanService;
import com.event.cia103g1springboot.plan.plantype.model.PlanType;
import com.event.cia103g1springboot.plan.plantype.model.PlanTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;


@Controller
    @RequestMapping("/api/plan")
public class PlanController {

    @Autowired
    private PlanService planService;

    @Autowired
    private PlanTypeService planTypeService;




    @PostMapping("/add")
    public String addPlan(@ModelAttribute Plan plan, @RequestParam("planTypeId") String planTypeId, Model model) {
        try {
            // 使用 existsByPlanTypeId 進行檢查
            if (!planTypeService.existsByPlanTypeId(planTypeId)) {
                model.addAttribute("error", "新增行程失敗，行程類型 ID 無效。");
                model.addAttribute("planTypes", planTypeService.getAllPlanTypes());
                System.out.println("新增行程失敗，行程類型 ID 不存在。: " + plan);
                return "plan/addplan";
            }

            // 如果存在，則繼續處理
            PlanType planType = planTypeService.findPlanTypeById(planTypeId);
            plan.setPlanType(planType);
            planService.savePlan(plan);
        } catch (Exception e) {
            model.addAttribute("error", "新增行程失敗：" + e.getMessage());
            model.addAttribute("planTypes", planTypeService.getAllPlanTypes());
            System.out.println("新增行程失敗，錯誤原因: " + e.getMessage());
            return "plan/addplan";
        }
        return "redirect:/plans/query";
    }

    @GetMapping("/calculateEndDate")
    public ResponseEntity<LocalDate> calculateEndDate(
            @RequestParam String planTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {

        // 根據行程類型 ID 獲取 PlanType
        PlanType planType = planTypeService.findPlanTypeById(planTypeId);
        if (planType == null) {
            return ResponseEntity.badRequest().build();
        }

        // 根據天數計算結束日期
        int days = planType.getPlanDay();
        LocalDate endDate = startDate.plusDays(days);

        return ResponseEntity.ok(endDate);
    }

    //前端端面
    @GetMapping("/planfront")
    public String frontlistall(Model model) {
        List<Plan> plans = planService.getAllPlans();
        List<Plan> filterPlans = plans.stream()
                .filter(plan -> plan.getAttMax()>0)
                .collect(Collectors.toList());
        model.addAttribute("plans", filterPlans);
        return "plan/planfront/planfrontlist";
    }

//

    @PostMapping("/edit")
    public String saveEditedPlan(@ModelAttribute Plan plan, @RequestParam("planTypeId") String planTypeId, Model model) {
        try {
            // 確認行程類型是否有效
            if (!planTypeService.existsByPlanTypeId(planTypeId)) {
                model.addAttribute("error", "修改行程失敗，行程類型 ID 無效。");
                model.addAttribute("planTypes", planTypeService.getAllPlanTypes());
                model.addAttribute("plan", plan);
                return "plan/editplan";
            }

            // 檢查最大參加人數是否小於目前參加人數
            if (plan.getAttMax() < plan.getAttEnd()) {
                model.addAttribute("error", "修改失敗：最大人數不得低於目前人數！");
                model.addAttribute("planTypes", planTypeService.getAllPlanTypes());
                model.addAttribute("plan", plan); // 保留使用者輸入的資料
                return "plan/editplan";
            }

            // 更新行程類型
            PlanType planType = planTypeService.findPlanTypeById(planTypeId);
            plan.setPlanType(planType);

            // 儲存修改後的行程
            planService.savePlan(plan);
            model.addAttribute("message", "行程已成功修改！");
        } catch (Exception e) {
            model.addAttribute("error", "行程修改失敗：" + e.getMessage());
            model.addAttribute("planTypes", planTypeService.getAllPlanTypes());
            model.addAttribute("plan", plan);
            return "plan/editplan";
        }
        return "redirect:/plans/query"; // 修改成功後返回到行程列表頁面
    }




    @PostMapping
    public void createPlan(@RequestBody Plan plan) {
        planService.savePlan(plan);
    }

    @GetMapping("/{planId}")
    public Plan getPlanById(@PathVariable int planId) {
        return planService.findPlanById(planId);
    }



//




    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Timestamp.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                try {
                    LocalDateTime dateTime = LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    setValue(Timestamp.valueOf(dateTime));
                } catch (DateTimeParseException e) {
                    setValue(null);
                }
            }
        });
    }
}
