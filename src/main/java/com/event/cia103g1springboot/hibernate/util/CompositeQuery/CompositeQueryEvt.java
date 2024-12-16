package com.event.cia103g1springboot.hibernate.util.CompositeQuery;

import com.event.cia103g1springboot.event.evtmodel.EvtVO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaBuilder;

@Component
public class CompositeQueryEvt {
    public static Specification<EvtVO> findActiveEvents(final Map<String, Object> criteria) {
        return new Specification<EvtVO>() {
    @Override
    public Predicate toPredicate(Root<EvtVO> root,
                                 CriteriaQuery<?> query,
                                 CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicates = new ArrayList<>();

        // 只檢查狀態是 1 或 3
        predicates.add(root.get("evtStat").in(Arrays.asList(1, 3)));

        // 關鍵字查詢（活動名稱和描述）
        if (criteria.get("keyword") != null) {
            String keyword = (String) criteria.get("keyword");
            if (!keyword.trim().isEmpty()) {
                String searchKeyword = "%" + keyword.trim() + "%";
                Predicate namePredicate = criteriaBuilder.like(
                        root.get("evtName"), searchKeyword);
                Predicate descPredicate = criteriaBuilder.like(
                        root.get("evtDesc"), searchKeyword);
                predicates.add(criteriaBuilder.or(namePredicate, descPredicate));
            }
        }

        // 日期範圍查詢
        if (criteria.get("startDate") != null) {
            LocalDate startDate = (LocalDate) criteria.get("startDate");
            LocalDateTime startDateTime = startDate.atStartOfDay();
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("evtDate"), startDateTime));
        }
        if (criteria.get("endDate") != null) {
            LocalDate endDate = (LocalDate) criteria.get("endDate");
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
            predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("evtDate"), endDateTime));
        }

        // 參加人數範圍查詢
        if (criteria.get("minAttend") != null) {
            Integer minAttend = (Integer) criteria.get("minAttend");
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("evtAttend"), minAttend));
        }
        if (criteria.get("maxAttend") != null) {
            Integer maxAttend = (Integer) criteria.get("maxAttend");
            predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("evtAttend"), maxAttend));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
        };
    }}



