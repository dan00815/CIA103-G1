package com.event.cia103g1springboot.product.pdtorderitem.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("productOrderItemService")
public class ProductOrderItemService {

	@Autowired
	private ProductOrderItemRepository repository;

	public void addProductOrderItem(ProductOrderItemVO productOrderItemVO) {
		repository.save(productOrderItemVO);
	}
	
//	//pdtOrderId搜尋訂單
//	public List<ProductOrderItemVO> getOrderItemByPdtOrderId(Integer pdtOrderId){
//		return repository.findByPdtOrderId(pdtOrderId);
//	}
	
	public List<ProductOrderItemVO> getOrderItemsByPdtOrderId(Integer pdtOrderId) {
		return repository.findByPdtOrderId(pdtOrderId);
	}

	
}
