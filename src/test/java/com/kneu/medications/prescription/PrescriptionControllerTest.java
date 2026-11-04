// Tests for the prescription API - 7 checks: create, list, and every error case.
// Calls: MockMvc (fake browser), MedicationRepository (seeds a drug). Tests: PrescriptionController through the whole stack.
package com.kneu.medications.prescription;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kneu.medications.Medication;
import com.kneu.medications.MedicationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PrescriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MedicationRepository medications;

    private Medication aspirin;

    @BeforeEach
    void seedMedication() {
        aspirin = medications.save(new Medication("Aspirin", "100mg"));
    }

    @Test
    void createPrescription_returns201WithLocationAndBody() throws Exception {
        mockMvc.perform(post("/prescriptions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"medicationId": %d, "timeOfDay": "08:00", "label": "Morning pills"}
                        """.formatted(aspirin.getId())))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", org.hamcrest.Matchers.matchesPattern(".*/prescriptions/\\d+")))
            .andExpect(jsonPath("$.medicationId").value(aspirin.getId()))
            .andExpect(jsonPath("$.medicationName").value("Aspirin"))
            .andExpect(jsonPath("$.timeOfDay").value("08:00:00"))
            .andExpect(jsonPath("$.label").value("Morning pills"));
    }

    @Test
    void getAllPrescriptions_returnsPreviouslyCreatedOnes() throws Exception {
        createPrescription("08:00", "Morning");
        createPrescription("20:30", null);

        mockMvc.perform(get("/prescriptions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].timeOfDay").value("08:00:00"))
            .andExpect(jsonPath("$[1].label").doesNotExist());
    }

    @Test
    void createPrescription_withUnknownMedication_returns404() throws Exception {
        mockMvc.perform(post("/prescriptions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"medicationId": 999999, "timeOfDay": "08:00"}
                        """))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("Medication 999999 does not exist"));
    }

    @Test
    void createPrescription_withMissingTimeOfDay_returns400() throws Exception {
        mockMvc.perform(post("/prescriptions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"medicationId": %d}
                        """.formatted(aspirin.getId())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.message").value("timeOfDay is required (e.g. \"08:00\")"));
    }

    @Test
    void createPrescription_withMalformedTime_returns400() throws Exception {
        mockMvc.perform(post("/prescriptions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"medicationId": %d, "timeOfDay": "tomorrow morning"}
                        """.formatted(aspirin.getId())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }

    @Test
    void createPrescription_withMissingMedicationId_returns400() throws Exception {
        mockMvc.perform(post("/prescriptions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"timeOfDay": "08:00"}
                        """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.message").value("medicationId is required"));
    }

    @Test
    void getUnknownPrescription_returns404() throws Exception {
        mockMvc.perform(get("/prescriptions/999999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    private String createPrescription(String timeOfDay, String label) throws Exception {
        String body = """
            {"medicationId": %d, "timeOfDay": "%s"%s}
            """.formatted(aspirin.getId(), timeOfDay, label == null ? "" : (", \"label\": \"" + label + "\""));

        String location = mockMvc.perform(post("/prescriptions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getHeader("Location");
        return location;
    }
}
