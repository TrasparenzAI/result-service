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

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.ToString;

/**
 * Entity che rappresenta i dati di un Ente pubblico.
 */
@ToString
@Data
@Embeddable
public class Company implements Serializable {

  private static final long serialVersionUID = 4740036434463012854L;

  private Long idIpa;
  private String codiceIpa;
  private String denominazioneEnte;
  private String codiceFiscaleEnte;
  private String tipologia;
  private String codiceCategoria;
  private String codiceNatura;
  private String acronimo;
  private String sitoIstituzionale;
  private String sorgente;
  
}