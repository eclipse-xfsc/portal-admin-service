package eu.gaiax.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Api("Filter for PR Location")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PrLocationFilterItem {
    @ApiModelProperty(name = "loc_code", value = "Location Code")
    @JsonProperty(value = "loc_code", required = true)
    private String locCode;
    @ApiModelProperty(name = "name", value = "Name of Location")
    @JsonProperty(value = "name", required = true)
    private String itemName;
    @ApiModelProperty(name = "qty", value = "Quantity of Items")
    @JsonProperty(value = "qty", required = true)
    private int count;

}
