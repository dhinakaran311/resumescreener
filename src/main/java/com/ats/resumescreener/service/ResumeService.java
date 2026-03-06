package com.ats.resumescreener.service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.*;
import com.ats.resumescreener.util.TextCleaner;
import com.ats.resumescreener.util.SkillDictionary;
import com.ats.resumescreener.util.PdfUtil;

import com.ats.resumescreener.model.CandidateResult;
import com.ats.resumescreener.entity.Candidate;
import com.ats.resumescreener.entity.JobDescription;
import com.ats.resumescreener.entity.MatchResult;
import com.ats.resumescreener.repository.CandidateRepository;
import com.ats.resumescreener.repository.JobDescriptionRepository;
import com.ats.resumescreener.repository.MatchResultRepository;
import com.ats.resumescreener.util.VectorUtil;
import com.ats.resumescreener.util.SimilarityUtil;
import com.ats.resumescreener.util.ExperienceUtil;
import com.ats.resumescreener.util.DatasetAppender;
import com.ats.resumescreener.model.ScoreExplanation;
import com.ats.resumescreener.model.FeatureVector;

@Service
public class ResumeService {

    @Autowired
    private CandidateRepository candidateRepo;

    @Autowired
    private JobDescriptionRepository jdRepo;

    @Autowired
    private MatchResultRepository resultRepo;

    @Autowired
    private DatasetAppender datasetAppender;

    public String handleFile(MultipartFile file) {

        try {
            String raw = PdfUtil.extractText(file);
            Map<String, Integer> categoryCount = new HashMap<>();

            var cleaned = TextCleaner.clean(raw);

            SkillDictionary.SKILL_MAP.forEach((category, skills) -> {

                int count = (int) cleaned.stream()
                        .filter(skills::contains)
                        .count();

                if (count > 0)
                    categoryCount.put(category, count);
            });

            System.out.println(categoryCount);
            return "Resume processed. Skills detected: " + categoryCount;

        } catch (IOException e) {
            return "Error reading PDF";
        }
    }

    public String matchResume(MultipartFile file, String jobDescription) {

        try {
            var resumeWords = TextCleaner.clean(PdfUtil.extractText(file));
            var jdWords = TextCleaner.clean(jobDescription);

            // Skill-based matching
            var candidateSkills = extractSkills(resumeWords);
            var requiredSkills = extractSkills(jdWords);

            if (requiredSkills.isEmpty())
                return "No valid skills found in Job Description.";

            long matched = requiredSkills.stream()
                    .filter(candidateSkills::contains)
                    .count();

            double skillScore = (double) matched / requiredSkills.size() * 100;

            // TF-IDF + Cosine Similarity (Global Corpus)
            List<List<String>> corpus = new ArrayList<>();
            candidateRepo.findAll().forEach(c -> {
                if (c.getResumeText() != null)
                    corpus.add(TextCleaner.clean(c.getResumeText()));
            });
            corpus.add(resumeWords);
            corpus.add(jdWords);
            Set<String> vocab = new HashSet<>(resumeWords);
            jdWords.forEach(vocab::add);
            var resumeVector = VectorUtil.vectorize(resumeWords, vocab, corpus);
            var jdVector = VectorUtil.vectorize(jdWords, vocab, corpus);
            double similarity = SimilarityUtil.cosineSimilarity(resumeVector, jdVector) * 100;

            double overallScore = (skillScore * 0.6) + (similarity * 0.4);

            return "Overall Match Score: " + (int) overallScore + "%" +
                    "\nSkill Match Score: " + (int) skillScore + "%" +
                    "\nTF-IDF Match Score: " + (int) similarity + "%" +
                    "\nMatched Skills: " + matched +
                    "\nMissing Skills: " +
                    requiredSkills.stream()
                            .filter(skill -> !candidateSkills.contains(skill))
                            .toList();

        } catch (IOException e) {
            return "Error reading PDF";
        }
    }

    private Set<String> extractSkills(List<String> words) {

        Set<String> foundSkills = new HashSet<>();

        SkillDictionary.SKILL_MAP.values().forEach(skills -> {
            words.stream()
                    .filter(skills::contains)
                    .forEach(foundSkills::add);
        });

        return foundSkills;
    }

    private CandidateResult evaluateCandidate(
            MultipartFile file,
            String jobDescription) throws Exception {

        String rawText = PdfUtil.extractText(file);
        int experienceYears = ExperienceUtil.extractYears(rawText);

        var resumeWords = TextCleaner.clean(rawText);
        var jdWords = TextCleaner.clean(jobDescription);

        var candidateSkills = extractSkills(resumeWords);
        var requiredSkills = extractSkills(jdWords);

        if (requiredSkills.isEmpty())
            return new CandidateResult(
                    file.getOriginalFilename(),
                    0,
                    List.of(),
                    List.of(),
                    new HashMap<>());

        var matchedSkills = requiredSkills.stream()
                .filter(candidateSkills::contains)
                .toList();

        var missingSkills = requiredSkills.stream()
                .filter(skill -> !candidateSkills.contains(skill))
                .toList();

        double skillScore = (double) matchedSkills.size()
                / requiredSkills.size() * 100;

        // Category-based scoring
        Map<String, Double> categoryScores = new HashMap<>();

        SkillDictionary.SKILL_MAP.forEach((category, skills) -> {

            long requiredCount = requiredSkills.stream()
                    .filter(skills::contains)
                    .count();

            if (requiredCount == 0) {
                categoryScores.put(category, 0.0);
                return;
            }

            long matchedCount = matchedSkills.stream()
                    .filter(skills::contains)
                    .count();

            double catScore = (double) matchedCount / requiredCount * 100;
            categoryScores.put(category, catScore);
        });

        // Build Global Corpus for TF-IDF across all stored resumes
        List<List<String>> corpus = new ArrayList<>();
        candidateRepo.findAll().forEach(c -> {
            if (c.getResumeText() != null)
                corpus.add(TextCleaner.clean(c.getResumeText()));
        });
        corpus.add(resumeWords);
        corpus.add(jdWords);

        Set<String> vocab = new HashSet<>();
        corpus.forEach(vocab::addAll);

        var resumeVector = VectorUtil.vectorize(resumeWords, vocab, corpus);
        var jdVector = VectorUtil.vectorize(jdWords, vocab, corpus);

        double similarity = SimilarityUtil.cosineSimilarity(resumeVector, jdVector) * 100;

        double experienceBoost = Math.min(experienceYears, 5) * 2;

        // Build ML Feature Vector
        List<Double> featureVector = buildFeatureVector(
                similarity,
                skillScore,
                matchedSkills.size(),
                missingSkills.size(),
                experienceYears,
                categoryScores);

        // ML Prediction
        double mlScore = com.ats.resumescreener.util.MLModelUtil.predict(featureVector);
        System.out.println("ML Score: " + mlScore);

        double ruleScore = (skillScore * 0.6) + (similarity * 0.4);

        // Hybrid Score: 70% ML, 30% Rules
        double finalScore = (mlScore * 100 * 0.7) + (ruleScore * 0.3) + experienceBoost;

        ScoreExplanation explanation = new ScoreExplanation(
                skillScore,
                similarity,
                experienceYears,
                experienceBoost,
                categoryScores);

        FeatureVector fv = new FeatureVector(file.getOriginalFilename(), featureVector);
        System.out.println("Feature Vector [" + fv.getCandidateName() + "]: " + fv.getFeatures());

        // Auto-append this resume's features to ML training dataset
        datasetAppender.appendRow(
                similarity, // tfidfScore
                skillScore, // skillScore
                matchedSkills.size(), // matchedCount
                missingSkills.size(), // missingCount
                experienceYears, // experienceYears
                categoryScores.getOrDefault("PROGRAMMING", 0.0), // programmingScore
                categoryScores.getOrDefault("BACKEND", 0.0), // backendScore
                categoryScores.getOrDefault("FRONTEND", 0.0), // frontendScore
                categoryScores.getOrDefault("DATABASE", 0.0), // databaseScore
                categoryScores.getOrDefault("CLOUD", 0.0), // cloudScore
                categoryScores.getOrDefault("DEVOPS", 0.0), // devopsScore
                finalScore // used to derive label
        );

        // Persist to DB
        Candidate candidate = candidateRepo.save(
                new Candidate(file.getOriginalFilename(), rawText));

        JobDescription jdEntity = jdRepo.save(
                new JobDescription(jobDescription));

        MatchResult matchResult = new MatchResult();
        matchResult.setFinalScore(finalScore);
        matchResult.setSkillScore(skillScore);
        matchResult.setTfidfScore(similarity);
        matchResult.setMatchedCount(matchedSkills.size());
        matchResult.setMissingCount(missingSkills.size());
        matchResult.setExperienceYears(experienceYears);
        matchResult.setCandidate(candidate);
        matchResult.setJobDescription(jdEntity);
        resultRepo.save(matchResult);

        return new CandidateResult(
                file.getOriginalFilename(),
                finalScore,
                matchedSkills,
                missingSkills,
                categoryScores,
                explanation);
    }

    public List<CandidateResult> rankCandidates(
            MultipartFile[] files,
            String jd) {

        List<CandidateResult> results = new ArrayList<>();

        for (MultipartFile file : files) {

            try {
                results.add(evaluateCandidate(file, jd));
            } catch (Exception e) {
                System.err.println("❌ Failed to process: " + file.getOriginalFilename() + " → " + e.getMessage());
                e.printStackTrace();
            }
        }

        return results.stream()
                .sorted((a, b) -> {
                    int scoreCompare = Double.compare(b.getScore(), a.getScore());

                    if (scoreCompare != 0)
                        return scoreCompare;

                    return Integer.compare(
                            b.getMatchedCount(),
                            a.getMatchedCount());
                })
                .toList();
    }

    public List<MatchResult> getAllResults() {
        return resultRepo.findAll();
    }

    public List<MatchResult> filterByScore(double score) {
        return resultRepo.findByFinalScoreGreaterThanEqual(score);
    }

    public List<MatchResult> filterByExperience(int years) {
        return resultRepo.findByExperienceYearsGreaterThanEqual(years);
    }

    public List<MatchResult> getTopCandidates() {
        return resultRepo.findTop5ByOrderByFinalScoreDesc();
    }

    private List<Double> buildFeatureVector(
            double tfidfScore,
            double skillScore,
            int matchedCount,
            int missingCount,
            int experienceYears,
            Map<String, Double> categoryScores) {

        List<Double> features = new ArrayList<>();

        features.add(tfidfScore);
        features.add(skillScore);
        features.add((double) matchedCount);
        features.add((double) missingCount);
        features.add((double) experienceYears);

        features.add(categoryScores.getOrDefault("PROGRAMMING", 0.0));
        features.add(categoryScores.getOrDefault("BACKEND", 0.0));
        features.add(categoryScores.getOrDefault("FRONTEND", 0.0));
        features.add(categoryScores.getOrDefault("DATABASE", 0.0));
        features.add(categoryScores.getOrDefault("ML_AI", 0.0));
        features.add(categoryScores.getOrDefault("DATA_SCIENCE", 0.0));
        features.add(categoryScores.getOrDefault("CLOUD", 0.0));
        features.add(categoryScores.getOrDefault("DEVOPS", 0.0));
        features.add(categoryScores.getOrDefault("TOOLS", 0.0));

        return features;
    }

}
