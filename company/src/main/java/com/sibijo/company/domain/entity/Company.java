package com.sibijo.company.domain.entity;

import com.sibijo.common.entity.BaseEntity;
import com.sibijo.company.domain.enums.CompanyType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import java.util.UUID;

@Entity
@Table(name = "p_company")
@SQLDelete(sql = "UPDATE p_company SET is_deleted = true WHERE company_id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company extends BaseEntity {

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

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public Company(String companyName, CompanyType companyType, UUID hubId, String address) {
        this.companyName = companyName;
        this.companyType = companyType;
        this.hubId = hubId;
        this.address = address;
    }
}
