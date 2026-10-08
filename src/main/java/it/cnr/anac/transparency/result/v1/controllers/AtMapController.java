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
import it.cnr.anac.transparency.result.models.AtMap;
import it.cnr.anac.transparency.result.repositories.AtMapDao;
import it.cnr.anac.transparency.result.repositories.AtMapRepository;
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
import java.util.Optional;

@SecurityRequirement(name = "bearer_authentication")
@Tag(
        name = "ATMap Controller",
        description = "Gestione delle informazioni sulle ATMap (la mappa dei link della sezione "
                + "\"Amministrazione Trasparente\" pubblicata dalle PA) individuate nelle scansioni")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiRoutes.BASE_PATH + "/atmaps")
public class AtMapController {

    private final AtMapRepository atMapRepository;
    private final AtMapMapper mapper;
    private final AtMapDao atMapDao;
    private final AtMapDtoToEntityConverter dtoToEntityConverter;

    @Operation(
            summary = "Visualizzazione delle informazioni di una ATMap.",
            description = "Sono restituite anche il contenuto del file della ATMap prelevato.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Restituiti i dati relativi alla ATMap."),
            @ApiResponse(responseCode = "404",
                    description = "ATMap non trovata con l'id fornito.",
                    content = @Content)
    })
    @GetMapping(ApiRoutes.SHOW)
    public ResponseEntity<AtMapContentShowDto> show(@NotNull @PathVariable("id") Long id) {
        val atMap = atMapRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ATMap non trovata con id = " + id));
        return ResponseEntity.ok().body(mapper.convertWithContent(atMap));
    }

    @Operation(
            summary = "Visualizzazione delle ATMap presenti nel sistema, filtrabili "
                    + "utilizzando alcuni parametri.",
            description = "Le informazioni sono restituite paginate e senza il contenuto del file "
                    + "della ATMap, prelevabile tramite gli endpoint di dettaglio.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Restituita una pagina della lista delle ATMap presenti.")
    })
    @GetMapping(ApiRoutes.LIST)
    public ResponseEntity<Page<AtMapShowDto>> list(
            @RequestParam("workflowId") Optional<String> workflowId,
            @RequestParam("codiceIpa") Optional<String> codiceIpa,
            @RequestParam("found") Optional<Boolean> found,
            @RequestParam("createdAfter") Optional<LocalDate> createdAfter,
            @Parameter(allowEmptyValue = true, example = "{ \"page\": 0, \"size\":100, \"sort\":\"id\"}")
            Pageable pageable) {
        Page<AtMapShowDto> atMaps =
                atMapDao.find(workflowId, codiceIpa, found, createdAfter, pageable)
                        .map(mapper::convert);
        return ResponseEntity.ok().body(atMaps);
    }

    @Operation(
            summary = "Visualizzazione delle informazioni dell'ultima ATMap individuata per una PA.",
            description = "È restituita la ATMap individuata più recentemente per il codice IPA "
                    + "richiesto, considerando solo le scansioni portate a termine correttamente. "
                    + "Sono restituite anche il contenuto del file della ATMap prelevato.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Restituiti i dati dell'ultima ATMap individuata per la PA."),
            @ApiResponse(responseCode = "404",
                    description = "Nessuna ATMap trovata per il codice IPA fornito.",
                    content = @Content)
    })
    @GetMapping(ApiRoutes.CODICE_IPA_LAST)
    public ResponseEntity<AtMapContentShowDto> lastByCodiceIpa(
            @RequestParam(value = "codiceIpa") String codiceIpa) {
        val atMap = atMapDao.lastAtMapForCodiceIpa(codiceIpa)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Nessuna ATMap trovata per codiceIpa = " + codiceIpa));
        log.debug("Richiesta l'ultima ATMap per il codice IPA {}, trovata quella del workflow {}",
                codiceIpa, atMap.getWorkflowId());
        return ResponseEntity.ok().body(mapper.convertWithContent(atMap));
    }

    @Operation(
            summary = "Creazione di una ATMap.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "ATMap creata correttamente."),
            @ApiResponse(responseCode = "400", description = "Validazione delle informazioni obbligatorie fallita.",
                    content = @Content)
    })
    @PutMapping(ApiRoutes.CREATE)
    public ResponseEntity<AtMapContentShowDto> create(@NotNull @Valid @RequestBody AtMapCreateDto atMapDto) {
        log.debug("AtMapController::create atMapDto = {}", atMapDto);
        val atMap = dtoToEntityConverter.createEntity(atMapDto);
        atMapRepository.save(atMap);
        log.info("Creata ATMap {}", atMap);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.convertWithContent(atMap));
    }

    @Operation(
            summary = "Aggiornamento dei dati di una ATMap.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ATMap aggiornata correttamente."),
            @ApiResponse(responseCode = "400", description = "Validazione delle informazioni obbligatorie fallita.")
    })
    @PostMapping(ApiRoutes.UPDATE)
    public ResponseEntity<AtMapContentShowDto> update(@NotNull @Valid @RequestBody AtMapUpdateDto atMapDto) {
        log.debug("AtMapController::update atMapDto = {}", atMapDto);
        val atMap = dtoToEntityConverter.updateEntity(atMapDto);
        atMapRepository.save(atMap);
        log.info("Aggiornata ATMap, i nuovi dati sono {}", atMap);
        return ResponseEntity.ok().body(mapper.convertWithContent(atMap));
    }

    @Operation(
            summary = "Eliminazione di una ATMap.",
            description = "Eliminazione definitiva di una ATMap.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ATMap eliminata correttamente")
    })
    @DeleteMapping(ApiRoutes.DELETE)
    ResponseEntity<Void> delete(@NotNull @PathVariable("id") Long id) {
        log.debug("AtMapController::delete id = {}", id);
        val atMap = atMapRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ATMap non trovata con id = " + id));
        atMapRepository.delete(atMap);
        log.info("Eliminata definitivamente ATMap {}", atMap);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Eliminazione delle ATMap associate a un workflow id.",
            description = "Eliminazione definitiva di tutte le ATMap individuate nella scansione "
                    + "associata al workflow id fornito.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ATMap eliminate correttamente")
    })
    @DeleteMapping(ApiRoutes.DELETE_BY_WORKFLOW_ID)
    ResponseEntity<Long> deleteByWorkflowId(@NotNull @PathVariable("id") String id) {
        log.debug("AtMapController::deleteByWorkflowId workflowId = {}", id);
        val deleted = atMapRepository.deleteByWorkflowId(id);
        log.info("Eliminate definitivamente {} ATMap del workflowId {}", deleted, id);
        return ResponseEntity.ok(deleted);
    }
}
