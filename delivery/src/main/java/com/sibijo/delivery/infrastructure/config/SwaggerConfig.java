package com.sibijo.delivery.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;

public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes
                        ("Bearer Authentication", createAPIKeyScheme()))
                .info(apiInfo());
//                .path("/api/user/signIn", pathItem());
    }

    private Info apiInfo() {
        return new Info().title("User REST API")
                .description("User 관련 API 문서입니다.")
                .version("1.0");
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

//    private PathItem pathItem() {
//        return new PathItem()
//                .post(
//                        new Operation()
//                                .summary("로그인")
//                                .description("Spring Security 필터를 통해 로그인 수행")
//                                .tags(List.of("auth-controller"))
//                                .requestBody(new RequestBody()
//                                        .content(new Content().addMediaType("application/json",
//                                                new MediaType().schema(new Schema<SignInRequestDto>().example(new SignInRequestDto("testUser", "password123"))))))
//                                .responses(new ApiResponses()
//                                        .addApiResponse("200", new ApiResponse().description("로그인 성공")
//                                                .content(new Content().addMediaType("application/json",
//                                                        new MediaType().schema(new Schema<com.sibijo.common.dto.ApiResponse<SignInResponseDto>>()
//                                                                .example(new com.sibijo.common.dto.ApiResponse<>(
//                                                                        "200",
//                                                                        "요청이 성공적으로 처리되었습니다.",
//                                                                        SignInResponseDto.builder().token("Bearer eygb...").build()
//                                                                ))))))
//                                        .addApiResponse("401", new ApiResponse().description("로그인 실패")
//                                                .content(new Content().addMediaType("application/json",
//                                                        new MediaType().schema(new Schema<com.sibijo.common.dto.ApiResponse<SignInResponseDto>>()
//                                                                .example(new com.sibijo.common.dto.ApiResponse<>(
//                                                                        "401",
//                                                                        "로그인 실패",
//                                                                        null
//                                                                ))))))
//                                )
//                );
//    }
}
