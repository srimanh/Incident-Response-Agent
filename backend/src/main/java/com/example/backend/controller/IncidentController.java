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
        private final com.example.backend.service.PolicyLoaderService policyLoaderService;

        public IncidentController(com.example.backend.service.ClassificationService classificationService,
                        com.example.backend.service.PolicyLoaderService policyLoaderService) {
                this.classificationService = classificationService;
                this.policyLoaderService = policyLoaderService;
        }

        @PostMapping("/analyze")
        public IncidentResponse analyzeIncident(@RequestBody IncidentRequest request) {
                IncidentResponse.Classification classification = classificationService
                                .classify(request.getIncidentDescription());

                // Hour 3: Load policies based on classification
                String policyText = policyLoaderService.loadPolicyForIncident(classification.getType());
                System.out.println("Loaded policy for " + classification.getType() + ": " + policyText);

                return new IncidentResponse(
                                classification,
                                new IncidentResponse.Severity(
                                                "HIGH",
                                                "Potential account compromise in production environment."),
                                Arrays.asList("POLICY-LOADED", classification.getType()),
                                Arrays.asList(
                                                "Reset affected user credentials",
                                                "Review access logs for lateral movement",
                                                "Enable MFA if not already active"),
                                "This response is advisory. Policies used: "
                                                + (policyText.length() > 50 ? policyText.substring(0, 50) + "..."
                                                                : policyText));
        }
}
