package com.sibijo.delivery.domain.service;

import com.sibijo.delivery.application.dto.DeliveryResponseDto;
import com.sibijo.delivery.domain.entity.Delivery;
import com.sibijo.delivery.domain.repository.DeliveryRepository;
import com.sibijo.delivery.presentation.dto.DeliveryRequestDto;
import com.sibijo.delivery.presentation.dto.OrderToDeliveryRequestDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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


    /**
     *  배송 전체 조회
     *  권한 : Hub_Manager -> 자신의 허브만    //   Delivery_Manager -> 자신의 배송만
     *                                      // Company_Manager -> 자신의 업체만
     */




    /**
     *  배송 상세 조회
     *  권한 : Hub_Manager -> 자신의 허브만    //   Delivery_Manager -> 자신의 배송만
     *                                      // Company_Manager -> 자신의 업체만
     */
    @Transactional(readOnly = true)
    public DeliveryResponseDto getDeliveryDetails(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found or has been deleted"));
        return new DeliveryResponseDto(delivery);
    }


    /**
     *  배송 수정
     *  권한 : Hub_Manager -> 자신의 허브만    //   Delivery_Manager -> 자신의 배송만
     *  업체 배송 담당자 수정을 어떻게 처리해야하는가
     *  -> 배송을 만들 때 유저 서버로 업체ID를 보내서 업체 관계자 정보 받아서 넣어야 하나?
     */




    /**
     *  배송 취소
     *  권한 : Hub_Manager -> 자신의 허브만
     */

}
