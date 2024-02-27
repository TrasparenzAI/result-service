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
package it.cnr.anac.transparency.result.services;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import it.cnr.anac.transparency.result.v1.dto.ResultCsvDto;
import it.cnr.anac.transparency.result.v1.dto.ResultCsvTerseDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CsvExportService {

  public String resultsToCsv(List<ResultCsvDto> results) throws IOException {
    log.debug("Deserializing to CSV {} results", results.size());

    final CsvMapper csvMapper = new CsvMapper();
    csvMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    csvMapper.registerModule(new JavaTimeModule());

    CsvSchema csvSchema = csvMapper.schemaFor(ResultCsvDto.class).withHeader();
    try (StringWriter strW = new StringWriter()) {
      SequenceWriter seqW = csvMapper.writer(csvSchema).writeValues(strW);
      results.forEach(result -> {
        try {
          seqW.write(result);
          log.trace("Writing result{}", result);
        } catch (IOException e) {
          log.warn("Unable to export to CSV Result {}", result, e);
        }
      });
      return strW.toString();
    }
  }

  public String resultsToCsvTerse(List<ResultCsvTerseDto> results) throws IOException {
    log.debug("Deserializing to CSV {} results", results.size());

    final CsvMapper csvMapper = new CsvMapper();
    csvMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    csvMapper.registerModule(new JavaTimeModule());

    CsvSchema csvSchema = csvMapper.schemaFor(ResultCsvTerseDto.class).withHeader();
    try (StringWriter strW = new StringWriter()) {
      SequenceWriter seqW = csvMapper.writer(csvSchema).writeValues(strW);
      results.forEach(result -> {
        try {
          seqW.write(result);
          log.trace("Writing result{}", result);
        } catch (IOException e) {
          log.warn("Unable to export to CSV Result {}", result, e);
        }
      });
      return strW.toString();
    }
  }
}