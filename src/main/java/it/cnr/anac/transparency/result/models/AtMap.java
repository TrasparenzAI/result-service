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

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

/**
 * Entity che rappresenta la ATMap (la mappa dei link della sezione "Amministrazione Trasparente"
 * prevista dagli schemi di pubblicazione ANAC) individuata per una PA nel corso di una scansione.
 * La ATMap è un file pubblicato dall'ente, tipicamente all'indirizzo https://www.dominio.it/at_map.xml,
 * individuabile anche tramite la direttiva "ATmap" presente nel robots.txt del sito.
 */
@ToString
@Getter
@Setter
@NoArgsConstructor
@Table(
    name = "at_maps",
    uniqueConstraints = { @UniqueConstraint(columnNames = { "workflowId", "codiceIpa" }) })
@Entity
public class AtMap extends MutableModel {

  /**
   * Formato del file della ATMap: gli schemi ANAC prevedono sia XML che JSON.
   */
  public enum AtMapFormat {
    XML,
    JSON
  }

  /**
   * Modalità con cui la ATMap è stata individuata: tramite la direttiva "ATmap" del robots.txt
   * oppure tramite l'URL convenzionale (/at_map.xml, /at_map.json).
   */
  public enum AtMapDiscovery {
    ROBOTS_TXT,
    WELL_KNOWN_URL
  }

  private Long idIpa;

  private String codiceIpa;

  // "6d7e4bd7-a890-439d-9dc7-f9f3f515d8b5"
  private String workflowId;

  // "https://www.cnr.it/at_map.xml"
  private String url;

  // Indica se la ATMap è stata trovata per la PA nella scansione
  private boolean found;

  // Codice di stato HTTP restituito dal prelievo della ATMap
  private Integer status;

  // Il contenuto della ATMap, salvato così come è stato prelevato
  @Column(columnDefinition = "TEXT")
  private String content;

  @Enumerated(EnumType.STRING)
  private AtMapFormat format = AtMapFormat.XML;

  @Enumerated(EnumType.STRING)
  private AtMapDiscovery discoveredBy;

  // Esito della validazione del contenuto rispetto allo schema (XSD/JSON Schema) pubblicato da ANAC
  private Boolean valid;

  // Messaggio di errore in caso di prelievo o validazione non andati a buon fine
  private String errorMessage;

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    AtMap atMap = (AtMap) o;
    return getId() != null && Objects.equals(getId(), atMap.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
  }

}
