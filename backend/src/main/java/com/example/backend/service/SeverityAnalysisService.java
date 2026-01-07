package com.example.backend.service;

import com.example.backend.dto.IncidentResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeverityAnalysisService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${llm.api.key}")
    private String apiKey;

    @Value("${llm.api.url}")
    private String apiUrl;

    @Value("${llm.model}")
    private String model;

    public SeverityAnalysisService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public IncidentResponse.Severity analyze(String description, String type, String environment,
            String policyContext) {
        if (apiKey == null || apiKey.equals("your_api_key_here")) {
            return new IncidentResponse.Severity("MEDIUM", "Demo mode: API Key missing.");
        }

        String prompt = "You are a cybersecurity risk analyst.\n\n" +
                "Based on the incident description, incident type, environment, and security policy,\n" +
                "assign ONE severity level: LOW, MEDIUM, HIGH, or CRITICAL.\n\n" +
                "Explain your reasoning briefly.\n" +
                "Respond ONLY in JSON.\n\n" +
                "Context:\n" +
                "- Incident Type: " + type + "\n" +
                "- Environment: " + environment + "\n" +
                "- Policy Context: " + policyContext + "\n" +
                "- Incident Description: " + description + "\n\n" +
                "Example response:\n" +
                "{\"level\": \"HIGH\", \"reason\": \"Unauthorized access in production...\"}";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("X-Title", "Incident Response Agent");

            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("messages", List.of(
                    Map.of("role", "user", "content", prompt)));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            String response = restTemplate.postForObject(apiUrl, request, String.class);

            JsonNode root = objectMapper.readTree(response);
            String content = root.path("choices").get(0).path("message").path("content").asText();

            // Handle potential markdown backticks in LLM response
            if (content.contains("```json")) {
                content = content.substring(content.indexOf("```json") + 7, content.lastIndexOf("```"));
            } else if (content.contains("```")) {
                content = content.substring(content.indexOf("```") + 3, content.lastIndexOf("```"));
            }

            JsonNode severityNode = objectMapper.readTree(content);
            String level = severityNode.path("level").asText().toUpperCase();
            String reason = severityNode.path("reason").asText();

            // Validate level
            if (!List.of("LOW", "MEDIUM", "HIGH", "CRITICAL").contains(level)) {
                level = "MEDIUM"; // Fallback
            }

            return new IncidentResponse.Severity(level, reason);

        } catch (Exception e) {
            e.printStackTrace();
            return new IncidentResponse.Severity("MEDIUM", "Error during analysis: " + e.getMessage());
        }
    }
}
