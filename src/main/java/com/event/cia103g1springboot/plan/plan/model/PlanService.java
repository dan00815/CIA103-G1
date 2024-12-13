package com.event.cia103g1springboot.plan.plan.model;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanService {

    @Autowired
    private PlanRepository planRepository;

    public void savePlan(Plan plan) {
        planRepository.save(plan);
    }

    public Plan findPlanById(int planId) {
        return planRepository.findById(planId).orElse(new Plan());
    }

    public List<Plan> getAllPlans() {
        return planRepository.findAll();
    }

    public void deletePlanById(int planId) {
        planRepository.deleteById(planId);
    }


    public void updatePlan(Plan plan) {
        planRepository.save(plan); // 使用 JPA 的 save 方法更新資料
    }
}
//
//    @Autowired
//    private PlanRepository planRepository;
//
//    @Autowired
//    private PlanTypeRepository planTypeRepository;
//
//    @Autowired
//    private PlanInfoRepository planInfoRepository;
//
//    // Create Plan
//    public Plan createPlan(Plan plan) {
//        return planRepository.save(plan);
//    }
//
//    // Update Plan
//    public Plan updatePlan(int planId, Plan updatedPlan) {
//        Optional<Plan> existingPlan = planRepository.findById(planId);
//        if (existingPlan.isPresent()) {
//            Plan plan = existingPlan.get();
//            plan.setPlanTypeId(updatedPlan.getPlanTypeId());
//            plan.setStartDate(updatedPlan.getStartDate());
//            plan.setEndDate(updatedPlan.getEndDate());
//            plan.setPlanPrice(updatedPlan.getPlanPrice());
//            plan.setAttMax(updatedPlan.getAttMax());
//            plan.setAttEnd(updatedPlan.getAttEnd());
//            return planRepository.save(plan);
//        } else {
//            throw new RuntimeException("Plan not found");
//        }
//    }
//
//    // Get Plan by ID
//    public Plan getPlanById(int planId) {
//        Optional<Plan> plan = planRepository.findById(planId);
//        if (plan.isPresent()) {
//            return plan.get();
//        } else {
//            throw new RuntimeException("Plan not found");
//        }
//    }
//
//    // Get all Plans
//    public List<Plan> getAllPlans() {
//        return planRepository.findAll();
//    }
//
//    // Create PlanType
//    public PlanType createPlanType(PlanType planType) {
//        return planTypeRepository.save(planType);
//    }
//
//    // Update PlanType
//    public PlanType updatePlanType(String planTypeId, PlanType updatedPlanType) {
//        Optional<PlanType> existingPlanType = planTypeRepository.findById(planTypeId);
//        if (existingPlanType.isPresent()) {
//            PlanType planType = existingPlanType.get();
//            planType.setPlanName(updatedPlanType.getPlanName());
//            planType.setPlanDay(updatedPlanType.getPlanDay());
//            return planTypeRepository.save(planType);
//        } else {
//            throw new RuntimeException("PlanType not found");
//        }
//    }
//
//    // Get PlanType by ID
//    public PlanType getPlanTypeById(String planTypeId) {
//        Optional<PlanType> planType = planTypeRepository.findById(planTypeId);
//        if (planType.isPresent()) {
//            return planType.get();
//        } else {
//            throw new RuntimeException("PlanType not found");
//        }
//    }
//
//    // Get all PlanTypes
//    public List<PlanType> getAllPlanTypes() {
//        return planTypeRepository.findAll();
//    }
//
//    // Create PlanInfo
//    public PlanInfo createPlanInfo(PlanInfo planInfo) {
//        return planInfoRepository.save(planInfo);
//    }
//
//    // Update PlanInfo
//    public PlanInfo updatePlanInfo(String planTypeId, int planDay, PlanInfo updatedPlanInfo) {
//        Optional<PlanInfo> existingPlanInfo = planInfoRepository.findById(new PlanInfoId(planTypeId, planDay));
//        if (existingPlanInfo.isPresent()) {
//            PlanInfo planInfo = existingPlanInfo.get();
//            planInfo.setPlanCon(updatedPlanInfo.getPlanCon());
//            return planInfoRepository.save(planInfo);
//        } else {
//            throw new RuntimeException("PlanInfo not found");
//        }
//    }
//
//    // Get PlanInfo by ID
//    public PlanInfo getPlanInfoById(String planTypeId, int planDay) {
//        Optional<PlanInfo> planInfo = planInfoRepository.findById(new PlanInfoId(planTypeId, planDay));
//        if (planInfo.isPresent()) {
//            return planInfo.get();
//        } else {
//            throw new RuntimeException("PlanInfo not found");
//        }
//    }
//
//    // Get all PlanInfos
//    public List<PlanInfo> getAllPlanInfos() {
//        return planInfoRepository.findAll();
//    }




