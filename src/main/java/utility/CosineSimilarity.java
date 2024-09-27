package utility;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//helper Class for cosineSimilarity as it used by both KMeans and Calculator.
public class CosineSimilarity {
    public static double calculate(double dotProduct, double norm1, double norm2) {
        // Compute similarity
        if (norm1 <= 0.0 || norm2 <= 0.0) {
            return -1;
        }
        if (dotProduct == 0) {
            return -1; //return -1 to make sure it will be the smallest number
        }

        return dotProduct / (norm1 * norm2);
    }

    private static Set<String> commonWords(Map<String, Double> a, Map<String, Double> b) {
        final Set<String> commonWords = new HashSet<>(a.keySet());
        commonWords.retainAll(b.keySet());
        return commonWords;
    }

    public static double dotProduct(Map<String, Double> a, Map<String, Double> b) {
        double dot = 0.0;

        Set<String> commonWords = commonWords(a, b);
        for (String word : commonWords) {
            dot += a.get(word) * b.get(word);
        }

        return dot;
    }

    public static double norm(Map<String, Double> entries) {
        double d = 0.0;
        for (Map.Entry<String, Double> entry : entries.entrySet()) {
            d += Math.pow(entry.getValue(), 2);
        }

        return Math.sqrt(d);
    }
}
