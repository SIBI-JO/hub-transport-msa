package com.sibijo.company.entity;

import com.sibijo.company.enums.CompanyType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "p_company")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "company_name", length = 50, nullable = false)
    private String companyName;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_type", length = 20, nullable = false)
    private CompanyType companyType;  // 예: 생산업체, 수령업체

    @Column(name = "hub_id")
    private Long hubId;          // 허브 ID (예: 1, 2, 3 ...)

    @Column(name = "address", length = 100)
    private String address;
}
