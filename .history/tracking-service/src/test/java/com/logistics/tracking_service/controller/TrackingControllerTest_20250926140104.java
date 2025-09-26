package com.logistics.tracking_service.controller;

import com.logistics.tracking_service.*;.dto.TrackingRequest;
import com.logistics.tracking.dto.TrackingResult;
import com.logistics.tracking.service.TrackingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrackingController.class)
class TrackingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrackingService trackingService;

    @Test
    void trackShipment_ValidRequest_ReturnsSuccess() throws Exception {
        // Given
        TrackingRequest request = new TrackingRequest("TEST123");
        TrackingResult result = TrackingResult.success("TEST123", "CREATED", true, "Status processed");
        
        when(trackingService.processTracking("TEST123")).thenReturn(result);

        // When & Then
        mockMvc.perform(post("/api/v1/trackings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingId").value("TEST123"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void trackShipment_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Given - empty tracking ID
        TrackingRequest request = new TrackingRequest("");

        // When & Then
        mockMvc.perform(post("/api/v1/trackings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getShipmentStatus_ExistingShipment_ReturnsStatus() throws Exception {
        // Given
        when(trackingService.getCurrentStatus("TEST123")).thenReturn(Optional.of("DELIVERED"));

        // When & Then
        mockMvc.perform(get("/api/v1/trackings/TEST123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingId").value("TEST123"))
                .andExpect(jsonPath("$.currentStatus").value("DELIVERED"));
    }

    @Test
    void getShipmentStatus_NonExistent_ReturnsNotFound() throws Exception {
        // Given
        when(trackingService.getCurrentStatus("UNKNOWN")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/trackings/UNKNOWN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void healthCheck_ReturnsServiceStatus() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("tracking-service"));
    }
}