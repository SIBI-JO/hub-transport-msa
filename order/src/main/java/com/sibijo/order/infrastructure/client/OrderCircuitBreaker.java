package com.sibijo.order.infrastructure.client;

import com.sibijo.order.domain.entity.Order;
import com.sibijo.order.domain.repository.OrderRepository;
import com.sibijo.order.infrastructure.client.Delivery.DeliveryClient;
import com.sibijo.order.infrastructure.client.Delivery.DeliveryRequestDto;
import com.sibijo.order.infrastructure.client.Product.HubStockResponseDto;
import com.sibijo.order.infrastructure.client.Product.ProductClient;
import com.sibijo.order.infrastructure.client.Product.UpdateStockRequestDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCircuitBreaker {

    private final ProductClient productClient;
    private final DeliveryClient deliveryClient;


    // 상품 조회 서킷 브레이커
    @CircuitBreaker(name = "productClient", fallbackMethod = "getProductOrderInfoFallback")
    public Long getProductOrderInfo(UUID productId) {
        return productClient.getProductOrderInfo(productId).getData().getAmount();
    }

    public Long getProductOrderInfoFallback(UUID productId, Throwable t) {
        log.error("Product Feign Client 호출 실패 (Fallback 처리): {}", t.getMessage());
        return null;
    }

    // 상품 재고 수량 변경  서킷 브레이커
    @CircuitBreaker(name = "productClient", fallbackMethod = "updateStockFallback")
    public HubStockResponseDto updateStock(UUID productId, UpdateStockRequestDto requestDto) {
        return productClient.updateStock(productId, requestDto).getData();
    }

    public void updateStockFallback(UUID productId, UpdateStockRequestDto requestDto, Throwable t) {
        log.error("상품 재고 차감 실패 (Fallback 처리): {}", t.getMessage());
        log.info("  주문 후 남았어야 하는 재고량 :  "+requestDto.getNewAmount()+" 개");
    }


    // 배송 생성  서킷 브레이커
    @CircuitBreaker(name = "deliveryClient", fallbackMethod = "createDeliveryFallback")
    public void createDelivery(DeliveryRequestDto requestDto) {
        deliveryClient.createDelivery(requestDto);
    }

    public void createDeliveryFallback(DeliveryRequestDto requestDto, Throwable t) {
        log.error("Delivery Feign Client 호출 실패 (Fallback 처리): {}", t.getMessage());
    }

}
