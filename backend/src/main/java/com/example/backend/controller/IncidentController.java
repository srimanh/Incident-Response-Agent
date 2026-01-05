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
        private final com.example.backend.service.EmbeddingService embeddingService;
        private final com.example.backend.service.VectorStoreService vectorStoreService;

        public IncidentController(com.example.backend.service.ClassificationService classificationService,
                        com.example.backend.service.EmbeddingService embeddingService,
                        com.example.backend.service.VectorStoreService vectorStoreService) {
                this.classificationService = classificationService;
                this.embeddingService = embeddingService;
                this.vectorStoreService = vectorStoreService;
        }

        @PostMapping("/analyze")
        public IncidentResponse analyzeIncident(@RequestBody IncidentRequest request) {
                // Step 1: Classification
                IncidentResponse.Classification classification = classificationService
                                .classify(request.getIncidentDescription());

                // Step 2: Semantic Retrieval (Hour 4)
                float[] incidentVector = embeddingService.getEmbedding(request.getIncidentDescription());
                com.example.backend.service.VectorStoreService.SearchResult match = vectorStoreService
                                .findBestMatch(incidentVector);

                String policyInfo = "No matching policy found.";
                String policyId = "NONE";

                if (match != null) {
                        policyId = match.getPolicyName();
                        policyInfo = match.getContent();
                        System.out.println("Semantic match: " + policyId + " (Score: " + match.getScore() + ")");
                }

                return new IncidentResponse(
                                classification,
                                new IncidentResponse.Severity(
                                                "HIGH",
                                                "Potential account compromise in production environment."),
                                Arrays.asList("RAG-SEARCH", policyId),
                                Arrays.asList(
                                                "Reset affected user credentials",
                                                "Review access logs for lateral movement",
                                                "Enable MFA if not already active"),
                                "Advisory grounded in: " + policyId + ". Score: "
                                                + (match != null ? String.format("%.2f", match.getScore()) : "0"));
        }
}
