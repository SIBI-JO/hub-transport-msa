package com.sibijo.company.presentation.controller;

import com.sibijo.common.dto.ApiResponse;
import com.sibijo.company.presentation.dto.CompanyRequest;
import com.sibijo.company.presentation.dto.CompanyResponseDto;
import com.sibijo.company.domain.entity.Company;
import com.sibijo.company.application.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    /**
     * 업체 전체 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Company>>> getAllCompanies() {
        List<Company> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(ApiResponse.success("업체 전체 조회 성공", companies));
    }

    /**
     * 특정 업체 조회
     */
    @GetMapping("/{companyId}")
    public ResponseEntity<ApiResponse<Company>> getCompany(@PathVariable UUID companyId) {
        Company company = companyService.getCompanyById(companyId);
        if (company == null) {
            // 404 응답
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.exception("업체를 찾을 수 없습니다.", null));
        }
        return ResponseEntity.ok(ApiResponse.success("업체 조회 성공", company));
    }

    /**
     * 신규 업체 등록
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Company>> createCompany(@RequestBody CompanyRequest request) {
        Company created = companyService.createCompany(request);
        return ResponseEntity.ok(ApiResponse.success("신규 업체 등록 성공", created));
    }

    /**
     * 기존 업체 정보 수정
     */
    @PutMapping("/{companyId}")
    public ResponseEntity<ApiResponse<Company>> updateCompany(@PathVariable UUID companyId,
            @RequestBody CompanyRequest request) {
        Company updated = companyService.updateCompany(companyId, request);
        if (updated == null) {
            // 404 응답
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.exception("업체가 존재하지 않습니다.", null));
        }
        return ResponseEntity.ok(ApiResponse.success("업체 정보 수정 성공", updated));
    }

    /**
     * 업체 삭제
     */
    @DeleteMapping("/{companyId}")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(@PathVariable UUID companyId) {
        companyService.deleteCompany(companyId);
        return ResponseEntity.ok(ApiResponse.success("업체 삭제 성공", null));
    }

    /**
     * 주문 처리를 위한 전용 엔드포인트
     * 해당 회사의 hubId와 배송지 정보를 반환합니다.
     */
    @GetMapping("/{companyId}/order")
    public ResponseEntity<ApiResponse<CompanyResponseDto>> getCompanyOrderInfo(@PathVariable UUID companyId) {
        Company company = companyService.getCompanyById(companyId);
        if (company == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.exception("업체를 찾을 수 없습니다.", null));
        }
        CompanyResponseDto responseDto = CompanyResponseDto.builder()
                .hubId(company.getHubId())
                .deliveryAddress(company.getAddress())  // address를 배송지로 활용
                .build();
        return ResponseEntity.ok(ApiResponse.success("주문 처리용 정보 조회 성공", responseDto));
    }
}
