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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

/**
 * Entity che rappresenta un workflow di analisi dei dati della trasparenza.
 * Il Workflow può essere relativo a un PA (se codiceIpa è diverso da null), oppure
 * relativo a tutte le PA presenti.
 */
@ToString
@Getter
@Setter
@NoArgsConstructor
@Table(
    name = "workflows",
    uniqueConstraints = { @UniqueConstraint(columnNames = { "workflowId" }) })
@Entity
public class Workflow extends MutableModel {

  public enum WorkflowStatus {
      RUNNING,
      COMPLETED,
      FAILED,
      TIMED_OUT,
      TERMINATED,
      PAUSED
  }

  // "6d7e4bd7-a890-439d-9dc7-f9f3f515d8b5"
  private String workflowId;

  private String codiceIpa;
  private String rootRule;

  @Enumerated(EnumType.STRING)
  private WorkflowStatus status;

  private Long createTime;
  private Long updateTime;
  private Long startTime;
  private Long endTime;

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    Workflow result = (Workflow) o;
    return getId() != null && Objects.equals(getId(), result.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
  }

}