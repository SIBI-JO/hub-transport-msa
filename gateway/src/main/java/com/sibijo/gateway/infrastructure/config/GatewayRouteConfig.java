package com.sibijo.gateway.infrastructure.config;

import static com.sibijo.gateway.infrastructure.config.RolePermissionPolicy.companyRolePermissions;
import static com.sibijo.gateway.infrastructure.config.RolePermissionPolicy.deliveryAgentRolePermissions;
import static com.sibijo.gateway.infrastructure.config.RolePermissionPolicy.deliveryRolePermissions;
import static com.sibijo.gateway.infrastructure.config.RolePermissionPolicy.hubRolePermissions;
import static com.sibijo.gateway.infrastructure.config.RolePermissionPolicy.hubRouteRolePermissions;
import static com.sibijo.gateway.infrastructure.config.RolePermissionPolicy.orderRolePermissions;
import static com.sibijo.gateway.infrastructure.config.RolePermissionPolicy.productRolePermissions;
import static com.sibijo.gateway.infrastructure.config.RolePermissionPolicy.userRolePermissions;

import com.sibijo.gateway.infrastructure.filter.RoleAuthorizationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRouteConfig {

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
                .route("user-service-delivery-agents", r -> r.path("/api/delivery-agents/**")
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
                .route("hub-route-service", r -> r.path("/api/hub-routes/**")
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
                                new RoleAuthorizationFilter.Config(deliveryRolePermissions))))
                        .uri("lb://delivery-service"))

                //swagger
                // Swagger 문서 경로 라우팅
                .route("user-service-swagger", r -> r.path("/swagger/user-service/**")
                        .filters(f -> f.rewritePath("/swagger/user-service(?<segment>/?.*)",
                                "/${segment}"))
                        .uri("lb://user-service"))

                .build();
    }
}