package com.sibijo.gateway.infrastructure.config;

import com.sibijo.gateway.infrastructure.filter.RoleAuthorizationFilter;
import java.util.Map;
import java.util.Set;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RolePermissionConfig {

    // 역할별 HTTP 메서드 제한 설정
    // 사용자
    private static final Map<String, Set<String>> userRolePermissions = Map.of(
            "MASTER", Set.of("GET", "POST", "PATCH", "PUT", "DELETE"),
            "HUB", Set.of("GET"),
            "DELIVERY", Set.of("GET"),
            "COMPANY", Set.of("GET")
    );

    // 배송 담당자
    private static final Map<String, Set<String>> deliveryAgentRolePermissions = Map.of(
            "MASTER", Set.of("GET", "POST", "PATCH", "PUT", "DELETE"),
            "HUB", Set.of("GET", "POST", "PATCH", "PUT", "DELETE"),  // 담당 허브에서만 가능
            "DELIVERY", Set.of("GET"),  // 본인 정보 조회만 가능
            "COMPANY", Set.of()  // 업체 담당자는 접근 불가
    );

    //허브
    private static final Map<String, Set<String>> hubRolePermissions = Map.of(
            "MASTER", Set.of("GET", "POST", "PATCH", "PUT", "DELETE"),
            "HUB", Set.of("GET"),
            "DELIVERY", Set.of("GET"),
            "COMPANY", Set.of("GET")
    );

    //허브 이동 경로
    private static final Map<String, Set<String>> hubRouteRolePermissions = Map.of(
            "MASTER", Set.of("GET", "POST", "PATCH", "PUT", "DELETE"),
            "HUB", Set.of("GET"),
            "DELIVERY", Set.of("GET"),
            "COMPANY", Set.of("GET")
    );

    //업체
    private static final Map<String, Set<String>> companyRolePermissions = Map.of(
            "MASTER", Set.of("GET", "POST", "PATCH", "PUT", "DELETE"),
            "HUB", Set.of("GET", "POST", "PATCH", "PUT", "DELETE"),
            "DELIVERY", Set.of("GET"),
            "COMPANY", Set.of("GET", "PATCH", "PUT")
    );

    //상품
    private static final Map<String, Set<String>> productRolePermissions = Map.of(
            "MASTER", Set.of("GET", "POST", "PATCH", "PUT", "DELETE"),
            "HUB", Set.of("GET", "POST", "PATCH", "PUT", "DELETE"),
            "DELIVERY", Set.of("GET"),
            "COMPANY", Set.of("GET", "POST", "PATCH", "PUT")
    );

    //주문
    private static final Map<String, Set<String>> orderRolePermissions = Map.of(
            "MASTER", Set.of("GET", "POST", "PATCH", "PUT", "DELETE"),
            "HUB", Set.of("GET", "POST", "PATCH", "PUT", "DELETE"),
            "DELIVERY", Set.of("GET", "POST"),
            "COMPANY", Set.of("GET", "POST")
    );

    //배송
    private static final Map<String, Set<String>> deliveryRolePermissions = Map.of(
            "MASTER", Set.of("GET", "POST", "PATCH", "PUT", "DELETE"),
            "HUB", Set.of("GET", "PATCH", "PUT", "DELETE"),
            "DELIVERY", Set.of("GET", "PATCH", "PUT"),
            "COMPANY", Set.of("GET")
    );

    //슬랙 메세지
    private static final Map<String, Set<String>> slackRolePermissions = Map.of(
            "MASTER", Set.of("GET", "POST", "PATCH", "PUT", "DELETE"),
            "HUB", Set.of("POST"),
            "DELIVERY", Set.of("POST"),
            "COMPANY", Set.of("POST")
    );


    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder,
            RoleAuthorizationFilter roleAuthorizationFilter) {
        return builder.routes()
                // User Service (일반 사용자 및 관리자)
                .route("user-service", r -> r.path("/api/users/**")
                        .filters(f -> f.filter(roleAuthorizationFilter.apply(
                                new RoleAuthorizationFilter.Config(userRolePermissions))))
                        .uri("lb://user-service"))

                // User Service (배송 담당자)
                .route("delivery-agent-service", r -> r.path("/api/delivery-agents/**")
                        .filters(f -> f.filter(roleAuthorizationFilter.apply(
                                new RoleAuthorizationFilter.Config(deliveryAgentRolePermissions))))
                        .uri("lb://user-service"))  // 같은 user-service지만 권한이 다름

                // Company
                .route("company-service", r -> r.path("/api/companies/**")
                        .filters(f -> f.filter(roleAuthorizationFilter.apply(
                                new RoleAuthorizationFilter.Config(companyRolePermissions))))
                        .uri("lb://company-service"))

                // Product
                .route("product-service", r -> r.path("/api/products/**")
                        .filters(f -> f.filter(roleAuthorizationFilter.apply(
                                new RoleAuthorizationFilter.Config(productRolePermissions))))
                        .uri("lb://product-service"))
                // Hub
                .route("hub-service", r -> r.path("/api/hubs/**")
                        .filters(f -> f.filter(roleAuthorizationFilter.apply(
                                new RoleAuthorizationFilter.Config(hubRolePermissions))))
                        .uri("lb://hub-service"))
                // Hub Route
                .route("hub-service", r -> r.path("/api/hub-routes/**")
                        .filters(f -> f.filter(roleAuthorizationFilter.apply(
                                new RoleAuthorizationFilter.Config(hubRouteRolePermissions))))
                        .uri("lb://hub-service"))

                // Order
                .route("order-service", r -> r.path("/api/orders/**")
                        .filters(f -> f.filter(roleAuthorizationFilter.apply(
                                new RoleAuthorizationFilter.Config(orderRolePermissions))))
                        .uri("lb://order-service"))

                // Delivery
                .route("delivery-service", r -> r.path("/api/deliveries/**")
                        .filters(f -> f.filter(roleAuthorizationFilter.apply(
                                new RoleAuthorizationFilter.Config(orderRolePermissions))))
                        .uri("lb://order-service"))

                .build();
    }
}