package com.sibijo.delivery.domain.entity;

import com.sibijo.common.entity.BaseEntity;
import com.sibijo.delivery.application.dto.DeliveryResponseDto;
import com.sibijo.delivery.domain.enums.DeliveryStatusEnum;
import com.sibijo.delivery.presentation.dto.DeliveryRouteRequestDto;
import com.sibijo.delivery.presentation.dto.DeliveryRouteUpdateRequestDto;
import com.sibijo.delivery.presentation.dto.DeliveryUpdateRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "p_delivery_route")
@SQLRestriction("is_deleted = false")
@SQLDelete(sql = "UPDATE p_delivery_route SET is_deleted = true WHERE route_id = ?")
public class DeliveryRoute extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private UUID routeId; // 배송 경로 ID

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", nullable = false, unique = true)
    private Delivery delivery; // 1:1 관계 (배송 ID)

    private Integer sequence;   // 배송 경로 상 허브의 순번 -> 배송담당자 배정 순번

    @Column(name = "route_sequence")
    private String routeSequence; //허브 총 경로 시퀀스

    @Column(nullable = false)
    private UUID startHubId; // 출발 허브 ID

    @Column(nullable = false)
    private UUID endHubId; // 도착 허브 ID

    @Column(nullable = false)
    private UUID recipientsId;

    @Column(nullable = false)
    private String expectedDistance; // 예상 거리

    @Column(nullable = false)
    private String expectedDuration; // 예상 소요 시간

    private String realDistance; // 실제 거리
    private String realDuration; // 실제 소요 시간

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatusEnum deliveryStatus;

    private Long deliveryManagerId; // 배송 담당자 ID


    public static DeliveryRoute createRoute(
            Delivery delivery, DeliveryRouteRequestDto requestDto) {
        return DeliveryRoute.builder()
                .delivery(delivery)
                .sequence(requestDto.getSequence())
                .routeSequence(requestDto.getRouteSequence())
                .startHubId(requestDto.getStartHubId())
                .endHubId(requestDto.getEndHubId())
                .recipientsId(requestDto.getRecipientsId())
                .expectedDistance(requestDto.getExpectedDistance())
                .expectedDuration(requestDto.getExpectedTime())
                .deliveryStatus(DeliveryStatusEnum.HUB_WAITING) // 기본값 : 허브 이동 대기 중
                .deliveryManagerId(requestDto.getDeliveryManagerId())
                .build();
    }

    public void updateRealTimeAndDistance(String realDistance, String realDuration) {
        this.realDistance = realDistance;
        this.realDuration = realDuration;
    }

    public void updateDeliveryManager(Long deliveryManagerId) {
        this.deliveryManagerId = deliveryManagerId;
    }

    public void updateDeliveryStatus(DeliveryStatusEnum status) {
        this.deliveryStatus = status;
    }

    public void updateRoute(DeliveryRouteUpdateRequestDto requestDto, Delivery delivery) {
        this.delivery = delivery;
        this.sequence = requestDto.getSequence();
        this.startHubId = requestDto.getStartHubId();
        this.endHubId = requestDto.getEndHubId();
        this.recipientsId = requestDto.getRecipientsId();
        this.expectedDistance = requestDto.getExpectedDistance();
        this.expectedDuration = requestDto.getExpectedDuration();
        this.realDistance = requestDto.getRealDistance();
        this.realDuration = requestDto.getRealDuration();
        this.deliveryStatus = requestDto.getDeliveryStatus();
        this.deliveryManagerId = requestDto.getDeliveryManagerId();
    }

}
