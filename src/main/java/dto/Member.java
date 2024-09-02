package dto;

public class Member {
    private String name;
    private String politicalParty;
    private String region;
    private String role;

    public Member(String name, String politicalParty, String region, String role) {
        this.name = name;
        this.politicalParty = politicalParty;
        this.region = region;
        this.role = role;
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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Member{");
        sb.append("name='").append(name).append('\'').append("\n");
        sb.append("politicalParty='").append(politicalParty).append('\'').append("\n");
        sb.append("region='").append(region).append('\'').append("\n");
        sb.append("role='").append(role).append('\'').append("\n");
        sb.append('}');
        return sb.toString();
    }
}
