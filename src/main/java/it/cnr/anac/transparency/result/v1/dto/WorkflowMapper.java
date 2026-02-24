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

import it.cnr.anac.transparency.result.models.Workflow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapping dei dati della Entity dei Workflow ai DTO per la creazione, modifica e visualizzazione,
 * oltre all'esportazione nel formato del Conductor.
 */
@Mapper(componentModel = "spring")
public interface WorkflowMapper {

    WorkflowShowDto convert(Workflow workflow);

    ConductorWorkflowDto convertToConductor(Workflow workflow);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void update(@MappingTarget Workflow workflow, WorkflowCreateDto companyDto);

    default String emptyToNull(String value) {
        return (value != null && value.isBlank()) ? null : value;
    }

}
