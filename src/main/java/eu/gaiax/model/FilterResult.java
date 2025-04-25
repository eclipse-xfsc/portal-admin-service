package eu.gaiax.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Api("Filter Results")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FilterResult {
    @ApiModelProperty(name = "data", value = "List of Pages")
    @JsonProperty("data")
    private List<? extends PartItem> items;
    @ApiModelProperty(name = "pages_count", value = "Page Count")
    @JsonProperty("pages_count")
    private int pageCount;
    @ApiModelProperty(name = "prev", value = "Previous Page Link")
    @JsonProperty("prev")
    private String prev;
    @ApiModelProperty(name = "next", value = "Next Page Link")
    @JsonProperty("next")
    private String next;
}
