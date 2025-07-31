package it.cnr.anac.transparency.result.v1.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class DestinationURLDto {
    @NotNull private String base;
    @NotNull private String target;
}
