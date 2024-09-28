package keyword;

import config.Config;
import database.KeyWordRepository;
import utility.Directory;
import utility.FileManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static utility.Functions.println;

public class Extractor {
    private final KeyWordRepository keyWordRepository;
    private final Collection<String> DATES;

    public Extractor() {
        this.keyWordRepository = new KeyWordRepository();
        this.DATES = getDates();
        FileManager.clearDirectory(Directory.KEYWORDS);

        FileManager.createDirectory(Directory.KEYWORDS);
        FileManager.createDirectory(Directory.MEMBERS);
        FileManager.createDirectory(Directory.POLITICAL_PARTIES);
        FileManager.createDirectory(Directory.SPEECHES);
    }

    public void extract() {
        if (Config.EXTRACT_MEMBER_KEY_WORDS) {
            println("Extracting Member keywords...");
            extractMemberKeyWords();
        }
        if (Config.EXTRACT_POLITICAL_PARTY_KEY_WORDS) {
            println("Extracting Political Party keywords...");
            extractKeyWordsForPoliticalParties();
        }
        if (Config.EXTRACT_SPEECH_KEY_WORDS) {
            println("Extracting Speech keywords...");
            extractKeyWordsForSpeeches();
        }
    }

    private void extractMemberKeyWords() {
        final Map<String, Entry> memberHighestScore = new HashMap<>(1524); //number of members in the big dataset

        for (String date : DATES) {
            println("Searched year " + date);
            Collection<Entry> membersKeyWordsForEachYear = keyWordRepository.getMembersKeyWordsForEachYear(date);
            for (Entry entry : membersKeyWordsForEachYear) {
                //For each member keep the word with the highest score
                memberHighestScore.merge(entry.getName(), entry, this::keepEntryWithMaxScore);

                FileManager.writeMemberKeyWords(entry);
            }
        }
        memberHighestScore.values().forEach(FileManager::writeMemberHighestScore);
    }

    private void extractKeyWordsForPoliticalParties() {
        final Map<String, Entry> politicalPartyHighestScore = new HashMap<>(32); //number of political parties in the big dataset

        for (String date : DATES) {
            Collection<Entry> keyWordsForPoliticalPartiesForEachYear = keyWordRepository.getKeyWordsForPoliticalPartiesForEachYear(date);
            for (Entry entry : keyWordsForPoliticalPartiesForEachYear) {
                //For each political party keep the word with the highest score
                politicalPartyHighestScore.merge(entry.getName(), entry, this::keepEntryWithMaxScore);

                FileManager.writePoliticalPartyKeyWords(entry);
            }
        }

        politicalPartyHighestScore.values().forEach(FileManager::writePoliticalPartyHighestScore);
    }

    private void extractKeyWordsForSpeeches() {
        for (String date : DATES) {
            println("searched year: " + date + " for speeches");
            Collection<Entry> keyWordForSpeech = keyWordRepository.getKeyWordForSpeech(date);
            for (Entry entry : keyWordForSpeech) {
                FileManager.writeSpeechScores(entry);
            }
        }
    }

    private Collection<String> getDates() {
        return keyWordRepository.getUniqueDates();
    }

    private Entry keepEntryWithMaxScore(Entry oldEntry, Entry newEntry) {
        if (oldEntry.getScore() >= newEntry.getScore()) {
            return oldEntry;
        }
        return newEntry;
    }

}
