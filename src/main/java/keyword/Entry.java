package keyword;

import java.util.Objects;

public class Entry {
    private final String date;
    private final String name;
    private final String keyWord;
    private final double score;

    public Entry(String date, String name, String keyWord, double score) {
        this.date = date;
        this.name = name;
        this.keyWord = keyWord;
        this.score = score;
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
