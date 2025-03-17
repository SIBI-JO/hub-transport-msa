package com.sibijo.company.service;



import com.sibijo.company.dto.CompanyRequest;
import com.sibijo.company.entity.Company;
import com.sibijo.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    /**
     * 업체 전체 조회
     */
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    /**
     * 특정 업체 조회
     */
    public Company getCompanyById(Long companyId) {
        return companyRepository.findById(companyId).orElse(null);
    }

    /**
     * 신규 업체 생성
     */
    public Company createCompany(CompanyRequest request) {
        Company newCompany = Company.builder()
                .companyName(request.getCompanyName())
                .companyType(request.getCompanyType())
                .hubId(request.getHubId())
                .address(request.getAddress())
                .build();

        return companyRepository.save(newCompany);
    }

    /**
     * 기존 업체 정보 수정
     */
    public Company updateCompany(Long companyId, CompanyRequest request) {
        Company existingCompany = getCompanyById(companyId);
        if (existingCompany == null) {
            return null; // 실제 프로젝트에서는 예외 처리 권장
        }

        existingCompany.setCompanyName(request.getCompanyName());
        existingCompany.setCompanyType(request.getCompanyType());
        existingCompany.setHubId(request.getHubId());
        existingCompany.setAddress(request.getAddress());

        return companyRepository.save(existingCompany);
    }

    /**
     * 업체 삭제
     */
    public void deleteCompany(Long companyId) {
        companyRepository.deleteById(companyId);
    }
}
