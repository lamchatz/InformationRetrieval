package entities;

import java.util.*;

public class InvertedIndex {

    private static final Set<String> R = Set.of("κι", "και", "Ο", "ο", "Η", "η", "Το", "το", "Οι", "οι",
            "Τα", "τα", "Τους", "τους", "αλλά", "Αλλά", "Άλλα", "άλλα", "ή", "Ή", "ενώ", "Ενώ", "άρα", "Άρα",
            "Μόλις", "μόλις", "Επειδή", "επειδή", "Αφού", "αφού", "Καθώς", "καθώς", "Λοιπόν", "λοιπόν", "Έτσι",
            "έτσι", "Δηλαδή", "δηλαδή", "Βασικά", "βασικά", "Μάλλον", "μάλλον", "Στο", "στο", "Στα", "στα",
            "Στη", "στη", "Στην", "στην", "Στον", "στον", "Στους", "στους", "Του", "του", "Των", "των", "Τις",
            "τις", "Τες", "τες", "Της", "της", "Την", "την", "Ένας", "ένας", "Μία", "μία", "Ένα", "ένα", "Ενός",
            "ενός", "Έναν", "έναν", "Μίας", "μίας", "Ότι", "ότι", "Διότι", "διότι", "Παρά", "παρά", "Ούτε", "ούτε",
            "Είτε", "είτε", "Ακομή", "ακόμη", "που", "Που", "Γιατί", "γιατί", ",", ".", "!", "'", ";", "\"", "-");

    private Map<String, Set<String>> index;

    public InvertedIndex () {
        index = new HashMap<>();
    }

    public void indexSpeech(String speechId, String content) {
        String[] words = content.split(" ");

        for (String word: words) {
            word = word.toLowerCase();

            index.computeIfAbsent(word, w -> new HashSet<>());
            index.get(word).add(speechId);
        }
    }

    private String processString(String speech) {
        String processedString = "";
        return processedString;
    }
}
