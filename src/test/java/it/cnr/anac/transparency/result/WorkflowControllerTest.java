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
import it.cnr.anac.transparency.result.models.Workflow;
import it.cnr.anac.transparency.result.repositories.WorkflowDao;
import it.cnr.anac.transparency.result.repositories.WorkflowRepository;
import it.cnr.anac.transparency.result.v1.controllers.WorkflowController;
import it.cnr.anac.transparency.result.v1.dto.WorkflowDtoToEntityConverter;
import it.cnr.anac.transparency.result.v1.dto.WorkflowMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WorkflowController.class)
@AutoConfigureMockMvc(addFilters = false) // evita dipendenze da auth nei test REST
class WorkflowControllerTest extends PostgresTestContainerBase {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean
    private WorkflowRepository workflowRepository;
    @MockitoBean
    private WorkflowMapper mapper;
    @MockitoBean
    private WorkflowDao workflowDao;
    @MockitoBean
    private WorkflowDtoToEntityConverter dtoToEntityConverter;

    @Test
    void show_ok() throws Exception {
        when(workflowRepository.findById(1L)).thenReturn(Optional.of(new Workflow()));
        when(mapper.convert(any(Workflow.class))).thenReturn(null);

        mockMvc.perform(get("/v1/workflows/1"))
                .andExpect(status().isOk());

        verify(workflowRepository).findById(1L);
        verify(mapper).convert(any(Workflow.class));
        verifyNoMoreInteractions(workflowDao, dtoToEntityConverter);
    }

    @Test
    void list_ok() throws Exception {
        when(workflowDao.find(any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new Workflow()), PageRequest.of(0, 20), 1));
        when(mapper.convert(any(Workflow.class))).thenReturn(null);

        mockMvc.perform(get("/v1/workflows")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk());

        verify(workflowDao).find(
                any(Optional.class),
                any(Optional.class),
                any(Optional.class),
                any(Optional.class),
                any()
        );
        verify(mapper, atLeastOnce()).convert(any(Workflow.class));
        verifyNoMoreInteractions(workflowRepository, dtoToEntityConverter);
    }

    @Test
    void listConductorLike_ok_withCodiceIpa() throws Exception {
        when(workflowRepository.findByCodiceIpa("IPA001")).thenReturn(List.of(new Workflow()));
        when(mapper.convertToConductor(any(Workflow.class))).thenReturn(null);

        mockMvc.perform(get("/v1/workflows/listConductorLike")
                        .param("codiceIpa", "IPA001"))
                .andExpect(status().isOk());

        verify(workflowRepository).findByCodiceIpa("IPA001");
        verify(mapper, atLeastOnce()).convertToConductor(any(Workflow.class));
        verifyNoMoreInteractions(workflowDao, dtoToEntityConverter);
    }

    @Test
    void create_created() throws Exception {
        Workflow entity = new Workflow();
        when(dtoToEntityConverter.createEntity(any())).thenReturn(entity);
        when(workflowRepository.save(any(Workflow.class))).thenReturn(entity);
        when(mapper.convert(any(Workflow.class))).thenReturn(null);

        // DTO senza vincoli @NotNull: basta JSON valido
        String jsonBody = objectMapper.writeValueAsString(java.util.Collections.emptyMap());

        mockMvc.perform(put("/v1/workflows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated());

        verify(dtoToEntityConverter).createEntity(any());
        verify(workflowRepository).save(any(Workflow.class));
        verify(mapper).convert(any(Workflow.class));
        verifyNoMoreInteractions(workflowDao);
    }

    @Test
    void update_ok() throws Exception {
        Workflow entity = new Workflow();
        when(dtoToEntityConverter.updateEntity(any())).thenReturn(entity);
        when(workflowRepository.save(any(Workflow.class))).thenReturn(entity);
        when(mapper.convert(any(Workflow.class))).thenReturn(null);

        // serve almeno "id" per la updateEntity (repo.findById verrà gestito nel converter mockato)
        String jsonBody = """
                {
                  "id": 1,
                  "workflowId": "wf-1",
                  "codiceIpa": "IPA001",
                  "status": "RUNNING",
                  "startTime": 1,
                  "endTime": 2
                }
                """;

        mockMvc.perform(post("/v1/workflows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk());

        verify(dtoToEntityConverter).updateEntity(any());
        verify(workflowRepository).save(any(Workflow.class));
        verify(mapper).convert(any(Workflow.class));
        verifyNoMoreInteractions(workflowDao);
    }

    @Test
    void delete_ok() throws Exception {
        Workflow entity = new Workflow();
        when(workflowRepository.findById(1L)).thenReturn(Optional.of(entity));

        mockMvc.perform(delete("/v1/workflows/1"))
                .andExpect(status().isOk());

        verify(workflowRepository).findById(1L);
        verify(workflowRepository).delete(entity);
        verifyNoMoreInteractions(workflowDao, dtoToEntityConverter, mapper);
    }

    @Test
    void deleteByWorkflowId_ok() throws Exception {
        when(workflowRepository.deleteByWorkflowId("wf-xyz")).thenReturn(1L);

        mockMvc.perform(delete("/v1/workflows/byWorkflow/wf-xyz"))
                .andExpect(status().isOk());

        verify(workflowRepository).deleteByWorkflowId("wf-xyz");
        verifyNoMoreInteractions(workflowDao, dtoToEntityConverter, mapper);
    }
}
