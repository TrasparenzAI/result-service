package it.cnr.anac.transparency.result.v1.dto;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Data
public class ResultRuleCreateDto {
    // "/it/amministrazione-trasparente?searchterm=amministrazione+trasparente"
    private String url;
    // "amministrazione-trasparente"
    private String ruleName;
    // "Amministrazione Trasparente"
    private String term;
    // "Amministrazione trasparente"
    private String content;
    // false
    private boolean isLeaf;
    // 200
    private Integer status;
    // 5.466414
    private BigDecimal score;
    // Valore restituito dal motore delle regole, indica dove è stata trovata l'occorrenza del termine, di tipo stringa
    private String where;

}
