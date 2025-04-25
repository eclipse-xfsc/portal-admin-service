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
public class LocationFilterDto {
    @ApiModelProperty(name = "items", value = "List of Filter Items")
    @JsonProperty(value = "items", required = true)
    private List<PrLocationFilterItem> items;
}
