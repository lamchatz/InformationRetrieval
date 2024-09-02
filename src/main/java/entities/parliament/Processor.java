package entities.parliament;

import database.PeriodRepository;

import java.util.HashSet;
import java.util.Set;

public class Processor {
    private Period lastPeriod;
    private Set<String> periodNames;
    private Set<String> sessionNames;
    private Set<String> sittingNames;
    private PeriodRepository periodRepository;

    public Processor() {
        this.lastPeriod = new Period("");
        this.periodNames = new HashSet<>(1);
        this.periodNames.add("");
        this.sessionNames = new HashSet<>();
        this.sittingNames = new HashSet<>();

        this.periodRepository = new PeriodRepository();
    }

    public void process(String periodName, String sessionName, String sittingName, String sittingDate) {
        if (!periodNames.contains(periodName)) {
            periodNames.clear();
            periodNames.add(periodName);

            periodRepository.save(lastPeriod);

            lastPeriod = new Period(periodName);
        }
        managePeriodSessionAndSitting(lastPeriod, sessionName, sittingName, sittingDate);
    }


    public void flush() {
        periodRepository.save(lastPeriod);
    }

    private void printPeriod(Period period) {
        println("Last Period: " + period.getName());
        for (Session session : period.getSessions()) {
            println("Session: " + session.getName());
            print("Sittings: ");
            for (Sitting sitting : session.getSittings()) {
                print(sitting.getName() + ", ");
            }
            println("");
        }
        println("~~~~~~~~~~~~~~");
    }

    public void println(String text) {
        System.out.println(text);
    }

    public void print(String text) {
        System.out.print(text);
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
