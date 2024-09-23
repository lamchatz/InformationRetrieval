package similarity;

import java.util.Objects;

public class Pair <T> {
    private static final String HYPHEN = " - ";
    private static final String COLON = ": ";
    private final T member1;
    private final T member2;
    private final Double similarity;

    public Pair(T memberName1, T member2, Double similarity) {
        this.member1 = memberName1;
        this.member2 = member2;
        this.similarity = similarity;
    }

    public T getMember1() {
        return member1;
    }

    public T getMember2() {
        return member2;
    }

    public Double getSimilarity() {
        return similarity;
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
        return member1 + HYPHEN + member2 + COLON + similarity;
    }
}
