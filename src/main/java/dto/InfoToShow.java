package dto;

public class InfoToShow {
    private final Period period;
    private final Member member;
    private final Speech speech;

    public InfoToShow(Speech speech, Member member, Period period) {
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

    public Speech getSpeech() {
        return speech;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("InfoToShow{");
        sb.append("speech=").append(speech).append("\n");
        sb.append("period=").append(period).append("\n");
        sb.append("member=").append(member).append("\n");
        sb.append('}');
        return sb.toString();
    }
}
