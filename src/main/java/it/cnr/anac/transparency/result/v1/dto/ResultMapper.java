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

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import it.cnr.anac.transparency.result.models.Company;
import it.cnr.anac.transparency.result.models.Result;
import it.cnr.anac.transparency.result.models.StorageData;

/**
 * Mapping dei dati delle Entity nei rispettivi DTO.
 *
 */
@Mapper(componentModel = "spring")
public interface ResultMapper {

  CompanyShowDto convert(Company company);

  StorageDataShowDto convert(StorageData storageData);

  ResultShowDto convert(Result result);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "version", ignore = true)
  public void update(@MappingTarget Result result, ResultCreateDto companyDto);
}