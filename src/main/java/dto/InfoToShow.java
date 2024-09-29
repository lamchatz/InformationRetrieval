package dto;

public class InfoToShow {
    private final Period period;
    private final Member member;
    private final String speech;

    public InfoToShow(String speech, Member member, Period period) {
        this.speech = speech;
        this.member = member;
        this.period = period;
    }

    public Period getPeriod() {
        return period;
    }

    public Member getMember() {
        return member;
    }

    public String getSpeech() {
        return speech;
    }

    @Override
    public String toString() {
        return "{\n\tSpeech: " + speech + "\n\tMember: " + member + "\n\tPeriod: " + period + "\n}";
    }
}
