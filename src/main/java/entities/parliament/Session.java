package entities.parliament;

import entities.Entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Session extends Entity {

    private final String name;
    private final Set<Sitting> sittings;

    public Session(String name, Sitting sitting) {
        super();
        this.name = name;
        this.sittings = new HashSet<>();
        this.sittings.add(sitting);
    }

    public void addSitting(Sitting sitting) {
        this.sittings.add(sitting);
    }

    public String getName() {
        return name;
    }

    public Set<Sitting> getSittings() {
        return sittings;
    }

    public Optional<Sitting> getSitting(String name) {
        return sittings.stream().filter(sitting -> name.equals(sitting.getName())).findFirst();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return Objects.equals(name, session.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
