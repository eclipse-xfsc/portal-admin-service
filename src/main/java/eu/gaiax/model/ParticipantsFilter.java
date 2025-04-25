package eu.gaiax.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Api("Filter for Participation")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ParticipantsFilter {
    @ApiModelProperty(name = "registration_type", value = "List of Request Types")
    @JsonProperty(value = "registration_type", required = true)
    private List<FilterItem> type;
    @ApiModelProperty(name = "location", value = "List of Locations")
    @JsonProperty(value = "location", required = true)
    private List<FilterItem> location;
}
