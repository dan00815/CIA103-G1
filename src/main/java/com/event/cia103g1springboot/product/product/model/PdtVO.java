package com.event.cia103g1springboot.product.product.model;

import com.event.cia103g1springboot.product.pdtorderitem.model.ProductOrderItemVO;
import com.event.cia103g1springboot.product.producttype.model.PdtTypeVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@EqualsAndHashCode(of ="pdtId" )
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "product")
public class PdtVO implements java.io.Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="pdtid")
	private Integer pdtId;

	@Column(name="pdtstat")
	@NotNull(message="商品狀態: 請勿空白")
	@Min(value = 0, message = "發布狀態只能為上架或未上架")
	@Max(value = 2, message = "發布狀態只能為上架或未上架")
	private Byte pdtStat;

	@Column(name="pdtname")
	@NotEmpty(message="商品名稱: 請勿空白")
	@Size(min=2,max=20,message="商品名稱: 長度必需在{min}到{max}之間")
	private String pdtName;

	@Column(name="pdtdesc")
	@NotEmpty(message="商品描述:請勿空白")
	@Size(min=2,max=500,message="商品描述: 長度必需在{min}到{max}之間")
	private String pdtDesc;

	@Column(name="pdtprice")
	@NotNull(message="商品價格: 請勿空白")
	@Min(value = 0, message = "商品價格:不可小於0")
	private Integer pdtPrice;
	
	@Transient
	private byte[] pdtImg;

	@ManyToOne
	@JoinColumn(name="pdttypeid")
	private PdtTypeVO pdtTypeVO;

	@OneToMany(mappedBy ="pdtVO",cascade = CascadeType.ALL)
	private Set<ProductOrderItemVO> productOrderItemVO;

	@Override
	public String toString() {
		return "PdtVO{" +
				"pdtId=" + pdtId +
				", pdtStat=" + pdtStat +
				", pdtName='" + pdtName + '\'' +
				", pdtDesc='" + pdtDesc + '\'' +
				", pdtPrice=" + pdtPrice +
				'}';
	}
}