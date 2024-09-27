package clusters;

import config.Config;
import database.ClustersRepository;
import database.SpeechRepository;
import dto.Speech;
import utility.CosineSimilarity;
import utility.Directory;
import utility.FileManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static utility.Functions.println;

public class KMeans {
    private static final int K = Config.K_CLUSTERS;
    private static final int MAX_ITERATIONS = Config.MAX_ITERATIONS;
    private final Map<String, MinMax> VOCABULARY;

    private List<Map<String, Double>> centroids;
    private final Map<Integer, Map<String, Double>> idfTfOfWordsOfSpeeches;
    private final Map<Integer, Double> normOfSpeech;

    public KMeans() {
        FileManager.clearDirectory(Directory.CLUSTERS);
        FileManager.createDirectory(Directory.CLUSTERS);

        final ClustersRepository clustersRepository = new ClustersRepository();
        this.VOCABULARY = clustersRepository.getVocabulary();
        this.idfTfOfWordsOfSpeeches = clustersRepository.getSpeechVectors();
        this.normOfSpeech = new HashMap<>(idfTfOfWordsOfSpeeches.size());

        idfTfOfWordsOfSpeeches.forEach((memberId, entries) -> {
            normOfSpeech.put(memberId, CosineSimilarity.norm(entries));
        });

        this.centroids = new ArrayList<>(K);
        initializeCentroids(centroids);
    }

    private double cosineSimilarity(Map.Entry<Integer, Map<String, Double>> entry, int centroidId, Map<String, Double> centroidValues) {
        double norm1 = normOfSpeech.get(entry.getKey());
        double norm2 = normOfSpeech.computeIfAbsent(centroidId, key -> CosineSimilarity.norm(centroidValues));

        return CosineSimilarity.calculate(
                CosineSimilarity.dotProduct(entry.getValue(), centroidValues),
                norm1,
                norm2);
    }

    private void initializeCentroids(List<Map<String, Double>> centroids) {
        Random random = new Random();

        for (int i = 0; i < K; i++) {
            Map<String, Double> centroid = new HashMap<>(VOCABULARY.size());

            for (Map.Entry<String, MinMax> vocabularyEntry : VOCABULARY.entrySet()) {
                String word = vocabularyEntry.getKey();
                MinMax minMax = vocabularyEntry.getValue();

                // Generate random value between min and max
                double randomValue = minMax.getMin() + (minMax.getMax() - minMax.getMin()) * random.nextDouble();

                centroid.put(word, randomValue);
            }

            centroids.add(centroid);
        }
    }

    private int findNearestCentroid(Map.Entry<Integer, Map<String, Double>> entry) {
        double minDistance = Double.MAX_VALUE;
        int nearestCentroid = -1;

        for (int i = 0; i < centroids.size(); i++) {
            double distance = cosineSimilarity(entry, i, centroids.get(i));

            if (distance < minDistance) {
                nearestCentroid = i;
                minDistance = distance;
            }
        }

        return nearestCentroid;
    }

    private Map<Integer, List<Integer>> createClusters() {
        Map<Integer, List<Integer>> clusters = new HashMap<>(K);

        for (int i = 0; i < K; i++) {
            clusters.put(i, new ArrayList<>());  // Add an empty ArrayList for each cluster
        }
        return clusters;
    }

    private List<Map<String, Double>> reComputeCentroids(Map<Integer, List<Integer>> clusters) {
        List<Map<String, Double>> newCentroids = new ArrayList<>(K); //initial capacity to avoid possible resizing
        for (Map.Entry<Integer, List<Integer>> cluster : clusters.entrySet()) {
            List<Integer> speechIds = cluster.getValue();
            Map<String, Double> centroid = new HashMap<>();
            for (String word : VOCABULARY.keySet()) {
                double sum = 0.0;
                for (int speechId : speechIds) {
                    sum += idfTfOfWordsOfSpeeches.get(speechId).getOrDefault(word, 0.0);
                }
                centroid.put(word, sum / speechIds.size());
            }
            newCentroids.add(centroid);
        }

        return newCentroids;
    }

    public void compute() {
        Map<Integer, List<Integer>> clusters = new HashMap<>(K); //initial capacity to avoid possible resizing

        int i = 0;
        boolean centroidsChanged = true;
        while (i < MAX_ITERATIONS && centroidsChanged) {
            println(i);
            clusters = createClusters();

            for (Map.Entry<Integer, Map<String, Double>> speechEntry : idfTfOfWordsOfSpeeches.entrySet()) {
                int nearestCentroid = findNearestCentroid(speechEntry);
                clusters.get(nearestCentroid).add(speechEntry.getKey());
            }

            List<Map<String, Double>> newCentroids = reComputeCentroids(clusters);

            if (newCentroids.equals(centroids)) {
                centroidsChanged = false;
            }
            centroids.clear();
            centroids = newCentroids;
            i++;
        }

        if (Config.SHOW_ONLY_SPEECH_IDS) {
            FileManager.writeClusters(clusters);
        } else {
            Map<Integer, List<Speech>> clusterWithSpeeches = new HashMap<>(K);

            SpeechRepository speechRepository = new SpeechRepository();
            Map<Integer, String> speechOfId = speechRepository.findByIds(idfTfOfWordsOfSpeeches.keySet());


            for (int ii = 0; ii < K; ii++) {
                List<Integer> clusterSpeechIds = clusters.get(ii);

                List<Speech> speeches = new ArrayList<>(clusterSpeechIds.size());

                clusterSpeechIds.forEach(speechId -> {
                    speeches.add(new Speech(speechId, speechOfId.remove(speechId)));
                    //remove this id as it will no longer be needed
                });

                clusterWithSpeeches.put(ii, speeches);
            }

            FileManager.writeClusters(clusterWithSpeeches);
        }
    }
}