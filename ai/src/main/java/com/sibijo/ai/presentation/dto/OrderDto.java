package com.sibijo.ai.presentation.dto;

import lombok.Data;
import java.util.List;

/**
 * AI 메시지 생성용 주문 정보 DTO
 */
@Data
public class OrderDto {
    private String orderId;
    private String ordererName;
    private String ordererEmail;
    private String productInfo;
    private String requestInfo;
    private String dispatchCenter;
    private List<String> transitCenters;
    private String destination;
    private String deliveryPersonName;
    private String deliveryPersonEmail;
}
