package com.sibijo.hub_routes.infrastructure.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HubServiceClientDto {
    @Builder.Default
    List<HubDto> hubs = new ArrayList<>();
}
