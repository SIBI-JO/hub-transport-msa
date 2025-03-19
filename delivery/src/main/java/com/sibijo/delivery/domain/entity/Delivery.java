package com.sibijo.delivery.domain.entity;

import com.sibijo.common.entity.BaseEntity;
import com.sibijo.delivery.presentation.dto.DeliveryRequestDto;
import com.sibijo.delivery.presentation.dto.OrderToDeliveryRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Table(catalog = "sibijo", name = "p_delivery")
@SQLRestriction("is_deleted = false")
@SQLDelete(sql = "UPDATE p_hub SET is_deleted = true WHERE hub_id = ?")
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private UUID deliveryId;

    @Column(nullable = false)
    private UUID startHubId;

    @Column(nullable = false)
    private UUID endHubId;

    @Column(nullable = false)
    private String deliveryAddress;

    @Column(nullable = false)
    private String receiver;

    @Column(nullable = false)
    private String receiverSlackId;


    private UUID deliveryManagerId;



    public static Delivery createDelivery(DeliveryRequestDto requestDto) {
        return Delivery.builder()
                .startHubId(requestDto.getStartHubId())
                .endHubId(requestDto.getEndHubId())
                .deliveryAddress(requestDto.getDeliveryAddress())
                .receiver(requestDto.getReceiver())
                .receiverSlackId(requestDto.getReceiverSlackId())
                .build();
    }

    public void updateDeliveryManager(UUID deliveryManagerId) {
        this.deliveryManagerId = deliveryManagerId;
    }


}
