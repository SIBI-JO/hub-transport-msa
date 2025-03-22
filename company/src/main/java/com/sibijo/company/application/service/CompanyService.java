package com.sibijo.company.application.service;

import com.sibijo.company.domain.enums.CompanyType;
import com.sibijo.company.presentation.dto.CompanyRequest;
import com.sibijo.company.domain.entity.Company;
import com.sibijo.company.infrastructure.repository.CompanyRepository;
import com.sibijo.common.exception.CustomException;
import com.sibijo.common.exception.codes.CommonExceptionCode;
import com.sibijo.common.utils.Auth.JwtUtil;
import com.sibijo.company.infrastructure.client.HubClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final JwtUtil jwtUtil; // JWT 토큰에서 권한 정보를 추출
    private final HubClient hubClient; // 허브 존재 여부를 확인하기 위한 클라이언트

    /**
     * 업체 전체 조회 (읽기 및 검색은 모든 역할에 대해 허용)
     */
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    /**
     * 특정 업체 조회 (읽기 권한 모두 허용)
     */
    public Company getCompanyById(UUID companyId) {
        return companyRepository.findById(companyId).orElse(null);
    }

    /**
     * 신규 업체 등록
     * 권한: MASTER, HUB_ADMIN만 가능 (HUB_ADMIN은 자신의 허브에 한정)
     */
    public Company createCompany(CompanyRequest request, String token) {
        String role = jwtUtil.extractRole(token);
        UUID tokenHubId = jwtUtil.extractHubId(token);

        if ("MASTER".equals(role)) {
            // 무조건 허용
        } else if ("HUB".equals(role) || "HUB_ADMIN".equals(role)) {
            if (!request.getHubId().equals(tokenHubId)) {
                throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
            }
        } else {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }

        // 허브 존재 여부 검증
        var hubExistsResponse = hubClient.hubExists(request.getHubId());
        if (hubExistsResponse.getData() == null || !hubExistsResponse.getData()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 허브 ID입니다.");
        }

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
     * 권한:
     * - MASTER: 모든 업체 수정 가능
     * - HUB_ADMIN: 자신의 허브 소속 업체만 수정 가능
     * - COMPANY: 본인의 업체만 수정 가능
     * - DELIVERY: 수정 불가
     */
    public Company updateCompany(UUID companyId, CompanyRequest request, String token) {
        Company existingCompany = getCompanyById(companyId);
        if (existingCompany == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "업체가 존재하지 않습니다.");
        }

        String role = jwtUtil.extractRole(token);
        UUID tokenHubId = jwtUtil.extractHubId(token);
        UUID tokenCompanyId = jwtUtil.extractCompanyId(token);

        if ("MASTER".equals(role)) {
            // 허용
        } else if ("HUB".equals(role) || "HUB_ADMIN".equals(role)) {
            if (!existingCompany.getHubId().equals(tokenHubId)) {
                throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
            }
        } else if ("COMPANY".equals(role)) {
            if (!existingCompany.getCompanyId().equals(tokenCompanyId)) {
                throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
            }
        } else {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }

        // 만약 수정 시 새로운 허브 ID가 전달되었다면 허브 존재 여부 검증
        if (!existingCompany.getHubId().equals(request.getHubId())) {
            var hubExistsResponse = hubClient.hubExists(request.getHubId());
            if (hubExistsResponse.getData() == null || !hubExistsResponse.getData()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 허브 ID입니다.");
            }
        }

        existingCompany.setCompanyName(request.getCompanyName());
        existingCompany.setCompanyType(request.getCompanyType());
        existingCompany.setHubId(request.getHubId());
        existingCompany.setAddress(request.getAddress());
        return companyRepository.save(existingCompany);
    }

    /**
     * 업체 삭제 (Soft Delete)
     * 권한: MASTER, HUB_ADMIN만 가능 (허브 관리자는 자신의 허브 소속 업체만 삭제)
     */
    @Transactional
    public Company deleteCompany(UUID companyId, String token) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "업체를 찾을 수 없습니다."));
        String role = jwtUtil.extractRole(token);
        UUID tokenHubId = jwtUtil.extractHubId(token);

        if ("MASTER".equals(role)) {
            // 허용
        } else if ("HUB".equals(role) || "HUB_ADMIN".equals(role)) {
            if (!company.getHubId().equals(tokenHubId)) {
                throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
            }
        } else {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }

        // 예시로 실제 삭제 처리 (논리 삭제를 원하면 deletedAt, deletedBy 업데이트)
        companyRepository.delete(company);
        return company;
    }

    // 검색 기능: 읽기 권한은 모두 허용 (권한 체크 생략)
    public Page<Company> searchCompanies(String companyName, CompanyType companyType, UUID hubId, Pageable pageable) {
        return companyRepository.searchCompanies(companyName, companyType, hubId, pageable);
    }

    // 업체 존재 여부를 확인하는 메서드 추가
    public boolean exists(UUID companyId) {
        return companyRepository.existsById(companyId);
    }
}
