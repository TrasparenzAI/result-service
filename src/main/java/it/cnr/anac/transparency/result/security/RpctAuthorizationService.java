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
import java.util.Optional;
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

    /**
     * Verifica che un utente RPCT (senza ruoli che danno accesso a tutte le amministrazioni)
     * stia richiedendo i dati di una delle amministrazioni presenti nel claim rpct_ipas.
     */
    public void checkCodiceIpa(Jwt jwt, String codiceIpa) {
        if (!isRestrictedRpct()) {
            return;
        }
        List<String> rpctIpas = jwt.getClaimAsStringList(CLAIM_RPCT_IPAS);
        if (rpctIpas == null || !rpctIpas.contains(codiceIpa)) {
            throw new AccessDeniedException(
                    "RPCT non autorizzato per il codice IPA: " + codiceIpa);
        }
    }

    /**
     * Come {@link #checkCodiceIpa(Jwt, String)}, ma con codice IPA facoltativo: per un utente
     * RPCT il codice IPA è obbligatorio, altrimenti otterrebbe i dati di tutte le amministrazioni.
     */
    public void checkCodiceIpa(Jwt jwt, Optional<String> codiceIpa) {
        if (!isRestrictedRpct()) {
            return;
        }
        if (codiceIpa.isEmpty() || codiceIpa.get().isBlank()) {
            throw new AccessDeniedException(
                    "RPCT: il parametro codiceIpa è obbligatorio");
        }
        checkCodiceIpa(jwt, codiceIpa.get());
    }

    /**
     * @return true se l'utente corrente ha il ruolo RPCT e nessuno dei ruoli che danno
     *     accesso ai dati di tutte le amministrazioni.
     */
    private boolean isRestrictedRpct() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.getAuthorities().contains(new SimpleGrantedAuthority(ROLE_RPCT))) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .noneMatch(UNRESTRICTED_ROLES::contains);
    }
}
