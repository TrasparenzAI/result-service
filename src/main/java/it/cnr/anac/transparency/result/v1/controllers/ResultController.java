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

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cnr.anac.transparency.result.models.Result;
import it.cnr.anac.transparency.result.repositories.ResultDao;
import it.cnr.anac.transparency.result.repositories.ResultRepository;
import it.cnr.anac.transparency.result.services.CsvExportService;
import it.cnr.anac.transparency.result.v1.ApiRoutes;
import it.cnr.anac.transparency.result.v1.dto.DtoToEntityConverter;
import it.cnr.anac.transparency.result.v1.dto.ResultCreateDto;
import it.cnr.anac.transparency.result.v1.dto.ResultMapper;
import it.cnr.anac.transparency.result.v1.dto.ResultShowDto;
import it.cnr.anac.transparency.result.v1.dto.ResultUpdateDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

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
          description = "Restituiti i risultati della validazine."),
      @ApiResponse(responseCode = "404", 
          description = "Risultati validazione non trovata con l'id fornito.",
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
      description = "Le informazioni sono restituite paginate'.")
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
      @RequestParam("isLeaf") Optional<Boolean> isLeaf,
      @RequestParam("status") Optional<Integer> status,
      @RequestParam("workflowId") Optional<String> workflowId,
      @RequestParam("createdAfter") Optional<LocalDate> createdAfter,
      @Parameter(required = false, allowEmptyValue = true, example = "{ \"page\": 0, \"size\":100, \"sort\":\"id\"}") 
      Pageable pageable) {
  codiceCategoria = codiceCategoria.isPresent() && codiceCategoria.get().isEmpty() ? 
      Optional.empty() : codiceCategoria;
    val results = 
        resultDao.findAll(idIpa, codiceCategoria, codiceFiscaleEnte, codiceIpa, 
            denominazioneEnte, isLeaf, status, workflowId, createdAfter, pageable)
          .map(mapper::convert);
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
          + "mostrati con i parameti disponibili e limitare i risultati utilizzando la paginazione'.")
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
      @RequestParam("isLeaf") Optional<Boolean> isLeaf,
      @RequestParam("status") Optional<Integer> status,
      @RequestParam("workflowId") Optional<String> workflowId,
      @RequestParam("createdAfter") Optional<LocalDate> createdAfter,
      @Parameter(required = false, allowEmptyValue = true, example = "{ \"page\": 0, \"size\":100000, \"sort\":\"id\"}") 
      Pageable pageable) throws IOException {
      codiceCategoria = codiceCategoria.isPresent() && codiceCategoria.get().isEmpty() ? 
          Optional.empty() : codiceCategoria;
      val results = 
        resultDao.findAll(idIpa, codiceCategoria, codiceFiscaleEnte, codiceIpa, 
            denominazioneEnte, isLeaf, status, workflowId, createdAfter, pageable).getContent()
        .stream().map(mapper::convertCsv).collect(Collectors.toList());
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
      headers.setContentDispositionFormData("attachment", "results.csv");
      String csv = csvExportService.resultsToCsv(results);
      return new ResponseEntity<String>(csv, headers, HttpStatus.OK);
  }

  @Operation(
      summary = "Visualizzazione dei risultati dell'ultima validazione registrata nel sistema.",
      description = "Le informazioni sono restituite in formato CSV, è poissibile limitare "
          + "i risultati utilizzando la paginazione'.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Restituito un CSV con la lista dei risultati dell'ultima validazione.")
  })
  @GetMapping("/lastRunAsCsv")
  public ResponseEntity<String> listLastRunAsCsv(
      @Parameter(required = false, allowEmptyValue = true, example = "{ \"page\": 0, \"size\":100000, \"sort\":\"id\"}") 
      Pageable pageable) throws IOException {
      Optional<Result> lastResult = resultDao.lastResult();
      Optional<String> lastWorkflowId = lastResult.isPresent() 
          ? Optional.of(lastResult.get().getWorkflowId()) : Optional.empty();
      val results = 
        resultDao.findAll(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), 
            Optional.empty(), Optional.empty(), Optional.empty(), lastWorkflowId, Optional.empty(), pageable).getContent()
        .stream().map(mapper::convertCsv).collect(Collectors.toList());
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
      headers.setContentDispositionFormData("attachment", "results.csv");
      String csv = csvExportService.resultsToCsv(results);
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
}