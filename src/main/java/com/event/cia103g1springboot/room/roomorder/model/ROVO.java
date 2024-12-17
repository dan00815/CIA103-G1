package com.event.cia103g1springboot.room.roomorder.model;

import com.event.cia103g1springboot.plan.planorder.model.PlanOrder;
import com.event.cia103g1springboot.room.roomtype.model.RTVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@EqualsAndHashCode(of = "roomOrderId")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="RoomOrder")
public class ROVO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="roomOrderId")
	public Integer roomOrderId;

	@Column(name = "roomTypeName", nullable = false)
	@NotEmpty(message = "房型名稱不能為空")
	private String roomTypeName;

	@Column(name = "roomPrice")
	@NotNull(message="訂房售價:請勿空白")
	@Min(value=0 , message="訂房售價:請勿小於{value}")
	public Integer roomPrice;

	@Column(name="orderQty")
	@NotNull(message="訂房數量:請勿空白")
	@Min(value=0 , message="訂房數量:不可小於{value}")
	public Integer orderQty;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "roomTypeId", referencedColumnName = "roomTypeId", nullable = false)
	private RTVO rtVO;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "planOrderId", referencedColumnName = "planOrderId")
	private PlanOrder planOrder;

	@Override
	public String toString() {
		return "ROVO{" +
				"orderQty=" + orderQty +
				", roomPrice=" + roomPrice +
				", roomOrderId=" + roomOrderId +
				'}';
	}
}




