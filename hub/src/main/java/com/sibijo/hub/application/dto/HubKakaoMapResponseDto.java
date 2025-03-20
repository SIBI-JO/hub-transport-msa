package com.sibijo.hub.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class HubKakaoMapResponseDto {

    private Meta meta;
    private List<Document> documents;

    @Getter
    public static class Meta {

        @JsonProperty("total_count")
        private int totalCount;
    }

    @Getter
    public static class Document {

        @JsonProperty("address_name")
        private String addressName;
        private String x;
        private String y;
    }

}
