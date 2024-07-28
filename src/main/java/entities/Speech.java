package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Speech {
    private static final Set<String> COMMON_WORDS = Set.of("και", "τους", "αλλά", "άλλα", "τέτοιο", "τέτοια", "τέτοιον", "τέτοιους", "τον", "κύριε", "κυρία", "κύριος",
            "ενώ", "άρα", "μόλις", "επειδή", "αφού", "καθώς", "λοιπόν", "έτσι", "δηλαδή", "βασικά", "εγώ", "εσύ", "αυτός", "εμείς", "εσείς", "αυτοί", "έχω", "μέχρι", "σας", "μας",
            "μάλλον", "στο", "στα", "στη", "στην", "στον", "στους", "του", "των", "μην", "περίπου",  "δεν", "μπορεί", "εννοείται", "κάνω", "κάποιο", "κάποια", "κάποιον",
            "τις", "τες", "της", "την", "ένας", "μία", "ένα", "ενός", "έναν", "μίας", "ότι", "φράση", "κυρίες", "κύριοι", "συνάδελφοι", "θα", "όπως", "κάτι", "τώρα", "έχει", "έχουμε", "έχεις",
            "διότι", "παρά", "ούτε", "είτε", "ακόμη", "που", "γιατί", "από", "μια", "για", "είναι", "εμένα", "εσένα", "εμάς", "εσάς", "αυτούς", "αυτόν", "αυτήν", "αυτή", "αυτό",
            "εαν", "εάν", "πως", "πώς", "μου", "σου", "κατά", "όμως", "είμαι", "είσαι", "είμαστε", "είσαστε", "ήσασταν",
            "ήμασταν", "πού", "έχετε", "είχα", "είχες", "είχε", "είχαμε", "είχατε", "είχαν", "έχουν", "ποιο", "ποιος", "ποιός", "ναι",
            "όχι", "όποιος", "όποια", "οποία", "οποίο", "οποίοι");

    private static final String SPACE = " ";
    private static final String REGEX = "[.!«»@#$%^&*()-_=+<>/?;'\",:]+";

    private long id;
    private TimePeriod timePeriod;
    private List<String> words;

    public Speech(long id) {
        this.id = id;
        this.words = new ArrayList<>();
    }

    public long getId() {
        return id;
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
        sb.append("id=").append(id);
        sb.append(", timePeriod=").append(timePeriod);
        sb.append(", words=").append(words);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Speech speech = (Speech) o;
        return id == speech.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    private void processAndSetWords(String speech) {
        for (String word : speech.toLowerCase().replaceAll(REGEX, " ").split(SPACE)) {
            if (!COMMON_WORDS.contains(word) && word.length() > 2) {
                words.add(word);
            }
        }
    }
}
