package it.cnr.anac.transparency.result.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class CategoryValueDto {
    private RuleDto category;
    private Integer value;
}
