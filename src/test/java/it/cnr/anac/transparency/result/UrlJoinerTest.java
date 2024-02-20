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
}