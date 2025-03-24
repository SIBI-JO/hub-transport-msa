package com.sibijo.delivery.infrastructure.client;

import com.sibijo.delivery.infrastructure.client.company.CompanyClient;
import com.sibijo.delivery.infrastructure.client.company.CompanyResponseDto;
import com.sibijo.delivery.infrastructure.client.hub.HubClient;
import com.sibijo.delivery.infrastructure.client.hub.HubResponseDto;
import com.sibijo.delivery.infrastructure.client.order.OrderClient;
import com.sibijo.delivery.infrastructure.client.order.OrderCreateUpdateRequestDto;
import com.sibijo.delivery.infrastructure.client.product.HubStockResponseDto;
import com.sibijo.delivery.infrastructure.client.product.ProductClient;
import com.sibijo.delivery.infrastructure.client.product.UpdateStockRequestDto;
import com.sibijo.delivery.infrastructure.client.user.UserClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryCircuitBreaker {

    private final CompanyClient companyClient;
    private final HubClient hubClient;
    private final OrderClient orderClient;
    private final ProductClient productClient;
    private final UserClient userClient;

    @CircuitBreaker(name = "getCompany", fallbackMethod = "getCompanyOrderInfoFallback")
    public CompanyResponseDto getCompanyOrderInfo(UUID companyId) {
        return companyClient.getCompanyOrderInfo(companyId).getData();
    }

    public CompanyResponseDto getCompanyOrderInfoFallback(UUID companyId, Throwable t) {
        log.error("Company 정보 조회 실패 (Fallback 처리): {}", t.getMessage());
        return new CompanyResponseDto();
    }


    @CircuitBreaker(name = "getHubRoute", fallbackMethod = "getHubRouteForOrderFallback")
    public HubResponseDto getHubRouteForOrder(UUID startHubId, UUID endHubId) {
        return hubClient.getHubRouteForOrder(startHubId, endHubId);
    }

    public HubResponseDto getHubRouteForOrderFallback(UUID startHubId, UUID endHubId, Throwable t) {
        log.error("허브 경로 조회 실패 (Fallback 처리): {}", t.getMessage());
        return new HubResponseDto();
    }

    @CircuitBreaker(name = "getDeliveryAgent", fallbackMethod = "getDeliveryAgentFallback")
    public Long getDeliveryAgent() {
        return userClient.getDeliveryAgent().getData();
    }

    public Long getDeliveryAgentFallback(Throwable t) {
        log.error("배송담당자 ID 조회 실패 (Fallback 처리): {}", t.getMessage());
        return null;
    }

    // 상품 재고 수량 변경  서킷 브레이커
    @CircuitBreaker(name = "updateProduct", fallbackMethod = "updateStockFallback")
    public HubStockResponseDto updateStock(UUID productId, UpdateStockRequestDto requestDto) {
        return productClient.updateStock(productId, requestDto).getData();
    }

    public HubStockResponseDto updateStockFallback(UUID productId, UpdateStockRequestDto requestDto, Throwable t) {
        log.error("상품 재고 복구 실패 (Fallback 처리): {}", t.getMessage());
        log.info("  주문 후 남았어야 하는 재고량 :  "+requestDto.getNewAmount()+" 개");
        return new HubStockResponseDto();
    }

    @CircuitBreaker(name = "deleteOrder", fallbackMethod = "deleteOrderFallback")
    public void deleteOrderInternal(UUID orderId) {
        orderClient.deleteOrderInternal(orderId);
        log.info("임시 주문 삭제 완료");
    }

    public void deleteOrderFallback(UUID orderId, Throwable t) {
        log.error("주문 삭제 처리 실패 (Fallback 처리): {}", t.getMessage());
    }

    @CircuitBreaker(name = "updateOrderWithDelivery", fallbackMethod = "updateOrderWithDeliveryFallback")
    public void updateOrderWithDelivery(UUID orderId, OrderCreateUpdateRequestDto requestDto) {
        orderClient.updateOrderWithDelivery(orderId, requestDto);
        log.info("주문에 배송 정보 전달 완료");
    }

    public void updateOrderWithDeliveryFallback(UUID orderId, OrderCreateUpdateRequestDto requestDto, Throwable t) {
        log.error("주문에 배송 정보 업데이트 실패 (Fallback 처리): {}", t.getMessage());
    }

}
