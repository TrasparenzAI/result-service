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
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.UriUtils;

@Slf4j
public class UrlResolver {

  public static Optional<String> getDestinationUrl(String base, String target) {
    if (base == null || target == null) {
      return Optional.empty();
    }
    
    base = sanitize(base);
    target = sanitize(normalizedTarget(target));
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
      //Se il traget inizia con ? allora viene restituita semplicemente la concatenazione
      if (!Strings.isNullOrEmpty(baseUri.getPath()) && target.startsWith("?")) {
        return Optional.of(baseUri.toString().concat(target));
      }
      if (Strings.isNullOrEmpty(baseUri.getPath()) && target.startsWith("..")) {
        target = target.substring(2);
      }
      if (Strings.isNullOrEmpty(baseUri.getPath()) && target.startsWith("#")) {
        baseUri = baseUri.resolve("/");
      }
      URI destination = safeResolve(baseUri, target);
      return Optional.of(destination.normalize().toString());
    } catch (URISyntaxException e) {
      log.info("Error during join of {} and {}. {}", base, target, 
          e.toString().substring(0, Math.min(300, e.toString().length())));
      return Optional.empty();
    }
  }

  public static URI safeResolve(URI base, String path) {
    if ("/".equals(path)) {
      return base;
    }
    return base.resolve(path);
  }

  public static String sanitize(String url) {
    url = url.strip();
    url = url.replace(" ", "");
    url = url.replace(" ", "%20");
    url = HtmlUtils.htmlUnescape(url);
    if (url.contains("?")) {
      String queryParams = url.substring(url.indexOf("?") + 1);
      String baseUrl = url.substring(0, url.indexOf("?"));
      baseUrl = sanitizeBaseUrl(baseUrl);
      return String.format("%s?%s", baseUrl, UriUtils.encodePath(queryParams, "UTF-8"));
    } else {
      return sanitizeBaseUrl(url);
    }
  }

  private static String sanitizeBaseUrl(String baseUrl) {
    //XXX: è corretto fare questa semplificazione?
    return baseUrl.replace("\\", "");
  }

  private static String normalizedTarget(String target) {
    if (!Strings.isNullOrEmpty(target) && target.toLowerCase().replaceAll("\\s", "").startsWith("javascript:void(")) {
      return "/";
    }
    if (!Strings.isNullOrEmpty(target) && target.toLowerCase().replaceAll("\\s", "").equals("javascript:")) {
      return "/";
    }
    if (!Strings.isNullOrEmpty(target) && target.toLowerCase().replaceAll("\\s", "").equals("javascript:;")) {
      return "/";
    }
    return target;
  }
}