package com.sibijo.order.application.dto;

import com.sibijo.order.domain.entity.Order;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderResponseDto {

    private UUID orderId;
    private UUID supplierId;
    private UUID recipientsId ;
    private UUID productId;
    private UUID deliveryId;
    private Integer amount;
    private String request;

    public OrderResponseDto(Order order) {
        this.orderId = order.getOrderId();
        this.supplierId = order.getSupplierId();
        this.recipientsId = order.getRecipientsId();
        this.productId = order.getProductId();
        this.deliveryId = order.getDeliveryId();
        this.amount = order.getAmount();
        this.request = order.getRequest();
    }
}
