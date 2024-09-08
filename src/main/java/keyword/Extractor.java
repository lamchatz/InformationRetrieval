package keyword;

import database.KeyWordRepository;
import utility.Functions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Extractor {
    private final KeyWordRepository keyWordRepository;
    public Extractor() {
        this.keyWordRepository = new KeyWordRepository();
        FileManager.clearDirectory();
        FileManager.createKeyWordsDirectory();
        FileManager.createMembersSubDirectory();
        FileManager.createPoliticalPartiesSubDirectory();
    }

    public void extractKeyWords() {
        final Map<String, Entry> memberHighestScore = new HashMap<>(1524); //number of members in the big dataset

        for (String date : getDates()) {
            Functions.println("Searched year " + date);
            for (Entry entry :  keyWordRepository.getMembersKeyWordForEachYear(date)) {
                memberHighestScore.merge(entry.getName(), entry, this::keepEntryWithMaxScore);

                FileManager.writeMemberKeyWords(entry);
            }
        }
        memberHighestScore.values().forEach(FileManager::writeMemberHighestScore);
    }

    public void extractKeyWordsForPoliticalParties() {
        final Map<String, Entry> politicalPartyHighestScore = new HashMap<>(32); //number of political parties in the big dataset

        for (String date : getDates()) {
            for (Entry entry : keyWordRepository.getKeyWordForPoliticalParties(date)) {
                politicalPartyHighestScore.merge(entry.getName(), entry, this::keepEntryWithMaxScore);

                FileManager.writePoliticalPartyKeyWords(entry);
            }
        }

        politicalPartyHighestScore.values().forEach(FileManager::writePoliticalPartyHighestScore);
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
