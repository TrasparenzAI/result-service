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

import lombok.Data;
import lombok.ToString;

/**
 * Data transfer object per le informazioni sulle Company.
 *
 */
@ToString
@Data
public class CompanyShowDto {

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
