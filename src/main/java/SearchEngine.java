import config.Config;
import database.SearchRepository;
import dto.InfoToShow;
import utility.Functions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import static utility.Functions.println;

public class SearchEngine {

    private static final String SINGLE_QUOTE = "'";
    private static final String WHITE_SPACE = "\\s";

    private final SearchRepository searchRepository;

    public SearchEngine() {
        this.searchRepository = new SearchRepository();
    }

    public void search(String... args) {
        if (args.length > 5) {
            throw new IllegalArgumentException("More than 4 arguments given!");
        }

        if (args.length > 0) {
            String question = args[0];
            Map<Integer, Double> accumulators = new HashMap<>(300_000);

            String[] words = question.toLowerCase().split(WHITE_SPACE);
            List<String> accentWords = new ArrayList<>(words.length);
            for (String searchWord : words) {
                if (Functions.hasAccent(searchWord)) {
                    accentWords.add(SINGLE_QUOTE + searchWord + SINGLE_QUOTE);
                } else {
                    searchForWordWithoutAccent(accumulators, searchWord, args);
                }
            }
            searchForAccentWords(accumulators, accentWords, args);

            normalizeValues(accumulators);

            getTopSpeeches(accumulators);
        }
    }

    private void printAccordingToUserInput(Collection<InfoToShow> infos) {
        if (!infos.isEmpty()) {
            println("Do you want to view the results?");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            Iterator<InfoToShow> iterator = infos.iterator();

            while ("y".equalsIgnoreCase(input) && iterator.hasNext()) {
                println(iterator.next());

                println("Continue? ");
                input = scanner.nextLine();
                println("");
            }

            scanner.close();
        } else {
            println("No matches found...");
        }
    }

    private void searchForAccentWords(Map<Integer, Double> accumulators, List<String> searchWords, String... args) {
        Map<String, Map<Integer, Double>> idfTfOfSpeechesForWords = searchRepository.selectIdfTFValues(searchWords, args);

        if (!idfTfOfSpeechesForWords.isEmpty()) {
            idfTfOfSpeechesForWords.forEach( (word, idfTFValuesOfWord) -> idfTFValuesOfWord.forEach((speechId, idfTf) ->
                    accumulators.merge(speechId, idfTf, Double::sum)));
        }
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
                .limit(Config.SEARCH_TOP_K)
                .collect(Collectors.toList());

        topK.forEach(entry -> {
            Integer speechId = entry.getKey();
            Double score = entry.getValue();
            println("Speech ID: " + speechId + ", Score: " + score);
        });

        printAccordingToUserInput(searchRepository.getAllInfoFor(topK.stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())));

    }
}
