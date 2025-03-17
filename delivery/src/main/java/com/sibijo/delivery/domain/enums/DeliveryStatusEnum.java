package com.sibijo.delivery.domain.enums;

import lombok.Getter;

@Getter
public enum DeliveryStatusEnum {

    HUB_WAITING,  // 허브 이동 대기

    HUB_MOVING,   // 허브 이동 중

    HUB_ARRIVED,   // 목적지 허브 도착

    COMPANY_DELIVERING,   // 업체 배송 중

    COMPLETED;    // 배송 완료


}
