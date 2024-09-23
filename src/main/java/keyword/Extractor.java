package keyword;

import database.KeyWordRepository;
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
        FileManager.clearKeywordsDirectory();
        FileManager.createKeyWordsDirectory();
        FileManager.createMembersSubDirectory();
        FileManager.createPoliticalPartiesSubDirectory();
        FileManager.createSpeechesSubDirectory();
    }

    public void extractMemberKeyWords() {
        final Map<String, Entry> memberHighestScore = new HashMap<>(1524); //number of members in the big dataset

        for (String date : DATES) {
            println("Searched year " + date);
            Collection<Entry> membersKeyWordsForEachYear = keyWordRepository.getMembersKeyWordsForEachYear(date);
            for (Entry entry : membersKeyWordsForEachYear) {
                memberHighestScore.merge(entry.getName(), entry, this::keepEntryWithMaxScore);

                FileManager.writeMemberKeyWords(entry);
            }
        }
        memberHighestScore.values().forEach(FileManager::writeMemberHighestScore);
    }

    public void extractKeyWordsForPoliticalParties() {
        final Map<String, Entry> politicalPartyHighestScore = new HashMap<>(32); //number of political parties in the big dataset

        for (String date : DATES) {
            Collection<Entry> keyWordsForPoliticalPartiesForEachYear = keyWordRepository.getKeyWordsForPoliticalPartiesForEachYear(date);
            for (Entry entry : keyWordsForPoliticalPartiesForEachYear) {
                politicalPartyHighestScore.merge(entry.getName(), entry, this::keepEntryWithMaxScore);

                FileManager.writePoliticalPartyKeyWords(entry);
            }
        }

        politicalPartyHighestScore.values().forEach(FileManager::writePoliticalPartyHighestScore);
    }

    public void extractKeyWordsForSpeeches() {
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
