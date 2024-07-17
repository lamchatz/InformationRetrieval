package entities;

import java.util.Objects;

public class Entry {

    private String memberName;
    private long occurances;

    public Entry(String memberName) {
        this.memberName = memberName;
        this.occurances = 0;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public long getOccurances() {
        return occurances;
    }

    public void increase() {
        this.occurances++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entry entry = (Entry) o;
        return memberName.equals(entry.memberName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberName);
    }
}
