package com.logistics.tracking_service.controller;
package com.logistics.tracking_service.controller;package com.logistics.tracking_service.controller;



import com.logistics.tracking_service.dto.TrackingRequest;

import com.logistics.tracking_service.dto.TrackingResult;

import com.logistics.tracking_service.service.TrackingService;import com.logistics.tracking_service.dto.TrackingRequest;import com.logistics.tracking_service.dto.TrackingRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;import com.logistics.tracking_service.dto.TrackingResult;import com.logistics.tracking_service.dto.TrackingResult;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;import com.logistics.tracking_service.service.TrackingService;import com.logistics.tracking_service.service.TrackingService;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.http.MediaType;import com.fasterxml.jackson.databind.ObjectMapper;import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.Test;import org.junit.jupiter.api.Test;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;import org.springframework.test.context.bean.override.mockito.MockitoBean;import org.springframework.boot.test.mock.mockito.MockBean;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.http.MediaType;import org.springframework.http.MediaType;

@WebMvcTest(TrackingController.class)

class TrackingControllerTest {import org.springframework.test.web.servlet.MockMvc;import org.springframework.test.web.servlet.MockMvc;



    @Autowired

    private MockMvc mockMvc;

import java.util.Optional;import java.util.Optional;

    @Autowired

    private ObjectMapper objectMapper;



    @MockitoBeanimport static org.mockito.Mockito.when;import static org.mockito.Mockito.when;

    private TrackingService trackingService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

    @Test

    void trackShipment_ValidRequest_ReturnsSuccess() throws Exception {import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

        // Given

        TrackingRequest request = new TrackingRequest("TEST123");import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

        TrackingResult result = TrackingResult.success("TEST123", "CREATED", true, "Status processed");

        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

        when(trackingService.processTracking("TEST123")).thenReturn(result);



        // When & Then

        mockMvc.perform(post("/api/v1/trackings")@WebMvcTest(TrackingController.class)@WebMvcTest(TrackingController.class)

                .contentType(MediaType.APPLICATION_JSON)

                .content(objectMapper.writeValueAsString(request)))class TrackingControllerTest {class TrackingControllerTest {

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.trackingId").value("TEST123"))

                .andExpect(jsonPath("$.success").value(true));

    }    @Autowired    @Autowired



    @Test    private MockMvc mockMvc;    private MockMvc mockMvc;

    void trackShipment_InvalidRequest_ReturnsBadRequest() throws Exception {

        // Given - empty tracking ID

        TrackingRequest request = new TrackingRequest("");

    @Autowired    @MockBean

        // When & Then

        mockMvc.perform(post("/api/v1/trackings")    private ObjectMapper objectMapper;    private TrackingService trackingService;

                .contentType(MediaType.APPLICATION_JSON)

                .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isBadRequest());

    }    @MockitoBean    @Autowired



    @Test    private TrackingService trackingService;    private MockMvc mockMvc;

    void getShipmentStatus_ExistingShipment_ReturnsStatus() throws Exception {

        // Given

        when(trackingService.getCurrentStatus("TEST123")).thenReturn(Optional.of("DELIVERED"));

    @Test    @Autowired

        // When & Then

        mockMvc.perform(get("/api/v1/trackings/TEST123"))    void trackShipment_ValidRequest_ReturnsSuccess() throws Exception {    private ObjectMapper objectMapper;

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.trackingId").value("TEST123"))        // Given

                .andExpect(jsonPath("$.currentStatus").value("DELIVERED"));

    }        TrackingRequest request = new TrackingRequest("TEST123");    @Mock



    @Test        TrackingResult result = TrackingResult.success("TEST123", "CREATED", true, "Status processed");    private TrackingService trackingService;

    void getShipmentStatus_NonExistent_ReturnsNotFound() throws Exception {

        // Given        

        when(trackingService.getCurrentStatus("UNKNOWN")).thenReturn(Optional.empty());

        when(trackingService.processTracking("TEST123")).thenReturn(result);    @Test

        // When & Then

        mockMvc.perform(get("/api/v1/trackings/UNKNOWN"))    void trackShipment_ValidRequest_ReturnsSuccess() throws Exception {

                .andExpect(status().isNotFound());

    }        // When & Then        // Given



    @Test        mockMvc.perform(post("/api/v1/trackings")        TrackingRequest request = new TrackingRequest("TEST123");

    void healthCheck_ReturnsServiceStatus() throws Exception {

        mockMvc.perform(get("/api/v1/health"))                .contentType(MediaType.APPLICATION_JSON)        TrackingResult result = TrackingResult.success("TEST123", "CREATED", true, "Status processed");

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.status").value("UP"))                .content(objectMapper.writeValueAsString(request)))        

                .andExpect(jsonPath("$.service").value("tracking-service"));

    }                .andExpect(status().isOk())        when(trackingService.processTracking("TEST123")).thenReturn(result);

}
                .andExpect(jsonPath("$.trackingId").value("TEST123"))

                .andExpect(jsonPath("$.success").value(true));        // When & Then

    }        mockMvc.perform(post("/api/v1/trackings")

                .contentType(MediaType.APPLICATION_JSON)

    @Test                .content(objectMapper.writeValueAsString(request)))

    void trackShipment_InvalidRequest_ReturnsBadRequest() throws Exception {                .andExpect(status().isOk())

        // Given - empty tracking ID                .andExpect(jsonPath("$.trackingId").value("TEST123"))

        TrackingRequest request = new TrackingRequest("");                .andExpect(jsonPath("$.success").value(true));

    }

        // When & Then

        mockMvc.perform(post("/api/v1/trackings")    @Test

                .contentType(MediaType.APPLICATION_JSON)    void trackShipment_InvalidRequest_ReturnsBadRequest() throws Exception {

                .content(objectMapper.writeValueAsString(request)))        // Given - empty tracking ID

                .andExpect(status().isBadRequest());        TrackingRequest request = new TrackingRequest("");

    }

        // When & Then

    @Test        mockMvc.perform(post("/api/v1/trackings")

    void getShipmentStatus_ExistingShipment_ReturnsStatus() throws Exception {                .contentType(MediaType.APPLICATION_JSON)

        // Given                .content(objectMapper.writeValueAsString(request)))

        when(trackingService.getCurrentStatus("TEST123")).thenReturn(Optional.of("DELIVERED"));                .andExpect(status().isBadRequest());

    }

        // When & Then

        mockMvc.perform(get("/api/v1/trackings/TEST123"))    @Test

                .andExpect(status().isOk())    void getShipmentStatus_ExistingShipment_ReturnsStatus() throws Exception {

                .andExpect(jsonPath("$.trackingId").value("TEST123"))        // Given

                .andExpect(jsonPath("$.currentStatus").value("DELIVERED"));        when(trackingService.getCurrentStatus("TEST123")).thenReturn(Optional.of("DELIVERED"));

    }

        // When & Then

    @Test        mockMvc.perform(get("/api/v1/trackings/TEST123"))

    void getShipmentStatus_NonExistent_ReturnsNotFound() throws Exception {                .andExpect(status().isOk())

        // Given                .andExpect(jsonPath("$.trackingId").value("TEST123"))

        when(trackingService.getCurrentStatus("UNKNOWN")).thenReturn(Optional.empty());                .andExpect(jsonPath("$.currentStatus").value("DELIVERED"));

    }

        // When & Then

        mockMvc.perform(get("/api/v1/trackings/UNKNOWN"))    @Test

                .andExpect(status().isNotFound());    void getShipmentStatus_NonExistent_ReturnsNotFound() throws Exception {

    }        // Given

        when(trackingService.getCurrentStatus("UNKNOWN")).thenReturn(Optional.empty());

    @Test

    void healthCheck_ReturnsServiceStatus() throws Exception {        // When & Then

        mockMvc.perform(get("/api/v1/health"))        mockMvc.perform(get("/api/v1/trackings/UNKNOWN"))

                .andExpect(status().isOk())                .andExpect(status().isNotFound());

                .andExpect(jsonPath("$.status").value("UP"))    }

                .andExpect(jsonPath("$.service").value("tracking-service"));

    }    @Test

}    void healthCheck_ReturnsServiceStatus() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("tracking-service"));
    }
}