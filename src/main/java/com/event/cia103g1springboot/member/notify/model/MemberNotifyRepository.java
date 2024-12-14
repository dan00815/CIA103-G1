package com.event.cia103g1springboot.member.notify.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberNotifyRepository extends JpaRepository<MemberNotifyVO, Integer> {
    
    // 獲取會員的主通知（沒有父通知的）
    @Query("SELECT n FROM MemberNotifyVO n WHERE n.member.memId = ?1 AND n.parentId IS NULL ORDER BY n.notifyTime DESC")
    List<MemberNotifyVO> findMainNotifications(Integer memId);
    
    // 獲取特定類型的主通知
    @Query("SELECT n FROM MemberNotifyVO n WHERE n.member.memId = ?1 AND n.notifyType = ?2 AND n.parentId IS NULL ORDER BY n.notifyTime DESC")
    List<MemberNotifyVO> findMainNotificationsByType(Integer memId, Integer notifyType);
    
    // 獲取通知的歷史記錄
    @Query("SELECT n FROM MemberNotifyVO n WHERE n.parentId = ?1 ORDER BY n.notifyTime DESC")
    List<MemberNotifyVO> findNotificationHistory(Integer parentId);
    
    // 獲取未讀通知數量
    Long countByMember_MemIdAndIsReadAndParentIdIsNull(Integer memId, Boolean isRead);
    
    // 標記所有主通知為已讀
    @Modifying
    @Query("UPDATE MemberNotifyVO n SET n.isRead = true WHERE n.member.memId = ?1 AND n.isRead = false AND n.parentId IS NULL")
    void markAllAsRead(Integer memId);
    
    // 根據內容搜索主通知
    @Query("SELECT n FROM MemberNotifyVO n WHERE n.member.memId = ?1 AND n.notifyCon LIKE %?2% AND n.parentId IS NULL ORDER BY n.notifyTime DESC")
    List<MemberNotifyVO> searchMainNotifications(Integer memId, String keyword);
    
    // 根據業務鍵查找通知
    @Query("SELECT n FROM MemberNotifyVO n WHERE n.member.memId = ?1 AND n.businessKey = ?2 AND n.notifyType = ?3 AND n.parentId IS NULL")
    MemberNotifyVO findByBusinessKeyAndType(Integer memId, String businessKey, Integer notifyType);
}
