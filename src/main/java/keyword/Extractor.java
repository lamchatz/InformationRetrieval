package keyword;

import database.KeyWordRepository;
import utility.Functions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Extractor {
    private final KeyWordRepository keyWordRepository;
    private final Collection<String> DATES;

    public Extractor() {
        this.keyWordRepository = new KeyWordRepository();
        this.DATES = getDates();
        FileManager.clearDirectory();
        FileManager.createKeyWordsDirectory();
        FileManager.createMembersSubDirectory();
        FileManager.createPoliticalPartiesSubDirectory();
        FileManager.createSpeechesSubDirectory();
    }

    public void extractMemberKeyWords() {
        final Map<String, Entry> memberHighestScore = new HashMap<>(1524); //number of members in the big dataset

        for (String date : DATES) {
            Functions.println("Searched year " + date);
            for (Entry entry :  keyWordRepository.getMembersKeyWordsForEachYear(date)) {
                memberHighestScore.merge(entry.getName(), entry, this::keepEntryWithMaxScore);

                FileManager.writeMemberKeyWords(entry);
            }
        }
        memberHighestScore.values().forEach(FileManager::writeMemberHighestScore);
    }

    public void extractKeyWordsForPoliticalParties() {
        final Map<String, Entry> politicalPartyHighestScore = new HashMap<>(32); //number of political parties in the big dataset

        for (String date : DATES) {
            Functions.println("searched year: " + date + " for political parties");
            for (Entry entry : keyWordRepository.getKeyWordForPoliticalParties(date)) {
                politicalPartyHighestScore.merge(entry.getName(), entry, this::keepEntryWithMaxScore);

                FileManager.writePoliticalPartyKeyWords(entry);
            }
        }

        politicalPartyHighestScore.values().forEach(FileManager::writePoliticalPartyHighestScore);
    }

    public void extractKeyWordsForSpeeches() {
        for (String date : DATES) {
            Functions.println("searched year: " + date + " for speeches");
            for (Entry entry : keyWordRepository.getKeyWordForSpeech(date)) {
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
