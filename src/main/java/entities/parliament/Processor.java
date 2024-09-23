package entities.parliament;

import database.PeriodRepository;

public class Processor {
    private Period lastPeriod;
    private String lastPeriodName;
    private final PeriodRepository periodRepository;

    public Processor() {
        this.lastPeriod = new Period("");
        this.lastPeriodName = "";

        this.periodRepository = new PeriodRepository();
    }

    public void process(String periodName, String sessionName, String sittingName, String sittingDate) {
        if (!periodName.equals(lastPeriodName)) {
            lastPeriodName = periodName;

            periodRepository.save(lastPeriod);

            lastPeriod = new Period(periodName);
        }
        managePeriodSessionAndSitting(lastPeriod, sessionName, sittingName, sittingDate);
    }


    public void flush() {
        periodRepository.save(lastPeriod);
    }

    public int getSittingId(String sessionName, String sittingName) {
        return lastPeriod.getSitting(sessionName, sittingName);
    }

    public static void managePeriodSessionAndSitting(Period period, String sessionName, String sittingName, String sittingDate) {
        period.getSession(sessionName).ifPresentOrElse(
                (session) -> session.addSitting(new Sitting(sittingName, sittingDate)),
                () -> period.addSession(
                        new Session(
                                sessionName,
                                new Sitting(sittingName, sittingDate)
                        )
                )
        );
    }
}
