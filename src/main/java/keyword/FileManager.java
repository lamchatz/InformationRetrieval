package keyword;

import config.Config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class FileManager {
    private static final String WORKING_DIR = System.getProperty("user.dir");
    private static final String TXT = ".txt";
    private static final String KEYWORDS_DIR = WORKING_DIR + "/keywords";
    private static final String MEMBERS_DIR = KEYWORDS_DIR + "/members";
    private static final String POLITICAL_PARTIES_DIR = KEYWORDS_DIR + "/politicalParties";
    private static final String SPEECHES_DIR = KEYWORDS_DIR + "/speeches";
    private static final Path KEYWORDS_DIR_PATH = Paths.get(KEYWORDS_DIR);
    private static final Path MEMBER_DIR_PATH = Paths.get(MEMBERS_DIR);
    private static final Path POLITICAL_PARTIES_DIR_PATH = Paths.get(POLITICAL_PARTIES_DIR);
    private static final Path SPEECHES_DIR_PATH = Paths.get(SPEECHES_DIR);
    private static final String ID = "ID: ";
    private static final String NAME = ", Name: ";
    private static final String WORD_SCORE = ", Word - Score: ";
    private static final String CONTENT = ", Content: ";
    private static final String HYPHEN = " - ";
    private static final String HIGHEST_SCORE_WORD = "\nHighest score word = ";
    private static final String COLON = ": ";
    private static final int NUMBER_OF_KEY_WORDS = Config.NUMBER_OF_KEY_WORDS;

    protected FileManager() {
        super();
    }

    protected static void writeMemberKeyWords(Entry entry) {
        writeValuesToFile(MEMBER_DIR_PATH, entry.getName(), format(entry));
    }

    protected static void writePoliticalPartyKeyWords(Entry entry) {
        writeValuesToFile(POLITICAL_PARTIES_DIR_PATH, entry.getName(), format(entry));
    }

    protected static void writeSpeechScores(Entry entry) {
        writeValuesToFile(SPEECHES_DIR_PATH, entry.getDate(), formatSpeeches(entry)); //concatenation would not work for this format
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

    private static void writeValuesToFile(Path path, String fileName, String values) {
        Path filePath = path.resolve(fileName + TXT);

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(values);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Failed to write to file: " + filePath);
        }
    }

    protected static void writeMemberHighestScore(Entry entry) {
        writeHighestScore(MEMBER_DIR_PATH, entry);
    }

    protected static void writePoliticalPartyHighestScore(Entry entry) {
        writeHighestScore(POLITICAL_PARTIES_DIR_PATH, entry);
    }

    private static void writeHighestScore(Path path, Entry entry) {
        Path filePath = path.resolve(entry.getName() + TXT);

        // Create a BufferedWriter to write to the file
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(formatHighestScore(entry)); //use concatenation to avoid trailing zeros from formatting
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Failed to write to file: " + filePath);
        }
    }

    private static String formatHighestScore(Entry entry) {
        return HIGHEST_SCORE_WORD + entry.getKeyWord() + HYPHEN + entry.getScore();
    }

    protected static void createKeyWordsDirectory() {
        try {
            // Create the directory and any necessary parent directories
            Files.createDirectories(KEYWORDS_DIR_PATH);
        } catch (IOException e) {
            System.out.println("Failed to create directory: " + e.getMessage());
        }
    }

    protected static void createMembersSubDirectory() {
        try {
            Files.createDirectories(MEMBER_DIR_PATH);
        } catch (IOException e) {
            System.out.println("Failed to create directory: " + e.getMessage());
        }
    }

    protected static void createPoliticalPartiesSubDirectory() {
        try {
            Files.createDirectories(POLITICAL_PARTIES_DIR_PATH);
        } catch (IOException e) {
            System.out.println("Failed to create directory: " + e.getMessage());
        }
    }

    protected static void createSpeechesSubDirectory() {
        try {
            Files.createDirectories(SPEECHES_DIR_PATH);
        } catch (IOException e) {
            System.out.println("Failed to create directory: " + e.getMessage());
        }
    }

    protected static void clearDirectory() {
        deleteFile(new File(KEYWORDS_DIR));
    }

    protected static void deleteFile(File file) {
        for (File subfile : Objects.requireNonNull(file.listFiles())) {
            if (subfile.isDirectory()) {
                deleteFile(subfile);
            }

            subfile.delete();
        }
    }
}
