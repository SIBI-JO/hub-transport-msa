package com.sibijo.ai.infrastructure.client.order;

import lombok.Data;

import java.util.UUID;

/**
 * Order 마이크로서비스 /api/orders/{orderId} 조회 시, data 필드 매핑
 * (OrderResponseDto와 동일 구조)
 */
@Data
public class OrderServiceResponseDto {
    private UUID orderId;
    private UUID supplierId;
    private UUID recipientsId;
    private UUID productId;
    private UUID deliveryId;
    private Integer amount;
    private String request;
}
