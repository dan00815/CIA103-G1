package com.event.cia103g1springboot.product.productorder.model;

import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.event.cia103g1springboot.member.mem.model.MemService;
import com.event.cia103g1springboot.member.mem.model.MemVO;
import com.event.cia103g1springboot.product.pdtorderitem.model.ProductOrderItemService;
import com.event.cia103g1springboot.product.pdtorderitem.model.ProductOrderItemVO;


//請自行引入打開
//import hibernate.util.CompositeQuery.HibernateUtil_CompositeQuery_HihiDatabase;


@Service("productOrderService")
public class ProductOrderService {
	
	@Autowired
	ProductOrderRepository repository;
	
	@Autowired
	JavaMailSender mailSender;
	
	@Autowired
	TemplateEngine templateEngine;

	@Autowired
	MemService memSvc;
	
	@Autowired
	ProductOrderItemService pdtItemSvc;
	
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

	
	
//	包裝mail所需內容資訊
	public Context pdtOrdContext(ProductOrderVO pdtOrd) {
		List<ProductOrderItemVO> pdtItems = pdtItemSvc.getOrderItemsByPdtOrderId(pdtOrd.getPdtOrderId());
		Context context = new Context();
		context.setVariable("memId", pdtOrd.getMemVO().getMemId());
		context.setVariable("pdtOrderId", pdtOrd.getPdtOrderId());
		context.setVariable("orderDate", pdtOrd.getOrderDate());
		context.setVariable("recName", pdtOrd.getRecName());
		context.setVariable("recAddr",pdtOrd.getRecAddr());
		context.setVariable("recTel", pdtOrd.getRecTel());
		context.setVariable("orderStat", pdtOrd.getOrderStat());
		context.setVariable("orderAmt", pdtOrd.getOrderAmt());
		context.setVariable("pdtItems", pdtItems);
		
		return context;
	}
	
//	訂單取消發送mail
	public void sendCancelPdtOrdMail(ProductOrderVO pdtOrd) throws MessagingException{
		
//		包裝會員及訂單明細
		MemVO mem = memSvc.getMem(pdtOrd.getMemVO().getMemId());
		Context context = pdtOrdContext(pdtOrd);
		String mailContext = templateEngine.process("back-end/pdtorder/pdtordcanclemail",context);

//		設定mail資訊
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message,false,"UTF-8");
		helper.setTo(mem.getEmail());
		helper.setSubject("[鄰星嗨嗨] 商城訂單取消通知");
		helper.setText(mailContext,true);
		
		mailSender.send(message);
	}
	
//	訂單成立發送mail
	public void sendSuccessPdtOrdMail(ProductOrderVO pdtOrd) throws MessagingException{
		MemVO mem = memSvc.getMem(pdtOrd.getMemVO().getMemId());
		Context context = pdtOrdContext(pdtOrd);
		String mailContext = templateEngine.process("back-end/pdtorder/pdtordsuccessemail",context);
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message,false,"UTF-8");
		helper.setTo(mem.getEmail());
		helper.setSubject("[鄰星嗨嗨] 商城訂單成立通知");
		helper.setText(mailContext,true);
		
		mailSender.send(message);
	}

//	訂單更新發送mail
	public void sendUpdatePdtOrdMail(ProductOrderVO pdtOrd) throws MessagingException{
		MemVO mem = memSvc.getMem(pdtOrd.getMemVO().getMemId());
		Context context = pdtOrdContext(pdtOrd);
		String mailContext = templateEngine.process("back-end/pdtorder/pdtordupdatemail",context);
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message,false,"UTF-8");
		helper.setTo(mem.getEmail());
		helper.setSubject("[鄰星嗨嗨] 商城訂單更新通知");
		helper.setText(mailContext,true);
		
		mailSender.send(message);
	}
}
