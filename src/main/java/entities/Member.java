package entities;

import java.util.Collection;

public class Member {

    private static final String FIELD_CAN_NOT_BE_NULL_OR_EMPTY = "%s can not be null or empty";
    private static final String NAME = "name";
    private static final String POLITICAL_PARTY = "politicalParty";
    private static final String REGION = "region";
    private static final String ROLE = "role";
    private String name;
    private String politicalParty;
    private String region;
    private String role;
    private boolean gender;

    private Collection<String> speeches;

    public Member() {
        super();
    }

    private Member(String name, String politicalParty, String region, String role, boolean gender) {
        this.name = name;
        this.politicalParty = politicalParty;
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

    public String getPoliticalParty() {
        return politicalParty;
    }

    public String getRegion() {
        return region;
    }

    public String getRole() {
        return role;
    }

    public boolean getGender() {
        return gender;
    }

    public Collection<String> getSpeeches() {
        return speeches;
    }

    public void addSpeech(String speech) {
        speeches.add(speech);
    }

    public void print() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Member{");
        sb.append("name='").append(name).append('\'');
        sb.append(", politicalParty='").append(politicalParty).append('\'');
        sb.append(", region='").append(region).append('\'');
        sb.append(", role='").append(role).append('\'');
        sb.append(", gender=").append(gender);
        sb.append('}');
        return sb.toString();
    }

    public static class Builder {
        private String name;
        private String politicalParty;
        private String region;
        private String role;
        private boolean gender;

        public Builder() {
            super();
        }

        public Builder name(String name) {
            validateField(name, NAME);
            this.name = name;
            return this;
        }

        public Builder politicalParty(String politicalParty) {
            validateField(politicalParty, POLITICAL_PARTY);
            this.politicalParty = politicalParty;
            return this;
        }

        public Builder region(String region) {
            validateField(region, REGION);
            this.region = region;
            return this;
        }

        public Builder role(String role) {
            validateField(role, ROLE);
            this.role = role;
            return this;
        }

        public Builder gender(boolean gender) {
            this.gender = gender;
            return this;
        }

        private void validateField(String field, String fieldName) {
            if (field == null || field.isBlank()) {
                throw new IllegalStateException(String.format(FIELD_CAN_NOT_BE_NULL_OR_EMPTY, fieldName));
            }
        }

        public Member create() {
            return new Member(name, politicalParty, region, role, gender);
        }
    }
}
