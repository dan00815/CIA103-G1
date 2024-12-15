package com.event.cia103g1springboot.hibernate.util.CompositeQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.event.cia103g1springboot.room.roomtype.model.RTVO;

public class HibernateUtil_CompositeQuery_RT {

	public static Predicate get_aPredicate_For_AnyDB(CriteriaBuilder builder,Root<RTVO> root,String columnName,String value) {
		Predicate predicate = null;
		if("roomTypeId".equals(columnName) || "roomQty".equals(columnName) || 
				"startRoomPrice".equals(columnName) || "endRoomPrice".equals(columnName) ||
				"startOccupancy".equals(columnName) || "endOccupancy".equals(columnName)) {
			if ("startRoomPrice".equals(columnName)) {
	            predicate = builder.greaterThanOrEqualTo(root.get("roomPrice"), Integer.valueOf(value));
	        } else if ("endRoomPrice".equals(columnName)) {
	            predicate = builder.lessThanOrEqualTo(root.get("roomPrice"), Integer.valueOf(value));
	        }else if("startOccupancy".equals(columnName)){
	        	predicate = builder.greaterThanOrEqualTo(root.get("occupancy"), Integer.valueOf(value));
	        }else if("endOccupancy".equals(columnName)){
	        	predicate = builder.lessThanOrEqualTo(root.get("occupancy"), Integer.valueOf(value));
	        }else {
	        	predicate = builder.equal(root.get(columnName),Integer.valueOf(value));}	
		} else if ("roomTypeDesc".equals(columnName) || "roomTypeName".equals(columnName)) {
			predicate = builder.like(root.get(columnName), "%" + value + "%");
		}
		return predicate;
	}
	
	@SuppressWarnings("unchecked")
	public static List<RTVO> getAllC(Map<String,String[]> map , Session session){
		Transaction tx = session.beginTransaction();
		List<RTVO> list = null;
		try {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<RTVO> criteriaQuery = builder.createQuery(RTVO.class);
			Root<RTVO> root = criteriaQuery.from(RTVO.class);
			List<Predicate> predicateList = new ArrayList<Predicate>();
			Set<String> keys = map.keySet();
			int count = 0;
			for(String key : keys) {
				String value = map.get(key)[0];
				if(value != null && !value.trim().isEmpty() && !"action".equals(key)) {
			        if ("startRoomPrice".equals(key) || "endRoomPrice".equals(key) || "startOccupancy".equals(key) || "endOccupancy".equals(key)) {
			            predicateList.add(get_aPredicate_For_AnyDB(builder, root, key, value));
			        } else {
			            predicateList.add(get_aPredicate_For_AnyDB(builder, root, key, value.trim()));
			        }
			        count++;
			        System.out.println("送出資料的欄位共:"+count+"欄");
					System.out.println("處理的欄位: " + key + ", 值: " + value);
				}
				Predicate predicate = builder.greaterThan(root.get("roomQty"),0);
				predicateList.add(predicate);
			}
			System.out.println("predicateList.size()="+predicateList.size());
			criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
			criteriaQuery.orderBy(builder.asc(root.get("roomTypeId")));
			Query query = session.createQuery(criteriaQuery);
			list = query.getResultList();
			
			tx.commit();
		}catch (RuntimeException ex) {
	        if (tx != null) {
	            tx.rollback(); // 回滾交易
	        }
	        
	        System.err.println("查詢商品列表時出現錯誤，已回滾交易。");
	        ex.printStackTrace();

	    } finally {
	        if (session != null) {
	            try {
	                session.clear(); // 清除 session
	            } catch (Exception e) {
	                System.err.println("清除 session 時發生錯誤。");
	                e.printStackTrace();
	            }
	        }
	    }
		if (list == null) {
	        list = new ArrayList<>();
	    }
		return list;
	}
	
}
