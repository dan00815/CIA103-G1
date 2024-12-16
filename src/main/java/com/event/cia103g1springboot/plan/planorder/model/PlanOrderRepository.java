package com.event.cia103g1springboot.plan.planorder.model;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlanOrderRepository extends JpaRepository<PlanOrder, Integer> {
    @Override
    Optional<PlanOrder> findById(Integer integer);

    @Override
    List<PlanOrder> findAll(Sort sort);

    List<PlanOrder> findByMemVO_MemId(Integer memId);
    @Query("SELECT p FROM PlanOrder p " +
            "WHERE p.memVO.memId = :memId " +
            "AND (p.plan.startDate <= :now AND p.plan.endDate >= :now OR p.plan.endDate >= :now)")
    List<PlanOrder> findActiveOrdersByMember(@Param("memId") Integer memberId, @Param("now") LocalDate now);






    List<PlanOrder> findByMemVO_MemIdAndPlan_EndDateBefore(Integer memId, LocalDate now);


    PlanOrder findByPlanOrderIdAndOrderStat(Integer id, Integer status);

    PlanOrder findByMemVO_MemIdAndPlanOrderId(Integer memId, Integer planOrderId);
}
