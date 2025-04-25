package eu.gaiax.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@ApiModel
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DetailsDto {
    @ApiModelProperty(name = "sd_data", value = "List of Self-Description Data")
    @JsonProperty(value = "sd_data", required = true)
    private List<SdData> sdDataList;
    @ApiModelProperty(name = "attachments", value = "List of Attachments")
    @JsonProperty(value = "attachments", required = true)
    private List<AttachmentDto> attachments;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class AttachmentDto {
        @ApiModelProperty(name = "name", value = "Name of Attachment", example = "sd.json")
        @JsonProperty(value = "name", required = true)
        private String name;
        @ApiModelProperty(name = "description", value = "Description for Attachment", example = "sd data")
        @JsonProperty(value = "description", required = true)
        private String description;
        @ApiModelProperty(name = "url", value = "Url for Attachment", example = "http://url.att")
        @JsonProperty(value = "url", required = true)
        private String url;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class SdData {
        @ApiModelProperty(name = "name", value = "Name for SD Data", example = "sd-data")
        @JsonProperty(value = "name", required = true)
        private String name;
        @ApiModelProperty(name = "attributes", value = "List of Attributes")
        @JsonProperty(value = "attributes", required = true)
        private List<Attribute> attributes;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Attribute {
        @ApiModelProperty(name = "name", value = "Name of the Attribute", example = "firstName")
        @JsonProperty(value = "name", required = true)
        private String name;
        @ApiModelProperty(name = "value", value = "Value of the Attribute", example = "John")
        @JsonProperty(value = "value", required = true)
        private String value;
        @ApiModelProperty(name = "mandatory", value = "Attribute is required", example = "false")
        @JsonProperty(value = "mandatory", required = true)
        private boolean mandatory;
    }
}
