package it.cnr.anac.transparency.result.v1.dto;

import lombok.*;

@ToString
@Data
@NoArgsConstructor
public class RuleDto {
    private int min;
    private int max;
    private String color;
    private String ngxcolor;
}
