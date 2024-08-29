package entities;

import java.util.Objects;

public class Speech extends Entity {
    private static final String SPACE = " ";
    private final String text;
    private final int size;
    //private final Sitting sitting;
    //private final Member member;
    private final int memberId;
    private final int sittingId;

    public Speech(int memberId, String text, int sittingId) {
        super();
        this.memberId = memberId;
        this.text = text;
        this.size = text.split(SPACE).length;
        this.sittingId = sittingId;
    }


    public String getText() {
        return text;
    }

    public int getSize() {
        return size;
    }

//    public Member getMember() {return member;}

    public int getMemberId() {
        return memberId;
    }

//    public Sitting getSitting() {
//        return sitting;
//    }

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
