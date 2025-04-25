package eu.gaiax.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Api("Filter items")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FilterDto {
    @ApiModelProperty(name = "items", value = "List of Filter Items")
    @JsonProperty(value = "items", required = true)
    private List<FilterItem> items;
}
