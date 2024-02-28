/*
 * Copyright (C) 2024 Consiglio Nazionale delle Ricerche
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package it.cnr.anac.transparency.result.v1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.ToString;

/**
 * DTO con le informazioni dei risultati da esportare in un file CSV.
 */
@ToString
@Data
public class ResultCsvDto {

  @JsonUnwrapped(prefix = "IPA-")
  private CompanyShowDto company;

  @JsonProperty("ID")
  private Long id;

  @JsonProperty("URL CALCOLATA")
  private String realUrl;

  @JsonProperty("RECORD CREATO")
  private LocalDateTime createdAt;
  @JsonProperty("RECORD AGGIORNATO")
  private LocalDateTime updatedAt;
  // 200
  @JsonProperty("STATO")
  private Integer status;

  @JsonUnwrapped(prefix = "STORAGE DATA-")
  private StorageDataShowDto storageData;

  // "/it/amministrazione-trasparente?searchterm=amministrazione+trasparente"
  private String url;

  // Calcolato a runtime tramite join realUrl e url
  private String destinationUrl;

  // "amministrazione-trasparente"
  private String ruleName;

  // "Amministrazione Trasparente"
  private String term;

  // "Amministrazione trasparente"
  private String content;

  // false
  private boolean isLeaf;

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