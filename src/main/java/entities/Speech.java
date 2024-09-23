package entities;

import java.util.Objects;

public class Speech extends Entity {
    private final String text;
    private int size;
    private final int memberId;
    private final int sittingId;

    public Speech(int memberId, String text, int sittingId) {
        super();
        this.memberId = memberId;
        this.text = text;
        this.sittingId = sittingId;
    }


    public String getText() {
        return text;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public int getMemberId() {
        return memberId;
    }

    public int getSittingId() {
        return sittingId;
    }


    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Speech speech = (Speech) o;
        return getId() == speech.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
