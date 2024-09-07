package entities;

import java.util.ArrayList;
import java.util.Collection;
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
            "θέση", "οποίo", "τοσο", "αντί", "ευχαριστώ", "λόγο", "πολύ", "αυτά", "πρέπει", "δύο", "όλα", "εδώ",
            "νέα", "εκεί", "μόνο", "στις", "μετά", "ήταν", "κάνει", "προς", "όλοι", "γίνει", "είπε", "επίσης", "όπου", "άλλο",
            "αυτές", "επί", "έξι", "λέω", "πάρα", "όταν", "ώστε", "ακόμα", "κάθε", "όλες", "όλους", "ήδη", "μπορούν", "έγινε",
            "πει", "πλέον", "όσο", "ώρα", "πολλά", "πριν", "όλο", "λέμε", "πάλι", "πιο", "σαν", "τρεις", "βέβαια", "είπα",
            "όλη", "δυο", "πάει", "ποτέ", "τρία", "δώδεκα", "εντός", "λένε", "λέτε", "ξέρω", "πέντα", "ποιοι", "λέει");
    private static final String SPACE = " ";
    private static final String WHITESPACE = "\\s";
    private static final String REGEX = "[.!«¶»@#$%…^&*()_=+<>/?‘;'\",:\\[\\]\\t\\s-]";

    private final Map<String, Long> index;
    private final Collection<TF> tfScores;

    public InvertedIndex() {
        this.index = new HashMap<>();
        this.tfScores = new ArrayList<>();
    }

    public void indexSpeech(Speech speech) {
        for (String word : speech.getText().toLowerCase().replaceAll(REGEX, SPACE).split(WHITESPACE)) {
            if (!COMMON_WORDS.contains(word) && word.length() > 2 && containsOnlyLetters(word)) {
                index.merge(word,  1L, Long::sum);
            }
        }

        speech.setSize(index.size());
        computeTFValues(speech);
    }

    private void computeTFValues(Speech speech) {
        TF tfOfSpeech = new TF(speech.getId(), speech.getSize());
        for (Map.Entry<String, Long> entry: index.entrySet()) {
            String word = entry.getKey();

            tfOfSpeech.calculate(word, index.get(word));
        }

        tfScores.add(tfOfSpeech);
        index.clear();
    }

    private boolean containsOnlyLetters(String word) {
        for (char c : word.toCharArray()) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    public Collection<TF> getTfScores() {
        return tfScores;
    }

    public void print() {
        index.entrySet().forEach(System.out::println);
    }
}
