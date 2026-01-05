package com.example.backend.service;

import com.example.backend.dto.IncidentResponse.Classification;
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
public class ClassificationService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${llm.api.key}")
    private String apiKey;

    @Value("${llm.api.url}")
    private String apiUrl;

    @Value("${llm.model}")
    private String model;

    public ClassificationService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public Classification classify(String incidentDescription) {
        if (apiKey == null || apiKey.equals("your_api_key_here")) {
            // Safe fallback for demo if no key is provided
            return new Classification("Unknown", 1.0, "API Key missing. Defaulting to Unknown.");
        }

        String prompt = "You are a cybersecurity incident classification assistant.\n\n" +
                "Classify the incident into ONE of the following categories only:\n" +
                "Malware, Phishing, Unauthorized Access, Data Breach, Misconfiguration, Unknown.\n\n" +
                "Rules:\n" +
                "- Do NOT invent new categories.\n" +
                "- If unclear, return \"Unknown\".\n" +
                "- Return confidence between 0 and 1.\n" +
                "- Respond ONLY in JSON.\n\n" +
                "Example response:\n" +
                "{\"type\": \"Unauthorized Access\", \"confidence\": 0.84, \"explanation\": \"Reasoning here\"}\n\n" +
                "Incident Description:\n" +
                incidentDescription;

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

            JsonNode classificationNode = objectMapper.readTree(content);
            String type = classificationNode.path("type").asText();
            double confidence = classificationNode.path("confidence").asDouble();
            String explanation = classificationNode.path("explanation").asText();

            // Safety threshold
            if (confidence < 0.6) {
                return new Classification("Unknown", confidence, "Low confidence: " + explanation);
            }

            return new Classification(type, confidence, explanation);

        } catch (Exception e) {
            e.printStackTrace();
            return new Classification("Unknown", 0.0, "Error during classification: " + e.getMessage());
        }
    }
}
