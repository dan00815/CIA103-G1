package com.event.cia103g1springboot.member.notify.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.event.cia103g1springboot.member.mem.model.MemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.event.cia103g1springboot.member.notify.model.MemberNotifyService;
import com.event.cia103g1springboot.member.notify.model.MemberNotifyVO;

@Controller
@RequestMapping("/member/notify")
public class MemberNotifyController {

    @Autowired
    private MemberNotifyService notifyService;

    // 顯示通知列表頁面
    @GetMapping("")
    public String showNotifications(Model model, HttpSession session) {
        // 測試用
        MemVO memVO = (MemVO) session.getAttribute("auth");
        Integer memId = memVO.getMemId();

        List<MemberNotifyVO> notifications = notifyService.getAllMainNotifications(memId);
        Long unreadCount = notifyService.getUnreadCount(memId);

        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
        return "front-end/mem/notifications";
    }

    // 獲取特定類型的通知
    @GetMapping("/type/{notifyType}")
    @ResponseBody
    public List<MemberNotifyVO> getNotificationsByType(
            @PathVariable Integer notifyType,
            HttpSession session) {
        MemVO memVO = (MemVO) session.getAttribute("auth");
        return notifyService.getMainNotificationsByType(memVO.getMemId(), notifyType);
    }

    // 獲取通知歷史記錄
    @GetMapping("/{notifyId}/history")
    @ResponseBody
    public List<MemberNotifyVO> getNotificationHistory(@PathVariable Integer notifyId) {
        return notifyService.getNotificationHistory(notifyId);
    }

    // 標記單個通知為已讀
    @PostMapping("/{notifyId}/read")
    @ResponseBody
    public ResponseEntity<?> markAsRead(@PathVariable Integer notifyId) {
        notifyService.markAsRead(notifyId);
        return ResponseEntity.ok().build();
    }

    // 標記所有通知為已讀
    @PostMapping("/read-all")
    @ResponseBody
    public ResponseEntity<?> markAllAsRead(HttpSession session) {
        MemVO memVO = (MemVO) session.getAttribute("auth");
        Integer memId = memVO.getMemId();
        notifyService.markAllAsRead(memId);
        return ResponseEntity.ok().build();
    }

    // 刪除通知
    @DeleteMapping("/{notifyId}")
    @ResponseBody
    public ResponseEntity<?> deleteNotification(@PathVariable Integer notifyId) {
        notifyService.deleteNotification(notifyId);
        return ResponseEntity.ok().build();
    }

    // 搜索通知
    @GetMapping("/search")
    @ResponseBody
    public List<MemberNotifyVO> searchNotifications(
            @RequestParam String keyword,
            HttpSession session) {
        MemVO memVO = (MemVO) session.getAttribute("auth");
        return notifyService.searchNotifications(memVO.getMemId(), keyword);
    }

    // 獲取未讀通知數量
    @GetMapping("/unread-count")
    @ResponseBody
    public Long getUnreadCount(HttpSession session) {
        MemVO memVO = (MemVO) session.getAttribute("auth");
        return notifyService.getUnreadCount(memVO.getMemId());
    }

}
