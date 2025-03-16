package com.sibijo.company.controller;




import com.sibijo.company.dto.CompanyRequest;
import com.sibijo.company.entity.Company;
import com.sibijo.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Company> getCompany(@PathVariable Long companyId) {
        Company company = companyService.getCompanyById(companyId);
        if (company == null) {
            // 회사가 없으면 404 Not Found
            return ResponseEntity.notFound().build();
        }
        // 회사가 있으면 200 OK + JSON 본문
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
    public Company updateCompany(@PathVariable Long companyId,
            @RequestBody CompanyRequest request) {
        return companyService.updateCompany(companyId, request);
    }

    /**
     * 업체 삭제
     */
    @DeleteMapping("/{companyId}")
    public void deleteCompany(@PathVariable Long companyId) {
        companyService.deleteCompany(companyId);
    }
}

