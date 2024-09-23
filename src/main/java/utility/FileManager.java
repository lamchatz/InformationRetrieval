package utility;

import config.Config;
import keyword.Entry;
import similarity.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

public class FileManager {
    private static final String WORKING_DIR = System.getProperty("user.dir");
    private static final String TXT = ".txt";
    private static final String KEYWORDS_DIR = WORKING_DIR + "/keywords";
    private static final String MEMBERS_DIR = KEYWORDS_DIR + "/members";
    private static final String POLITICAL_PARTIES_DIR = KEYWORDS_DIR + "/politicalParties";
    private static final String SPEECHES_DIR = KEYWORDS_DIR + "/speeches";
    private static final String SIMILARITIES_DIR = WORKING_DIR + "/similarities";
    private static final Path KEYWORDS_DIR_PATH = Paths.get(KEYWORDS_DIR);
    private static final Path MEMBER_DIR_PATH = Paths.get(MEMBERS_DIR);
    private static final Path POLITICAL_PARTIES_DIR_PATH = Paths.get(POLITICAL_PARTIES_DIR);
    private static final Path SPEECHES_DIR_PATH = Paths.get(SPEECHES_DIR);
    private static final Path SIMILARITIES_DIR_PATH = Paths.get(SIMILARITIES_DIR);
    private static final String ID = "ID: ";
    private static final String NAME = ", Name: ";
    private static final String WORD_SCORE = ", Word - Score: ";
    private static final String CONTENT = ", Content: ";
    private static final String HYPHEN = " - ";
    private static final String HIGHEST_SCORE_WORD = "\nHighest score word = ";
    private static final String COLON = ": ";
    private static final int NUMBER_OF_KEY_WORDS = Config.NUMBER_OF_KEY_WORDS;

    public FileManager() {
        super();
    }

    public static void writeMemberKeyWords(Entry entry) {
        writeValuesToFile(MEMBER_DIR_PATH, entry.getName(), format(entry));
    }

    public static void writePoliticalPartyKeyWords(Entry entry) {
        writeValuesToFile(POLITICAL_PARTIES_DIR_PATH, entry.getName(), format(entry));
    }

    public static void writeSpeechScores(Entry entry) {
        writeValuesToFile(SPEECHES_DIR_PATH, entry.getDate(), formatSpeeches(entry));
    }

    private static String format(Entry entry) {
            return entry.getDate() + COLON + entry.getKeyWord() + HYPHEN + entry.getScore();
    }

    private static String formatSpeeches(Entry entry) {
        if (NUMBER_OF_KEY_WORDS > 1) {
            return ID + entry.getSpeechId() +
                    NAME + entry.getName() +
                    ", Top " + NUMBER_OF_KEY_WORDS + " Word Scores: {" + entry.getKeyWordScores() + "}" +
                    CONTENT + entry.getContent();
        }

        return ID + entry.getSpeechId() +
                NAME + entry.getName() +
                WORD_SCORE + entry.getKeyWord() + HYPHEN + entry.getScore() +
                CONTENT + entry.getContent();
    }

    public static void writeSimilarities(Deque<Pair<String>> queue) {
        writeValuesToFile(SIMILARITIES_DIR_PATH, "similarities", formatSimilarities(queue));
    }

    private static String formatSimilarities(Deque<Pair<String>> queue) {
        List<String> values = new ArrayList<>();

        queue.forEach(pair -> values.add(pair.toString()));

        return String.join("\n", values);
    }

    private static void writeValuesToFile(Path path, String fileName, String values) {
        Path filePath = path.resolve(fileName + TXT);

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(values);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Failed to write to file: " + filePath);
        }
    }

    public static void writeMemberHighestScore(Entry entry) {
        writeHighestScore(MEMBER_DIR_PATH, entry);
    }

    public static void writePoliticalPartyHighestScore(Entry entry) {
        writeHighestScore(POLITICAL_PARTIES_DIR_PATH, entry);
    }

    private static void writeHighestScore(Path path, Entry entry) {
        Path filePath = path.resolve(entry.getName() + TXT);

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(formatHighestScore(entry));
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Failed to write to file: " + filePath);
        }
    }

    private static String formatHighestScore(Entry entry) {
        return HIGHEST_SCORE_WORD + entry.getKeyWord() + HYPHEN + entry.getScore();
    }

    public static void createKeyWordsDirectory() {
        try {
            Files.createDirectories(KEYWORDS_DIR_PATH);
        } catch (IOException e) {
            System.out.println("Failed to create directory: " + e.getMessage());
        }
    }

    public static void createMembersSubDirectory() {
        try {
            Files.createDirectories(MEMBER_DIR_PATH);
        } catch (IOException e) {
            System.out.println("Failed to create directory: " + e.getMessage());
        }
    }

    public static void createPoliticalPartiesSubDirectory() {
        try {
            Files.createDirectories(POLITICAL_PARTIES_DIR_PATH);
        } catch (IOException e) {
            System.out.println("Failed to create directory: " + e.getMessage());
        }
    }

    public static void createSpeechesSubDirectory() {
        try {
            Files.createDirectories(SPEECHES_DIR_PATH);
        } catch (IOException e) {
            System.out.println("Failed to create directory: " + e.getMessage());
        }
    }

    public static void createSimilaritiesSubDirectory() {
        try {
            Files.createDirectories(SIMILARITIES_DIR_PATH);
        } catch (IOException e) {
            System.out.println("Failed to create directory: " + e.getMessage());
        }
    }

    public static void clearKeywordsDirectory() {
        deleteFile(new File(KEYWORDS_DIR));
    }

    private static void deleteFile(File file) {
        for (File subfile : Objects.requireNonNull(file.listFiles())) {
            if (subfile.isDirectory()) {
                deleteFile(subfile);
            }

            subfile.delete();
        }
    }

    public static void clearSimilaritiesDirectory() {
        deleteFile(new File(SIMILARITIES_DIR));
    }
}
