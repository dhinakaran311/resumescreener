package com.ats.resumescreener.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.*;
import java.util.Locale;

/**
 * Appends a new feature row to dataset.csv whenever a resume is evaluated.
 * This keeps the ML training dataset growing with real production data.
 *
 * Label is determined by finalScore:
 * >= threshold → 1 (good candidate)
 * < threshold → 0 (bad candidate)
 */
@Component
public class DatasetAppender {

    private static final String CSV_HEADER = "tfidfScore,skillScore,matchedCount,missingCount,experienceYears," +
            "programmingScore,backendScore,frontendScore,databaseScore,cloudScore,devopsScore,label";

    /** Path to dataset.csv — configurable via application.properties */
    @Value("${ml.dataset.path:ML/dataset.csv}")
    private String datasetPath;

    /** Score threshold for label=1 (good). Default 50. */
    @Value("${ml.dataset.label.threshold:50.0}")
    private double labelThreshold;

    /**
     * Appends one row to dataset.csv.
     *
     * @param tfidfScore       TF-IDF / cosine similarity score (0–100)
     * @param skillScore       skill match score (0–100)
     * @param matchedCount     number of matched skills
     * @param missingCount     number of missing skills
     * @param experienceYears  years of experience
     * @param programmingScore category score for PROGRAMMING (0–100)
     * @param backendScore     category score for BACKEND (0–100)
     * @param frontendScore    category score for FRONTEND (0–100)
     * @param databaseScore    category score for DATABASE (0–100)
     * @param cloudScore       category score for CLOUD (0–100)
     * @param devopsScore      category score for DEVOPS (0–100)
     * @param finalScore       overall ATS score — used to determine label
     */
    public synchronized void appendRow(
            double tfidfScore,
            double skillScore,
            int matchedCount,
            int missingCount,
            int experienceYears,
            double programmingScore,
            double backendScore,
            double frontendScore,
            double databaseScore,
            double cloudScore,
            double devopsScore,
            double finalScore) {

        int label = finalScore >= labelThreshold ? 1 : 0;

        String row = String.format(Locale.US,
                "%.3f,%.3f,%d,%d,%d,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%d",
                tfidfScore, skillScore,
                matchedCount, missingCount, experienceYears,
                programmingScore, backendScore, frontendScore,
                databaseScore, cloudScore, devopsScore,
                label);

        try {
            Path path = Paths.get(datasetPath);

            // Create parent directories and file if they don't exist
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.writeString(path, CSV_HEADER + System.lineSeparator());
                System.out.println("📄 [DatasetAppender] Created new dataset: " + path.toAbsolutePath());
            }

            // Append the row (StandardOpenOption.APPEND keeps existing data)
            try (BufferedWriter writer = Files.newBufferedWriter(
                    path,
                    StandardOpenOption.APPEND,
                    StandardOpenOption.CREATE)) {
                writer.write(row);
                writer.newLine();
            }

            System.out.printf("✅ [DatasetAppender] Row appended → label=%d | score=%.1f%n",
                    label, finalScore);

        } catch (IOException e) {
            System.err.println("⚠ [DatasetAppender] Failed to write dataset row: " + e.getMessage());
            // Non-fatal — main ATS flow continues even if logging fails
        }
    }
}
