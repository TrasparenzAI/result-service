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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.ToString;

/**
 * DTO con le informazioni di base dei risultati da esportare in un file CSV.
 */
@JsonPropertyOrder(
    { "company", "id","realUrl", "createdAt", 
      "updatedAt", "status" })
@ToString
@Data
public class ResultCsvTerseDto {

  @JsonUnwrapped(prefix = "IPA-")
  private CompanyShowTerseCsvDto company;

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

}