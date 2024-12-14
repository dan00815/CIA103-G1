package com.event.cia103g1springboot.member.notify.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.event.cia103g1springboot.member.mem.model.MemVO;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "membernotify")
public class MemberNotifyVO {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer notifyId;

    // 多對一關聯到會員表
    @ManyToOne
    @JoinColumn(name = "memId", nullable = false)
    @JsonBackReference
    private MemVO member;

    // 通知類型 (1: 活動通知, 2: 活動訂單通知, ...)
    @Column(nullable = false)
    private Integer notifyType;

    // 通知内容
    @Column(nullable = false, length = 1500)
    private String notifyCon;

    // 是否已讀
    @Column(nullable = false)
    private Boolean isRead;

    // 通知時間
    @Column(nullable = false)
    private LocalDateTime notifyTime;

    // 父通知ID (null 表示主通知)
    @Column
    private Integer parentId;

    // 業務關連鍵
    @Column
    private String businessKey;

    // 用於自動設置默認值的生命周期回調
    @PrePersist
    public void setDefaultValues() {
        if (this.isRead == null) {
            this.isRead = false;
        }
        if (this.notifyTime == null) {
            this.notifyTime = LocalDateTime.now();
        }
    }

    // 提供類型映射以優化 getNotifyTypeText
    private static final Map<Integer, String> NOTIFY_TYPE_TEXT = new HashMap<>();
    static {
        NOTIFY_TYPE_TEXT.put(1, "活動通知");
        NOTIFY_TYPE_TEXT.put(2, "活動訂單通知");
        NOTIFY_TYPE_TEXT.put(3, "商品訂單通知");
        NOTIFY_TYPE_TEXT.put(4, "商品通知");
        NOTIFY_TYPE_TEXT.put(5, "佈告欄通知");
        NOTIFY_TYPE_TEXT.put(6, "行程訂單通知");
    }

    // 獲取通知類行文本
    public String getNotifyTypeText() {
        return NOTIFY_TYPE_TEXT.getOrDefault(notifyType, "未知類型");
    }
}



//package com.event.cia103g1springboot.member.notify.model;
//
//import java.time.LocalDateTime;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.Table;
//
//import com.event.cia103g1springboot.member.mem.model.MemVO;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//
//@NoArgsConstructor
//@AllArgsConstructor
//@Data
//@Entity
//@Table(name = "membernotify")
//public class MemberNotifyVO {
//    
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer notifyId;
//    
//    @ManyToOne
//    @JoinColumn(name = "memId", nullable = false)
//    private MemVO member;
//    
//    @Column(nullable = false)
//    private Integer notifyType;  // 1:活動通知、2:活動訂單通知、3:商品訂單通知、4:商品通知、5:佈告欄通知、6:行程訂單通知
//    
//    @Column(nullable = false, length = 1500)
//    private String notifyCon;
//    
//    @Column(nullable = false)
//    private Boolean isRead = false;
//    
//    @Column(nullable = false)
//    private LocalDateTime notifyTime = LocalDateTime.now();
//    
//    @Column
//    private Integer parentId;  // 父級通知ID，為null表示這是主通知
//    
//    @Column
//    private String businessKey;  // 新增業務關聯鍵
//
//    // 用於前端顯示的輔助方法
//    public String getNotifyTypeText() {
//        switch (notifyType) {
//            case 1: return "活動通知";
//            case 2: return "活動訂單通知";
//            case 3: return "商品訂單通知";
//            case 4: return "商品通知";
//            case 5: return "佈告欄通知";
//            case 6: return "行程訂單通知";
//            default: return "未知類型";
//        }
//    }
//}
