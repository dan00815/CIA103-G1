package com.event.cia103g1springboot.product.pdtorderitem.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.event.cia103g1springboot.product.pdtorderitem.model.ProductOrderItemService;
import com.event.cia103g1springboot.product.pdtorderitem.model.ProductOrderItemVO;

@RestController
@RequestMapping("/pdtorderitem")
public class ItemsController {
	
	@Autowired
	ProductOrderItemService pdtOrderItemSvc;

		@PostMapping("/export/pdf")
	    public ResponseEntity<byte[]> exportToPdf( 
	    		@RequestBody Map<String, String> request) throws IOException {
	        // 設置回應類型為 PDF
	    	
	        Integer pdtOrderId= Integer.valueOf(request.get("pdtOrderId"));
	        List<ProductOrderItemVO> list = pdtOrderItemSvc.getOrderItemsByPdtOrderId(pdtOrderId);
	        // 模擬訂單數據
//	        List<Order> orders = Arrays.asList(
//	                new Order(1, "John Doe", 100.0, "2024-12-01"),
//	                new Order(2, "Jane Smith", 150.5, "2024-12-02"),
//	                new Order(3, "Alice Brown", 200.0, "2024-12-03")
//	        );
	        

	        // 創建 PDF 文件
	        try (PDDocument document = new PDDocument()) {
	            PDPage page = new PDPage();
	            document.addPage(page);

	            // 建立內容流
	            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
	            	// 使用 NotoSansTC 變量字體（.ttf 文件）
	                File fontFile = new File("src/main/resources/static/fonts/Noto_Sans_TC/NotoSansTC-VariableFont_wght.ttf"); // 請替換為字體文件的實際路徑
	                PDType0Font font = PDType0Font.load(document, fontFile);
	                
	                // 設置字體和起始位置
	                contentStream.setFont(font, 16);
	                contentStream.beginText();
	                contentStream.setLeading(20f);
	                contentStream.newLineAtOffset(50, 750);

	                // 添加標題
	                contentStream.showText("訂單明細");
	                contentStream.newLine();
	                contentStream.newLine();
	                contentStream.setFont(font, 12);
	                String pdtOrderIdStr  =  request.get("pdtOrderId").toString();
	                contentStream.showText("訂單編號："+ pdtOrderIdStr );
	                contentStream.newLine();
	                
	                // 定義初始位置和行高
//	                float startX = 50; // 起始 X 坐標
//	                float startY = 700; // 起始 Y 坐標
	                float lineHeight = 20; // 每行之間的高度
	                
	             // 定義列寬（以頁面上的 X 坐標位置為準）
	                float column1X = 50;  // 商品編號起始位置
	                float column2X = 150; // 商品名稱起始位置
	                float column3X = 250; // 商品價格起始位置
	                float column4X = 350; // 數量起始位置

	                // 添加表格頭
	                contentStream.newLineAtOffset(0, -lineHeight); // 初始位置
	                contentStream.showText("商品編號");
	                contentStream.newLineAtOffset(column2X - column1X, 0); // 移動到下一列
	                contentStream.showText("商品名稱");
	                contentStream.newLineAtOffset(column3X - column2X, 0); // 移動到下一列
	                contentStream.showText("商品價格");
	                contentStream.newLineAtOffset(column4X - column3X, 0); // 移動到下一列
	                contentStream.showText("數量");
	                
	             // 顯示分隔線
	                contentStream.newLineAtOffset(column1X - column4X, -lineHeight);
	                contentStream.showText("-----------------------------------------------------------------------------------");

	                // 遍歷訂單項並輸出內容
	                for (ProductOrderItemVO item : list) {
	                    contentStream.newLineAtOffset(0, -lineHeight); // 移動到下一行

	                    // 使用 String.format 保證每列長度一致，這裡我們強制每個欄位為固定長度
	                    String row = String.format("%-30s%-30s%-30s%-30s",
	                            item.getPdtId(),
	                            item.getPdtName(),
	                            item.getPdtPrice(),
	                            item.getOrderQty());

	                    // 顯示此行
	                    contentStream.newLineAtOffset(0, 0); // 回到該行的起始位置
	                    contentStream.showText(row);
	                }

	                contentStream.endText();
	                contentStream.close();
	                
	                
	                

//	             // 添加表格頭
//	                contentStream.showText("商品編號                              商品名稱                              商品價格                              數量");
//	                contentStream.newLineAtOffset(0, -lineHeight);
//	                contentStream.showText("-----------------------------------------------------------------------");
//
//	                
//	                // 遍歷訂單項並輸出內容
//	                for (ProductOrderItemVO item : list) {
//	                    contentStream.newLineAtOffset(0, -lineHeight); // 移動到下一行
//	                    String row = String.format("%-15s%-30s%-20s%-10s",
//	                            item.getPdtId(),
//	                            item.getPdtName(),
//	                            item.getPdtPrice(),
//	                            item.getOrderQty());
//	                    contentStream.showText(row);
//	                }
//
//	                contentStream.endText();
//	                
//	                contentStream.close();
	                
	             // 將 PDF 文件寫入 ByteArrayOutputStream
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                document.save(out);

	                // 設置 HTTP 回應頭
	                HttpHeaders headers = new HttpHeaders();
	                headers.setContentType(MediaType.APPLICATION_PDF);
	                headers.setContentDispositionFormData("attachment", "order-" + pdtOrderId + ".pdf");

	                return ResponseEntity.ok()
	                        .headers(headers)
	                        .body(out.toByteArray());
	            } catch (Exception e) {
	                e.printStackTrace();
	                return ResponseEntity.status(500).body(null);
	            }

	        }
	    }

//	    // 模擬 Order 資料類別
//	    static class Order {
//	        private int id;
//	        private String customerName;
//	        private double totalAmount;
//	        private String orderDate;
//
//	        public Order(int id, String customerName, double totalAmount, String orderDate) {
//	            this.id = id;
//	            this.customerName = customerName;
//	            this.totalAmount = totalAmount;
//	            this.orderDate = orderDate;
//	        }
//
//	        public int getId() {
//	            return id;
//	        }
//
//	        public String getCustomerName() {
//	            return customerName;
//	        }
//
//	        public double getTotalAmount() {
//	            return totalAmount;
//	        }
//
//	        public String getOrderDate() {
//	            return orderDate;
//	        }
//	    }
}
