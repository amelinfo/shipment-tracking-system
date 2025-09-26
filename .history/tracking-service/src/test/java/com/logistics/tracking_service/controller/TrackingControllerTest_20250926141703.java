package com.logistics.tracking_service.controller;package com.logistics.tracking_service.controller;



import com.logistics.tracking_service.dto.TrackingRequest;import com.logistics.tracking_service.dto.TrackingRequest;

import com.logistics.tracking_service.dto.TrackingResult;import com.logistics.tracking_service.dto.TrackingResult;

import com.logistics.tracking_service.service.TrackingService;import com.logistics.tracking_service.service.TrackingService;

import com.fasterxml.jackson.databind.ObjectMapper;import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;import org.springframework.test.web.servlet.MockMvc;



import java.util.Optional;import java.util.Optional;



import static org.mockito.Mockito.when;import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;



@WebMvcTest(TrackingController.class)@WebMvcTest(TrackingController.class)

class TrackingControllerTest {class TrackingControllerTest {



    @Autowired    @Autowired

    private MockMvc mockMvc;    private MockMvc mockMvc;



    @Autowired    @MockBean

    private ObjectMapper objectMapper;    private TrackingService trackingService;



    @MockitoBean    @Autowired

    private TrackingService trackingService;    private MockMvc mockMvc;



    @Test    @Autowired

    void trackShipment_ValidRequest_ReturnsSuccess() throws Exception {    private ObjectMapper objectMapper;

        // Given

        TrackingRequest request = new TrackingRequest("TEST123");    @Mock

        TrackingResult result = TrackingResult.success("TEST123", "CREATED", true, "Status processed");    private TrackingService trackingService;

        

        when(trackingService.processTracking("TEST123")).thenReturn(result);    @Test

    void trackShipment_ValidRequest_ReturnsSuccess() throws Exception {

        // When & Then        // Given

        mockMvc.perform(post("/api/v1/trackings")        TrackingRequest request = new TrackingRequest("TEST123");

                .contentType(MediaType.APPLICATION_JSON)        TrackingResult result = TrackingResult.success("TEST123", "CREATED", true, "Status processed");

                .content(objectMapper.writeValueAsString(request)))        

                .andExpect(status().isOk())        when(trackingService.processTracking("TEST123")).thenReturn(result);

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