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
package it.cnr.anac.transparency.result.models;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.ToString;

/**
 * Oggetto che contiene le informazioni sullo storage delle pagine e screenshot prelevate dal
 * web crawler.
 */
@ToString
@Data
@Embeddable
public class StorageData implements Serializable {

  @Serial
  private static final long serialVersionUID = 4740036434463012854L;

  private String objectBucket;
  private String objectId;
  private String objectResult;
  private String screenshotBucket;
  private String screenshotId;
  private String screenshotResult;

}