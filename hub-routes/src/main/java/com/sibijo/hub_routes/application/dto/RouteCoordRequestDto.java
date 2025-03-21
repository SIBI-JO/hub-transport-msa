package com.sibijo.hub_routes.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RouteCoordRequestDto {
    @JsonProperty("origin")
    private Location departure;
    private Location destination;
    private List<WayPoint> wayPoints;

    @Getter
    @Builder
    public static class Location {
        private String x;
        private String y;
        private Integer angle;
    }

    @Getter
    @Builder
    public static class WayPoint {
        private String x;
        private String y;
    }

}
