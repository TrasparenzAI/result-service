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
package it.cnr.anac.transparency.result.utils;

import com.google.common.base.Strings;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;

@Slf4j
public class UrlResolver {

  public static Optional<String> getDestinationUrl(String base, String target) {
    try {
      //Se il target è già un URL assoluta viene restituita quella
      URIBuilder uriBuilderTarget = new URIBuilder(target);
      if (uriBuilderTarget.isAbsolute()) {
        return Optional.of(uriBuilderTarget.build().normalize().toString());
      }
      if (Strings.isNullOrEmpty(target)) {
        log.warn("Target url is null or empty, unable to calculate destination (base = {})", base);
        return Optional.empty();
      }
      //Se il target non è assoluto si prova a fare la join con la baseUrl
      URIBuilder uriBuilderBase = new URIBuilder(base);
      if (!uriBuilderBase.isAbsolute()) {
        log.warn("Base url {} is not absolute, enable to join with {}", base, target);
        return Optional.empty();
      }
      URI baseUri = uriBuilderBase.build().normalize();
      if (Strings.isNullOrEmpty(baseUri.getPath()) && target.startsWith("..")) {
        target = target.substring(2);
      }
      if (Strings.isNullOrEmpty(baseUri.getPath()) && target.startsWith("#")) {
        baseUri = baseUri.resolve("/");
      }
      URI destination = baseUri.resolve(target);
      return Optional.of(destination.normalize().toString());
    } catch (URISyntaxException e) {
      log.warn("Error during join of {} and {}", base, target, e);
      return Optional.empty();
    }
  }
}