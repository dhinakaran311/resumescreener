package com.ats.resumescreener.util;

import java.util.List;

public class SimilarityUtil {

    public static double cosineSimilarity(
            List<Double> v1,
            List<Double> v2){

        double dot = 0.0;
        double mag1 = 0.0;
        double mag2 = 0.0;

        for(int i=0;i<v1.size();i++){

            dot += v1.get(i) * v2.get(i);

            mag1 += Math.pow(v1.get(i),2);
            mag2 += Math.pow(v2.get(i),2);
        }

        if(mag1 == 0 || mag2 == 0)
            return 0;

        return dot / (Math.sqrt(mag1) * Math.sqrt(mag2));
    }
}
