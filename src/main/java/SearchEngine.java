import database.SearchRepository;
import database.SpeechRepository;
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

    private final SearchRepository searchRepository;
    private final SpeechRepository speechRepository;

    public SearchEngine() {
        this.searchRepository = new SearchRepository();
        this.speechRepository = new SpeechRepository();
        speechRepository.clear();
    }

    public void search(String... args) {
        if (args.length > 5) {
            throw new IllegalArgumentException("More than 4 arguments given!");
        }

        if (args.length > 0) {
            String question = args[0];
            Map<Integer, Double> accumulators = new HashMap<>(300_000);

            for (String searchWord : question.toLowerCase().split("\\s")) {
                if (Functions.hasAccent(searchWord)) {
                    searchForAccentWord(accumulators, searchWord, args);
                } else {
                    searchForWordWithoutAccent(accumulators, searchWord, args);
                }
            }

            normalizeValues(accumulators);

            getTopSpeeches(accumulators);

            //printAccordingToUserInput(speechRepository.getAllInfoFor(accumulators.keySet()));
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
        Map<Integer, Double> idfTFValuesOfWord = searchRepository.selectIdfTFValuesOfWord(searchWord, args);

        idfTFValuesOfWord.forEach((speechId, idfTf) ->
                accumulators.merge(speechId, idfTf, Double::sum));
    }

    private void searchForWordWithoutAccent(Map<Integer, Double> searchAccumulators, String searchWord, String... args) {
        Map<String, Map<Integer, Double>> idfTfOfSpeechesForWords = searchRepository.selectIdfTfValuesForWordWithoutAccent(searchWord, args);

        if (!idfTfOfSpeechesForWords.isEmpty()) {
            Map<Integer, Double> highestScores = idfTfOfSpeechesForWords.values().stream()
                    .flatMap(wordScores -> wordScores.entrySet().stream())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            Double::max));

            highestScores.forEach((speechId, sum) -> searchAccumulators.merge(speechId, sum, Double::sum));
        }
    }

    private void normalizeValues(Map<Integer, Double> accumulators) {
        Map<Integer, Integer> speechTotalWords = searchRepository.getSpeechTotalWords(accumulators.keySet());

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
