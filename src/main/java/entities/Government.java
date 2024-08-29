package entities;

import java.util.Objects;

public class Government {
    private String primeMinister;
    private String startDate;
    private String endDate;

    public Government(String primeMinister, String startDate, String endDate) {
        this.primeMinister = primeMinister;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getPrimeMinister() {
        return primeMinister;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public static Government processGovernment(String government) {
        // ['μητσοτακη κυριακου(08/07/2019-28/07/2020)']
        String[] parts = government.substring(2, government.length() - 2).split("\\(");
        String name = parts[0];
        String[] dates = parts[1].substring(0, parts[1].length() - 1).split("-");

        return new Government(name, dates[0], dates[1]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Government that = (Government) o;
        return Objects.equals(primeMinister, that.primeMinister) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(primeMinister, startDate, endDate);
    }

    @Override
    public String toString() {
        return "Government{" +
                "primeMinister='" + primeMinister + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}
