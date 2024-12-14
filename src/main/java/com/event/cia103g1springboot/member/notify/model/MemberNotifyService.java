package com.event.cia103g1springboot.member.notify.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberNotifyService {
    
    @Autowired
    private MemberNotifyRepository notifyRepository;
    
    // 獲取會員所有主通知
    public List<MemberNotifyVO> getAllMainNotifications(Integer memId) {
        return notifyRepository.findMainNotifications(memId);
    }
    
    // 根據類型獲取主通知
    public List<MemberNotifyVO> getMainNotificationsByType(Integer memId, Integer notifyType) {
        return notifyRepository.findMainNotificationsByType(memId, notifyType);
    }
    
    // 獲取通知的歷史記錄
    public List<MemberNotifyVO> getNotificationHistory(Integer notifyId) {
        return notifyRepository.findNotificationHistory(notifyId);
    }
    
    // 搜索通知
    public List<MemberNotifyVO> searchNotifications(Integer memId, String keyword) {
        return notifyRepository.searchMainNotifications(memId, keyword);
    }
    
    // 獲取未讀通知數量
    public Long getUnreadCount(Integer memId) {
        return notifyRepository.countByMember_MemIdAndIsReadAndParentIdIsNull(memId, false);
    }
    
    // 創建新通知
    @Transactional
    public MemberNotifyVO createNotification(MemberNotifyVO notification) {
        if (notification.getBusinessKey() == null) {
            throw new RuntimeException("業務關聯鍵不能為空");
        }
        notification.setParentId(null);  // 確保是主通知
        return notifyRepository.save(notification);
    }
    
    // 添加通知歷史記錄
    @Transactional
    public MemberNotifyVO addNotificationHistory(String businessKey, Integer memId, Integer notifyType, MemberNotifyVO historyNotify) {
        // 根據業務鍵查找父通知
        MemberNotifyVO parentNotify = notifyRepository.findByBusinessKeyAndType(memId, businessKey, notifyType);
        if (parentNotify == null) {
            throw new RuntimeException("找不到對應的父通知");
        }
        
        // 設置父通知ID和業務鍵
        historyNotify.setParentId(parentNotify.getNotifyId());
        historyNotify.setBusinessKey(businessKey);
        return notifyRepository.save(historyNotify);
    }
    
    // 標記單個通知為已讀
    @Transactional
    public void markAsRead(Integer notifyId) {
        MemberNotifyVO notify = notifyRepository.findById(notifyId)
                .orElseThrow(() -> new RuntimeException("通知不存在"));
        notify.setIsRead(true);
        notifyRepository.save(notify);
    }
    
    // 標記所有通知為已讀
    @Transactional
    public void markAllAsRead(Integer memId) {
        notifyRepository.markAllAsRead(memId);
    }
    
    // 刪除通知（包括其歷史記錄）
    @Transactional
    public void deleteNotification(Integer notifyId) {
        // 先刪除所有子通知
        List<MemberNotifyVO> history = notifyRepository.findNotificationHistory(notifyId);
        notifyRepository.deleteAll(history);
        // 再刪除主通知
        notifyRepository.deleteById(notifyId);
    }
}
