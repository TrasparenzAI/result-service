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
package it.cnr.anac.transparency.result.v1.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cnr.anac.transparency.result.models.Workflow;
import it.cnr.anac.transparency.result.repositories.WorkflowDao;
import it.cnr.anac.transparency.result.repositories.WorkflowRepository;
import it.cnr.anac.transparency.result.v1.ApiRoutes;
import it.cnr.anac.transparency.result.v1.dto.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SecurityRequirement(name = "bearer_authentication")
@Tag(
        name = "Workflow Controller",
        description = "Gestione delle informazioni dei workflow di validazione dei siti delle PA")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiRoutes.BASE_PATH + "/workflows")
public class WorkflowController {

    private final WorkflowRepository workflowRepository;
    private final WorkflowMapper mapper;
    private final WorkflowDao workflowDao;
    private final WorkflowDtoToEntityConverter dtoToEntityConverter;

    @Operation(
            summary = "Visualizzazione delle informazioni di un workflow di validazione.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Restituiti i dati relativi al workflow."),
            @ApiResponse(responseCode = "404",
                    description = "Workflow non trovato con l'id fornito.",
                    content = @Content)
    })
    @GetMapping(ApiRoutes.SHOW)
    public ResponseEntity<WorkflowShowDto> show(@NotNull @PathVariable("id") Long id) {
        val workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Workflow non trovato con id = " + id));
        return ResponseEntity.ok().body(mapper.convert(workflow));
    }

    @Operation(
            summary = "Visualizzazione dei workflow presenti nel sistema, filtrabili "
                    + "utilizzando alcuni parametri.",
            description = "Le informazioni sono restituite paginate.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Restituita una pagina della lista dei workflow presenti.")
    })
    @GetMapping(ApiRoutes.LIST)
    public ResponseEntity<Page<WorkflowShowDto>> list(
            @RequestParam("workflowId") Optional<String> workflowId,
            @RequestParam("codiceIpa") Optional<String> codiceIpa,
            @RequestParam("status") Optional<Workflow.WorkflowStatus> status,
            @RequestParam("createdAfter") Optional<LocalDate> createdAfter,
            @Parameter(allowEmptyValue = true, example = "{ \"page\": 0, \"size\":100, \"sort\":\"id\"}")
            Pageable pageable) {
        Page<WorkflowShowDto> workflows =
                    workflowDao.find(workflowId, codiceIpa, status, createdAfter, pageable)
                            .map(mapper::convert);
        return ResponseEntity.ok().body(workflows);
    }

    @Operation(
            summary = "Visualizzazione dei workflow presenti nel sistema nel formato esportato dal Conductor. ",
            description = "Sono restituiti tutti i workflow, come lista senza paginazione.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Restituita la lista dei workflow presenti.")
    })
    @GetMapping(ApiRoutes.LIST + "/listConductorLike")
    public ResponseEntity<List<ConductorWorkflowDto>> listConductorLike(
            @RequestParam("codiceIpa") Optional<String> codiceIpa
    ) {
        //Se non viene passato nessun codice ipa allora si restituiscono tutti i workflow con codiceIpa null.
        List<Workflow> all = workflowRepository.findByCodiceIpa(codiceIpa.orElse(null));
        val workflows = all.stream()
                .sorted(Comparator.comparing(Workflow::getCreateTime).reversed())
                .map(mapper::convertToConductor).collect(Collectors.toList());
        return ResponseEntity.ok().body(workflows);
    }

    @Operation(
            summary = "Creazione di un workflow.",
            description = "Questa è la creazione di workflow con le info del conductor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workflow creato correttamente."),
            @ApiResponse(responseCode = "400", description = "Validazione delle informazioni obbligatorie fallita.",
                    content = @Content)
    })
    @PutMapping(ApiRoutes.CREATE)
    public ResponseEntity<WorkflowShowDto> create(@NotNull @Valid @RequestBody WorkflowCreateDto workflowDto) {
        log.debug("WorkflowController::create workflowDto = {}", workflowDto);
        val result = dtoToEntityConverter.createEntity(workflowDto);
        workflowRepository.save(result);
        log.info("Creato Result {}", result);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.convert(result));
    }

    @Operation(
            summary = "Aggiornamento dei dati di un workflow.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workflow aggiornato correttamente."),
            @ApiResponse(responseCode = "400", description = "Validazione delle informazioni obbligatorie fallita.")
    })
    @PostMapping(ApiRoutes.UPDATE)
    public ResponseEntity<WorkflowShowDto> update(@NotNull @Valid @RequestBody WorkflowUpdateDto workflowDto) {
        log.debug("WorkflowController::update workflowDto = {}", workflowDto);
        val result = dtoToEntityConverter.updateEntity(workflowDto);
        workflowRepository.save(result);
        log.info("Aggiornato workflow, i nuovi dati sono {}", result);
        return ResponseEntity.ok().body(mapper.convert(result));
    }

    @Operation(
            summary = "Eliminazione di un workflow.",
            description = "Eliminazione definitiva un workflow.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workflow eliminato correttamente")
    })
    @DeleteMapping(ApiRoutes.DELETE)
    ResponseEntity<Void> delete(@NotNull @PathVariable("id") Long id) {
        log.debug("WorkflowController::delete id = {}", id);
        val workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Workflow non trovato con id = " + id));
        workflowRepository.delete(workflow);
        log.info("Eliminato definitivamente workflow {}", workflow);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Eliminazione di un workflow tramite il suo workflowId.",
            description = "Eliminazione definitiva di un workflow tramite il suo workflow id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workflow eliminato correttamente")
    })
    @DeleteMapping(ApiRoutes.DELETE_BY_WORKFLOW_ID)
    ResponseEntity<Long> deleteByWorkflowId(
            @NotNull @PathVariable("id") String id) {
        log.debug("WorkflowController::deleteByWorkflowId workflowId = {}", id);
        workflowRepository.deleteByWorkflowId(id);
        log.info("Eliminato definitivamente workflow con workflowId = {}", id);
        return ResponseEntity.ok().build();
    }
}
