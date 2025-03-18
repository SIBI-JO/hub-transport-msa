package com.sibijo.delivery.domain.service;

import com.sibijo.delivery.domain.entity.Delivery;
import com.sibijo.delivery.domain.entity.DeliveryRoute;
import com.sibijo.delivery.domain.repository.DeliveryRouteRepository;
import com.sibijo.delivery.presentation.dto.DeliveryRouteRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "배송경로 Service")
@Service
@RequiredArgsConstructor
public class DeliveryRouteService {

    private final DeliveryRouteRepository routeRepository;

    @Transactional
    public DeliveryRoute createDeliveryRoute(DeliveryRouteRequestDto requestDto, Delivery delivery) {

        // 2. 배송 경로 생성
        DeliveryRoute deliveryRoute = DeliveryRoute.createRoute(delivery, requestDto);
        routeRepository.save(deliveryRoute);

        return deliveryRoute;
    }

}
