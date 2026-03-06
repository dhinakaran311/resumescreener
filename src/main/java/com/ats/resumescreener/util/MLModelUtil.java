package com.ats.resumescreener.util;

import ai.onnxruntime.*;
import java.util.*;

public class MLModelUtil {

    private static OrtEnvironment env;
    private static OrtSession session;

    static {
        try {
            env = OrtEnvironment.getEnvironment();
            // We use the absolute path or classpath resource stream for safety
            session = env.createSession(
                    "src/main/resources/model/ats_model.onnx",
                    new OrtSession.SessionOptions());
        } catch (Exception e) {
            System.err.println("Failed to load ONNX model. Is src/main/resources/model/ats_model.onnx available?");
            e.printStackTrace();
        }
    }

    public static double predict(List<Double> features) {
        if (session == null) {
            return 0.0; // Fallback if model failed to load
        }

        try {
            float[][] input = new float[1][features.size()];

            for (int i = 0; i < features.size(); i++) {
                input[0][i] = features.get(i).floatValue();
            }

            OnnxTensor tensor = OnnxTensor.createTensor(env, input);
            Map<String, OnnxTensor> inputs = Map.of("float_input", tensor);
            OrtSession.Result result = session.run(inputs);

            float[][] output = (float[][]) result.get(0).getValue();

            // output usually gives the probability arrays for labels
            // output[0][1] represents the probability of label 1 (Good match)
            // Note: Logistic Regression in ONNX might return float[] or float[][]
            // Depending on the exact ONNX output signature, index might vary slightly.
            // Let's print out the output length to be paranoid:
            if (output.length > 0 && output[0].length > 1) {
                return output[0][1]; // Probability of positive class
            } else {
                return output[0][0]; // If only a single probability is returned
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0.0;
    }
}
