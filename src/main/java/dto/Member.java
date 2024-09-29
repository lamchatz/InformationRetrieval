package dto;

public class Member {
    private final String name;
    private final String politicalParty;
    private final String region;
    private final String role;

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
        return "{\n\t\tName: " + name +
                "\n\t\tPolitical Party: " + politicalParty +
                "\n\t\tRegion: " +  region +
                "\n\t\tRole: " + role +
                "\n\t}";
    }
}
