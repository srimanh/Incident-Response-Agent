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
        private final com.example.backend.service.SeverityAnalysisService severityAnalysisService;

        public IncidentController(com.example.backend.service.ClassificationService classificationService,
                        com.example.backend.service.EmbeddingService embeddingService,
                        com.example.backend.service.VectorStoreService vectorStoreService,
                        com.example.backend.service.SeverityAnalysisService severityAnalysisService) {
                this.classificationService = classificationService;
                this.embeddingService = embeddingService;
                this.vectorStoreService = vectorStoreService;
                this.severityAnalysisService = severityAnalysisService;
        }

        // Safety Threshold (Hour 5)
        private static final double SIMILARITY_THRESHOLD = 0.75;

        @PostMapping("/analyze")
        public IncidentResponse analyzeIncident(@RequestBody IncidentRequest request) {
                // Step 1: Semantic Retrieval (Moved before Classification for safety gating)
                float[] incidentVector = embeddingService.getEmbedding(request.getIncidentDescription());
                com.example.backend.service.VectorStoreService.SearchResult match = vectorStoreService
                                .findBestMatch(incidentVector);

                double score = (match != null) ? match.getScore() : 0.0;
                String policyId = (match != null) ? match.getPolicyName() : "NONE";
                String policyInfo = (match != null) ? match.getContent() : "No matching policy found.";

                // Step 2: Safety Gating
                if (score < SIMILARITY_THRESHOLD) {
                        System.out.println("GATE: FAILED | Incident Type: SKIPPED | Score: " + score
                                        + " | Action: REFUSED");
                        return IncidentResponse.createRefusal(
                                        "No sufficiently relevant security policy was found for this incident.",
                                        "Please provide more context or consult a security professional.");
                }

                // Step 3: Classification (Only if gate passes)
                IncidentResponse.Classification classification = classificationService
                                .classify(request.getIncidentDescription());

                System.out.println("GATE: PASSED | Incident Type: " + classification.getType() + " | Score: " + score);

                // Step 4: Severity Analysis (Hour 6)
                IncidentResponse.Severity severity = severityAnalysisService.analyze(
                                request.getIncidentDescription(),
                                classification.getType(),
                                request.getEnvironment(),
                                policyInfo);

                return new IncidentResponse(
                                classification,
                                severity,
                                Arrays.asList("RAG-SEARCH", policyId),
                                Arrays.asList(
                                                "Reset affected user credentials",
                                                "Review access logs for lateral movement",
                                                "Enable MFA if not already active"),
                                "Advisory grounded in: " + policyId + ". Score: " + String.format("%.2f", score));
        }
}
