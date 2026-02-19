package com.ats.resumescreener.util;

import java.util.*;
import java.util.stream.Collectors;

public class TextCleaner {

    private static final Set<String> STOP_WORDS = Set.of(
            "a", "an", "the", "is", "are", "was", "were",
            "in", "on", "at", "to", "for", "with", "and", "or",
            "of", "by", "from", "as", "it", "this", "that",
            "using", "used", "built", "developed",
            "project", "projects", "link", "current",
            "year", "role", "work", "experience");

    public static List<String> clean(String text) {

        if (text == null || text.isEmpty())
            return Collections.emptyList();

        return Arrays.stream(
                text.toLowerCase()
                        .replaceAll("[^a-z ]", " ")
                        .split("\\s+"))
                .filter(word -> word.length() > 2)
                .filter(word -> !STOP_WORDS.contains(word))
                .collect(Collectors.toList());
    }
}
