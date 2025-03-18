package com.sibijo.delivery.application.service;

import com.sibijo.delivery.domain.entity.Delivery;
import com.sibijo.delivery.domain.entity.DeliveryRoute;
import com.sibijo.delivery.domain.repository.DeliveryRepository;
import com.sibijo.delivery.domain.repository.DeliveryRouteRepository;
import com.sibijo.delivery.presentation.dto.DeliveryCreateRequestDto;
import com.sibijo.delivery.presentation.dto.DeliveryRequestDto;
import com.sibijo.delivery.presentation.dto.DeliveryRouteRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "배송 Service")
@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryRouteRepository routeRepository;

    @Transactional
    public void createDelivery(DeliveryCreateRequestDto requestDto, String userId) {

        // 1. 배송 정보 생성 및 저장
        Delivery delivery = Delivery.createDelivery(requestDto.getDeliveryRequest());
        deliveryRepository.save(delivery);

        // 2. 배송 경로 생성
        DeliveryRoute deliveryRoute = DeliveryRoute.createRoute(delivery, requestDto.getRouteRequest());
        routeRepository.save(deliveryRoute);

    }
}
