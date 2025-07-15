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
package it.cnr.anac.transparency.result;

import it.cnr.anac.transparency.result.utils.UrlResolver;
import java.util.Optional;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UrlJoinerTest {

  private final static String ABSOLUTE_BASE_URL = "https://www.cnr.it";

  @Test
  public void joinBaseAndRelative() {
    Optional<String> joined = UrlResolver.getDestinationUrl("https://www.cnr.it/", "amministrazione-trasparente");
    Assertions.assertTrue(joined.isPresent());
    Assertions.assertEquals("https://www.cnr.it/amministrazione-trasparente", joined.get());
    
    joined = UrlResolver.getDestinationUrl("https://www.cnr.it", "amministrazione-trasparente");
    Assertions.assertTrue(joined.isPresent());
    Assertions.assertEquals("https://www.cnr.it/amministrazione-trasparente", joined.get());
    
    joined = UrlResolver.getDestinationUrl("https://www.cnr.it", "/amministrazione-trasparente");
    Assertions.assertTrue(joined.isPresent());
    Assertions.assertEquals("https://www.cnr.it/amministrazione-trasparente", joined.get());

    joined = UrlResolver.getDestinationUrl("https://www.cnr.it", "../amministrazione-trasparente");
    Assertions.assertTrue(joined.isPresent());
    Assertions.assertEquals("https://www.cnr.it/amministrazione-trasparente", joined.get());
    
    joined = UrlResolver.getDestinationUrl("https://www.cnr.it", "#");
    Assertions.assertTrue(joined.isPresent());
    Assertions.assertEquals("https://www.cnr.it/#", joined.get());

    joined = UrlResolver.getDestinationUrl("https://www.cnr.it", "javascript:");
    Assertions.assertTrue(joined.isPresent());
    Assertions.assertEquals("https://www.cnr.it", joined.get());

    joined = UrlResolver.getDestinationUrl("https://www.cnr.it", "javascript:");
    Assertions.assertTrue(joined.isPresent());
    Assertions.assertEquals("https://www.cnr.it", joined.get());

    joined = UrlResolver.getDestinationUrl("https://www.cnr.it", "javascript: void()");
    Assertions.assertTrue(joined.isPresent());
    Assertions.assertEquals("https://www.cnr.it", joined.get());

    joined = UrlResolver.getDestinationUrl("https://www.cnr.it/it/amministrazione-trasparente", "javascript:void(0);");
    Assertions.assertTrue(joined.isPresent());
    Assertions.assertEquals("https://www.cnr.it/it/amministrazione-trasparente", joined.get());

    joined = UrlResolver.getDestinationUrl("https://www.parcoaveto.it/amministrazione-trasparente.php", "?l1=1");
    Assertions.assertTrue(joined.isPresent());
    Assertions.assertEquals("https://www.parcoaveto.it/amministrazione-trasparente.php?l1=1", joined.get());

  }

  @Test
  public void getAbsoluteTargetUrl() {
    String target = "https://trasparenza-pa.net/?codcli=SC26149";
    Optional<String> joined = UrlResolver.getDestinationUrl(ABSOLUTE_BASE_URL, "https://trasparenza-pa.net/?codcli=SC26149");
    Assertions.assertTrue(joined.isPresent());
    Assertions.assertEquals(target, joined.get());
  }

  @Test
  public void emptyUrls() {
    var joined = UrlResolver.getDestinationUrl(ABSOLUTE_BASE_URL, null);
    Assertions.assertTrue(joined.isEmpty());
    joined = UrlResolver.getDestinationUrl(null, "/amministrazione-transparente");
    Assertions.assertTrue(joined.isEmpty());
    joined = UrlResolver.getDestinationUrl(null, null);
    Assertions.assertTrue(joined.isEmpty());
  }
  
  @Test
  public void illegalCharacterInQuery() {
    var target = "https://web.spaggiari.eu/sdg/app/default/trasparenza.php?sede_codice=BAIT0004&amp;referer=http:\\www.itclenoci.it";
    var joined = UrlResolver.getDestinationUrl(ABSOLUTE_BASE_URL, target);
    Assertions.assertTrue(joined.isPresent());
  }

  @Test
  public void encodeUrl() {
    var target = "https://web.spaggiari.eu/?referer=http:\\\\www.itclenoci.it";
    val sanitizedTarget = UrlResolver.sanitize(target);
    Assertions.assertEquals("https://web.spaggiari.eu/?referer=http:%5C%5Cwww.itclenoci.it", sanitizedTarget);
  }

  @Test
  public void encodeUrl2() {
    var target = "https&#x3a;&#x2f;&#x2f;www&#x2e;regione&#x2e;veneto&#x2e;it&#x2f;organizzazione";
    val sanitizedTarget = UrlResolver.sanitize(target);
    Assertions.assertEquals("https://www.regione.veneto.it/organizzazione", sanitizedTarget);
  }

  @Test
  public void saniteUrl() {
    var url = "http://og.maggioli.cloud/ATGovWeb/BURAGODIMOLGORA/EntryPoint.aspx ";
    var sanitizedUrl = UrlResolver.sanitize(url);
    Assertions.assertEquals("http://og.maggioli.cloud/ATGovWeb/BURAGODIMOLGORA/EntryPoint.aspx", sanitizedUrl);
    
    url = "http://og.maggioli.cloud/ATGovWeb/BURAGODIMOLGORA/EntryPoint.aspx ";
    sanitizedUrl = UrlResolver.sanitize(url);
    Assertions.assertEquals("http://og.maggioli.cloud/ATGovWeb/BURAGODIMOLGORA/EntryPoint.aspx", sanitizedUrl);
  }
}