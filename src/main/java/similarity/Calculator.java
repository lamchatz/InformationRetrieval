package similarity;

import config.Config;
import database.MemberSimilarityRepository;
import utility.CosineSimilarity;
import utility.Directory;
import utility.FileManager;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static config.Config.TOP_K_SIMILARITIES;
import static utility.Functions.println;

public class Calculator {
    private static final int SIMILARITY_BATCH = Config.SIMILARITY_BATCH;
    private final int NUMBER_OF_MEMBER_IDS;
    private final MemberSimilarityRepository memberSimilarityRepository;
    private final List<Integer> memberIds;
    private final Map<Integer, Double> membersNorm;
    private final Map<Integer, Set<Integer>> calculatedMemberSimilarities;
    private final PriorityQueue<Pair<Integer>> topKSimilarities;
    private final Set<Integer> blackList;

    public Calculator()  {
        this.memberSimilarityRepository = new MemberSimilarityRepository();
        this.memberIds = memberSimilarityRepository.getMemberIds();
        this.NUMBER_OF_MEMBER_IDS = memberIds.size();
        this.calculatedMemberSimilarities = new HashMap<>(NUMBER_OF_MEMBER_IDS);
        this.membersNorm = new HashMap<>(NUMBER_OF_MEMBER_IDS);
        this.blackList = new HashSet<>((NUMBER_OF_MEMBER_IDS / 10) + 1);

        topKSimilarities = new PriorityQueue<>(Comparator.comparingDouble(Pair::getSimilarity));

        FileManager.clearDirectory(Directory.SIMILARITIES);
        FileManager.createDirectory(Directory.SIMILARITIES);
    }

    public void calculate() {
        if (Config.FIND_SIMILARITIES_IN_BATCHES) {
            println("Calculating similarities in batches...");
            calculateInBatches();
        } else {
            println("Calculating similarities...");
            calculateInOneGo();
        }
    }

    public void calculateInOneGo() {
        topKSimilarities.clear();
        
        Map<Integer, Map<String, Double>> wordsAndScoresForMembers = memberSimilarityRepository.getAllMemberWords();
        wordsAndScoresForMembers.forEach((memberId, entries) -> {
            membersNorm.put(memberId, CosineSimilarity.norm(entries));
        });

        Set<Integer> memberIds = new HashSet<>(wordsAndScoresForMembers.keySet());

        for (Integer memberId : memberIds) {
            Map<String, Double> memberValues = wordsAndScoresForMembers.remove(memberId);
            //remove this entry to avoid comparing it with itself, also slowly reduce the size of the dataset

            for (Map.Entry<Integer, Map<String, Double>> wordsAndScoresForMemberEntry : wordsAndScoresForMembers.entrySet()) {
                topKSimilarities.offer(new Pair<>(memberId, wordsAndScoresForMemberEntry.getKey(), cosineSimilarity(memberId, memberValues, wordsAndScoresForMemberEntry)));

                // If the size of the priority queue exceeds k, remove the smallest element
                if (topKSimilarities.size() > TOP_K_SIMILARITIES) {
                    topKSimilarities.poll();
                }
            }
        }

        getTopSimilarities();
    }

    public void calculateInBatches() {
        topKSimilarities.clear();
        initializeCalculatedSimilarities();
        for (int i = 0; i < NUMBER_OF_MEMBER_IDS; i++) {
            Integer originalMemberId = memberIds.get(i);

            Map<String, Double> originalMemberWordsAndScores = memberSimilarityRepository.getWordsForId(originalMemberId);
            List<Integer> notCalculatedIds = iterateListAndGetIdsWithNoCalculatedSimilarity(calculatedMemberSimilarities.get(originalMemberId), i);

            Map<Integer, Map<String, Double>> wordsAndScoresForMemberBatch = memberSimilarityRepository.getWordsForIds(notCalculatedIds);
            while (!(notCalculatedIds.isEmpty() || wordsAndScoresForMemberBatch.isEmpty())) {

                compareMemberWithDataSet(originalMemberId, originalMemberWordsAndScores, wordsAndScoresForMemberBatch);

                Set<Integer> batchMemberIds = new HashSet<>(wordsAndScoresForMemberBatch.keySet());

                for (Integer batchMemberId : batchMemberIds) {
                    Map<String, Double> batchMemberValues = wordsAndScoresForMemberBatch.remove(batchMemberId);

                    if (batchMemberValues.isEmpty()) {
                        blackList.add(batchMemberId);
                        continue;
                    }

                    compareMemberWithDataSet(batchMemberId, batchMemberValues, wordsAndScoresForMemberBatch);
                }
                notCalculatedIds = iterateListAndGetIdsWithNoCalculatedSimilarity(calculatedMemberSimilarities.get(originalMemberId), i + SIMILARITY_BATCH);
            }
        }

        getTopSimilarities();
    }

    private void compareMemberWithDataSet(Integer memberId, Map<String, Double> memberValues, Map<Integer, Map<String, Double>> dataSet) {
        Set<Integer> calculatedSimilarities = calculatedMemberSimilarities.get(memberId);

        for (Map.Entry<Integer, Map<String, Double>> entry : dataSet.entrySet()) {
            Integer otherMemberId = entry.getKey();

            if (shouldSimilarityBeCalculated(memberId, otherMemberId, calculatedSimilarities)) {
                calculatedSimilarities.add(otherMemberId);
                calculatedMemberSimilarities.get(otherMemberId).add(memberId);

                topKSimilarities.offer(new Pair<>(memberId, otherMemberId, cosineSimilarity(memberId, memberValues, entry)));

                // If the size of the priority queue exceeds k, remove the smallest element
                if (topKSimilarities.size() > TOP_K_SIMILARITIES) {
                    topKSimilarities.poll(); // Remove the smallest (root of the min-heap)
                }
            }
        }

        calculatedMemberSimilarities.put(memberId, calculatedSimilarities);
    }

    private boolean shouldSimilarityBeCalculated(Integer id, Integer otherMemberId, Set<Integer> calculatedSimilarities) {
        return !(id.equals(otherMemberId) || hasSimilarityBeenCalculated(calculatedSimilarities, id, otherMemberId));
    }

    private boolean hasSimilarityBeenCalculated(Set<Integer> calculatedSimilarities, Integer id, Integer otherMember) {
        return (calculatedSimilarities.contains(otherMember) || calculatedMemberSimilarities.get(otherMember).contains(id));
    }

    private double cosineSimilarity(Integer id, Map<String, Double> values, Map.Entry<Integer, Map<String, Double>> other) {
        double d1 = membersNorm.computeIfAbsent(id, key -> CosineSimilarity.norm(values));
        double d2 = membersNorm.computeIfAbsent(other.getKey(), key -> CosineSimilarity.norm(other.getValue()));

        // Compute similarity
        return CosineSimilarity.calculate(
                CosineSimilarity.dotProduct(values, other.getValue()),
                d1,
                d2
        );
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

    private void getTopSimilarities() {
        Deque<Pair<String>> queue = new ArrayDeque<>();
        Set<Integer> ids = topKSimilarities.stream()
                .flatMap(pair -> Stream.of(pair.getMember1(), pair.getMember2()))
                .collect(Collectors.toSet());

        Map<Integer, String> names = memberSimilarityRepository.getMemberNames(ids);

        topKSimilarities
                .forEach(pair -> {
                    String name1 = Optional.ofNullable(names.get(pair.getMember1())).orElse("Unknown");
                    String name2 = Optional.ofNullable(names.get(pair.getMember2())).orElse("Unknown");
                    queue.push(new Pair<>(name1, name2, pair.getSimilarity()));
                });

        FileManager.writeSimilarities(queue);
    }

    private void initializeCalculatedSimilarities() {
        memberIds.forEach(id -> {
            Set<Integer> set = new HashSet<>(NUMBER_OF_MEMBER_IDS);
            set.add(id);  // Add the id itself to its own set
            calculatedMemberSimilarities.put(id, set);
        });
    }
}
