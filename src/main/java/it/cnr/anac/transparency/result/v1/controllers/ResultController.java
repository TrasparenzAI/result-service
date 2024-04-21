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
package it.cnr.anac.transparency.result.v1.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cnr.anac.transparency.result.models.Result;
import it.cnr.anac.transparency.result.models.ResultCount;
import it.cnr.anac.transparency.result.repositories.ResultDao;
import it.cnr.anac.transparency.result.repositories.ResultRepository;
import it.cnr.anac.transparency.result.services.CsvExportService;
import it.cnr.anac.transparency.result.v1.ApiRoutes;
import it.cnr.anac.transparency.result.v1.dto.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(
        name = "Result Controller",
        description = "Gestione delle informazioni dei risultati di validazione dei siti delle PA")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiRoutes.BASE_PATH + "/results")
public class ResultController {

    private final ResultRepository resultRepository;
    private final ResultDao resultDao;
    private final ResultMapper mapper;
    private final DtoToEntityConverter dtoToEntityConverter;
    private final CsvExportService csvExportService;

    @Operation(
            summary = "Visualizzazione delle informazioni di un risultato di validazione.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Restituiti i risultati della validazione."),
            @ApiResponse(responseCode = "404",
                    description = "Risultati validazione non trovati con l'id fornito.",
                    content = @Content)
    })
    @GetMapping(ApiRoutes.SHOW)
    public ResponseEntity<ResultShowDto> show(@NotNull @PathVariable("id") Long id) {
        val company = resultRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Result non trovato con id = " + id));
        return ResponseEntity.ok().body(mapper.convert(company));
    }

    @Operation(
            summary = "Visualizzazione dei risultati di validazione presenti nel sistema, filtrabili "
                    + "utilizzando alcuni parametri.",
            description = "Le informazioni sono restituite paginate.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Restitutita una pagina della lista risultati di validazione presenti.")
    })
    @GetMapping(ApiRoutes.LIST)
    public ResponseEntity<Page<ResultShowDto>> list(
            @RequestParam("idIpa") Optional<Long> idIpa,
            @RequestParam("codiceCategoria") Optional<String> codiceCategoria,
            @RequestParam("codiceFiscaleEnte") Optional<String> codiceFiscaleEnte,
            @RequestParam("codiceIpa") Optional<String> codiceIpa,
            @RequestParam("denominazioneEnte") Optional<String> denominazioneEnte,
            @RequestParam("ruleName") Optional<String> ruleName,
            @RequestParam("isLeaf") Optional<Boolean> isLeaf,
            @RequestParam("status") Optional<Integer> status,
            @RequestParam("workflowId") Optional<String> workflowId,
            @RequestParam("createdAfter") Optional<LocalDate> createdAfter,
            @Parameter(required = false, allowEmptyValue = true, example = "{ \"page\": 0, \"size\":100, \"sort\":\"id\"}")
            Pageable pageable) {
        codiceCategoria = codiceCategoria.isPresent() && codiceCategoria.get().isEmpty() ?
                Optional.empty() : codiceCategoria;
        val results =
                resultDao.find(idIpa, codiceCategoria, codiceFiscaleEnte, codiceIpa,
                                denominazioneEnte, ruleName, isLeaf, status, workflowId, createdAfter, pageable)
                        .map(mapper::convert);
        return ResponseEntity.ok().body(results);
    }

    @Operation(
            summary = "Visualizzazione dei risultati di validazione presenti nel sistema, filtrabili "
                    + "utilizzando alcuni parametri.",
            description = "Sono restitutite tutte informazioni, in modo non paginato.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Restitutita la lista risultati di validazione presenti.")
    })
    @GetMapping(ApiRoutes.LIST_ALL)
    public ResponseEntity<List<ResultShowDto>> listAll(
            @RequestParam("idIpa") Optional<Long> idIpa,
            @RequestParam("codiceCategoria") Optional<String> codiceCategoria,
            @RequestParam("codiceFiscaleEnte") Optional<String> codiceFiscaleEnte,
            @RequestParam("codiceIpa") Optional<String> codiceIpa,
            @RequestParam("denominazioneEnte") Optional<String> denominazioneEnte,
            @RequestParam("ruleName") Optional<String> ruleName,
            @RequestParam("isLeaf") Optional<Boolean> isLeaf,
            @RequestParam("status") Optional<Integer> status,
            @RequestParam("workflowId") String workflowId,
            @RequestParam("createdAfter") Optional<LocalDate> createdAfter,
            @Parameter(required = false, allowEmptyValue = true) Sort sort) {
        codiceCategoria = codiceCategoria.isPresent() && codiceCategoria.get().isEmpty() ?
                Optional.empty() : codiceCategoria;
        val results =
                resultDao.find(idIpa, codiceCategoria, codiceFiscaleEnte, codiceIpa,
                                denominazioneEnte, ruleName, isLeaf, status, Optional.of(workflowId), createdAfter, sort)
                        .stream().map(mapper::convert).collect(Collectors.toList());
        return ResponseEntity.ok().body(results);
    }

    @Operation(
            summary = "Creazione di un risultato di validazione.",
            description = "Questa è la creazione di risultato di validazione.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Risultato creato correttamente."),
            @ApiResponse(responseCode = "400", description = "Validazione delle informazioni obbligatorie fallita.",
                    content = @Content)
    })
    @PutMapping(ApiRoutes.CREATE)
    public ResponseEntity<ResultShowDto> create(@NotNull @Valid @RequestBody ResultCreateDto resultDto) {
        log.debug("CompanyController::create companyDto = {}", resultDto);
        val result = dtoToEntityConverter.createEntity(resultDto);
        resultRepository.save(result);
        log.info("Creato Result {}", result);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.convert(result));
    }

    @Operation(
            summary = "Creazione di più di un risultato di validazione.",
            description = "Questa è la creazione di più risultati di validazione.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Risultato creato correttamente."),
            @ApiResponse(responseCode = "400", description = "Validazione delle informazioni obbligatorie fallita.",
                    content = @Content)
    })
    @PutMapping(ApiRoutes.CREATE_BULK)
    public ResponseEntity<List<ResultShowDto>> createBulk(@NotNull @Valid @RequestBody ResultBulkCreateDto resultDto) {
        log.debug("CompanyController::create companyDto = {}", resultDto);
        val result = dtoToEntityConverter.createBulkEntity(resultDto);
        resultRepository.saveAll(result);
        log.info("Creato Result {}", result);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.convert(result));
    }

    @Operation(
            summary = "Aggiornamento dei dati di un risultato di validazione.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Risultato aggiornato correttamente."),
            @ApiResponse(responseCode = "400", description = "Validazione delle informazioni obbligatorie fallita.")
    })
    @PostMapping(ApiRoutes.UPDATE)
    public ResponseEntity<ResultShowDto> update(@NotNull @Valid @RequestBody ResultUpdateDto resultDto) {
        log.debug("ResultController::update resultDto = {}", resultDto);
        val result = dtoToEntityConverter.updateEntity(resultDto);
        resultRepository.save(result);
        log.info("Aggiornato risultato, i nuovi dati sono {}", result);
        return ResponseEntity.ok().body(mapper.convert(result));
    }

    @Operation(
            summary = "Eliminazione di un risultato di validazione.",
            description = "Eliminazione definitiva un risultato di validazione.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Risultato eliminato correttamente")
    })
    @DeleteMapping(ApiRoutes.DELETE)
    ResponseEntity<Void> delete(
            @NotNull @PathVariable("id") Long id) {
        log.debug("ResultController::delete id = {}", id);
        val result = resultRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Result non trovato con id = " + id));
        resultRepository.delete(result);
        log.info("Eliminato definitivamente result {}", result);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Visualizzazione dei risultati di validazione presenti nel sistema.",
            description = "Le informazioni sono restituite in formato CSV, è possibile filtrare i risultati"
                    + "mostrati con i parameti disponibili e limitare i risultati utilizzando la paginazione'. "
                    + "La dimensione massima della pagina è di 100.000 elementi."
                    + "È possibile utilizzare il parametro 'terse' per avere solo le informazioni principali esportate.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Restituito un CSV con la lista dei risultati di validazione presenti.")
    })
    @GetMapping(ApiRoutes.LIST_AS_CSV)
    public ResponseEntity<String> listAsCsv(
            HttpServletResponse servletResponse,
            @RequestParam("idIpa") Optional<Long> idIpa,
            @RequestParam("codiceCategoria") Optional<String> codiceCategoria,
            @RequestParam("codiceFiscaleEnte") Optional<String> codiceFiscaleEnte,
            @RequestParam("codiceIpa") Optional<String> codiceIpa,
            @RequestParam("denominazioneEnte") Optional<String> denominazioneEnte,
            @RequestParam("ruleName") Optional<String> ruleName,
            @RequestParam("isLeaf") Optional<Boolean> isLeaf,
            @RequestParam("status") Optional<Integer> status,
            @RequestParam("workflowId") Optional<String> workflowId,
            @RequestParam("createdAfter") Optional<LocalDate> createdAfter,
            @Parameter(required = false, example = "false",
                    description = "Permettere di esportare solo le informazioni principali")
            @RequestParam("terse") Optional<Boolean> terse,
            @Parameter(required = false, allowEmptyValue = true) Sort sort) throws IOException {
        codiceCategoria = codiceCategoria.isPresent() && codiceCategoria.get().isEmpty() ?
                Optional.empty() : codiceCategoria;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "results.csv");

        String csv = null;
        if (terse.isPresent() && terse.get()) {
            val results =
                    resultDao.find(idIpa, codiceCategoria, codiceFiscaleEnte, codiceIpa,
                                    denominazioneEnte, ruleName, isLeaf, status, workflowId, createdAfter, sort)
                            .stream().map(mapper::convertCsvTerse).collect(Collectors.toList());
            csv = csvExportService.resultsToCsvTerse(results);
        } else {
            val results =
                    resultDao.find(idIpa, codiceCategoria, codiceFiscaleEnte, codiceIpa,
                                    denominazioneEnte, ruleName, isLeaf, status, workflowId, createdAfter, sort)
                            .stream().map(mapper::convertCsv).collect(Collectors.toList());
            csv = csvExportService.resultsToCsv(results);
        }
        return new ResponseEntity<String>(csv, headers, HttpStatus.OK);
    }

    @Operation(
            summary = "Visualizzazione dei risultati dell'ultima validazione registrata nel sistema.",
            description = "Le informazioni sono restituite in formato CSV, è poissibile limitare "
                    + "i risultati utilizzando la paginazione'."
                    + "È possibile utilizzare il parametro 'terse' per avere solo le informazioni principali esportate."
                    + "La dimensione massima della pagina è di 100.000 elementi.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Restituito un CSV con la lista dei risultati dell'ultima validazione.")
    })
    @GetMapping("/lastRunAsCsv")
    public ResponseEntity<String> listLastRunAsCsv(
            @Parameter(required = false, example = "false",
                    description = "Permettere di esportare solo le informazioni principali")
            @RequestParam("terse") Optional<Boolean> terse, @Parameter(required = false, allowEmptyValue = true) Sort sort) throws IOException {
        Optional<Result> lastResult = resultDao.lastResult();
        Optional<String> lastWorkflowId = lastResult.isPresent()
                ? Optional.of(lastResult.get().getWorkflowId()) : Optional.empty();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "results.csv");
        String csv = null;
        if (terse.isPresent() && terse.get()) {
            val results =
                    resultDao.find(Optional.empty(), Optional.empty(),
                                    Optional.empty(), Optional.empty(),
                                    Optional.empty(), Optional.empty(),
                                    Optional.empty(), Optional.empty(),
                                    lastWorkflowId, Optional.empty(), sort)
                            .stream().map(mapper::convertCsvTerse).collect(Collectors.toList());
            csv = csvExportService.resultsToCsvTerse(results);
        } else {
            val results =
                    resultDao.find(Optional.empty(), Optional.empty(),
                                    Optional.empty(), Optional.empty(),
                                    Optional.empty(), Optional.empty(),
                                    Optional.empty(), Optional.empty(),
                                    lastWorkflowId, Optional.empty(), sort)
                            .stream().map(mapper::convertCsv).collect(Collectors.toList());
            csv = csvExportService.resultsToCsv(results);
        }
        return new ResponseEntity<String>(csv, headers, HttpStatus.OK);
    }

    @Operation(
            summary = "Visualizzazione delle informazioni del ultimo risultato memorizzato nel sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Restituito il risultato dell'ultima validazine registrata.")
    })
    @GetMapping("/lastResult")
    public ResponseEntity<ResultShowDto> lastResult() {
        Result lastResult = resultDao.lastResult()
                .orElseThrow(() -> new EntityNotFoundException("Nessun risultato di validazione trovato"));
        return ResponseEntity.ok().body(mapper.convert(lastResult));
    }

    @Operation(
            summary = "Visualizzazione delle informazioni presenti nel sistema ragruppate per flusso e stato della regola applicata.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Restituito il risultato ragruppato.")
    })
    @GetMapping("/countAndGroupByWorkflowIdAndStatus")
    public ResponseEntity<Map<String, List<ResultCountShowDto>>> countAndGroupByWorkflowIdAndStatus(@RequestParam(value = "ruleName",required = false) String ruleName, @RequestParam(value = "workflowIds",required = false) List<String> workflowIds) {
        final List<ResultCount> resultCounts = resultDao.countAndGroupByWorkflowIdAndStatus(ruleName, workflowIds);
        return ResponseEntity.ok().body(
                mapper.convertCounts(resultCounts)
                .stream()
                .collect(Collectors.groupingBy(ResultCountShowDto::getWorkflowId))
        );
    }
}