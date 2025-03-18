package com.sibijo.delivery.domain.entity;

import com.sibijo.delivery.presentation.dto.DeliveryRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Table(catalog = "sibijo", name = "p_delivery")
public class Delivery {

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


    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private LocalDateTime deletedAt;
    private String deletedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }


    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


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
        this.updatedAt = LocalDateTime.now();
    }

    public void deleteDelivery(String deletedBy) {
        this.deletedBy = deletedBy;
        this.deletedAt = LocalDateTime.now();
    }

}
