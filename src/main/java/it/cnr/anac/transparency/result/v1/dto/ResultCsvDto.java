package it.cnr.anac.transparency.result.v1.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Data;

@Data
public class ResultCsvDto {

  private Long id;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  
  @JsonUnwrapped(prefix = "company.")
  private CompanyShowDto company;

  private String realUrl;

  @JsonUnwrapped(prefix = "storageData.")
  private StorageDataShowDto storageData;

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

  // "6d7e4bd7-a890-439d-9dc7-f9f3f515d8b5"
  private String workflowId;
  private String workflowChildId;

  //  Messaggio di errore restituito dal crawler di tipo stringa
  private String errorMessage;
  // Lunghezza in byte della pagina
  private Integer length;
  // Valore restituito dal motore delle regole, indica dove è stata trovata l'occorrenza del termine, di tipo stringa
  private String where;
}
