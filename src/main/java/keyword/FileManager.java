package keyword;

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
    private static final Path KEYWORDS_DIR_PATH = Paths.get(KEYWORDS_DIR);
    private static final Path MEMBER_DIR_PATH = Paths.get(MEMBERS_DIR);
    private static final Path POLITICAL_PARTIES_DIR_PATH = Paths.get(POLITICAL_PARTIES_DIR);
    private static final String HIGHEST_SCORE_WORD = "\nHighest score word = %s - ";
    private static final String YEAR_WORD_SCORE = "%s: %s - ";

    protected FileManager() {
        super();
    }

    protected static void writeMemberKeyWords(Entry entry) {
        writeValuesToFile(MEMBER_DIR_PATH, entry);
    }

    protected static void writePoliticalPartyKeyWords(Entry entry) {
        writeValuesToFile(POLITICAL_PARTIES_DIR_PATH, entry);
    }

    private static void writeValuesToFile(Path path, Entry entry) {
        Path filePath = path.resolve(entry.getName() + TXT);

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(String.format(YEAR_WORD_SCORE, entry.getDate(), entry.getKeyWord()) + entry.getScore()); //use concatenation to avoid trailing zeros from formatting
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
            writer.write(String.format(HIGHEST_SCORE_WORD, entry.getKeyWord()) + entry.getScore()); //use concatenation to avoid trailing zeros from formatting
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Failed to write to file: " + filePath);
        }
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
