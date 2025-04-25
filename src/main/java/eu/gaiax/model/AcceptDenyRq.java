package eu.gaiax.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@ApiModel
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AcceptDenyRq {
    @ApiModelProperty(name = "id", value = "Request Identifier", example = "askdlf83orijfk34")
    private String id;
    @ApiModelProperty(name = "status", value = "Type of Request", example = "accept", allowableValues = "accept, deny")
    private String status;
}
