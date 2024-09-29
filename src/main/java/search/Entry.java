package search;

public class Entry {
    private final String question;
    private final String memberName;
    private final String from;
    private final String to;
    private final String periodOrSession;

    public Entry(String question, String memberName, String from, String to, String periodOrSession) {
        this.question = question;
        this.memberName = memberName;
        this.from = from;
        this.to = to;
        this.periodOrSession = periodOrSession;
    }

    public String getQuestion() {
        return question;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getPeriodOrSession() {
        return periodOrSession;
    }
}
