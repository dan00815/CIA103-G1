package com.event.cia103g1springboot.plan.planroom.model;

import com.event.cia103g1springboot.plan.plan.model.Plan;
import com.event.cia103g1springboot.room.roomtype.model.RTVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import javax.persistence.*;


@EqualsAndHashCode(of = "roomTypeName")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "PlanRoom")
@IdClass(PlanRoomId.class)
public class PlanRoom {
    @Id
    @Column(name = "planId", nullable = false)
    private Integer planId;

    @Id
    @Column(name = "roomTypeId", nullable = false)
    private Integer roomTypeId;

    @Column(name = "roomTypeName", nullable = false, length = 60)
    private String roomTypeName;

    @Column(name = "roomPrice", nullable = false)
    private Integer roomPrice;

    @Column(name = "roomQty")
    private Integer roomQty;

    @Column(name = "reservedRoom", nullable = false, columnDefinition = "int default 0")
    private Integer reservedRoom ;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomTypeId", referencedColumnName = "roomTypeId", insertable = false, updatable = false)
    private RTVO rtvo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planid",referencedColumnName = "planid", insertable = false, updatable = false)
    private Plan plan;

    public PlanRoom(Integer planId, Integer roomTypeId, String roomTypeName, Integer roomQty, Integer roomPrice, Integer reservedRoom) {
        this.planId = planId;
        this.roomTypeId = roomTypeId;
        this.roomTypeName = roomTypeName;
        this.roomQty = roomQty;
        this.roomPrice = roomPrice;
        this.reservedRoom = reservedRoom;
    }


    @Override
    public String toString() {
        return "PlanRoom{" +
                "reservedRoom=" + reservedRoom +
                ", roomQty=" + roomQty +
                ", roomPrice=" + roomPrice +
                ", roomTypeName='" + roomTypeName + '\'' +
                ", roomTypeId=" + roomTypeId +
                ", planId=" + planId +
                '}';
    }
}
