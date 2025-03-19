package com.sibijo.user.domain.model;

import com.sibijo.common.entity.BaseEntity;
import com.sibijo.user.domain.enums.DeliveryType;
import com.sibijo.user.domain.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "p_delivery_agent")
@SQLRestriction("is_deleted = false")
@SQLDelete(sql = "UPDATE p_delivery_agent SET is_deleted = true WHERE user_id = ?")
public class DeliveryAgent extends BaseEntity {

    @Id
    private Long id;

    @Column
    private String hubId;

    @Column
    @Enumerated(EnumType.STRING)
    private DeliveryType deliveryType;

    @Column
    private int deliveryOrder;

    @OneToOne
    @MapsId  // userId가 User 엔티티의 PK를 그대로 매핑
    @JoinColumn(name = "user_id")  // FK 설정
    private User user;

    @Version  // 낙관적 락 적용
    private Integer version;

    private DeliveryAgent(User user, String hubId, DeliveryType deliveryType, int deliveryOrder) {
        this.user = user;
        this.id = user.getId();  // User의 PK를 가져옴
        this.hubId = hubId;
        this.deliveryType = deliveryType;
        this.deliveryOrder = deliveryOrder;
    }

    public static DeliveryAgent of(User user, String hubId, DeliveryType deliveryType, int deliveryOrder) {
        return new DeliveryAgent(user, hubId, deliveryType, deliveryOrder);
    }

    public void update(String hubId, DeliveryType deliveryType, int deliveryOrder) {
        this.hubId = hubId;
        this.deliveryType = deliveryType;
        this.deliveryOrder = deliveryOrder;
    }

}
