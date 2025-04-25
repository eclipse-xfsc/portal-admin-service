package eu.gaiax.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Api("Participant Data for Tile")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PartRequest implements PartItem {
    @ApiModelProperty(name = "id", value = "ID", example = "lk34klkl34")
    @JsonProperty(value = "id", required = true)
    private String id;
    @ApiModelProperty(name = "participant_name", value = "Name of Participant", example = "Gregory House")
    @JsonProperty(value = "participant_name", required = true)
    private String participantName;
    @ApiModelProperty(name = "location", value = "Location", example = "Germany")
    @JsonProperty(value = "location", required = true)
    private String location;
    @ApiModelProperty(name = "request_type", value = "Request Type", example = "PR")
    @JsonProperty(value = "request_type")
    private String requestType;

    @Override
    public String getType() {
        return requestType;
    }
}
