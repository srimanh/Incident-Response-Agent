package com.example.backend.controller;

import com.example.backend.dto.IncidentRequest;
import com.example.backend.dto.IncidentResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/incident")
@CrossOrigin(origins = "*") // For local development
public class IncidentController {

    @PostMapping("/analyze")
    public IncidentResponse analyzeIncident(@RequestBody IncidentRequest request) {
        // Hardcoded stub as per Hour 1 requirement
        return new IncidentResponse(
                new IncidentResponse.Classification(
                        "Unauthorized Access",
                        0.86,
                        "Login activity does not match known user behavior."
                ),
                new IncidentResponse.Severity(
                        "HIGH",
                        "Potential account compromise in production environment."
                ),
                Arrays.asList("NIST-IR-4", "OWASP-A2"),
                Arrays.asList(
                        "Reset affected user credentials",
                        "Review access logs for lateral movement",
                        "Enable MFA if not already active"
                ),
                "This response is advisory and based on retrieved security policies."
        );
    }
}
