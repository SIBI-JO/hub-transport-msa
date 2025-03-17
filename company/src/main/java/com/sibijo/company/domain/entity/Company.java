package com.sibijo.company.domain.entity;

import com.sibijo.company.domain.enums.CompanyType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.util.UUID;

@Entity
@Table(name = "p_company")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name="UUID", strategy="org.hibernate.id.UUIDGenerator")
    @Column(name = "company_id", updatable = false, nullable = false)
    private UUID companyId;

    @Column(name = "company_name", length = 50, nullable = false)
    private String companyName;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_type", length = 20, nullable = false)
    private CompanyType companyType;

    @Column(name = "hub_id", nullable = false)
    private UUID hubId;

    @Column(name = "address", length = 100)
    private String address;
}
