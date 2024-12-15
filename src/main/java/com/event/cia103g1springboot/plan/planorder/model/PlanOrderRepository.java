package com.event.cia103g1springboot.plan.planorder.model;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanOrderRepository extends JpaRepository<PlanOrder, Integer> {
    @Override
    Optional<PlanOrder> findById(Integer integer);

    @Override
    List<PlanOrder> findAll(Sort sort);

    List<PlanOrder> findByMemVO_MemId(Integer memId);

  PlanOrder findByPlanOrderIdAndOrderStat(Integer id, Integer status);

    PlanOrder findByMemVO_MemIdAndPlanOrderId(Integer memId, Integer planOrderId);
}
