package com.example.backend.controller;

import com.example.backend.dto.IncidentRequest;
import com.example.backend.dto.IncidentResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/incident")
@CrossOrigin(origins = "*") // For local development
public class IncidentController {

        private final com.example.backend.service.ClassificationService classificationService;

        public IncidentController(com.example.backend.service.ClassificationService classificationService) {
                this.classificationService = classificationService;
        }

        @PostMapping("/analyze")
        public IncidentResponse analyzeIncident(@RequestBody IncidentRequest request) {
                IncidentResponse.Classification classification = classificationService
                                .classify(request.getIncidentDescription());

                return new IncidentResponse(
                                classification,
                                new IncidentResponse.Severity(
                                                "HIGH",
                                                "Potential account compromise in production environment."),
                                Arrays.asList("NIST-IR-4", "OWASP-A2"),
                                Arrays.asList(
                                                "Reset affected user credentials",
                                                "Review access logs for lateral movement",
                                                "Enable MFA if not already active"),
                                "This response is advisory and based on retrieved security policies.");
        }
}
