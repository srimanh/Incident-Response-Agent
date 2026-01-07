package com.example.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResponsePlaybookService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${llm.api.key}")
    private String apiKey;

    @Value("${llm.api.url}")
    private String apiUrl;

    @Value("${llm.model}")
    private String model;

    public ResponsePlaybookService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<String> generatePlaybook(String description, String type, String severity, String policyContext) {
        if (apiKey == null || apiKey.equals("your_api_key_here")) {
            return List.of("Contact Security Team IMMEDIATELY.", "Isolate the machine.", "Do not power off.");
        }

        if (policyContext == null || policyContext.isEmpty() || policyContext.equals("No matching policy found.")) {
            // Return generic safe defaults if no policy context
            return List.of("Isolate affected systems if applicable", "Review relevant access logs",
                    "Notify the security team");
        }

        String prompt = "You are a cybersecurity incident response advisor.\n\n" +
                "Using ONLY the provided security policy and incident details,\n" +
                "recommend step-by-step response actions.\n\n" +
                "Rules:\n" +
                "- Do NOT suggest automated actions.\n" +
                "- Use advisory language.\n" +
                "- Keep steps concise and practical.\n" +
                "- Respond ONLY in JSON.\n\n" +
                "Context:\n" +
                "- Incident Type: " + type + "\n" +
                "- Severity: " + severity + "\n" +
                "- Policy Context: " + policyContext + "\n" +
                "- Incident Description: " + description + "\n\n" +
                "Example response:\n" +
                "{ \"recommendedActions\": [\n" +
                "    \"Isolate affected systems if applicable\",\n" +
                "    \"Review relevant access logs\"\n" +
                "  ]\n" +
                "}";

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

            // Handle potential markdown backticks
            if (content.contains("```json")) {
                content = content.substring(content.indexOf("```json") + 7, content.lastIndexOf("```"));
            } else if (content.contains("```")) {
                content = content.substring(content.indexOf("```") + 3, content.lastIndexOf("```"));
            }

            JsonNode playbookNode = objectMapper.readTree(content);
            List<String> actions = new ArrayList<>();
            if (playbookNode.has("recommendedActions")) {
                for (JsonNode action : playbookNode.get("recommendedActions")) {
                    actions.add(action.asText());
                }
            }

            // Fallback if empty parsing
            if (actions.isEmpty()) {
                actions.add("Consult internal security documentation.");
            }

            // Trim to max 6 steps
            if (actions.size() > 6) {
                return actions.subList(0, 6);
            }

            return actions;

        } catch (Exception e) {
            e.printStackTrace();
            return List.of("Error generating playbook: " + e.getMessage(), "Contact Security Operations.");
        }
    }
}
