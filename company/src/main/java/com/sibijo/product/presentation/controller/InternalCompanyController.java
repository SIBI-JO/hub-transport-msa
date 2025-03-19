package com.sibijo.product.presentation.controller;

import com.sibijo.product.presentation.dto.InternalCompanyResponseDto;
import com.sibijo.common.dto.ApiResponse;
import com.sibijo.product.domain.entity.Company;
import com.sibijo.product.application.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/companies")
public class InternalCompanyController {

    private final CompanyService companyService;

    /**
     * 내부 전용: 업체 존재 여부 확인 및 HubId 반환
     */
    @GetMapping("/{companyId}")
    public ResponseEntity<ApiResponse<InternalCompanyResponseDto>> getCompanyHubByCompanyId(@PathVariable UUID companyId) {
        Company company = companyService.getCompanyById(companyId);
        if (company == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.exception("업체를 찾을 수 없습니다.", null));
        }
        // 업체가 존재하면 HubId 반환
        InternalCompanyResponseDto responseDto = InternalCompanyResponseDto.builder()
                .hubId(company.getHubId())
                .build();
        return ResponseEntity.ok(ApiResponse.success("내부 전용 업체 조회 성공", responseDto));
    }
}
