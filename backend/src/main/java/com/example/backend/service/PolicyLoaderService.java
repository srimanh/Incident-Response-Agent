package com.example.backend.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class PolicyLoaderService {

    private final ResourceLoader resourceLoader;
    private final Map<String, String> incidentToPolicyMap;

    public PolicyLoaderService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        this.incidentToPolicyMap = new HashMap<>();
        incidentToPolicyMap.put("Unauthorized Access", "unauthorized-access.txt");
        incidentToPolicyMap.put("Phishing", "phishing.txt");
        incidentToPolicyMap.put("Malware", "malware.txt");
        incidentToPolicyMap.put("Data Breach", "data-breach.txt");
        incidentToPolicyMap.put("Misconfiguration", "misconfiguration.txt");
    }

    public String loadPolicyForIncident(String incidentType) {
        String fileName = incidentToPolicyMap.get(incidentType);
        if (fileName == null) {
            return "No specific policy found for incident type: " + incidentType;
        }

        try {
            Resource resource = resourceLoader.getResource("classpath:policies/" + fileName);
            Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            return "Error loading policy for " + incidentType + ": " + e.getMessage();
        }
    }
}
