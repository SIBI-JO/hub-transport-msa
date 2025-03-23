package com.sibijo.ai.infrastructure.client.hub;

import lombok.Data;
import java.util.UUID;

@Data
public class HubInfoDto {
    private UUID hubId;
    private String hubName;
    private String hubLocation;
}