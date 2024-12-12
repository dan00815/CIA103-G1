package com.event.cia103g1springboot.hibernate.util.CompositeQuery;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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

import com.event.cia103g1springboot.bb.model.BBVO;




public class HibernateUtil_CompositeQuery_BB {

	public static Predicate get_aPredicate_For_AnyDB(CriteriaBuilder builder,Root<BBVO> root, String columnName, String value) {
		Predicate predicate = null;
		
		if("msgid".equals(columnName))
			predicate = builder.equal(root.get(columnName),Integer.valueOf(value));
		else if( "msgtitle".equals(columnName) || "msgcon".equals(columnName)) 
			predicate = builder.like(root.get(columnName), "%"+ value +"%"); 
		else if("poststat".equals(columnName) || "msgtype".equals(columnName))
			predicate = builder.equal(root.get(columnName), Byte.valueOf(value));
		else if ("startPosttime".equals(columnName) || "endPosttime".equals(columnName)) {
		    try {
		        LocalDateTime dateTime = LocalDateTime.parse(value);
		        if ("startPosttime".equals(columnName)) {
		            predicate = builder.greaterThanOrEqualTo(root.get("posttime"), Timestamp.valueOf(dateTime));
		        } else if ("endPosttime".equals(columnName)) {
		            predicate = builder.lessThanOrEqualTo(root.get("posttime"), Timestamp.valueOf(dateTime));
		        }
		    } catch (Exception e) {
		        e.printStackTrace(); // 處理日期格式不正確的情況
		    }
		}
		    // 假設 value 是一個特定日期範圍，可能是"startpostTime" 和 "endpostTime" 的兩個日期範圍查詢
		return predicate;
	}
	
	@SuppressWarnings("unchecked")
	public static List<BBVO> getAllC(Map<String,String[]> map, Session session){
		Transaction tx = session.beginTransaction();
		List<BBVO> list = null;
		try {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<BBVO> criteriaQuery = builder.createQuery(BBVO.class);
			Root<BBVO> root = criteriaQuery.from(BBVO.class);
			List<Predicate> predicateList = new ArrayList<Predicate>();
			Set<String> keys = map.keySet();
			int count = 0;
			for(String key : keys) {
				String value = map.get(key)[0];
				if (value != null && !value.trim().isEmpty() && !"action".equals(key)) {
			        if ("startPosttime".equals(key) || "endPosttime".equals(key)) {
			            predicateList.add(get_aPredicate_For_AnyDB(builder, root, key, value));
			        } else {
			            predicateList.add(get_aPredicate_For_AnyDB(builder, root, key, value.trim()));
			        }
			        count++;
			        System.out.println("送出資料的欄位共:"+count+"欄");
					 System.out.println("處理的欄位: " + key + ", 值: " + value);
				}
				Predicate predicate = builder.lessThan(root.get("poststat"), 2);
				predicateList.add(predicate);
			}
			System.out.println("predicateList.size()="+predicateList.size());
			criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
			criteriaQuery.orderBy(builder.asc(root.get("msgid")));
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



