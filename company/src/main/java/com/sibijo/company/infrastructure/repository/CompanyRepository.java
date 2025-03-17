package com.sibijo.company.infrastructure.repository;

import com.sibijo.company.domain.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
}
