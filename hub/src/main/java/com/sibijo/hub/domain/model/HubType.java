package com.sibijo.hub.domain.model;

import com.sibijo.common.exception.CustomException;
import com.sibijo.hub.exception.domain.HubDomainExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum HubType {
    CENTRAL("중앙 허브"),
    LOCAL("지역 허브");

    private String hubTypeName;

    public static HubType fromHubTypeName(String hubTypeName) {
        for (HubType hubType : HubType.values()) {
            if (hubType.hubTypeName.equals(hubTypeName)) {
                return hubType;
            }
        }
        throw new CustomException(HubDomainExceptionCode.HUB_TYPE_NOT_FOUND);
    }
}
