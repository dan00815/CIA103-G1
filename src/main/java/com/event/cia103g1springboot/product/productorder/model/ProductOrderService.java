package com.event.cia103g1springboot.product.productorder.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


//請自行引入打開
//import hibernate.util.CompositeQuery.HibernateUtil_CompositeQuery_HihiDatabase;


@Service("productOrderService")
public class ProductOrderService {
	
	@Autowired
	ProductOrderRepository repository;

	
	public Integer addProductOrder(ProductOrderVO productOrderVO) {
		ProductOrderVO savedOrder = repository.save(productOrderVO);
		return savedOrder.getPdtOrderId(); //為了回傳自增訂單ID
	}
	
	public void updateProductOrder(ProductOrderVO productOrderVO) {
		repository.save(productOrderVO);
	}
	
	public ProductOrderVO getOneProductOrder(Integer pdtOrderId) {
		Optional<ProductOrderVO> optional = repository.findById(pdtOrderId);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}
	
	//自訂搜尋
	public List<ProductOrderVO> getProductOrderByMemId(Integer memId) {
		return repository.findByMemId(memId);
	}
	
	//自訂搜尋
	public List<ProductOrderVO> getOneProductOrderByOrderStat(Integer orderStat) {
		return repository.findByOrderStat(orderStat);
	}
	
	public List<ProductOrderVO> getAll() {
		return repository.findAll();
	}

//	我沒有要自己打開
//	public List<ProductOrderVO> getAll(Map<String, String[]> map) {
//		return HibernateUtil_CompositeQuery_HihiDatabase.getAllC(map,sessionFactory.openSession());
//	}
	
	//得到新訂單狀態
	public void getNewOrderStat(Integer orderStat, Integer pdtOrderId) {
		repository.updateTheOrderStat(orderStat, pdtOrderId);
	}

}
