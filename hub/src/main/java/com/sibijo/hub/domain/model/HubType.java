package com.sibijo.hub.domain.model;

import com.sibijo.common.exception.CustomException;
import com.sibijo.hub.exception.domain.HubDomainExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
/**
 * 허브 타입 ENUM 클래스
 * CENTRAL, LOCAL 허브로 구성
 */
public enum HubType {
    CENTRAL("중앙허브"),
    LOCAL("지역허브");

    private String hubTypeName;

    /**
     * 허브 타입 검증
     * @param hubTypeName 허브 타입 이름
     * @return 해당하는 허브 타입 객체
     * @throws HubDomainExceptionCode 허브 타입의 이름이 유효하지 않는 경우
     */
    public static HubType fromHubTypeName(String hubTypeName) {
        for (HubType hubType : HubType.values()) {
            if (hubType.hubTypeName.equals(hubTypeName)) {
                return hubType;
            }
        }
        throw new CustomException(HubDomainExceptionCode.HUB_TYPE_NOT_FOUND);
    }
}
