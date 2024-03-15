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
package it.cnr.anac.transparency.result.v1.dto;

import org.mapstruct.*;
import it.cnr.anac.transparency.result.models.Company;
import it.cnr.anac.transparency.result.models.Result;
import it.cnr.anac.transparency.result.models.StorageData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mapping dei dati delle Entity nei rispettivi DTO.
 *
 */
@Mapper(componentModel = "spring")
public interface ResultMapper {

  @Named("company-mapping")
  CompanyShowDto convert(Company company);

  @Named("company-mapping-terse")
  CompanyShowTerseDto convertTerse(Company company);

  @Named("company-csv-mapping")
  CompanyShowCsvDto convertCsv(Company company);

  @Named("company-csv-mapping-terse")
  CompanyShowTerseCsvDto convertCsvTerse(Company company);

  StorageDataShowDto convert(StorageData storageData);

  @Mapping(target = "destinationUrl", 
      expression = "java(it.cnr.anac.transparency.result.utils.UrlResolver.getDestinationUrl(result.getRealUrl(), result.getUrl()))")
  ResultShowDto convert(Result result);

  @Mapping(target = "destinationUrl",
          expression = "java(it.cnr.anac.transparency.result.utils.UrlResolver.getDestinationUrl(result.getRealUrl(), result.getUrl()))")
  List<ResultShowDto> convert(List<Result> results);

  @Mapping(source ="company", target = "company", qualifiedByName = "company-csv-mapping")
  @Mapping(target = "destinationUrl", 
      expression = "java(it.cnr.anac.transparency.result.utils.UrlResolver.getDestinationUrl(result.getRealUrl(), result.getUrl()).orElse(null))")
  ResultCsvDto convertCsv(Result result);

  @Mapping(source ="company", target = "company", qualifiedByName = "company-csv-mapping-terse")
  ResultCsvTerseDto convertCsvTerse(Result result);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "version", ignore = true)
  public void update(@MappingTarget Result result, ResultCreateDto companyDto);

  Company convert(CompanyShowDto companyShowDto);

  StorageData convert(StorageDataShowDto storageDataShowDto);

  Result convert(ResultRuleCreateDto resultRuleCreateDto);

  default List<Result> toResults(ResultBulkCreateDto resultBulkCreateDto) {
    List<Result> results = new ArrayList<>();
    Optional.ofNullable(resultBulkCreateDto).ifPresent(rbcd -> {
      results .addAll(
              rbcd
              .getResultRuleCreateDtos()
              .stream()
              .map(rrcd -> {
                Result result = convert(rrcd);
                result.setRealUrl(rbcd.getRealUrl());
                result.setCompany(convert(rbcd.getCompany()));
                result.setStorageData(convert(rbcd.getStorageData()));
                result.setWorkflowId(rbcd.getWorkflowId());
                result.setWorkflowChildId(rbcd.getWorkflowChildId());
                result.setErrorMessage(rbcd.getErrorMessage());
                result.setLength(rbcd.getLength());
                return result;
              })
              .collect(Collectors.toList())
      );
    });
    return results;
  }
}