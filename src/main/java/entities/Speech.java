package entities;

import java.util.Objects;

public class Speech {
    private static final String SPACE = " ";
    private final long id;
    private TimePeriod timePeriod;
    private String text;
    private int size;

    public Speech(long id) {
        this.id = id;
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

    public void setText(String text) {
        this.text = text;
        this.size = text.split(SPACE).length;
    }

    public String getText() {
        return text;
    }

    public int getSize() {
        return size;
    }


    @Override
    public String toString() {
        return String.format("Speech{id=%s, timePeriod=%s, text=%s, size=%s}", id, timePeriod, text, size);
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
}
