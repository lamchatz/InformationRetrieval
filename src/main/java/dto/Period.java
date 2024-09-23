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

    public String getName() {
        return name;
    }

    public String getSession() {
        return session;
    }

    public String getSitting() {
        return sitting;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Period{").append("\n");
        sb.append("name='").append(name).append('\'').append("\n");
        sb.append("session='").append(session).append('\'').append("\n");
        sb.append("sitting='").append(sitting).append('\'').append("\n");
        sb.append("date='").append(date).append('\'').append("\n");
        sb.append('}');
        return sb.toString();
    }
}
