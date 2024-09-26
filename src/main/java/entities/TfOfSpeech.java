package entities;

import java.util.ArrayList;
import java.util.Collection;

public class TfOfSpeech {
    private final int speechId;
    private final Collection<Entry> score;
    private final int totalWords;

    public TfOfSpeech(int speechId, int totalWords) {
        this.speechId = speechId;
        this.totalWords = totalWords;
        this.score = new ArrayList<>(totalWords);
    }

    public void calculate(String word, Long counter) {
        this.score.add(new Entry(word, counter.doubleValue() / totalWords));
    }

    public int getSpeechId() {
        return speechId;
    }

    public Collection<Entry> getScore() {
        return score;
    }
}
