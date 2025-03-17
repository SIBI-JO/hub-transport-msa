package com.sibijo.company.dto;

import com.sibijo.company.enums.CompanyType;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyRequest {
    private String companyName;
    private CompanyType companyType;  // "생산업체" 또는 "수령업체"
    private UUID hubId;
    private String address;
}
