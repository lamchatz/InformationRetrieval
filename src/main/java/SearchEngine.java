import database.SpeechRepository;
import database.ViewRepository;
import dto.InfoToShow;
import utility.Functions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class SearchEngine {

    private static final int MAX_SIZE = 5;

    private final ViewRepository viewRepository;
    private final SpeechRepository speechRepository;

    public SearchEngine() {
        this.viewRepository = new ViewRepository();
        this.speechRepository = new SpeechRepository();
        speechRepository.clear();
    }

    public void search(String... args) {
        if (args.length > 5) {
            throw new IllegalArgumentException("More than 4 arguments given!");
        }

        if (args.length > 0) {
            String question = args[0];
            Map<Integer, Double> accumulators = new HashMap<>();

            for (String searchWord : question.toLowerCase().split("\\s")) {
                if (Functions.hasAccent(searchWord)) {
                    searchForAccentWord(accumulators, searchWord, args);
                } else {
                    searchForWordWithoutAccent(accumulators, searchWord, args);
                }
            }

            normalizeValues(accumulators);

            getTopSpeeches(accumulators);

            printAccordingToUserInput(speechRepository.getAllInfoFor(accumulators.keySet()));
        }
    }

    private void printAccordingToUserInput(Collection<InfoToShow> infos) {
        if (!infos.isEmpty()) {
            Functions.println("Do you want to view the results?");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            Iterator<InfoToShow> iterator = infos.iterator();

            while ("y".equalsIgnoreCase(input) && iterator.hasNext()) {
                Functions.println(iterator.next());

                Functions.println("Continue? ");
                input = scanner.nextLine();
                Functions.println("");
            }

            scanner.close();
        } else {
            Functions.println("No matches found...");
        }
    }


    private void searchForAccentWord(Map<Integer, Double> accumulators, String searchWord, String... args) {
        double idf = viewRepository.getIdfValueOfWord(searchWord);
        Map<Integer, Double> tfOfSpeeches = viewRepository.selectTFValueOfWord(searchWord, args);

        tfOfSpeeches.forEach((speechId, tfValue) ->
                accumulators.merge(speechId, tfValue * idf, Double::sum));
    }

    private void searchForWordWithoutAccent(Map<Integer, Double> searchAccumulators, String searchWord, String... args) {
        Map<String, Map<Integer, Double>> accumulatorsForWord = new HashMap<>();
        Map<String, Double> possibleIdfValues = viewRepository.getPossibleIdfValuesOfWordWithoutAccent(searchWord);
        Map<String, Map<Integer, Double>> tfOfSpeechesForWord = viewRepository.selectTFValueOfWordWithoutAccent(searchWord, args);

        if (!tfOfSpeechesForWord.isEmpty()) {
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
    }

    private void normalizeValues(Map<Integer, Double> accumulators) {
        Map<Integer, Integer> speechTotalWords = viewRepository.getSpeechTotalWords(accumulators.keySet());

        accumulators.replaceAll((speechId, score) -> {
            double length = speechTotalWords.getOrDefault(speechId, 1); // Avoid division by zero
            return score / length;
        });

    }

    private void getTopSpeeches(Map<Integer, Double> accumulators) {
        List<Map.Entry<Integer, Double>> topK = accumulators.entrySet()
                .stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(MAX_SIZE)
                .collect(Collectors.toList());

        // Print or use the top-k results
        topK.forEach(entry -> {
            Integer speechId = entry.getKey();
            Double score = entry.getValue();
            Functions.println("Speech ID: " + speechId + ", Score: " + score);
        });
    }
}
