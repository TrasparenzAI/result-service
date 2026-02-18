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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Data transfer object che contiene le informazioni sui Workflow nello stesso
 * formato restituito dal Conductor.
 */
@ToString
@Data
@EqualsAndHashCode
public class ConductorWorkflowDto {

    //1735416017000,
    Long createTime;
    //1738081835741
    Long updateTime;
    //COMPLETED
    String status;
    //1735471487000
    Long startTime;
    //1735471487000
    Long endTime;
    //5e230eee-d1e8-4690-88c2-0b3583e7a3d1
    String workflowId;
}