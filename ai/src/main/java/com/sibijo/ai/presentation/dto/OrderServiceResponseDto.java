package com.sibijo.ai.presentation.dto;

import lombok.Data;

/**
 * Order 마이크로서비스의 주문 상세조회 응답 중 "data" 필드에 해당하는 구조를 매핑하는 DTO
 * (추후 실제 응답에 맞춰서 수정)
 */
@Data
public class OrderServiceResponseDto {
    private Long orderId;
    private String supplierName;
    private String recipientsName;
    private String productName;
    private int amount;
    private String request;
}
