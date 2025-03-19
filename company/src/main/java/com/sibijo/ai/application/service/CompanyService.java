package com.sibijo.ai.application.service;

import com.sibijo.ai.presentation.dto.CompanyRequest;
import com.sibijo.ai.domain.entity.Company;
import com.sibijo.ai.infrastructure.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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
     * - 존재하지 않으면 null 반환 (Controller 단에서 처리)
     */
    public Company getCompanyById(UUID companyId) {
        return companyRepository.findById(companyId).orElse(null);
    }

    /**
     * 신규 업체 생성
     */
    public Company createCompany(CompanyRequest request) {
        // Builder 대신 생성자를 사용하여 객체를 간단하게 생성
        Company newCompany = new Company(
                request.getCompanyName(),
                request.getCompanyType(),
                request.getHubId(),
                request.getAddress()
        );
        return companyRepository.save(newCompany);
    }

    /**
     * 기존 업체 정보 수정
     */
    public Company updateCompany(UUID companyId, CompanyRequest request) {
        Company existingCompany = getCompanyById(companyId);
        if (existingCompany == null) {
            return null;  // Controller에서 404 처리
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
    public void deleteCompany(UUID companyId) {
        companyRepository.deleteById(companyId);
    }
}
