package eu.gaiax.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ManagementFilter {
    @ApiModelProperty(name = "request_type", value = "List of Request Types")
    @JsonProperty(value = "request_type", required = true)
    private List<String> requestType;
    @ApiModelProperty(name = "location", value = "List of Locations")
    @JsonProperty(value = "location", required = true)
    private List<String> location;
}
