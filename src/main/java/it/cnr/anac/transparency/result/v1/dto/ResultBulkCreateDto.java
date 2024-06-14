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

import java.util.List;

import lombok.Data;
import lombok.ToString;

/**
 * Data transfer object per le informazioni relativa alla creazione di un risultato di validazione.
 */
@ToString
@Data
public class ResultBulkCreateDto {

  private CompanyShowDto company;

  private String realUrl;
  private StorageDataShowDto storageData;
  // "6d7e4bd7-a890-439d-9dc7-f9f3f515d8b5"
  private String workflowId;
  private String workflowChildId;
  //  Messaggio di errore restituito dal crawler di tipo stringa
  private String errorMessage;
  // Lunghezza in byte della pagina
  private Integer length;

  private List<ResultRuleCreateDto> resultRuleCreateDtos;
}