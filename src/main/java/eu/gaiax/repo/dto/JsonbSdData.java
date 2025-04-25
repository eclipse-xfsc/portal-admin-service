package eu.gaiax.repo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Getter
public class JsonbSdData {
    @JsonProperty("sd")
    private List<SdDetailsDto> sd;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class SdDetailsDto {
        @JsonProperty(value = "name", required = true)
        private String name;
        @JsonProperty(value = "attributes", required = true)
        private List<Attribute> attributes;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class Attribute {
        @JsonProperty(value = "name", required = true)
        private String name;
        @JsonProperty(value = "value", required = true)
        private String value;
        @JsonProperty(value = "mandatory", required = true)
        private boolean mandatory;
    }
}
