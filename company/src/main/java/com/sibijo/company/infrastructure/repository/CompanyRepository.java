package com.sibijo.company.infrastructure.repository;

import com.sibijo.company.domain.entity.Company;
import com.sibijo.company.domain.enums.CompanyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

    @Query("SELECT c FROM Company c " +
            "WHERE (:companyName IS NULL OR LOWER(c.companyName) LIKE CONCAT('%', LOWER(:companyName), '%')) " +
            "AND (:companyType IS NULL OR c.companyType = :companyType) " +
            "AND (:hubId IS NULL OR c.hubId = :hubId) " +
            "AND c.deletedAt IS NULL")
    Page<Company> searchCompanies(@Param("companyName") String companyName,
            @Param("companyType") CompanyType companyType,
            @Param("hubId") UUID hubId,
            Pageable pageable);
}
