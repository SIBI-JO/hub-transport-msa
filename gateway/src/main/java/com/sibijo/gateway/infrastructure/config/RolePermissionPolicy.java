package com.sibijo.gateway.infrastructure.config;

import java.util.Map;
import java.util.Set;

public class RolePermissionPolicy {

    // 역할별 HTTP 메서드 제한 설정
    // 사용자
    public static final Map<String, Set<String>> userRolePermissions = Map.of(
            "MASTER", Set.of("GET", "POST", "PATCH", "PUT", "DELETE"),
            "HUB", Set.of("GET"),
            "DELIVERY", Set.of("GET"),
            "COMPANY", Set.of("GET")
    );

    // 배송 담당자
    public static final Map<String, Set<String>> deliveryAgentRolePermissions = Map.of(
            "MASTER", Set.of("GET", "POST", "PATCH", "PUT", "DELETE"),
            "HUB", Set.of("GET", "POST", "PATCH", "PUT", "DELETE"),  // 담당 허브에서만 가능
            "DELIVERY", Set.of("GET"),  // 본인 정보 조회만 가능
            "COMPANY", Set.of()  // 업체 담당자는 접근 불가
    );

    //허브
    public static final Map<String, Set<String>> hubRolePermissions = Map.of(
            "MASTER", Set.of("GET", "POST", "PATCH", "PUT", "DELETE"),
            "HUB", Set.of("GET"),
            "DELIVERY", Set.of("GET"),
            "COMPANY", Set.of("GET")
    );

    //허브 이동 경로
    public static final Map<String, Set<String>> hubRouteRolePermissions = Map.of(
            "MASTER", Set.of("GET", "POST", "PATCH", "PUT", "DELETE"),
            "HUB", Set.of("GET"),
            "DELIVERY", Set.of("GET"),
            "COMPANY", Set.of("GET")
    );

    //업체
    public static final Map<String, Set<String>> companyRolePermissions = Map.of(
            "MASTER", Set.of("GET", "POST", "PATCH", "PUT", "DELETE"),
            "HUB", Set.of("GET", "POST", "PATCH", "PUT", "DELETE"),
            "DELIVERY", Set.of("GET"),
            "COMPANY", Set.of("GET", "PATCH", "PUT")
    );

    //상품
    public static final Map<String, Set<String>> productRolePermissions = Map.of(
            "MASTER", Set.of("GET", "POST", "PATCH", "PUT", "DELETE"),
            "HUB", Set.of("GET", "POST", "PATCH", "PUT", "DELETE"),
            "DELIVERY", Set.of("GET"),
            "COMPANY", Set.of("GET", "POST", "PATCH", "PUT")
    );

    //주문
    public static final Map<String, Set<String>> orderRolePermissions = Map.of(
            "MASTER", Set.of("GET", "POST", "PATCH", "PUT", "DELETE"),
            "HUB", Set.of("GET", "POST", "PATCH", "PUT", "DELETE"),
            "DELIVERY", Set.of("GET", "POST"),
            "COMPANY", Set.of("GET", "POST")
    );

    //배송
    public static final Map<String, Set<String>> deliveryRolePermissions = Map.of(
            "MASTER", Set.of("GET", "POST", "PATCH", "PUT", "DELETE"),
            "HUB", Set.of("GET", "PATCH", "PUT", "DELETE"),
            "DELIVERY", Set.of("GET", "PATCH", "PUT"),
            "COMPANY", Set.of("GET")
    );

    //슬랙 메세지
    public static final Map<String, Set<String>> slackRolePermissions = Map.of(
            "MASTER", Set.of("GET", "POST", "PATCH", "PUT", "DELETE"),
            "HUB", Set.of("POST"),
            "DELIVERY", Set.of("POST"),
            "COMPANY", Set.of("POST")
    );

}
