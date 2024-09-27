package clusters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DBScan {
    private double epsilon;
    private int minPoints;

    public DBScan(double epsilon, int minPoints) {
        this.epsilon = epsilon;
        this.minPoints = minPoints;
    }

    public Map<Integer, Integer> fit(Map<Integer, Map<String, Double>> idfTfOfWordsOfSpeech, Set<String> vocabulary) {
        Map<Integer, Integer> clusterAssignments = new HashMap<>(); // Speech ID to cluster ID
        int clusterId = 0;
        Set<Integer> visited = new HashSet<>();

        for (Integer speechId : idfTfOfWordsOfSpeech.keySet()) {
            if (visited.contains(speechId)) {
                continue;
            }
            visited.add(speechId);
            List<Integer> neighbors = getNeighbors(speechId, idfTfOfWordsOfSpeech, vocabulary);

            if (neighbors.size() < minPoints) {
                // Mark as noise
                clusterAssignments.put(speechId, -1);
            } else {
                clusterId++;
                expandCluster(speechId, neighbors, clusterId, clusterAssignments, visited, idfTfOfWordsOfSpeech, vocabulary);
            }
        }

        return clusterAssignments;
    }

    private void expandCluster(Integer speechId, List<Integer> neighbors, int clusterId,
                               Map<Integer, Integer> clusterAssignments, Set<Integer> visited,
                               Map<Integer, Map<String, Double>> idfTfOfWordsOfSpeech, Set<String> vocabulary) {
        // Mark the initial speech as part of the cluster
        clusterAssignments.put(speechId, clusterId);

        // Create a temporary list to hold new neighbors to be added
        List<Integer> newNeighbors = new ArrayList<>();

        for (Integer neighbor : neighbors) {
            // If the neighbor has not been visited
            if (!visited.contains(neighbor)) {
                visited.add(neighbor);
                // Get the neighbors of this neighbor
                List<Integer> neighborNeighbors = getNeighbors(neighbor, idfTfOfWordsOfSpeech, vocabulary);

                // If this neighbor has enough neighbors, add them to the newNeighbors list
                if (neighborNeighbors.size() >= minPoints) {
                    newNeighbors.addAll(neighborNeighbors);
                }
            }
            // Assign the neighbor to the current cluster
            if (!clusterAssignments.containsKey(neighbor)) {
                clusterAssignments.put(neighbor, clusterId);
            }
        }
        // After the loop, add all new neighbors to the original neighbors list
        neighbors.addAll(newNeighbors);
    }

    private List<Integer> getNeighbors(Integer speechId,
                                       Map<Integer, Map<String, Double>> idfTfOfWordsOfSpeech,
                                       Set<String> vocabulary) {
        List<Integer> neighbors = new ArrayList<>();
        Map<String, Double> speechVector = idfTfOfWordsOfSpeech.get(speechId);

        for (Integer otherSpeechId : idfTfOfWordsOfSpeech.keySet()) {
            if (otherSpeechId.equals(speechId)) {
                continue; // Skip itself
            }
            Map<String, Double> otherSpeechVector = idfTfOfWordsOfSpeech.get(otherSpeechId);
            double distance = calculateDistance(speechVector, otherSpeechVector, vocabulary);
            if (distance <= epsilon) {
                neighbors.add(otherSpeechId);
            }
        }

        return neighbors;
    }

    private double calculateDistance(Map<String, Double> vec1, Map<String, Double> vec2, Set<String> vocabulary) {
        double sum = 0.0;
        for (String word : vocabulary) {
            double val1 = vec1.getOrDefault(word, 0.0);
            double val2 = vec2.getOrDefault(word, 0.0);
            sum += Math.pow(val1 - val2, 2);
        }
        return Math.sqrt(sum);
    }

    public static void main(String[] args) {
        // Sample data: Map of Speech ID to Word -> tf-idf score
//        ClustersRepository clustersRepository = new ClustersRepository();
//        Map<Integer, Map<String, Double>> idfTfOfWordsOfSpeech = clustersRepository.getSpeechVectors();
//        Set<String> vocabulary = clustersRepository.getVocabulary();
//
//        // Initialize DBSCAN with ε = 0.5 and MinPts = 2
//        DBScan dbscan = new DBScan(69, 3);
//        Map<Integer, Integer> clusters = dbscan.fit(idfTfOfWordsOfSpeech, vocabulary);
//
//        // Print the results
//        System.out.println("Cluster Assignments:");
//        for (Map.Entry<Integer, Integer> entry : clusters.entrySet()) {
//            System.out.println("Speech ID " + entry.getKey() + ": Cluster " + entry.getValue());
//        }
    }
}

