package entities;

import java.util.HashSet;
import java.util.Set;

public class PoliticalParty {
    private String name;
    private Set<Member> members;

    public PoliticalParty(String name) {
        this.name = name;
        this.members = new HashSet<>();
    }

    public void addMember(Member member) {
        this.members.add(member);
    }

    public String getName() {
        return name;
    }
}
