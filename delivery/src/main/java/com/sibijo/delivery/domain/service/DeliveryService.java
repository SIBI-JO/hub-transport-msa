package com.sibijo.delivery.domain.service;

import com.sibijo.delivery.domain.entity.Delivery;
import com.sibijo.delivery.domain.repository.DeliveryRepository;
import com.sibijo.delivery.presentation.dto.DeliveryRequestDto;
import com.sibijo.delivery.presentation.dto.OrderToDeliveryRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "배송 Service")
@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    @Transactional
    public Delivery createDelivery(DeliveryRequestDto requestDto) {

        // 배송 정보 생성 및 저장
        Delivery delivery = Delivery.createDelivery(requestDto);
        deliveryRepository.save(delivery);

        return delivery;
    }

}
