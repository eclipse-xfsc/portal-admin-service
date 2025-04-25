package eu.gaiax.rest;

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
public class ParticipantsTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ParticipantsController service;
    @MockBean
    private ManagementService manSrv;

    @Test
    void getParticipantsList() throws Exception {
        ResponseEntity rs = ResponseEntity.ok().build();
        when(service.getParticipantsList(any())).thenReturn(rs);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/pr/registrations/search"))
                .andExpect(status().isOk());
    }

    @Test
    void getPrRegistrationTypes() throws Exception {
        ResponseEntity rs = ResponseEntity.ok().build();
        when(service.getPrRegistrationTypes(any())).thenReturn(rs);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/pr/registration-types"))
                .andExpect(status().isOk());
    }

    @Test
    void getPrDetails() throws Exception {
        ResponseEntity rs = ResponseEntity.ok().build();
        when(service.getPrDetails(any(), any())).thenReturn(rs);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/pr/registrations/12/details"))
                .andExpect(status().isOk());
    }

    @Test
    void getPrLocations() throws Exception {
        ResponseEntity rs = ResponseEntity.ok().build();
        when(service.getPrLocations(any())).thenReturn(rs);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/pr/locations"))
                .andExpect(status().isOk());
    }

}