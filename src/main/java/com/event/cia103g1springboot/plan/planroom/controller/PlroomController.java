package com.event.cia103g1springboot.plan.planroom.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.event.cia103g1springboot.plan.plan.model.Plan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.event.cia103g1springboot.plan.plan.model.PlanService;
import com.event.cia103g1springboot.plan.planroom.model.PlanRoom;
import com.event.cia103g1springboot.plan.planroom.model.PlanRoomService;
import com.event.cia103g1springboot.room.roomtype.model.RTService;
import com.event.cia103g1springboot.room.roomtype.model.RTVO;
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






//    @PostMapping("/add")
//    public String AddPost(PlanRoom planRoom, Model model) {
//            // 1. 查詢現有房型
//            RTVO rtvo = rtService.getOneRT(planRoom.getRoomTypeId());
//            if (rtvo == null) {
//                // 處理找不到房型的情況
//                return "error_page";
//            }
//            int totalqty = rtvo.getRoomQty() - planRoom.getRoomQty();
//            rtService.updateRoomQty(planRoom.getRoomTypeId(), totalqty);
//            planRoomService.save(planRoom);
//            return "redirect:/planroom/listall";
//    }


//    @PostMapping("/add")
//    public String addPlanRooms(@RequestParam("planId") Long planId,
//                               @RequestParam("roomTypeId") List<Long> roomTypeIds,
//                               @RequestParam("roomQty") List<Integer> roomQuantities,
//                               @RequestParam("roomPrice") List<Integer> roomPrices,
//                               @RequestParam("roomName") List<String> roomNames,
//                               RedirectAttributes redirectAttributes) {
//
//        // 檢查數據一致性
//        if (roomTypeIds == null || roomQuantities == null || roomPrices == null || roomNames == null ||
//                roomTypeIds.size() != roomQuantities.size() || roomTypeIds.size() != roomPrices.size() || roomTypeIds.size() != roomNames.size()) {
//            redirectAttributes.addFlashAttribute("errorMessage", "房型數據不匹配或不完整！");
//            return "redirect:/planroom/add";
//        }
//
//        try {
//            // 獲取選擇的行程
//            Plan selectedPlan = planService.findPlanById(planId.intValue());
//            if (selectedPlan == null) {
//                redirectAttributes.addFlashAttribute("errorMessage", "找不到對應的行程 ID：" + planId);
//                return "redirect:/planroom/add";
//            }
//
//            // 暫存所有 PlanRoom
//            List<PlanRoom> planRooms = new ArrayList<>();
//
//            for (int i = 0; i < roomTypeIds.size(); i++) {
//                int qty = roomQuantities.get(i);
//                int price = roomPrices.get(i);
//                String roomName = roomNames.get(i);
//
//                // 驗證房型數量和價格
//                if (qty <= 0 || price <= 0) {
//                    throw new IllegalArgumentException("房型數量或價格無效，請檢查輸入數據！");
//                }
//
//                // 創建 PlanRoom
//                PlanRoom planRoom = new PlanRoom();
//                planRoom.setPlanId(planId.intValue());
//                planRoom.setRoomTypeId(roomTypeIds.get(i).intValue());
//                planRoom.setRoomTypeName(roomName);
//                planRoom.setRoomQty(qty);
//                planRoom.setRoomPrice(price);
//                planRoom.setReservedRoom(0); // 初始預訂數量為 0
//
//                // 添加到批量保存列表
//                planRooms.add(planRoom);
//            }
//
//            // 批量保存所有 PlanRoom
//            planRoomService.addPlanRooms(planRooms);
//
//            // 添加成功訊息
//            redirectAttributes.addFlashAttribute("successMessage", "房型新增成功！行程 ID：" + planId);
//            return "redirect:/planroom/listall";
//
//        } catch (IllegalArgumentException e) {
//            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
//            return "redirect:/planroom/add";
//
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("errorMessage", "系統錯誤，請稍後重試！");
//            return "redirect:/planroom/add";
//        }
//    }



    @PostMapping("/add")
    public String addPlanRooms(@RequestParam("planId") Long planId,
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
            Plan selectedPlan = planService.findPlanById(planId.intValue());
            if (selectedPlan == null) throw new IllegalArgumentException("找不到行程 ID：" + planId);

            List<PlanRoom> planRooms = new ArrayList<>();
            for (int i = 0; i < roomTypeIds.size(); i++) {
                if (roomQuantities.get(i) <= 0 || roomPrices.get(i) <= 0)
                    throw new IllegalArgumentException("房型數量或價格無效，請檢查輸入數據！");

                planRooms.add(new PlanRoom(planId.intValue(), roomTypeIds.get(i).intValue(), roomTypeNames.get(i),
                        roomQuantities.get(i), roomPrices.get(i), 0));
            }

            planRoomService.addPlanRooms(planRooms); // 批量保存
            redirectAttributes.addFlashAttribute("successMessage", "房型新增成功！行程 ID：" + planId);
            return "redirect:/planroom/listall";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "系統錯誤，請稍後重試！");
        }
        return "redirect:/planroom/add";
    }

///  調整區

//@PostMapping("/add")
//public String addPlanRooms(@RequestParam("planId") Long planId,
//                           @RequestParam("roomTypeId") List<Long> roomTypeIds,
//                           @RequestParam("roomQty") List<Integer> roomQuantities,
//                           @RequestParam("roomPrice") List<Integer> roomPrices,
//                           @RequestParam("roomTypeName") List<String> roomTypeNames,
//                           RedirectAttributes redirectAttributes) {
//
//    try {
//        Plan selectedPlan = planService.findPlanById(planId.intValue());
//        if (selectedPlan == null) throw new IllegalArgumentException("找不到行程 ID：" + planId);
//
//        int remainingParticipants = selectedPlan.getAttMax() - selectedPlan.getAttEnd();
//        List<PlanRoom> planRooms = new ArrayList<>();
//
//        for (int i = 0; i < roomTypeIds.size(); i++) {
//            String roomTypeName = roomTypeNames.get(i);
//            int roomCapacity = planRoomService.extractCapacityFromRoomTypeName(roomTypeName); // 使用 roomTypeName
//            int qty = roomQuantities.get(i);
//            int price = roomPrices.get(i);
//
//            int requiredParticipants = roomCapacity * qty;
//            if (requiredParticipants > remainingParticipants) {
//                throw new IllegalArgumentException(
//                        "房型 '" + roomTypeName + "' 的數量超出剩餘空位！剩餘人數: " + remainingParticipants);
//            }
//
//            planRooms.add(new PlanRoom(planId.intValue(), roomTypeIds.get(i).intValue(), roomTypeName,
//                    qty, price, 0));
//            remainingParticipants -= requiredParticipants;
//        }
//
//        planRoomService.addPlanRooms(planRooms); // 批量保存
//        selectedPlan.setAttEnd(selectedPlan.getAttMax() - remainingParticipants);
//        planService.updatePlan(selectedPlan);
//
//        redirectAttributes.addFlashAttribute("successMessage", "房型新增成功！行程 ID：" + planId);
//        return "redirect:/planroom/listall";
//    } catch (IllegalArgumentException e) {
//        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
//    } catch (Exception e) {
//        redirectAttributes.addFlashAttribute("errorMessage", "系統錯誤，請稍後重試！");
//    }
//    return "redirect:/planroom/add";
//}


    @Transactional
    @GetMapping("/editpage/{roomid}/{planid}")
    public String editPage(@PathVariable Integer planid,@PathVariable Integer roomid, Model model) {
        PlanRoom planroom = planRoomService.findByRmTypeIdAndPlanId(roomid, planid);
        // 將資料加入 Model
        model.addAttribute("planroom", planroom);
        model.addAttribute("errors", new HashMap<String, String>());
        return "plan/planroom/editpage";
    }

    @Transactional
    @PostMapping("/edit")
    public String edit(PlanRoom planRoom, Model model) {
        planRoomService.save(planRoom);
        return "redirect:/planroom/listall";
    }



}
