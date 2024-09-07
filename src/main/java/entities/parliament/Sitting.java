package entities.parliament;

import entities.Entity;

import java.util.Objects;

public class Sitting extends Entity {

    private final String name;
    private final String date;

    public Sitting(String name, String date) {
        super();
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sitting sitting = (Sitting) o;
        return Objects.equals(name, sitting.name) && Objects.equals(date, sitting.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, date);
    }
}
