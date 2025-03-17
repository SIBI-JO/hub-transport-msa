package com.sibijo.company.controller;

import com.sibijo.company.dto.CompanyRequest;
import com.sibijo.company.dto.CompanyResponseDto;
import com.sibijo.company.entity.Company;
import com.sibijo.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
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
    public List<Company> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    /**
     * 특정 업체 조회
     */
    @GetMapping("/{companyId}")
    public ResponseEntity<Company> getCompany(@PathVariable UUID companyId) {
        Company company = companyService.getCompanyById(companyId);
        if (company == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(company);
    }

    /**
     * 신규 업체 등록
     */
    @PostMapping
    public Company createCompany(@RequestBody CompanyRequest request) {
        return companyService.createCompany(request);
    }

    /**
     * 기존 업체 정보 수정
     */
    @PutMapping("/{companyId}")
    public Company updateCompany(@PathVariable UUID companyId,
            @RequestBody CompanyRequest request) {
        return companyService.updateCompany(companyId, request);
    }

    /**
     * 업체 삭제
     */
    @DeleteMapping("/{companyId}")
    public void deleteCompany(@PathVariable UUID companyId) {
        companyService.deleteCompany(companyId);
    }

    /**
     * 주문 처리를 위한 전용 엔드포인트
     * 해당 회사의 hubId와 배송지 정보를 반환합니다.
     */
    @GetMapping("/{companyId}/order")
    public ResponseEntity<CompanyResponseDto> getCompanyOrderInfo(@PathVariable UUID companyId) {
        Company company = companyService.getCompanyById(companyId);
        if (company == null) {
            return ResponseEntity.notFound().build();
        }
        CompanyResponseDto responseDto = CompanyResponseDto.builder()
                .hubId(company.getHubId())
                .deliveryAddress(company.getAddress())  // address를 배송지로 활용하거나 별도의 필드를 사용
                .build();
        return ResponseEntity.ok(responseDto);
    }
}
