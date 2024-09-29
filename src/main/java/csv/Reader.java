package csv;

import config.Config;
import database.BatchManager;
import database.PoliticalPartyMembersRepository;
import entities.InvertedIndex;
import entities.Member;
import entities.PoliticalParty;
import entities.PoliticalPartyMemberRelation;
import entities.Speech;
import entities.parliament.Processor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import search.Entry;
import utility.Directory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static utility.Functions.println;

public class Reader {
    private static final int EXECUTE_BATCH_AFTER = Config.EXECUTE_BATCH_AFTER;
    private static final String MEMBER_NAME = "member_name";
    private static final String SITTING_DATE = "sitting_date";
    private static final String PARLIAMENTARY_PERIOD = "parliamentary_period";
    private static final String PARLIAMENTARY_SESSION = "parliamentary_session";
    private static final String PARLIAMENTARY_SITTING = "parliamentary_sitting";
    private static final String POLITICAL_PARTY = "political_party";
    private static final String GOVERNMENT = "government";
    private static final String MEMBER_REGION = "member_region";
    private static final String ROLES = "roles";
    private static final String MEMBER_GENDER = "member_gender";
    private static final String SPEECH = "speech";

    private static final String[] HEADER = {MEMBER_NAME, SITTING_DATE, PARLIAMENTARY_PERIOD, PARLIAMENTARY_SESSION, PARLIAMENTARY_SITTING, POLITICAL_PARTY, GOVERNMENT, MEMBER_REGION, ROLES, MEMBER_GENDER, SPEECH};
    private static final String ANONYMOUS = "Anonymous";
    private static final String QUESTION = "question";
    private static final String MEMBER_NAME1 = "memberName";
    private static final String FROM = "from";
    private static final String TO = "to";
    private static final String PERIOD_OR_SESSION = "periodOrSession";
    private static final String[] SEARCH_HEADER = {QUESTION, MEMBER_NAME1, FROM, TO, PERIOD_OR_SESSION};
    private static final String QUESTIONS_CSV = "/questions.csv";

    public static void read() {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(Config.CSV_TO_READ))) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader(HEADER).withFirstRecordAsHeader());

            final InvertedIndex invertedIndex = new InvertedIndex();
            final Processor parliamentProcessor = new Processor();

            final Set<PoliticalPartyMemberRelation> politicalPartyMemberRelations = new HashSet<>(5000); //an approximation to avoid constant resizing
            final Map<String, Integer> politicalParties = new HashMap<>(32); //based on the number of political parties in the big dataset
            final Map<String, Integer> members = new HashMap<>(1524); //based on the number of members in the big dataset

            final PoliticalPartyMembersRepository politicalPartyMembersRepository = new PoliticalPartyMembersRepository();
            final BatchManager batchManager = new BatchManager();

            long counter = 0;
            for (CSVRecord csvRecord : csvParser) {
                counter++;

                String name = csvRecord.get(MEMBER_NAME);

                if (name == null || name.isBlank()) {
                    name = ANONYMOUS;
                }

                //Map of political party names to their id, used for proper mapping and ensuring no duplicates
                String politicalPartyName = csvRecord.get(POLITICAL_PARTY);
                if (!politicalParties.containsKey(politicalPartyName)) {
                    PoliticalParty politicalParty = new PoliticalParty(politicalPartyName);

                    batchManager.addToBatch(politicalParty);
                    politicalParties.put(politicalPartyName, politicalParty.getId());
                }

                //Map of member names to their id, used for proper mapping and ensuring no duplicates
                if (!members.containsKey(name)) {
                    Member member = Member.with().name(name)
                            .politicalPartyId(politicalParties.get(politicalPartyName))
                            .region(csvRecord.get(MEMBER_REGION))
                            .role(csvRecord.get(ROLES))
                            .gender(csvRecord.get(MEMBER_GENDER))
                            .create();

                    batchManager.addToBatch(member);
                    members.put(name, member.getId());
                }

                String sittingDate = csvRecord.get(SITTING_DATE);

                PoliticalPartyMemberRelation politicalPartyMemberRelation = new PoliticalPartyMemberRelation(
                        politicalParties.get(politicalPartyName),
                        members.get(name),
                        sittingDate);

                if (politicalPartyMemberRelations.add(politicalPartyMemberRelation)) {
                    politicalPartyMembersRepository.insert(politicalPartyMemberRelation);
                }

                final String sessionName = csvRecord.get(PARLIAMENTARY_SESSION);
                final String sittingName = csvRecord.get(PARLIAMENTARY_SITTING);

                parliamentProcessor.process(csvRecord.get(PARLIAMENTARY_PERIOD),
                        sessionName,
                        sittingName,
                        sittingDate);

                final Speech speech = new Speech(members.get(name),
                        csvRecord.get(SPEECH),
                        parliamentProcessor.getSittingId(sessionName, sittingName)
                );

                batchManager.addToBatch(invertedIndex.indexSpeech(speech));
                batchManager.addToBatch(speech);

                if (counter == EXECUTE_BATCH_AFTER) {
                    println(csvRecord.getRecordNumber());
                    counter = 0;

                    println("Flushing batches");
                    batchManager.flushBatches();
                }
            }

            println("Flushing parliament records...");
            parliamentProcessor.flush();

            println("Flushing batches");
            batchManager.flushBatches();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Collection<Entry> readQuestions() {
        Collection<Entry> questions = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(Directory.SEARCH.getPathString() + QUESTIONS_CSV))) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader(SEARCH_HEADER).withFirstRecordAsHeader());

            for (CSVRecord csvRecord : csvParser) {
                questions.add(
                        new Entry(csvRecord.get(QUESTION),
                        csvRecord.get(MEMBER_NAME1),
                        csvRecord.get(FROM),
                        csvRecord.get(TO),
                        csvRecord.get(PERIOD_OR_SESSION))
                );
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return questions;
    }
}