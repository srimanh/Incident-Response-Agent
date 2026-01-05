package com.example.backend.service;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class VectorStoreService {

    private final EmbeddingService embeddingService;
    private final ResourceLoader resourceLoader;
    private final Map<String, float[]> policyVectors = new HashMap<>();
    private final Map<String, String> policyContent = new HashMap<>();

    public VectorStoreService(EmbeddingService embeddingService, ResourceLoader resourceLoader) {
        this.embeddingService = embeddingService;
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init() {
        try {
            Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
                    .getResources("classpath:policies/*.txt");

            for (Resource resource : resources) {
                String fileName = resource.getFilename();
                Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
                String content = FileCopyUtils.copyToString(reader);

                float[] vector = embeddingService.getEmbedding(content);
                policyVectors.put(fileName, vector);
                policyContent.put(fileName, content);

                System.out.println("Embedded policy: " + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SearchResult findBestMatch(float[] incidentVector) {
        String bestPolicy = null;
        double maxSimilarity = -1.0;

        for (Map.Entry<String, float[]> entry : policyVectors.entrySet()) {
            double similarity = cosineSimilarity(incidentVector, entry.getValue());
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                bestPolicy = entry.getKey();
            }
        }

        if (bestPolicy != null && maxSimilarity > 0.0) {
            return new SearchResult(bestPolicy, policyContent.get(bestPolicy), maxSimilarity);
        }
        return null;
    }

    private double cosineSimilarity(float[] vectorA, float[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public static class SearchResult {
        private final String policyName;
        private final String content;
        private final double score;

        public SearchResult(String policyName, String content, double score) {
            this.policyName = policyName;
            this.content = content;
            this.score = score;
        }

        public String getPolicyName() {
            return policyName;
        }

        public String getContent() {
            return content;
        }

        public double getScore() {
            return score;
        }
    }
}
