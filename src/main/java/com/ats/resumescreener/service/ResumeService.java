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

@Service
public class ResumeService {

    @Autowired
    private CandidateRepository candidateRepo;

    @Autowired
    private JobDescriptionRepository jdRepo;

    @Autowired
    private MatchResultRepository resultRepo;

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

            // TF-IDF + Cosine Similarity
            var vocab = VectorUtil.buildVocab(resumeWords, jdWords);
            var docs = List.of(resumeWords, jdWords);
            var resumeVector = VectorUtil.vectorize(resumeWords, vocab, docs);
            var jdVector = VectorUtil.vectorize(jdWords, vocab, docs);
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

        var resumeWords = TextCleaner.clean(PdfUtil.extractText(file));
        var jdWords = TextCleaner.clean(jobDescription);

        var candidateSkills = extractSkills(resumeWords);
        var requiredSkills = extractSkills(jdWords);

        if (requiredSkills.isEmpty())
            return new CandidateResult(
                    file.getOriginalFilename(),
                    0,
                    List.of(),
                    List.of());

        var matchedSkills = requiredSkills.stream()
                .filter(candidateSkills::contains)
                .toList();

        var missingSkills = requiredSkills.stream()
                .filter(skill -> !candidateSkills.contains(skill))
                .toList();

        double skillScore = (double) matchedSkills.size()
                / requiredSkills.size() * 100;

        var vocab = VectorUtil.buildVocab(resumeWords, jdWords);
        var docs = List.of(resumeWords, jdWords);

        var resumeVector = VectorUtil.vectorize(resumeWords, vocab, docs);
        var jdVector = VectorUtil.vectorize(jdWords, vocab, docs);

        double similarity = SimilarityUtil.cosineSimilarity(resumeVector, jdVector) * 100;

        double finalScore = (skillScore * 0.6) +
                (similarity * 0.4);

        // Persist to DB
        Candidate candidate = candidateRepo.save(
                new Candidate(file.getOriginalFilename()));

        JobDescription jdEntity = jdRepo.save(
                new JobDescription(jobDescription));

        MatchResult matchResult = new MatchResult();
        matchResult.setFinalScore(finalScore);
        matchResult.setSkillScore(skillScore);
        matchResult.setTfidfScore(similarity);
        matchResult.setMatchedCount(matchedSkills.size());
        matchResult.setMissingCount(missingSkills.size());
        matchResult.setCandidate(candidate);
        matchResult.setJobDescription(jdEntity);
        resultRepo.save(matchResult);

        return new CandidateResult(
                file.getOriginalFilename(),
                finalScore,
                matchedSkills,
                missingSkills);
    }

    public List<CandidateResult> rankCandidates(
            MultipartFile[] files,
            String jd) {

        List<CandidateResult> results = new ArrayList<>();

        for (MultipartFile file : files) {

            try {
                results.add(evaluateCandidate(file, jd));
            } catch (Exception e) {
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

}
