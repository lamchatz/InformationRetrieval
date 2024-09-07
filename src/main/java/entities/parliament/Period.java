package entities.parliament;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Period {

    private final String name;
    private final Set<Session> sessions;

    public Period(String name) {
        this.name = name;
        this.sessions = new HashSet<>();
    }

    public void addSession(Session session) {
        this.sessions.add(session);
    }

    public Optional<Session> getSession(String sessionName) {
        return sessions.stream().filter(session -> sessionName.equals(session.getName())).findFirst();
    }

    public int getSitting(String sessionName, String sittingName) {
        return sessions.stream()
                .filter(session -> sessionName.equals(session.getName()))
                .findFirst()
                .flatMap(session -> session.getSitting(sittingName))
                .map(Sitting::getId)
                .orElse(-1);
    }

    public String getName() {
        return name;
    }

    public Set<Session> getSessions() {
        return sessions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Period period = (Period) o;
        return Objects.equals(name, period.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
