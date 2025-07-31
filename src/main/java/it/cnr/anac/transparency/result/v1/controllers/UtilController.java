/*
 * Copyright (C) 2025 Consiglio Nazionale delle Ricerche
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
import it.cnr.anac.transparency.result.models.Result;
import it.cnr.anac.transparency.result.models.ResultCount;
import it.cnr.anac.transparency.result.repositories.ResultDao;
import it.cnr.anac.transparency.result.repositories.ResultRepository;
import it.cnr.anac.transparency.result.services.CachingService;
import it.cnr.anac.transparency.result.services.CsvExportService;
import it.cnr.anac.transparency.result.services.MinioService;
import it.cnr.anac.transparency.result.utils.UrlResolver;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@SecurityRequirement(name = "bearer_authentication")
@Tag(
        name = "Util Controller",
        description = "Utilità di servizio per la gestione delle informazioni dei risultati di validazione dei siti delle PA")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiRoutes.BASE_PATH + "/utils")
public class UtilController {

    @Operation(
            summary = "Fornisce la URL conforme componendo quella di base e il target.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "La URL viene correttamente estrapolata."),
            @ApiResponse(responseCode = "404",
                    description = "La URL calcolata non risulta valida.",
                    content = @Content)
    })
    @PostMapping(ApiRoutes.DESTINATION_URL)
    public ResponseEntity<Map<String, String>> destinationURL(
            @NotNull @Valid @RequestBody DestinationURLDto destinationURLDto
    ) {
        return UrlResolver.getDestinationUrl(destinationURLDto.getBase(), destinationURLDto.getTarget())
                .map(s -> ResponseEntity.ok().body(Collections.singletonMap("result", s)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}