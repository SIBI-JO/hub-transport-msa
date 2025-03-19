package com.sibijo.product.infrastructure.repository;

import com.sibijo.product.domain.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
}
