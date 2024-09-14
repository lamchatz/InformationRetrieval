package similarity;

import java.util.Objects;

public class Pair <T>{
    private final T member1;
    private final T member2;

    public Pair(T memberName1, T member2) {
        this.member1 = memberName1;
        this.member2 = member2;
    }

    public T getMember1() {
        return member1;
    }

    public T getMember2() {
        return member2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<T> pair = (Pair<T>) o;
        return Objects.equals(member1, pair.member1) && Objects.equals(member2, pair.member2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(member1, member2);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Pair{");
        sb.append("memberName1='").append(member1).append('\'');
        sb.append(", memberName2='").append(member2).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
