package com.ats.resumescreener.service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.*;
import com.ats.resumescreener.util.TextCleaner;
import com.ats.resumescreener.util.SkillDictionary;
import com.ats.resumescreener.util.PdfUtil;

@Service
public class ResumeService {

    public String handleFile(MultipartFile file) {

        try {
            String raw = PdfUtil.extractText(file);
            Map<String, Integer> categoryCount = new HashMap<>();

            var cleaned = TextCleaner.clean(raw);

            SkillDictionary.SKILL_MAP.forEach((category, skills) -> {

                int count = (int) cleaned.stream()
                        .filter(skills::contains)
                        .count();

                if(count > 0)
                    categoryCount.put(category, count);
            });

            System.out.println(categoryCount);
            return "Resume processed. Skills detected: " + categoryCount;

        } 
        catch (IOException e) {
            return "Error reading PDF";
        }
    }

    public String matchResume(MultipartFile file, String jobDescription) {

        try {
            var resumeWords = TextCleaner.clean(PdfUtil.extractText(file));
            var jdWords = TextCleaner.clean(jobDescription);

            var candidateSkills = extractSkills(resumeWords);
            var requiredSkills = extractSkills(jdWords);

            if(requiredSkills.isEmpty())
                return "No valid skills found in Job Description.";

            long matched = requiredSkills.stream()
                    .filter(candidateSkills::contains)
                    .count();

            double score = requiredSkills.size() > 0 
                    ? (double) matched / requiredSkills.size() * 100 
                    : 0;

            return "Match Score: " + (int) score + "%" +
                    "\nMatched Skills: " + matched +
                    "\nMissing Skills: " +
                    requiredSkills.stream()
                            .filter(skill -> !candidateSkills.contains(skill))
                            .toList();

        } 
        catch (IOException e) {
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
}

