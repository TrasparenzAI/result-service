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
import lombok.Data;
import lombok.ToString;

/**
 * Data transfer object per le informazioni sulle Company, informazioni di base
 *
 */
@JsonPropertyOrder(
    { "codiceIpa", "codiceCategoria","codiceFiscaleEnte", "denominazioneEnte", 
      "tipologia", "codiceNatura", "acronimo", "sitoIstituzionale" })
@ToString
@Data
public class CompanyShowTerseCsvDto {

  @JsonProperty("CODICE")
  private String codiceIpa;
  @JsonProperty("CATEGORIA")
  private String codiceCategoria;
  @JsonProperty("CODICE FISCALE")
  private String codiceFiscaleEnte;
  @JsonProperty("DENOMINAZIONE")
  private String denominazioneEnte;
  @JsonProperty("TIPOLOGIA")
  private String tipologia;
  @JsonProperty("NATURA GIURIDICA")
  private String codiceNatura;
  @JsonProperty("ACRONIMO")
  private String acronimo;
  @JsonProperty("SITO ISTITUZIONALE")
  private String sitoIstituzionale;

}