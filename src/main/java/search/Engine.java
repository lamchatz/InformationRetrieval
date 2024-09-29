package search;

import config.Config;
import csv.Reader;
import database.SearchRepository;
import dto.InfoToShow;
import entities.InvertedIndex;
import utility.Directory;
import utility.FileManager;
import utility.Functions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static utility.Functions.println;

public class Engine {
    private static final String COMMA = ",";
    private static final String QUESTION = "Question";
    private static final String NO_MATCHES_FOUND = "No matches found!";
    private static int QUESTION_COUNTER = 0;

    private static final String SINGLE_QUOTE = "'";
    private static final String SPACE = " ";

    private final SearchRepository searchRepository;

    public Engine() {
        this.searchRepository = new SearchRepository();
        FileManager.clearDirectory(Directory.ANSWERS);
        FileManager.createDirectory(Directory.ANSWERS);
    }

    public void readQuestions() {
        Collection<Entry> questions = Reader.readQuestions();

        println("Searching... ");
        for (Entry questionEntry : questions) {
            QUESTION_COUNTER++;
            search(questionEntry);
        }
    }

    private void search(Entry searchEntry) {
        String question = searchEntry.getQuestion();
        if (Functions.isNotEmpty(question)) {
            Map<Integer, Double> accumulators = new HashMap<>(100_000);//arbitrary big number to avoid constant resizing

            String[] words = question.toLowerCase().replaceAll(InvertedIndex.REGEX, SPACE).split(SPACE);
            List<String> accentWords = new ArrayList<>(words.length);
            for (String searchWord : words) {
                if (Functions.hasAccent(searchWord)) {
                    accentWords.add(SINGLE_QUOTE + searchWord + SINGLE_QUOTE);
                } else {
                    searchForWordWithoutAccent(accumulators, searchWord, searchEntry);
                }
            }
            searchForAccentWords(accumulators, accentWords, searchEntry);

            normalizeValues(accumulators);

            getTopSpeeches(accumulators);
        }
    }

    private void searchForAccentWords(Map<Integer, Double> accumulators, List<String> searchWords, Entry searchEntry) {
        Map<String, Map<Integer, Double>> idfTfOfSpeechesForWords = searchRepository.selectIdfTFValues(searchWords, searchEntry);

        if (!idfTfOfSpeechesForWords.isEmpty()) {
            idfTfOfSpeechesForWords.forEach( (word, idfTFValuesOfWord) -> idfTFValuesOfWord.forEach((speechId, idfTf) ->
                    accumulators.merge(speechId, idfTf, Double::sum)));
        }
    }

    private void searchForWordWithoutAccent(Map<Integer, Double> searchAccumulators, String searchWord, Entry searchEntry) {
        Map<String, Map<Integer, Double>> idfTfOfSpeechesForWords = searchRepository.selectIdfTFValues(Functions.generateAccentVariants(searchWord), searchEntry);

        if (!idfTfOfSpeechesForWords.isEmpty()) {
            //If a speech id is associated with more than one word, keep the highest score
            Map<Integer, Double> highestScoreForEachSpeechId = idfTfOfSpeechesForWords.values().stream()
                    .flatMap(wordScores -> wordScores.entrySet().stream())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            Double::max));

            highestScoreForEachSpeechId.forEach((speechId, highestScore) -> searchAccumulators.merge(speechId, highestScore, Double::sum));
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

        writeAnswers(searchRepository.getAllInfoFor(topK.stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())));
    }

    private void writeAnswers(Collection<InfoToShow> infos) {
        String fileName = QUESTION + QUESTION_COUNTER;

        if (infos.isEmpty()) {
            FileManager.writeSearchAnswers(NO_MATCHES_FOUND, fileName);
            return;
        }
        for (InfoToShow infoToShow : infos) {
            FileManager.writeSearchAnswers(infoToShow + COMMA, fileName);
        }
    }
}
