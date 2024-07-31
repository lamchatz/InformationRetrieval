package entities;

import java.io.*;
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
    private static final String INVERTED_INDEX = Config.INVERTED_INDEX;
    public static final String COMMA = ", ";
    public static final String EQUALS_SIGN = "=";
    public static final String COLON = ":";

    private static long i;
    private long counter;
    private Map<String, Map<Long, Long>> index;

    public InvertedIndex() {
        this.index = new HashMap<>();
        this.counter = 0;
        i = 0;
    }

    private void saveAndFreeMemory() {
        System.out.println("saving");
        File file = new File(INVERTED_INDEX);
        try (BufferedWriter bf = new BufferedWriter(new FileWriter(file, true))) {
            for (Map.Entry<String, Map<Long, Long>> entry : index.entrySet()) {
                bf.write(entry.getKey() + ":" + entry.getValue());
                bf.newLine();
            }

            bf.flush();
            index.clear();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void readMap() {
        File file = new File(INVERTED_INDEX);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] record = line.split(COLON);
                String key = record[0];

                //Οταν καποια λεξη εμφανιζεται πανω απο μια φορες στο αρχειο, συνεννουμε τις εγγραφες
                //Οταν καποιο ζευγαρι speechId:πληθος εμφανισεων υπαρχει πανω απο μια φορες, τα αθροιζουμε
                index.merge(key, deserialize(record[1]), (existingMap, newMap) -> {
                    newMap.forEach((k, v) -> existingMap.merge(k, v, Long::sum));
                    return existingMap;
                });
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        print();
    }

    private Map<Long, Long> deserialize(String entries) {
        Map<Long, Long> map = new HashMap<>();

        // ειναι της μορφης {ΧΧ=xx, YY=yy, ...} οποτε κραταμε ΧΧ=ΧΧ, YY=yy,... και στην συνεχεια τα ζευγαρια
        // XX=xx, YY=yy, τελος τα χωριζουμε σε XX και χχ, YY και yy, ...
        for (String entry : entries.substring(1, entries.length() - 1).split(COMMA)) {
            String[] keyValuePair = entry.split(EQUALS_SIGN);

            if (keyValuePair.length == 2) {
                map.put(Long.parseLong(keyValuePair[0]), Long.parseLong(keyValuePair[1]));
            }
        }

        return map;
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

    public Map<String, Map<Long, Long>> getIndex() {
        return index;
    }

    public Map<Long, Long> search(String word) {
        return index.get(word);
    }

    public void printI() {
        System.out.println(i + " of " + counter);
    }

    public void print() {
        index.entrySet().forEach(System.out::println);
    }
}
