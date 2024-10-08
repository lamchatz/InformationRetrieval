package entities;

import java.util.Objects;

public class Member extends Entity {

    private final String name;
    private final int politicalPartyId;
    private final String region;
    private final String role;
    private final String gender;

    private Member(String name, int politicalPartyId, String region, String role, String gender) {
        super();
        this.name = name;
        this.politicalPartyId = politicalPartyId;
        this.region = region;
        this.role = role;
        this.gender = gender;
    }

    public static Builder with() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public int getPoliticalPartyId() {
        return politicalPartyId;
    }

    public String getRegion() {
        return region;
    }

    public String getRole() {
        return role;
    }

    public String getGender() {
        return gender;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Member{");
        sb.append("name='").append(name).append('\'');
        sb.append(", politicalParty='").append(politicalPartyId).append('\'');
        sb.append(", region='").append(region).append('\'');
        sb.append(", role='").append(role).append('\'');
        sb.append(", gender=").append(gender);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return name.equals(member.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public static class Builder {
        private String name;
        private int politicalPartyId;
        private String region;
        private String role;
        private String gender;

        public Builder() {
            super();
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder politicalPartyId(int politicalParty) {
            this.politicalPartyId = politicalParty;
            return this;
        }

        public Builder region(String region) {
            this.region = region;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public Builder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public Member create() {
            return new Member(name, politicalPartyId, region, role, gender);
        }
    }
}
