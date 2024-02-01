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
package it.cnr.anac.transparency.result.models;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Entity che rappresenta il risultato di un controllo dei dati della transparenza su una pagina
 * di un sito pubblico.
 */
@ToString
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "results")
@Entity
public class Result extends MutableModel {

  private static final long serialVersionUID = -1398612405357496772L;

  @Embedded
  private Company company;

  private String realUrl;

  @Embedded
  private StorageData storageData;

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
  // Id del flusso di dettaglio che ha elaborato la richiesta, valore di tipo stringa
  private String workflowChildId;

  //  Messaggio di errore restituito dal crawler di tipo stringa
  private String errorMessage;
  // Lunghezza in byte della pagina
  private Integer length;
  @Column(name = "location")
  // Valore restituito dal motore delle regole, indica dove è stata trovata l'occorrenza del termine, di tipo stringa
  private String where;

}