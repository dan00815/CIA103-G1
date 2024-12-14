package com.event.cia103g1springboot.plan.planroom.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.event.cia103g1springboot.plan.plan.model.Plan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.event.cia103g1springboot.plan.plan.model.PlanService;
import com.event.cia103g1springboot.plan.planroom.model.PlanRoom;
import com.event.cia103g1springboot.plan.planroom.model.PlanRoomService;
import com.event.cia103g1springboot.room.roomtype.model.RTService;
import com.event.cia103g1springboot.room.roomtype.model.RTVO;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping("/planroom")
@Controller
public class PlroomController {

    @Autowired
    PlanRoomService planRoomService;

    @Autowired
    PlanService planService;

    @Autowired
    RTService rtService;

    @GetMapping("/listall")
    public String ListALL(PlanRoom planRoom, Model model) {
       List<PlanRoom> list =planRoomService.findAll();
       model.addAttribute("list", list);
       return "/plan/planroom/planroom";
    }

    @GetMapping("/addpage")
    public String Add(PlanRoom planRoom, Model model) {
        model.addAttribute("plans", planService.getAllPlans()); //直接拿不爽啦
        model.addAttribute("rooms", rtService.getAllRT());
        return "/plan/planroom/addpage";
    }



///  新增房型資訊 and 修改行程最大人數
@PostMapping("/add")
public String addPlanRooms(
        @RequestParam("planId") Long planId,
        @RequestParam("roomTypeId") List<Long> roomTypeIds,
        @RequestParam("roomQty") List<Integer> roomQuantities,
        @RequestParam("roomPrice") List<Integer> roomPrices,
        @RequestParam("roomTypeName") List<String> roomTypeNames,
        RedirectAttributes redirectAttributes) {
    // 檢查數據一致性
    if (roomTypeIds == null || roomQuantities == null || roomPrices == null || roomTypeNames == null ||
            roomTypeIds.size() != roomQuantities.size() || roomTypeIds.size() != roomPrices.size() || roomTypeIds.size() != roomTypeNames.size()) {
        redirectAttributes.addFlashAttribute("errorMessage", "房型數據不匹配或不完整！");
        return "redirect:/planroom/add";
    }

    try {
        // 查詢 Plan 資料
        Plan selectedPlan = planService.findPlanById(planId.intValue());
        if (selectedPlan == null) throw new IllegalArgumentException("找不到行程 ID：" + planId);

        List<PlanRoom> planRooms = new ArrayList<>();
        int totalCapacity = 0; // 計算新增的總人數

        // 遍歷房型數據
        for (int i = 0; i < roomTypeIds.size(); i++) {
            Long roomTypeId = roomTypeIds.get(i);
            Integer roomQty = roomQuantities.get(i);
            Integer roomPrice = roomPrices.get(i);
            String roomTypeName = roomTypeNames.get(i);

            if (roomQty <= 0 || roomPrice <= 0) {
                throw new IllegalArgumentException("房型數量或價格無效，請檢查輸入數據！");
            }

            // 計算房型總人數容量
            int roomCapacity = planRoomService.extractCapacityFromRoomTypeName(roomTypeName);
            int roomTotalCapacity = roomCapacity * roomQty;

            // 累加房型容量
            totalCapacity += roomTotalCapacity;

            // 創建 PlanRoom 實例
            PlanRoom planRoom = new PlanRoom();
            planRoom.setPlanId(planId.intValue());
            planRoom.setRoomTypeId(roomTypeId.intValue());
            planRoom.setRoomTypeName(roomTypeName);
            planRoom.setRoomQty(roomQty);
            planRoom.setRoomPrice(roomPrice);

            planRooms.add(planRoom);
        }

        // 批量保存 PlanRoom 資料
        planRoomService.addPlanRooms(planRooms);

        // 更新 Plan 表的 attMax
        selectedPlan.setAttMax(totalCapacity);
        planService.updatePlan(selectedPlan);

        redirectAttributes.addFlashAttribute("successMessage", "房型新增成功！行程 ID：" + planId);
        return "redirect:/planroom/listall";

    } catch (IllegalArgumentException e) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorMessage", "系統錯誤，請稍後重試！");
    }
    return "redirect:/planroom/add";
}


    @Transactional
    @GetMapping("/editpage/{roomid}/{planid}")
    public String editPage(@PathVariable Integer planid,@PathVariable Integer roomid, Model model) {
        PlanRoom planroom = planRoomService.findByRmTypeIdAndPlanId(roomid, planid);
        // 將資料加入 Model
        model.addAttribute("planroom", planroom);
        model.addAttribute("errors", new HashMap<String, String>());
        return "plan/planroom/editpage";
    }

//        @Transactional
//        @PostMapping("/edit")
//        public String edit(PlanRoom planRoom, Model model) {
//            planRoomService.save(planRoom);
//
//            return "redirect:/planroom/listall";
//        }

////  修改區

    @Transactional
    @PostMapping("/edit")
    public String edit(PlanRoom planRoom,
                       @RequestParam("roomQty") Integer roomQty,
                       @RequestParam("originalRoomQty") Integer originalRoomQty,
                       @RequestParam("roomTypeName") String roomTypeName,
                       Model model) {
        planRoomService.save(planRoom);
        int roomCapacity = planRoomService.extractCapacityFromRoomTypeName(roomTypeName);
        int roomTotalCapacity = (roomQty - originalRoomQty) * roomCapacity;

        Plan plan = planService.findPlanById(planRoom.getPlanId());
        plan.setAttMax(roomTotalCapacity+ plan.getAttMax());



        return "redirect:/planroom/listall";
    }

}
