package com.sibijo.order.domain.entity;


import com.sibijo.common.entity.BaseEntity;
import com.sibijo.order.domain.enums.OrderStatusEnum;
import com.sibijo.order.presentation.dto.OrderRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
@Table(catalog = "sibijo", name = "p_order")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private UUID supplierId;

    @Column(nullable = false)
    private UUID recipientsId;

    @Column(nullable = false)
    private UUID productId;

    private UUID deliveryId;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private String request;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatusEnum status;


    public static Order createOrder(OrderRequestDto requestDto) {
        return Order.builder()
                .supplierId(requestDto.getSupplierId())
                .recipientsId(requestDto.getRecipientsId())
                .productId(requestDto.getProductId())
                .amount(requestDto.getAmount())
                .request(requestDto.getRequest())
                .status(OrderStatusEnum.PENDING)
                .build();
    }

    // 주문 ID(Delivery ID)만 업데이트하는 메서드 추가
    public void updateDelivery(UUID deliveryId) {
        this.deliveryId = deliveryId;
    }

}
