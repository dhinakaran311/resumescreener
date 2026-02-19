package com.ats.resumescreener.util;

import java.util.*;

public class VectorUtil {

    // Build vocabulary
    public static Set<String> buildVocab(List<String> doc1, List<String> doc2) {

        Set<String> vocab = new HashSet<>(doc1);
        vocab.addAll(doc2);

        return vocab;
    }

    // Term Frequency
    private static double tf(String term, List<String> doc) {

        long count = doc.stream()
                .filter(word -> word.equals(term))
                .count();

        return (double) count / doc.size();
    }

    // Inverse Document Frequency
    private static double idf(String term, List<List<String>> docs) {

        long docsWithTerm = docs.stream()
                .filter(doc -> doc.contains(term))
                .count();

        return Math.log((double) (docs.size() + 1) / (docsWithTerm + 1)) + 1;
    }

    // Build TF-IDF Vector
    public static List<Double> vectorize(
            List<String> doc,
            Set<String> vocab,
            List<List<String>> allDocs) {

        List<Double> vector = new ArrayList<>();

        for (String term : vocab) {

            double tf = tf(term, doc);
            double idf = idf(term, allDocs);

            vector.add(tf * idf);
        }

        return vector;
    }
}
