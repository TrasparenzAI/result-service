/*
 * Copyright (C) 2026 Consiglio Nazionale delle Ricerche
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

import it.cnr.anac.transparency.result.security.RpctAuthorizationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RpctAuthorizationServiceTest {

    private final RpctAuthorizationService service = new RpctAuthorizationService();

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    private Jwt buildJwt(List<String> rpctIpas) {
        var builder = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("sub", "user")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600));
        if (rpctIpas != null) {
            builder.claim("rpct_ipas", rpctIpas);
        }
        return builder.build();
    }

    private void setAuthWithRoles(Jwt jwt, String... roles) {
        var authorities = java.util.Arrays.stream(roles)
                .map(SimpleGrantedAuthority::new)
                .toList();
        var authentication = new JwtAuthenticationToken(jwt, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void rpctAutorizzatoPerCodiceIpa() {
        var jwt = buildJwt(List.of("cnr", "infn_fr", "ingv"));
        setAuthWithRoles(jwt, "ROLE_RPCT");

        assertDoesNotThrow(() -> service.checkCodiceIpa(jwt, "cnr"));
    }

    @Test
    void rpctNonAutorizzatoPerCodiceIpa() {
        var jwt = buildJwt(List.of("cnr", "infn_fr", "ingv"));
        setAuthWithRoles(jwt, "ROLE_RPCT");

        assertThrows(AccessDeniedException.class, () -> service.checkCodiceIpa(jwt, "miur"));
    }

    @Test
    void rpctSenzaClaimRpctIpas() {
        var jwt = buildJwt(null);
        setAuthWithRoles(jwt, "ROLE_RPCT");

        assertThrows(AccessDeniedException.class, () -> service.checkCodiceIpa(jwt, "cnr"));
    }

    @Test
    void utenteNonRpctPassaSenzaControllo() {
        var jwt = buildJwt(null);
        setAuthWithRoles(jwt, "ROLE_ADMIN");

        assertDoesNotThrow(() -> service.checkCodiceIpa(jwt, "qualsiasi"));
    }

    @Test
    void utenteNonAutenticatoPassaSenzaControllo() {
        SecurityContextHolder.clearContext();
        var jwt = buildJwt(null);

        assertDoesNotThrow(() -> service.checkCodiceIpa(jwt, "qualsiasi"));
    }
}
