package similarity;

import config.Config;
import database.MemberSimilarityRepository;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import static config.Config.TOP_K;
import static utility.Functions.println;

public class Calculator {
    private static final int INITIAL_CAPACITY = 1524; //
    private static final int SIMILARITY_BATCH = Config.SIMILARITY_BATCH;
    private final int NUMBER_OF_MEMBER_IDS;
    private final MemberSimilarityRepository memberSimilarityRepository;
    private final List<Integer> memberIds;
    private final Map<Integer, Double> membersNorm;
    private final Map<Integer, Set<Integer>> calculatedMemberSimilarities;
    private final PriorityQueue<Map.Entry<Pair<Integer>, Double>> topKSimilarities;
    private final Map<Pair<String>, Double> similarities2;
    private final Set<Integer> blackList;

    public Calculator() {
        this.memberSimilarityRepository = new MemberSimilarityRepository();
        this.memberIds = memberSimilarityRepository.getMemberIds();
        this.NUMBER_OF_MEMBER_IDS = memberIds.size();
        this.calculatedMemberSimilarities = new HashMap<>(1524);
        this.similarities2 = new HashMap<>(); // Holds 1_159_026 combinations
        this.membersNorm = new HashMap<>(1524); // Cache for norm values
        this.blackList = new HashSet<>(1524);

        topKSimilarities = new PriorityQueue<>(Comparator.comparingDouble(Map.Entry::getValue));

        memberIds.forEach(id -> {
            Set<Integer> set = new HashSet<>(INITIAL_CAPACITY);
            set.add(id);  // Add the id itself to its own set
            calculatedMemberSimilarities.put(id, set);
        });
    }

    public void calculate() {
        Map<String, Map<String, Double>> membersWords = memberSimilarityRepository.getMembersWords();
        Map<String, Double> membersNorm = new HashMap<>(INITIAL_CAPACITY);
        membersWords.forEach((member, entries) -> {
            membersNorm.put(member, norm(entries));
        });

        Set<String> members = new HashSet<>(membersWords.keySet());

        for (String member : members) {
            Map<String, Double> values = membersWords.remove(member);

            for (Map.Entry<String, Map<String, Double>> entry : membersWords.entrySet()) {
                double dot = dotProduct(values, entry.getValue());
                double d1 = membersNorm.get(member);
                double d2 = membersNorm.get(entry.getKey());

                double similarity;
                if (d1 <= 0.0 || d2 <= 0.0) {
                    similarity = dot;
                } else {
                    similarity = dot / (d1 * d2);
                }

                similarities2.put(new Pair<>(member, entry.getKey()), similarity);
                //System.out.println(similarity);
            }
        }

        getTopSimilarities(similarities2);
    }

    public void calculate5() {
        for (int i = 0; i < NUMBER_OF_MEMBER_IDS; i++) {
            //get next 500 not calculated members
            Integer originalMemberId = memberIds.get(i);

            //println(i);

            Map<String, Double> originalMemberWordsAndScores = memberSimilarityRepository.getWordsForId(originalMemberId);
            List<Integer> notCalculatedIds = iterateListAndGetIdsWithNoCalculatedSimilarity(calculatedMemberSimilarities.get(originalMemberId), i);

//            println("Querying originalMemberId: " + originalMemberId);
//            notCalculatedIds.forEach(e -> System.out.print(e + ", "));
//            println("");

            Map<Integer, Map<String, Double>> memberWordsAndScore = memberSimilarityRepository.getWordsForIds(notCalculatedIds);
            while (!(notCalculatedIds.isEmpty() || memberWordsAndScore.isEmpty())) {

                compareWithMember(originalMemberId, originalMemberWordsAndScores, memberWordsAndScore);

                Set<Integer> fetchedMemberIds = new HashSet<>(memberWordsAndScore.keySet());

                for (Integer fetchedMemberId : fetchedMemberIds) {
                    //System.out.println(fetchedMemberId);
                    Map<String, Double> values = memberWordsAndScore.remove(fetchedMemberId);

                    if (values.isEmpty()) {
                        blackList.add(fetchedMemberId);
                        continue;
                    }

                    compareWithMember(fetchedMemberId, values, memberWordsAndScore);
                }
                // println("Calculated");

                notCalculatedIds = iterateListAndGetIdsWithNoCalculatedSimilarity(calculatedMemberSimilarities.get(originalMemberId), i + SIMILARITY_BATCH);

//                println("Querying originalMemberId: " + originalMemberId);
//                notCalculatedIds.forEach(e -> System.out.print(e + ", "));
//                println("");
            }
        }

        getTopSimilarities2();
    }


    private void compareWithMember(Integer id, Map<String, Double> originalMemberWordsAndScores, Map<Integer, Map<String, Double>> memberWordsAndScore) {
        Set<Integer> calculatedSimilarities = calculatedMemberSimilarities.get(id);

        for (Map.Entry<Integer, Map<String, Double>> entry : memberWordsAndScore.entrySet()) {
            Integer otherMemberId = entry.getKey();

            if (shouldSimilarityBeCalculated(id, otherMemberId, calculatedSimilarities)) {
                calculatedSimilarities.add(otherMemberId);
                calculatedMemberSimilarities.get(otherMemberId).add(id);

                topKSimilarities.offer(new AbstractMap.SimpleEntry<>(new Pair<>(id, otherMemberId), cosineSimilarity(id, originalMemberWordsAndScores, entry)));

                // If the size of the priority queue exceeds k, remove the smallest element
                if (topKSimilarities.size() > TOP_K) {
                    topKSimilarities.poll(); // Remove the smallest (root of the min-heap)
                }
            }
        }

        calculatedMemberSimilarities.put(id, calculatedSimilarities);
    }

    private boolean shouldSimilarityBeCalculated(Integer id, Integer otherMemberId, Set<Integer> calculatedSimilarities) {
        return !(id.equals(otherMemberId) || hasSimilarityBeenCalculated(calculatedSimilarities, id, otherMemberId));
    }

    private boolean hasSimilarityBeenCalculated(Set<Integer> calculatedSimilarities, Integer id, Integer otherMember) {
        return (calculatedSimilarities.contains(otherMember) || calculatedMemberSimilarities.get(otherMember).contains(id));
    }

    private double cosineSimilarity(Integer id, Map<String, Double> values, Map.Entry<Integer, Map<String, Double>> other) {
        // Get or compute norms for both members
        double d1 = membersNorm.computeIfAbsent(id, key -> norm(values));
        double d2 = membersNorm.computeIfAbsent(other.getKey(), key -> norm(other.getValue()));

        // Compute similarity
        if (d1 <= 0.0 || d2 <= 0.0) {
            return -1;
        }
        double dotProduct = dotProduct(values, other.getValue());
        if (dotProduct == 0) {
            return -1;
        }

        return dotProduct / (d1 * d2);
    }

    private List<Integer> iterateListAndGetIdsWithNoCalculatedSimilarity(Set<Integer> calculatedSimilarities, int from) {
        int counter = 0;
        List<Integer> notCalculatedIds = new ArrayList<>();

        for (int i = from; counter < SIMILARITY_BATCH && i < NUMBER_OF_MEMBER_IDS; i++) {
            Integer id = memberIds.get(i);
            if (!(calculatedSimilarities.contains(id) || blackList.contains(id))) {
                notCalculatedIds.add(id);
                counter++;
            }
        }

        return notCalculatedIds;
    }

    private void getTopSimilarities2() {
        Stack<Map.Entry<Pair<Integer>, Double>> stack = new Stack<>();
        // Print or use the top-k results
        for (int i = 0; i < TOP_K; i++) {
            stack.push(topKSimilarities.poll());
        }

        for (int i = 0; i < TOP_K; i++) {
            println(stack.pop());
        }
    }

    private void getTopSimilarities(Map<Pair<String>, Double> pairSimilarities) {
        List<Map.Entry<Pair<String>, Double>> topK = pairSimilarities.entrySet()
                .stream()
                .sorted(Map.Entry.<Pair<String>, Double>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toList());

        // Print or use the top-k results
        topK.forEach(entry -> {
            Pair<String> pair = entry.getKey();
            Double score = entry.getValue();
            println("Pair: " + pair + ", Score: " + score);
        });
    }

    private Set<String> commonWords(Map<String, Double> a, Map<String, Double> b) {
        final Set<String> commonWords = new HashSet<>(a.keySet());
        commonWords.retainAll(b.keySet());
        return commonWords;
    }

    private double dotProduct(Map<String, Double> a, Map<String, Double> b) {
        double dot = 0.0;

        Set<String> commonWords = commonWords(a, b);
        for (String word : commonWords) {
            dot += a.get(word) * b.get(word);
        }

        return dot;
    }

    private double norm(Map<String, Double> entries) {
        double d = 0.0;
        for (Map.Entry<String, Double> entry : entries.entrySet()) {
            d += Math.pow(entry.getValue(), 2);
        }

        return Math.sqrt(d);
    }
}
