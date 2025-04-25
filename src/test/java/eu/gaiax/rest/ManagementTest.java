package eu.gaiax.rest;

import eu.gaiax.model.AcceptDenyRq;
import eu.gaiax.model.DetailsDto;
import eu.gaiax.model.FilterDto;
import eu.gaiax.model.FilterResult;
import eu.gaiax.service.ManagementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("unused")
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test"})
public class ManagementTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ManagementController service;
    @MockBean
    private ManagementService manSrv;

    @Test
    void getRequestsList() throws Exception {
        ResponseEntity<FilterResult> rs = ResponseEntity.ok().build();
        when(service.getRequestsList(any(), any())).thenReturn(rs);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/management/requests/search"))
                .andExpect(status().isOk());
    }

    @Test
    void getRequestTypesFilter() throws Exception {
        FilterDto body = new FilterDto();
        ResponseEntity<FilterDto> rs = ResponseEntity.ok(body);
        when(service.getRequestTypesFilter(any())).thenReturn(rs);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/management/request-types"))
                .andExpect(status().isOk());
    }

    @Test
    void getManagementFilterLocations() throws Exception {
        ResponseEntity rs = ResponseEntity.ok().build();
        when(service.getManagementFilterLocations(any())).thenReturn(rs);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/management/locations"))
                .andExpect(status().isOk());
    }

    @Test
    void getPrDetails() throws Exception {
        DetailsDto body = new DetailsDto();
        ResponseEntity<DetailsDto> rs = ResponseEntity.ok(body);
        when(service.getPrDetails(any(), any())).thenReturn(rs);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/management/requests/12/details"))
                .andExpect(status().isOk());
    }

    @Test
    void acceptDenyRequest() throws Exception {
        ResponseEntity rs = ResponseEntity.ok().build();
        AcceptDenyRq rq = new AcceptDenyRq();
        String content = JsonUtil.asJsonString(rq);
        when(service.acceptOrDenyRequest(any(), any())).thenReturn(rs);
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/admin/management/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());
    }

    @Test
    void download() throws Exception {
        ResponseEntity rs = ResponseEntity.ok().build();
        when(service.download(any(), any(), any(), any())).thenReturn(rs);
        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/admin/management/download?requestId=1&filename=sd.json")
                        .accept(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isOk());
    }

}