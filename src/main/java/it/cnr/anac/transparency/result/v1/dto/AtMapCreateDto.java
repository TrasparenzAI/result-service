/*
 * Copyright (C) 2026 Consiglio Nazionale delle Ricerche
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

import it.cnr.anac.transparency.result.models.AtMap;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class AtMapCreateDto {

  private CompanyShowDto company;

  // "6d7e4bd7-a890-439d-9dc7-f9f3f515d8b5"
  private String workflowId;

  // "https://www.cnr.it/at_map.xml"
  private String url;

  // Indica se la ATMap è stata trovata per la PA nella scansione
  private boolean found;

  // Codice di stato HTTP restituito dal prelievo della ATMap
  private Integer status;

  // Il contenuto della ATMap, salvato così come è stato prelevato
  private String content;

  // Gli schemi ANAC prevedono sia XML che JSON, se non indicato si assume XML
  private AtMap.AtMapFormat format = AtMap.AtMapFormat.XML;

  private AtMap.AtMapDiscovery discoveredBy;

  // Esito della validazione del contenuto rispetto allo schema pubblicato da ANAC
  private Boolean valid;

  private String errorMessage;

}
