package entities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InvertedIndex {
    private static final String INVERTED_INDEX = "src/main/resources/inverted_index.txt";
    private Map<String, Map<Long, Long>> index;

    private static long i;
    private long counter;
    private final boolean saveIndexToFile;
    private final int saveAfter;

    public InvertedIndex (boolean saveIndexToFile, int saveAfter) {
        this.index = new HashMap<>();
        this.saveIndexToFile = saveIndexToFile;
        this.saveAfter = saveAfter;
        this.counter = 0;
        i = 0;
    }

    private void saveAndFreeMemory() {
        System.out.println("saving");
        File file = new File(INVERTED_INDEX);
        try (BufferedWriter bf = new BufferedWriter(new FileWriter(file, true))) {
            for (Map.Entry<String, Map<Long, Long>> entry : index.entrySet()) {
                bf.write(entry.getKey() + ":" + entry.getValue());
                bf.newLine();
            }

            bf.flush();
            index.clear();

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void printI() {
        System.out.println(i + " of " + counter);
    }
    public void print() {
        index.entrySet().forEach(System.out::println);
    }

    public void indexSpeech(Speech speech) {
        if (saveIndexToFile && counter == saveAfter) {
            counter = 0;
            saveAndFreeMemory();
        }

        for (String word: speech.getWords()) {
            index.computeIfAbsent(word, w -> new HashMap<>()).merge(speech.getId(), 1L, Long::sum);
        }

        counter++;
    }

    public Map<Long, Long> search(String word) {
        return index.get(word);
    }


}
