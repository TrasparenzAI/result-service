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

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configurazione dei parametri generali della documentazione tramite OpenAPI.
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(title = "Transparency Results Service", 
    version = "0.2.0", 
    description = "Transparency Results Service si occupa di gestire i risultati delle verifiche di conformità "
        + "sulla legge della trasparenza del decreto legge 33/2013 per i siti degli enti pubblici italiani."),
    servers = {
        @Server(url = "/result-service", description = "Transparency Results Service URL"),
        @Server(url = "/", description = "Transparency Results Service URL")}
    )
@SecuritySchemes(value = {
    @SecurityScheme(
        name = "bearer_authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer")
})
public class OpenApiConfiguration {

  public static final String BEARER_AUTHENTICATION = "Bearer Authentication";

}