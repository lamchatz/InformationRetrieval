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

    @Override
    public String toString() {
        return "{\n\tSpeech: " + speech + "\n\tMember: " + member + "\n\tPeriod: " + period + "\n}";
    }
}
