package com.event.cia103g1springboot.room.roomtype.model;

import com.event.cia103g1springboot.plan.planroom.model.PlanRoom;
import com.event.cia103g1springboot.room.roomorder.model.ROVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@EqualsAndHashCode(of = "roomTypeId")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="roomType")
public class RTVO implements java.io.Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="roomTypeId")
    private Integer roomTypeId;

    @Column(name="roomTypeName")
    @NotEmpty(message="房型名稱: 請勿空白")
    @Size(min=2,max=20,message="房型名稱: 長度必需在{min}到{max}之間")
    private String roomTypeName;

    @Column(name="roomTypeDesc")
    @NotEmpty(message="房型描述: 請勿空白")
    @Size(min=2,max=500,message="房型描述: 長度必需在{min}到{max}之間")
    private String roomTypeDesc;

    @Column(name="roomQty")
    @NotNull(message="房型數量: 請勿空白")
    @Min(value = 1, message = "房型數量:不可小於{value}")
    private Integer roomQty;

    @Column(name="roomPrice")
    @NotNull(message="房型售價: 請勿空白")
    @Min(value = 0, message = "房型售價:不可小於{value}")
    private Integer roomPrice;

    @Column(name="occupancy")
    @NotNull(message="容納人數: 請勿空白")
    @Min(value = 1, message = "容納人數:不可小於{value}")
    private Integer occupancy;

    @Transient
	private byte[] rImg;
    
    public byte[] getrImg() {
		return rImg;
	}

	public void setrImg(byte[] rImg) {
		this.rImg = rImg;
	}

    @OneToMany(mappedBy = "rtvo",cascade = CascadeType.ALL)
    private Set<PlanRoom> planRoom;

    @OneToMany(mappedBy = "rtVO")
    private Set<ROVO> roomOrders;

    @Override
    public String toString() {
        return "RTVO{" +
                "occupancy=" + occupancy +
                ", roomPrice=" + roomPrice +
                ", roomQty=" + roomQty +
                ", roomTypeDesc='" + roomTypeDesc + '\'' +
                ", roomTypeName='" + roomTypeName + '\'' +
                ", roomTypeId=" + roomTypeId +
                '}';
    }
}
