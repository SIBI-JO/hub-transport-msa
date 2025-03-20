package com.sibijo.company.presentation.controller;

import com.sibijo.common.dto.ApiResponse;
import com.sibijo.company.presentation.dto.CompanyRequest;
import com.sibijo.company.presentation.dto.CompanyResponseDto;
import com.sibijo.company.domain.entity.Company;
import com.sibijo.company.application.service.CompanyService;
import com.sibijo.company.domain.enums.CompanyType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.sibijo.common.utils.Auth.JwtUtil;

import java.util.UUID;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Company>>> getAllCompanies() {
        List<Company> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(ApiResponse.success("업체 전체 조회 성공", companies));
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<ApiResponse<Company>> getCompany(@PathVariable UUID companyId) {
        Company company = companyService.getCompanyById(companyId);
        if (company == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.exception("업체를 찾을 수 없습니다.", null));
        }
        return ResponseEntity.ok(ApiResponse.success("업체 조회 성공", company));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Company>> createCompany(HttpServletRequest request, @RequestBody CompanyRequest req) {
        String token = jwtUtil.extractToken(request);
        Company created = companyService.createCompany(req, token);
        return ResponseEntity.ok(ApiResponse.success("신규 업체 등록 성공", created));
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<ApiResponse<Company>> updateCompany(@PathVariable UUID companyId, HttpServletRequest request, @RequestBody CompanyRequest req) {
        String token = jwtUtil.extractToken(request);
        Company updated = companyService.updateCompany(companyId, req, token);
        return ResponseEntity.ok(ApiResponse.success("업체 정보 수정 성공", updated));
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<ApiResponse<Company>> deleteCompany(@PathVariable UUID companyId, HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        Company deletedCompany = companyService.deleteCompany(companyId, token);
        return ResponseEntity.ok(ApiResponse.success("업체 삭제 성공", deletedCompany));
    }

    @GetMapping("/{companyId}/order")
    public ResponseEntity<ApiResponse<CompanyResponseDto>> getCompanyOrderInfo(@PathVariable UUID companyId) {
        Company company = companyService.getCompanyById(companyId);
        if (company == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.exception("업체를 찾을 수 없습니다.", null));
        }
        CompanyResponseDto responseDto = CompanyResponseDto.builder()
                .hubId(company.getHubId())
                .deliveryAddress(company.getAddress())
                .build();
        return ResponseEntity.ok(ApiResponse.success("주문 처리용 정보 조회 성공", responseDto));
    }

    // 검색 엔드포인트 (읽기)
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<Company>>> searchCompanies(
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) CompanyType companyType,
            @RequestParam(required = false) UUID hubId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortDirection) {

        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }
        Sort sort = Sort.by("createdAt").ascending().and(Sort.by("updatedAt").ascending());
        if (sortField != null && !sortField.isEmpty()) {
            Sort.Direction direction = Sort.Direction.ASC;
            if (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) {
                direction = Sort.Direction.DESC;
            }
            sort = Sort.by(direction, sortField);
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Company> companies = companyService.searchCompanies(companyName, companyType, hubId, pageable);
        return ResponseEntity.ok(ApiResponse.success("검색 결과", companies));
    }
}
