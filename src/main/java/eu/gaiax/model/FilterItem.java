package eu.gaiax.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Api("Filter item")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FilterItem {
    @ApiModelProperty(name = "items", value = "Item Name")
    @JsonProperty(value = "name", required = true)
    private String itemName;
    @ApiModelProperty(name = "qty", value = "Quantity of Items")
    @JsonProperty(value = "qty", required = true)
    private int count;
}
