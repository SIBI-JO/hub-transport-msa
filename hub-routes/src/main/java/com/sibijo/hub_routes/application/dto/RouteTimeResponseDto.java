package com.sibijo.hub_routes.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RouteTimeResponseDto {

    @JsonProperty("trans_id")
    private String transId;

    @JsonProperty("routes")
    private List<Route> routes;

    @Getter
    public static class Route {
        @JsonProperty("summary")
        private RouteSummary summary;
    }

    @Getter
    public static class RouteSummary {
        @JsonProperty("distance")
        private int distance;

        @JsonProperty("duration")
        private int duration;

        public BigDecimal getDistanceToKm() {
            return new BigDecimal(distance).divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_UP);
        }

        public int getDurationToMinutes() {
            return duration / 60;
        }
    }
}
