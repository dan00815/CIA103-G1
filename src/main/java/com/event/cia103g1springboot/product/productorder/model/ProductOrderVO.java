package com.event.cia103g1springboot.product.productorder.model;

import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.format.annotation.DateTimeFormat;

import com.event.cia103g1springboot.member.mem.model.MemVO;
import com.event.cia103g1springboot.product.pdtorderitem.model.ProductOrderItemVO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of = "pdtOrderId")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "PRODUCTORDER")
public class ProductOrderVO implements java.io.Serializable {
	private static final long serialVersionUID = 1L;



	@Id
	@Column(name = "PDTORDERID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer pdtOrderId;

	@Column(name = "ORDERDATE", insertable = false, updatable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Timestamp orderDate;

	@Column(name = "ORDERAMT")
	private Integer orderAmt;

	@Column(name = "ORDERSTAT")
	private Integer orderStat;

	@Column(name = "PAYMETHOD")
	@NotNull(message = "請選擇支付方式")
	private Integer payMethod;

	@Column(name = "DELMETHOD")
	@NotNull(message = "請選擇配送方式")
	private Integer delMethod;

	@Column(name = "RECNAME")
	@Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,10}$", message = "姓名只能是中、英文字母、數字和_ , 且長度必需在2到10之間")
	private String recName;

	@Column(name = "RECADDR")
	@Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9\\s\\-#,.，。]+$", message = "地址只能是中、英文字母、數字和短橫線（-）、井號（#）、逗號（,）、句號（.）等")
	private String recAddr;

	@Column(name = "RECTEL")
	@Pattern(regexp = "^(0[2-9]{1})(\\d{2,4})(\\d{4})$", message = "電話請輸入正確的電話號碼格式(無特殊符號)")
	private String recTel;

	@OneToMany(mappedBy = "productOrderVO", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<ProductOrderItemVO>productOrderItemVO;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MEMID" ,referencedColumnName = "memid")
	private MemVO memVO;

}
