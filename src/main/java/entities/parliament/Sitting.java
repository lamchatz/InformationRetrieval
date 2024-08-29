package entities.parliament;

import entities.Entity;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Objects;

public class Sitting extends Entity {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("dd/MM/yyyy");
    private final String name;
    private LocalDate date;

    public Sitting(String name) {
        super();
        this.name = name;
    }

    public Sitting(String name, String date) {
        this.name = name;
        this.date = LocalDate.parse(date, DATE_TIME_FORMATTER);
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
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
