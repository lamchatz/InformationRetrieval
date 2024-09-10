package entities;

import java.util.Objects;

public class PoliticalPartyMemberRelation {
    private final int politicalPartyId;
    private final int memberId;
    private final String from;
    private String to;

    public PoliticalPartyMemberRelation(int politicalPartyId, int memberId, String from) {
        this.politicalPartyId = politicalPartyId;
        this.memberId = memberId;
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getPoliticalPartyId() {
        return politicalPartyId;
    }

    public int getMemberId() {
        return memberId;
    }

    public String getFrom() {
        return from;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PoliticalPartyMemberRelation that = (PoliticalPartyMemberRelation) o;
        return politicalPartyId == that.politicalPartyId && memberId == that.memberId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(politicalPartyId, memberId);
    }
}
