package it.cnr.anac.transparency.result.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResultCount {
    private String workflowId;
    private Integer status;
    private Long count;
}
