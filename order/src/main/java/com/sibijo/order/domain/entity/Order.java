package com.sibijo.order.domain.entity;


import com.sibijo.common.entity.BaseEntity;
import com.sibijo.order.domain.enums.OrderStatusEnum;
import com.sibijo.order.presentation.dto.OrderCreateUpdateRequestDto;
import com.sibijo.order.presentation.dto.OrderRequestDto;
import com.sibijo.order.presentation.dto.OrderUpdateRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "p_order")
@SQLRestriction("is_deleted = false")
@SQLDelete(sql = "UPDATE p_order SET is_deleted = true WHERE order_id = ?")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private UUID supplierId;

    private UUID supplierHubId;

    @Column(nullable = false)
    private UUID recipientsId;

    private UUID recipientHubId;

    @Column(nullable = false)
    private UUID productId;

    private UUID deliveryId;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private String request;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatusEnum orderStatus;


    public static Order createOrder(OrderRequestDto requestDto) {
        return Order.builder()
                .supplierId(requestDto.getSupplierId())
                .recipientsId(requestDto.getRecipientsId())
                .productId(requestDto.getProductId())
                .amount(requestDto.getAmount())
                .request(requestDto.getRequest())
                .orderStatus(OrderStatusEnum.PENDING)
                .build();
    }

    // 미완성인 주문 배송 정보 업데이트
    public void updateDelivery(OrderCreateUpdateRequestDto requestDto) {
        this.deliveryId = requestDto.getDeliveryId();
        this.supplierHubId = requestDto.getSupplierHubId();
        this.recipientHubId = requestDto.getRecipientHubId();
        this.orderStatus = OrderStatusEnum.COMPLETED;
    }

    public void updateOrder(OrderUpdateRequestDto requestDto) {
        this.supplierId = requestDto.getSupplierId();
        this.recipientsId = requestDto.getRecipientsId();
        this.productId = requestDto.getProductId();
        this.amount = requestDto.getAmount();
        this.request = requestDto.getRequest();
    }

}
