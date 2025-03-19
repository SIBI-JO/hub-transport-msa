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

    /**
     *  배송 경로 생성
     *  권한 : ALL
     */
    @Transactional
    public DeliveryRoute createDeliveryRoute(DeliveryRouteRequestDto requestDto, Delivery delivery) {

        DeliveryRoute deliveryRoute = DeliveryRoute.createRoute(delivery, requestDto);
        routeRepository.save(deliveryRoute);

        return deliveryRoute;
    }

    /**
     *  배송 경로 전체 조회
     *  권한 : Hub_Manager -> 자신의 허브만    //   Delivery_Manager -> 자신의 배송만
     *                                      // Company_Manager -> 자신의 업체만
     */




    /**
     *  배송 경로 상세 조회
     *  권한 : Hub_Manager -> 자신의 허브만    // Delivery_Manager -> 자신의 배송만
     *                                      // Company_Manager -> 자신의 업체만
     */


    /**
     *  배송 경로 수정
     *  권한 : Hub_Manager -> 자신의 허브만    //   Delivery_Manager -> 자신의 배송만
     *  실제 거리 / 실제 소요 시간만 수정 가능? 아니면 다른 부분도 수정 가눙?
     */


    /**
     *  배송 경로 삭제
     *  권한 : Hub_Manager -> 자신의 허브만
     */

}
