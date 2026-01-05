package com.example.backend.dto;

import java.util.List;

public class IncidentResponse {
    private Classification classification;
    private Severity severity;
    private List<String> policyReferences;
    private List<String> recommendedActions;
    private String notes;

    public IncidentResponse() {}

    public IncidentResponse(Classification classification, Severity severity, List<String> policyReferences, List<String> recommendedActions, String notes) {
        this.classification = classification;
        this.severity = severity;
        this.policyReferences = policyReferences;
        this.recommendedActions = recommendedActions;
        this.notes = notes;
    }

    // Getters and Setters
    public Classification getClassification() { return classification; }
    public void setClassification(Classification classification) { this.classification = classification; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public List<String> getPolicyReferences() { return policyReferences; }
    public void setPolicyReferences(List<String> policyReferences) { this.policyReferences = policyReferences; }

    public List<String> getRecommendedActions() { return recommendedActions; }
    public void setRecommendedActions(List<String> recommendedActions) { this.recommendedActions = recommendedActions; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public static class Classification {
        private String type;
        private double confidence;
        private String explanation;

        public Classification() {}
        public Classification(String type, double confidence, String explanation) {
            this.type = type;
            this.confidence = confidence;
            this.explanation = explanation;
        }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }
    }

    public static class Severity {
        private String level;
        private String reason;

        public Severity() {}
        public Severity(String level, String reason) {
            this.level = level;
            this.reason = reason;
        }
        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}
