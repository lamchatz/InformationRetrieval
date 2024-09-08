package entities;

import java.util.Objects;

public class PoliticalParty extends Entity{
    private final String name;

    public PoliticalParty(String name) {
        super();
        this.name = name;
    }

    public PoliticalParty(int id, String name) {
        setId(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PoliticalParty that = (PoliticalParty) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
