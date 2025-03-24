package com.sibijo.delivery.domain.service;

import com.sibijo.common.exception.CustomException;
import com.sibijo.common.exception.codes.CommonExceptionCode;
import com.sibijo.common.utils.Auth.JwtUtil;
import com.sibijo.delivery.application.dto.DeliveryResponseDto;
import com.sibijo.delivery.domain.entity.Delivery;
import com.sibijo.delivery.domain.repository.DeliveryRepository;
import com.sibijo.delivery.presentation.dto.DeliveryRequestDto;
import com.sibijo.delivery.presentation.dto.DeliveryUpdateRequestDto;
import com.sibijo.delivery.presentation.dto.OrderToDeliveryRequestDto;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j(topic = "배송 Service")
@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final JwtUtil jwtUtil;
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
    @Transactional(readOnly = true)
    public Page<Delivery> getDeliveries(String token, Pageable pageable) {

        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserID(token);
        UUID hubId = jwtUtil.extractHubIdForOrder(token);
        UUID companyId = jwtUtil.extractCompanyIdForOrder(token);

        Page<Delivery> deliveryList = switch (role) {
            case "MASTER" -> deliveryRepository.findAllByDeletedAtIsNull(pageable);
            case "HUB" -> deliveryRepository.findDeliveriesByHubId(hubId, pageable);
            case "DELIVERY" -> deliveryRepository.findByDeliveryManagerIdAndDeletedAtIsNull(userId, pageable);
            case "COMPANY" -> deliveryRepository.findByRecipientsIdAndDeletedAtIsNull(companyId, pageable);
            default -> throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        };

        return deliveryList;
    }

    /**
     *  배송 상세 조회
     *  권한 : Hub_Manager -> 자신의 허브만    //   Delivery_Manager -> 자신의 배송만
     *                                      // Company_Manager -> 자신의 업체만
     */
    @Transactional(readOnly = true)
    public DeliveryResponseDto getDeliveryDetails(UUID deliveryId, String token) {
        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserID(token);
        UUID hubId = jwtUtil.extractHubIdForOrder(token);
        UUID companyId = jwtUtil.extractCompanyIdForOrder(token);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "배송이 삭제 되었거나 없습니다."));
        System.out.println("deliver: " + delivery);
        switch (role) {
            case "HUB":
                if (!hubId.equals(delivery.getStartHubId()) && !hubId.equals(delivery.getEndHubId())) {
                    // 허브 담당자인데 공급업체나 수령업체의 허브 담당자가 아닐 때
                    throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
                }
                break;
            case "DELIVERY":
                if (!Objects.equals(userId, delivery.getDeliveryManagerId())) {
                    // 배송담당자 -> 만약 업체 배송 담당자라면 -> 본인이 담당 업체 배송 담당자인지 확인
                    throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
                }
                break;
            case "COMPANY":
                if (!companyId.equals(delivery.getRecipientsId())) {
                    // 업체 담당자인데 수령업체가 자신의 업체가 아닐 때
                    throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
                }
        }
        return new DeliveryResponseDto(delivery);
    }


    /**
     *  배송 수정
     *  권한 : Hub_Manager -> 자신의 허브만    //   Delivery_Manager -> 자신의 배송만
     *  업체 배송 담당자 수정을 어떻게 처리해야하는가
     *  -> 배송을 만들 때 유저 서버로 업체ID를 보내서 업체 관계자 정보 받아서 넣어야 하나?
     */
    @Transactional
    public DeliveryResponseDto updateDelivery(UUID deliveryId, DeliveryUpdateRequestDto requestDto, String token) {
        // JWT에서 Role 추출
        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserID(token);
        UUID hubId = jwtUtil.extractHubIdForOrder(token);

        if (role.equals("COMPANY")) {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "배송이 삭제 되었거나 없습니다."));

        // 허브 담당자인데 공급업체나 수령업체의 허브 담당자가 아닐 때
        switch (role) {
            case "HUB":
                if (!hubId.equals(delivery.getStartHubId()) && !hubId.equals(delivery.getEndHubId())) {
                    // 허브 담당자인데 공급업체나 수령업체의 허브 담당자가 아닐 때
                    throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
                }
                break;
            case "DELIVERY":
                if (!Objects.equals(userId, delivery.getDeliveryManagerId())) {
                    // 배송담당자 -> 만약 업체 배송 담당자라면 -> 본인이 담당 업체 배송 담당자인지 확인
                    throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
                }
        }

        delivery.updateDelivery(requestDto);
        Delivery updateDelivery = deliveryRepository.save(delivery);
        return new DeliveryResponseDto(updateDelivery);
    }



    /**
     *  배송 취소
     *  권한 : Hub_Manager -> 자신의 허브만
     */
    @Transactional
    public DeliveryResponseDto deleteDelivery(UUID deliveryId, String token) {

        String role = jwtUtil.extractRole(token);
        UUID hubId = jwtUtil.extractHubIdForOrder(token);

        if (!role.equals("HUB") && !role.equals("MASTER")) {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "배송이 삭제 되었거나 없습니다."));

        // 허브 담당자인데 공급업체나 수령업체의 허브 담당자가 아닐 때
        if (role.equals("HUB") && (!hubId.equals(delivery.getStartHubId()) && !hubId.equals(delivery.getEndHubId()))) {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }

        deliveryRepository.deleteById(deliveryId);
        return new DeliveryResponseDto(delivery);
    }

    /**
     *  배송 경로 수정 용 배송 조회
     */
    @Transactional(readOnly = true)
    public Delivery getDeliveryDetailsForUpdate(UUID deliveryId) {

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "배송이 삭제 되었거나 없습니다."));

        return delivery;
    }

}
