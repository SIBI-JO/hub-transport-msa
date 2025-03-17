package com.sibijo.delivery.domain.entity;

import com.sibijo.delivery.domain.enums.DeliveryStatusEnum;
import com.sibijo.delivery.presentation.dto.DeliveryRouteRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(catalog = "sibijo", name = "p_delivery_route")
public class DeliveryRoute {

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private UUID routeId; // 배송 경로 ID

    @OneToOne
    @JoinColumn(name = "delivery_id", nullable = false, unique = true)
    private Delivery delivery; // 1:1 관계 (배송 ID)

    private Long sequence;   // 배송 경로 상 허브의 순번

    @Column(nullable = false)
    private UUID startHubId; // 출발 허브 ID

    @Column(nullable = false)
    private UUID endHubId; // 도착 허브 ID

    @Column(nullable = false)
    private String expectedDistance; // 예상 거리

    @Column(nullable = false)
    private String expectedTime; // 예상 소요 시간

    private String realDistance; // 실제 거리
    private String realTime; // 실제 소요 시간

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatusEnum status;

    private UUID deliveryManagerId; // 배송 담당자 ID

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

    public static DeliveryRoute createRoute(
            Delivery delivery, DeliveryRouteRequestDto requestDto) {
        return DeliveryRoute.builder()
                .delivery(delivery)
                .sequence(requestDto.getSequence())
                .startHubId(requestDto.getStartHubId())
                .endHubId(requestDto.getEndHubId())
                .expectedDistance(requestDto.getExpectedDistance())
                .expectedTime(requestDto.getExpectedTime())
                .status(DeliveryStatusEnum.HUB_WAITING) // 기본값 : 허브 이동 대기 중
                .build();
    }

    public void updateRealTimeAndDistance(String realDistance, String realTime) {
        this.realDistance = realDistance;
        this.realTime = realTime;
        this.updatedAt = LocalDateTime.now();
    }

    public void deleteRoute(String deletedBy) {
        this.deletedBy = deletedBy;
        this.deletedAt = LocalDateTime.now();
    }
}
