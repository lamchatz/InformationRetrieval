package entities;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Speech {
    private static final Set<String> COMMON_WORDS = Set.of("κι", "και", "ο", "η", "το", "οι", "τα", "τους", "αλλά", "άλλα", "ή", "τέτοιο", "τέτοια", "τέτοιον", "τέτοιους", "τον", "κ", "κύριε", "κυρία", "κύριος",
            "ενώ", "άρα", "μόλις", "επειδή", "αφού", "καθώς", "λοιπόν", "έτσι", "δηλαδή", "βασικά", "εγώ", "εσύ", "αυτός", "εμείς", "εσείς", "αυτοί", "έχω", "μέχρι", "σας", "μας",
            "μάλλον", "στο", "στα", "στη", "στην", "στον", "στους", "του", "των", "μη", "μην", "περίπου",  "να", "δεν", "δε", "μπορεί", "εννοείται", "κάνω", "κάποιο", "κάποια", "κάποιον",
            "τις", "τες", "της", "την", "ένας", "μία", "ένα", "ενός", "έναν", "μίας", "ότι", "φράση", "κυρίες", "κύριοι", "συνάδελφοι", "θα", "όπως", "κάτι", "τώρα", "έχει", "έχουμε", "έχεις",
            "διότι", "παρά", "ούτε", "είτε", "ακόμη", "που", "γιατί", "από", "τη", "μια", "για", "είναι", "εμένα", "εσένα", "εμάς", "εσάς", "αυτούς", "αυτόν", "αυτήν", "αυτή", "αυτό",
            "με", "αν", "εαν", "εάν", "πως", "πώς", "μου", "σου", "τι", "κατά", "όμως", "σε", "είμαι", "είσαι", "είμαστε", "είσαστε", "ήσασταν",
            "ήμασταν", "πού", "έχετε", "είχα", "είχες", "είχε", "είχαμε", "είχατε", "είχαν", "έχουν", "ποιο", "ποιος", "ποιός", "τί", "ναι",
            "όχι", "όποιος", "όποια", "οποία", "οποίο", "οποίοι");

    private static final String SPACE = " ";
    private static final String REGEX = "[.!«»@#$%^&*()\\-_=+<>/?;'\",:]+";

    private TimePeriod timePeriod;
    private List<String> words;

    public Speech() {
        this.words = new ArrayList<>();
    }

    public TimePeriod getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(TimePeriod timePeriod) {
        this.timePeriod = timePeriod;
    }

    public void setContent(String content) {
        processAndSetWords(content);
    }

    public List<String> getWords() {
        return words;
    }

    public int getDocSize() {
        return words.size();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Speech{");
        sb.append("date=").append(timePeriod);
        sb.append(", words=").append(words);
        sb.append('}');
        return sb.toString();
    }

    private void processAndSetWords(String speech) {
        for (String word : speech.toLowerCase().replaceAll(REGEX, " ").split(SPACE)) {
            if (!COMMON_WORDS.contains(word) && word.length() > 3) {
                words.add(word);
            }
        }
    }
}
