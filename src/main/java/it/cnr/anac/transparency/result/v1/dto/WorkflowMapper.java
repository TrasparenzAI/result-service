package it.cnr.anac.transparency.result.v1.dto;

import it.cnr.anac.transparency.result.models.Workflow;
import org.mapstruct.Mapper;

/**
 * Mapping dei dati della Entity dei Workflow ai DTO per la creazione, modifica e visualizzazione,
 * oltre all'esportazione nel formato del Conductor.
 */
@Mapper(componentModel = "spring")
public interface WorkflowMapper {

    WorkflowShowDto convert(Workflow workflow);

    ConductorWorkflowDto convertToConductor(Workflow workflow);
}
