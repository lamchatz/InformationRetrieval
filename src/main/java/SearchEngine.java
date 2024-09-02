import database.MemberRepository;
import database.ViewRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SearchEngine {

    private static final String accentPattern = ".*[άέήίΐόΰύώ].*";
    private static final int MAX_SIZE = 5;

    private final ViewRepository viewRepository;
    private final MemberRepository memberRepository;

    public SearchEngine() {
        this.viewRepository = new ViewRepository();
        this.memberRepository = new MemberRepository();
    }

    public void search(String question) {
//        search(question, null);
        Map<Integer, Double> accumulators = new HashMap<>();
        for (String word : question.toLowerCase().split("\\s")) {
            if (hasAccent(word)) {
                searchForAccentWord(accumulators, word, viewRepository.getTFValueOfWord(word));
            } else {
                searchForNoAccentWord(accumulators, word);
            }
        }

        rankSpeeches(accumulators);
    }

    private void rankSpeeches(Map<Integer, Double> accumulators) {
        Map<Integer, Integer> speechTotalWords = viewRepository.getSpeechTotalWords(accumulators.keySet());

        accumulators.replaceAll((speechId, score) -> {
            double length = speechTotalWords.getOrDefault(speechId, 1); // Avoid division by zero
            return score / length;
        });

        List<Map.Entry<Integer, Double>> topK = accumulators.entrySet()
                .stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(MAX_SIZE)
                .collect(Collectors.toList());

        // Print or use the top-k results
        topK.forEach(entry -> {
            Integer speechId = entry.getKey();
            Double score = entry.getValue();
            System.out.println("Speech ID: " + speechId + ", Score: " + score);
        });
    }

    private void searchForAccentWord(Map<Integer, Double> accumulators, String word, Map<Integer, Double> tfOfSpeeches) {
        double idf = viewRepository.getIdfValueOfWord(word);

        tfOfSpeeches.forEach((speechId, tfValue) ->
                accumulators.merge(speechId, tfValue * idf, Double::sum));
    }

    private void searchForNoAccentWord(Map<Integer, Double> searchAccumulators, String searchWord) {
        Map<String, Map<Integer, Double>> accumulatorsForWord = new HashMap<>();
        Map<String, Double> possibleIdfValues = viewRepository.getPossibleIdfValuesOfWordIgnoringAccent(searchWord);
        Map<String, Map<Integer, Double>> tfOfSpeechesForWord = viewRepository.getTFValueOfWordWithoutAccent(searchWord);

        for (String word : possibleIdfValues.keySet()) {
            Double idf = possibleIdfValues.get(word);

            Map<Integer, Double> speechAccumulators = new HashMap<>();

            Map<Integer, Double> tfOfSpeeches = tfOfSpeechesForWord.get(word);

            tfOfSpeeches.forEach((speechId, tfValue) ->
                    speechAccumulators.merge(speechId, tfValue * idf, Double::sum));

            accumulatorsForWord.putIfAbsent(word, speechAccumulators);
        }

        Map<Integer, Double> highestScores = accumulatorsForWord.values().stream()
                .flatMap(wordScores -> wordScores.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        Double::max));

        highestScores.forEach((speechId, sum) -> searchAccumulators.merge(speechId, sum, Double::sum));
    }

    public void search(String question, String memberName) {
        search(question, memberName, null, null);
//        Map<Integer, Double> accumulators = new HashMap<>();
//        for (String word : question.toLowerCase().split("\\s")) {
//            if (hasAccent(word)) {
//                searchForAccentWord(accumulators, word, viewRepository.getTfValueOfWordForMember(word, memberName));
//            } else {
//                searchForNoAccentWord(accumulators, word);
//            }
//        }
//
//        rankSpeeches(accumulators);
    }


    public void search(String question, String name, String from, String to) {

        viewRepository.ss(question, name, from, to);
        //        Map<Integer, Double> accumulators = new HashMap<>();
//        for (String word : question.toLowerCase().split("\\s")) {
//            if (hasAccent(word)) {
//                searchForAccentWord(accumulators, word, viewRepository.getTfValueOfWordForMember(word, memberName));
//            } else {
//                searchForNoAccentWord(accumulators, word);
//            }
//        }
//
//        rankSpeeches(accumulators);

    }

    public boolean hasAccent(String word) {
        return word.matches(accentPattern);
    }

}
