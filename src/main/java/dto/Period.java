package dto;

public class Period {
    private final String name;
    private final String session;
    private final String sitting;
    private final String date;

    public Period(String name, String session, String sitting, String date) {
        this.name = name;
        this.session = session;
        this.sitting = sitting;
        this.date = date;
    }

    @Override
    public String toString() {
        return "{\n\t\tName: " + name +
                "\n\t\tSession: " + session +
                "\n\t\tSitting: " + sitting +
                "\n\t\tDate: " + date + "\n\t}";

    }
}
