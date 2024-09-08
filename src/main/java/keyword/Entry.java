package keyword;

import java.util.Objects;

public class Entry {
    private final String date;
    private final String name;
    private String keyWord;
    private double score;
    private int speechId;
    private String content;
    private String keyWordScores;

    public Entry(String date, String name, String keyWord, double score) {
        this.date = date;
        this.name = name;
        this.keyWord = keyWord;
        this.score = score;
    }

    public Entry (String date, String name, String keyWord, double score, int speechId, String content) {
        this(date, name, keyWord, score);
        this.speechId = speechId;
        this.content = content;
    }

    public Entry (String date, String name, String keyWordScores, int speechId, String content) {
        this.date = date;
        this.name = name;
        this.keyWordScores = keyWordScores;
        this.speechId = speechId;
        this.content = content;
    }


    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public double getScore() {
        return score;
    }

    public int getSpeechId() {
        return speechId;
    }

    public String getContent() {
        return content;
    }

    public String getKeyWordScores() {
        return keyWordScores;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entry entry = (Entry) o;
        return Objects.equals(date, entry.date) && Objects.equals(name, entry.name) && Objects.equals(keyWord, entry.keyWord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, name, keyWord);
    }
}
