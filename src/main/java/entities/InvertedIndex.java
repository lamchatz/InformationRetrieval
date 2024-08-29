package entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InvertedIndex {
    private static final Set<String> COMMON_WORDS = Set.of("και", "τους", "αλλά", "άλλα", "τέτοιο", "τέτοια", "τέτοιον", "τέτοιους", "τον", "κύριε", "κυρία", "κύριος",
            "ενώ", "άρα", "μόλις", "επειδή", "αφού", "καθώς", "λοιπόν", "έτσι", "δηλαδή", "βασικά", "εγώ", "εσύ", "αυτός", "εμείς", "εσείς", "αυτοί", "έχω", "μέχρι", "σας", "μας",
            "μάλλον", "στο", "στα", "στη", "στην", "στον", "στους", "του", "των", "μην", "περίπου", "δεν", "μπορεί", "εννοείται", "κάνω", "κάποιο", "κάποια", "κάποιον",
            "τις", "τες", "της", "την", "ένας", "μία", "ένα", "ενός", "έναν", "μίας", "ότι", "φράση", "κυρίες", "κύριοι", "συνάδελφοι", "θα", "όπως", "κάτι", "τώρα", "έχει", "έχουμε", "έχεις",
            "διότι", "παρά", "ούτε", "είτε", "ακόμη", "που", "γιατί", "από", "μια", "για", "είναι", "εμένα", "εσένα", "εμάς", "εσάς", "αυτούς", "αυτόν", "αυτήν", "αυτή", "αυτό",
            "εαν", "εάν", "πως", "πώς", "μου", "σου", "κατά", "όμως", "είμαι", "είσαι", "είμαστε", "είσαστε", "ήσασταν",
            "ήμασταν", "πού", "έχετε", "είχα", "είχες", "είχε", "είχαμε", "είχατε", "είχαν", "έχουν", "ποιο", "ποιος", "ποιός", "ναι",
            "όχι", "όποιος", "όποια", "οποία", "οποίο", "οποίοι", "ετών", "επομένως", "γίνονται", "γίνονταν", "αριθμό", "γράψει", "μιλώ", "μιλάω",
            "συμβαίνει", "συμβαίνουν", "μιας", "είσασταν", "πήγαμε", "θέμα", "θέλω", "είμεθα", "γνωρίζετε", "εκάστοτε", "ίδια", "ίδιο", "ίδιος",
            "θέση", "οποίo", "τοσο", "αντί");
    private static final String SPACE = " ";
    private static final String WHITESPACE = "\\s";
    private static final String REGEX = "[.!«¶»@#$%…^&*()_=+<>/?‘;'\",:\\[\\]\\t\\s-]";

    private static long i;
    private long counter;
    private Map<String, Map<Integer, Long>> index;

    public InvertedIndex() {
        this.index = new HashMap<>();
        this.counter = 0;
        i = 0;
    }

    public void indexSpeech(Speech speech) {
        for (String word : speech.getText().toLowerCase().replaceAll(REGEX, SPACE).split(WHITESPACE)) {
            if (!COMMON_WORDS.contains(word) && word.length() > 2) {
                index.computeIfAbsent(word, w -> new HashMap<>()).merge(speech.getId(), 1L, Long::sum);
            }
        }

        // counter++;
    }

    private void processAndSetWords(String speech) {

    }

    public Map<String, Map<Integer, Long>> getIndex() {
        return index;
    }

    public Map<Integer, Long> search(String word) {
        return index.get(word);
    }

    public void printI() {
        System.out.println(i + " of " + counter);
    }

    public void print() {
        index.entrySet().forEach(System.out::println);
    }
}
