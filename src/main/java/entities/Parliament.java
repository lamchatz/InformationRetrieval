package entities;

public class Parliament {
    private String period;
    private String session;
    private String sitting;

    public Parliament() {
        super();
    }

    private Parliament(String period, String session, String sitting) {
        this.period = period;
        this.session = session;
        this.sitting = sitting;
    }

    public String getPeriod() {
        return period;
    }

    public String getSession() {
        return session;
    }

    public String getSitting() {
        return sitting;
    }

    public static class Builder {
        private String period;
        private String session;
        private String sitting;

        public Builder period(String period) {
            this.period = period;
            return this;
        }

        public Builder session(String session) {
            this.session = session;
            return this;
        }

        public Builder sitting(String sitting) {
            this.sitting = sitting;
            return this;
        }

        public Parliament create() {
            return new Parliament(period, session, sitting);
        }
    }
}
