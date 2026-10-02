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

import com.fasterxml.jackson.databind.ObjectMapper;
import it.cnr.anac.transparency.result.models.AtMap;
import it.cnr.anac.transparency.result.repositories.AtMapDao;
import it.cnr.anac.transparency.result.repositories.AtMapRepository;
import it.cnr.anac.transparency.result.v1.controllers.AtMapController;
import it.cnr.anac.transparency.result.v1.dto.AtMapDtoToEntityConverter;
import it.cnr.anac.transparency.result.v1.dto.AtMapMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AtMapController.class,
        excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false) // evita dipendenze da auth nei test REST
class AtMapControllerTest extends PostgresTestContainerBase {

    @TestConfiguration
    static class CacheConfig {
        @Bean
        CacheManager cacheManager() {
            return new NoOpCacheManager();
        }
    }

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean
    private AtMapRepository atMapRepository;
    @MockitoBean
    private AtMapMapper mapper;
    @MockitoBean
    private AtMapDao atMapDao;
    @MockitoBean
    private AtMapDtoToEntityConverter dtoToEntityConverter;

    @Test
    void show_ok() throws Exception {
        when(atMapRepository.findById(1L)).thenReturn(Optional.of(new AtMap()));
        when(mapper.convertWithContent(any(AtMap.class))).thenReturn(null);

        mockMvc.perform(get("/v1/atmaps/1"))
                .andExpect(status().isOk());

        verify(atMapRepository).findById(1L);
        verify(mapper).convertWithContent(any(AtMap.class));
        verifyNoMoreInteractions(atMapDao, dtoToEntityConverter);
    }

    @Test
    void show_notFound() throws Exception {
        when(atMapRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/atmaps/1"))
                .andExpect(status().isNotFound());

        verify(atMapRepository).findById(1L);
        verifyNoMoreInteractions(atMapDao, dtoToEntityConverter, mapper);
    }

    @Test
    void list_ok() throws Exception {
        when(atMapDao.find(any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new AtMap()), PageRequest.of(0, 20), 1));
        when(mapper.convert(any(AtMap.class))).thenReturn(null);

        mockMvc.perform(get("/v1/atmaps")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk());

        verify(atMapDao).find(
                any(Optional.class),
                any(Optional.class),
                any(Optional.class),
                any(Optional.class),
                any()
        );
        verify(mapper, atLeastOnce()).convert(any(AtMap.class));
        verifyNoMoreInteractions(atMapRepository, dtoToEntityConverter);
    }

    @Test
    void list_ok_filteredByFound() throws Exception {
        when(atMapDao.find(any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new AtMap()), PageRequest.of(0, 20), 1));
        when(mapper.convert(any(AtMap.class))).thenReturn(null);

        mockMvc.perform(get("/v1/atmaps")
                        .param("codiceIpa", "IPA001")
                        .param("found", "true"))
                .andExpect(status().isOk());

        verify(atMapDao).find(
                eq(Optional.empty()),
                eq(Optional.of("IPA001")),
                eq(Optional.of(Boolean.TRUE)),
                eq(Optional.empty()),
                any()
        );
        verifyNoMoreInteractions(atMapRepository, dtoToEntityConverter);
    }

    @Test
    void lastByCodiceIpa_ok() throws Exception {
        when(atMapDao.lastAtMapForCodiceIpa("IPA001")).thenReturn(Optional.of(new AtMap()));
        when(mapper.convertWithContent(any(AtMap.class))).thenReturn(null);

        mockMvc.perform(get("/v1/atmaps/codiceipa/last")
                        .param("codiceIpa", "IPA001"))
                .andExpect(status().isOk());

        verify(atMapDao).lastAtMapForCodiceIpa("IPA001");
        verify(mapper).convertWithContent(any(AtMap.class));
        verifyNoMoreInteractions(atMapRepository, dtoToEntityConverter);
    }

    @Test
    void lastByCodiceIpa_notFound() throws Exception {
        when(atMapDao.lastAtMapForCodiceIpa("IPA001")).thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/atmaps/codiceipa/last")
                        .param("codiceIpa", "IPA001"))
                .andExpect(status().isNotFound());

        verify(atMapDao).lastAtMapForCodiceIpa("IPA001");
        verifyNoMoreInteractions(atMapRepository, dtoToEntityConverter, mapper);
    }

    @Test
    void create_created() throws Exception {
        AtMap entity = new AtMap();
        when(dtoToEntityConverter.createEntity(any())).thenReturn(entity);
        when(atMapRepository.save(any(AtMap.class))).thenReturn(entity);
        when(mapper.convertWithContent(any(AtMap.class))).thenReturn(null);

        String jsonBody = """
                {
                  "workflowId": "wf-1",
                  "codiceIpa": "IPA001",
                  "url": "https://www.example.org/at_map.xml",
                  "found": true,
                  "status": 200,
                  "format": "XML",
                  "discoveredBy": "ROBOTS_TXT",
                  "valid": true,
                  "content": "<?xml version=\\"1.0\\" encoding=\\"UTF-8\\"?><at_map/>"
                }
                """;

        mockMvc.perform(put("/v1/atmaps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated());

        verify(dtoToEntityConverter).createEntity(any());
        verify(atMapRepository).save(any(AtMap.class));
        verify(mapper).convertWithContent(any(AtMap.class));
        verifyNoMoreInteractions(atMapDao);
    }

    @Test
    void create_created_withEmptyBody() throws Exception {
        AtMap entity = new AtMap();
        when(dtoToEntityConverter.createEntity(any())).thenReturn(entity);
        when(atMapRepository.save(any(AtMap.class))).thenReturn(entity);
        when(mapper.convertWithContent(any(AtMap.class))).thenReturn(null);

        // DTO senza vincoli @NotNull: basta JSON valido
        String jsonBody = objectMapper.writeValueAsString(java.util.Collections.emptyMap());

        mockMvc.perform(put("/v1/atmaps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated());

        verify(dtoToEntityConverter).createEntity(any());
        verifyNoMoreInteractions(atMapDao);
    }

    @Test
    void update_ok() throws Exception {
        AtMap entity = new AtMap();
        when(dtoToEntityConverter.updateEntity(any())).thenReturn(entity);
        when(atMapRepository.save(any(AtMap.class))).thenReturn(entity);
        when(mapper.convertWithContent(any(AtMap.class))).thenReturn(null);

        // serve almeno "id" per la updateEntity (repo.findById verrà gestito nel converter mockato)
        String jsonBody = """
                {
                  "id": 1,
                  "workflowId": "wf-1",
                  "codiceIpa": "IPA001",
                  "found": false
                }
                """;

        mockMvc.perform(post("/v1/atmaps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk());

        verify(dtoToEntityConverter).updateEntity(any());
        verify(atMapRepository).save(any(AtMap.class));
        verify(mapper).convertWithContent(any(AtMap.class));
        verifyNoMoreInteractions(atMapDao);
    }

    @Test
    void delete_ok() throws Exception {
        AtMap entity = new AtMap();
        when(atMapRepository.findById(1L)).thenReturn(Optional.of(entity));

        mockMvc.perform(delete("/v1/atmaps/1"))
                .andExpect(status().isOk());

        verify(atMapRepository).findById(1L);
        verify(atMapRepository).delete(entity);
        verifyNoMoreInteractions(atMapDao, dtoToEntityConverter, mapper);
    }

    @Test
    void deleteByWorkflowId_ok() throws Exception {
        when(atMapRepository.deleteByWorkflowId("wf-xyz")).thenReturn(2L);

        mockMvc.perform(delete("/v1/atmaps/byWorkflow/wf-xyz"))
                .andExpect(status().isOk());

        verify(atMapRepository).deleteByWorkflowId("wf-xyz");
        verifyNoMoreInteractions(atMapDao, dtoToEntityConverter, mapper);
    }
}
