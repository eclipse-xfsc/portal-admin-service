package eu.gaiax.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Participant implements PartItem {
    @ApiModelProperty(name = "id", value = "ID")
    @JsonProperty(value = "id", required = true)
    private String id;
    @ApiModelProperty(name = "participant_name", value = "Participant Name", example = "Gregory House")
    @JsonProperty(value = "participant_name", required = true)
    private String participantName;
    @ApiModelProperty(name = "registration_type", value = "Registration Type", example = "PR")
    @JsonProperty(value = "registration_type", required = true)
    private String registrationType;
    @ApiModelProperty(name = "location", value = "Location", example = "Germany")
    @JsonProperty(value = "location", required = true)
    private String location;

    @Override
    public String getType() {
        return registrationType;
    }
}
