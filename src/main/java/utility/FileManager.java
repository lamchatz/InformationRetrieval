package utility;

import config.Config;
import keyword.Entry;
import similarity.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class FileManager {
    private static final String TXT = ".txt";
    private static final String ID = "ID: ";
    private static final String NAME = ", Name: ";
    private static final String WORD_SCORE = ", Word - Score: ";
    private static final String CONTENT = ", Content: ";
    private static final String HYPHEN = " - ";
    private static final String HIGHEST_SCORE_WORD = "\nHighest score word = ";
    private static final String COLON = ": ";
    private static final int NUMBER_OF_KEY_WORDS = Config.NUMBER_OF_KEY_WORDS;
    private static final String TOP = ", Top ";
    private static final String WORD_SCORES = " Word Scores: {";
    private static final String CURLY_BRACE = "}";
    private static final String SIMILARITIES = "similarities";
    private static final String CLUSTERS = "clusters";
    private static final String LINE_BREAK = "\n";
    private static final String FAILED_TO_WRITE_TO_FILE = "Failed to write to file: ";
    private static final String FAILED_TO_CREATE_DIRECTORY = "Failed to create directory: ";

    public FileManager() {
        super();
    }

    //Keywords
    public static void writeMemberKeyWords(Entry entry) {
        writeValuesToFile(Directory.MEMBERS, entry.getName(), format(entry));
    }

    public static void writePoliticalPartyKeyWords(Entry entry) {
        writeValuesToFile(Directory.POLITICAL_PARTIES, entry.getName(), format(entry));
    }

    public static void writeSpeechScores(Entry entry) {
        writeValuesToFile(Directory.SPEECHES, entry.getDate(), formatSpeeches(entry));
    }

    public static void writeMemberHighestScore(Entry entry) {
        writeHighestScore(Directory.MEMBERS, entry);
    }

    public static void writePoliticalPartyHighestScore(Entry entry) {
        writeHighestScore(Directory.POLITICAL_PARTIES, entry);
    }

    private static String format(Entry entry) {
        return entry.getDate() + COLON + entry.getKeyWord() + HYPHEN + entry.getScore();
    }

    private static String formatSpeeches(Entry entry) {
        if (NUMBER_OF_KEY_WORDS > 1) {
            return ID + entry.getSpeechId() +
                    NAME + entry.getName() +
                    TOP + NUMBER_OF_KEY_WORDS + WORD_SCORES + entry.getKeyWordScores() + CURLY_BRACE +
                    CONTENT + entry.getContent();
        }

        return ID + entry.getSpeechId() +
                NAME + entry.getName() +
                WORD_SCORE + entry.getKeyWord() + HYPHEN + entry.getScore() +
                CONTENT + entry.getContent();
    }

    private static String formatHighestScore(Entry entry) {
        return HIGHEST_SCORE_WORD + entry.getKeyWord() + HYPHEN + entry.getScore();
    }

    //Similarities

    public static void writeSimilarities(Deque<Pair<String>> queue) {
        writeValuesToFile(Directory.SIMILARITIES, SIMILARITIES, formatSimilarities(queue));
    }

    private static String formatSimilarities(Deque<Pair<String>> queue) {
        List<String> values = new ArrayList<>(queue.size());

        queue.forEach(pair -> values.add(pair.toString()));

        return String.join(LINE_BREAK, values);
    }


    //CLUSTERS

    public static <T> void writeClusters(Map<Integer, List<T>> clusters) {
        writeValuesToFile(Directory.CLUSTERS, CLUSTERS, formatClusters(clusters));
    }

    private static <T> String formatClusters(Map<Integer, List<T>> clusters) {
        List<String> values = new ArrayList<>(clusters.size());

        for (Map.Entry<Integer, List<T>> cluster : clusters.entrySet()) {
            values.add(formatCluster(cluster));
        }

        return String.join(LINE_BREAK, values);
    }

    private static <T> String formatCluster(Map.Entry<Integer, List<T>> cluster) {
        return cluster.getKey() + COLON + cluster.getValue(); //if T = Speech, Speech.toString() is called
    }

    //WRITE

    private static void writeValuesToFile(Directory directory, String fileName, String values) {
        Path filePath = directory.getPath().resolve(fileName + TXT);

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(values);
            writer.newLine();
        } catch (IOException e) {
            System.out.println(FAILED_TO_WRITE_TO_FILE + filePath);
        }
    }

    private static void writeHighestScore(Directory directory, Entry entry) {
        Path filePath = directory.getPath().resolve(entry.getName() + TXT);

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(formatHighestScore(entry));
            writer.newLine();
        } catch (IOException e) {
            System.out.println(FAILED_TO_WRITE_TO_FILE + filePath);
        }
    }


    public static void createDirectory(Directory directory) {
        try {
            Files.createDirectories(directory.getPath());
        } catch (IOException e) {
            System.out.println(FAILED_TO_CREATE_DIRECTORY + e.getMessage());
        }
    }

    //DELETE

    public static void clearDirectory(Directory directory) {
        deleteFile(new File(directory.getPathString()));
    }

    private static void deleteFile(File file) {
        //Recursive function that deletes all files in a directory and then the directory
        if (file != null) {

            File[] files = file.listFiles();
            if (files != null) {
                for (File subfile : files) {
                    if (subfile.isDirectory()) {
                        deleteFile(subfile);
                    }

                    subfile.delete();
                }
            }
        }

    }
}
