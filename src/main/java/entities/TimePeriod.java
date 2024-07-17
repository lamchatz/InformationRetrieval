package entities;

import org.joda.time.LocalDate;

public class TimePeriod {
    private LocalDate sittingDate;
    private String parliamentaryPeriod;
    private String parliamentarySession;
    private String parliamentarySitting;
    private String government;

    public TimePeriod() {
        super();
    }

    private TimePeriod(LocalDate sittingDate, String parliamentaryPeriod, String parliamentarySession, String parliamentarySitting, String government) {
        this.sittingDate = sittingDate;
        this.parliamentaryPeriod = parliamentaryPeriod;
        this.parliamentarySession = parliamentarySession;
        this.parliamentarySitting = parliamentarySitting;
        this.government = government;
    }

    public static Builder with() {
        return new Builder();
    }

    public LocalDate getSittingDate() {
        return sittingDate;
    }

    public String getParliamentaryPeriod() {
        return parliamentaryPeriod;
    }

    public String getParliamentarySession() {
        return parliamentarySession;
    }

    public String getParliamentarySitting() {
        return parliamentarySitting;
    }

    public String getGovernment() {
        return government;
    }

    public static class Builder {
        private LocalDate sittingDate;
        private String parliamentaryPeriod;
        private String parliamentarySession;
        private String parliamentarySitting;
        private String government;

        public Builder() {
            super();
        }

        public Builder sittingDate(LocalDate sittingDate) {
            this.sittingDate = sittingDate;
            return this;
        }

        public Builder parliamentaryPeriod(String parliamentaryPeriod) {
            this.parliamentaryPeriod = parliamentaryPeriod;
            return this;
        }

        public Builder parliamentarySession(String parliamentarySession) {
            this.parliamentarySession = parliamentarySession;
            return this;
        }

        public Builder parliamentarySitting(String parliamentarySitting) {
            this.parliamentarySitting = parliamentarySitting;
            return this;
        }

        public Builder government(String government) {
            this.government = government;
            return this;
        }

        public TimePeriod create() {
            return new TimePeriod(sittingDate, parliamentaryPeriod, parliamentarySession, parliamentarySitting, government);
        }
    }
}
