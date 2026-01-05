package com.example.backend.dto;

public class IncidentRequest {
    private String incidentDescription;
    private String environment;
    private String reportedBy;

    // Default constructor for Jackson
    public IncidentRequest() {}

    public IncidentRequest(String incidentDescription, String environment, String reportedBy) {
        this.incidentDescription = incidentDescription;
        this.environment = environment;
        this.reportedBy = reportedBy;
    }

    public String getIncidentDescription() { return incidentDescription; }
    public void setIncidentDescription(String incidentDescription) { this.incidentDescription = incidentDescription; }

    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }

    public String getReportedBy() { return reportedBy; }
    public void setReportedBy(String reportedBy) { this.reportedBy = reportedBy; }
}
