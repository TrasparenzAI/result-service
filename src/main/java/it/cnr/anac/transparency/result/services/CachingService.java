/*
 * Copyright (C) 2024  Consiglio Nazionale delle Ricerche
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

import it.cnr.anac.transparency.result.repositories.ResultDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Contiene i metodi di supporto per ripulire le cache.
 *
 * @author Cristian Lucchesi
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CachingService {

  @CacheEvict(value = ResultDao.RESULTS_CACHE_NAME, allEntries = true)
  @Scheduled(fixedRateString = "${caching.spring.results}")
  public void evictResultsCachesAtIntervals() {
    log.info("Svuota la cache dei risultati");
  }

  @CacheEvict(value = ResultDao.RESULTS_GROUPED_BY_CACHE_NAME, allEntries = true)
  @Scheduled(fixedRateString = "${caching.spring.results}")
  public void evictResultsGroupedByCachesAtIntervals() {
    log.info("Svuota la cache dei risultati aggregati");
  }
}