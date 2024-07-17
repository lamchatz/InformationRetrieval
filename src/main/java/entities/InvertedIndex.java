package entities;

import java.io.*;
import java.util.*;

public class InvertedIndex {


    private static final String INVERTED_INDEX = "src/main/resources/inverted_index.txt";
    private Map<String, Map<String, Long>> index;

    private static long i;
    private static long c;

    public InvertedIndex () {
        index = new HashMap<>();
        c = 0;
        i = 0;
    }

    private void saveAndFreeMemory() {
        File file = new File(INVERTED_INDEX);
        try (BufferedWriter bf = new BufferedWriter(new FileWriter(file, true))) {
            for (Map.Entry<String, Map<String, Long>> entry : index.entrySet()) {
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
        System.out.println(i + " of " + c);
    }
    public void print() {
        index.entrySet().forEach(System.out::println);
    }

    public void indexSpeech(String memberName, List<String> content, long rows) {
//        if (rows % 5000 == 0) {
//            System.out.println("Clearing memory");
//            //saveAndFreeMemory();
//        }

        for (String word:  content) {
            index.computeIfAbsent(word, w -> new HashMap<>()).merge(memberName, 1L, Long::sum);
        }
    }


}
