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
package it.cnr.anac.transparency.result.security;

import java.util.List;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class RpctAuthorizationService {

    private static final String ROLE_RPCT = "ROLE_RPCT";
    private static final String CLAIM_RPCT_IPAS = "rpct_ipas";
    /**
     * Ruoli che danno accesso ai risultati di tutte le amministrazioni:
     * se l'utente ne possiede almeno uno il vincolo RPCT non si applica.
     */
    private static final Set<String> UNRESTRICTED_ROLES =
            Set.of("ROLE_ADMIN", "ROLE_SUPERUSER", "ROLE_USER");

    public void checkCodiceIpa(Jwt jwt, String codiceIpa) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.getAuthorities().contains(new SimpleGrantedAuthority(ROLE_RPCT))) {
            return;
        }
        boolean unrestricted = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(UNRESTRICTED_ROLES::contains);
        if (unrestricted) {
            return;
        }

        List<String> rpctIpas = jwt.getClaimAsStringList(CLAIM_RPCT_IPAS);
        if (rpctIpas == null || !rpctIpas.contains(codiceIpa)) {
            throw new AccessDeniedException(
                    "RPCT non autorizzato per il codice IPA: " + codiceIpa);
        }
    }
}
