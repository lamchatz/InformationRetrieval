package csv;

import config.Config;
import database.InvertedIndexRepository;
import database.MemberRepository;
import database.SpeechRepository;
import entities.Government;
import entities.InvertedIndex;
import entities.Member;
import entities.Speech;
import entities.parliament.Processor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Reader {
    private static final int EXECUTE_BATCH_AFTER = Config.EXECUTE_BATCH_AFTER;
    public static final String MEMBER_NAME = "member_name";
    public static final String SITTING_DATE = "sitting_date";
    public static final String PARLIAMENTARY_PERIOD = "parliamentary_period";
    public static final String PARLIAMENTARY_SESSION = "parliamentary_session";
    public static final String PARLIAMENTARY_SITTING = "parliamentary_sitting";
    public static final String POLITICAL_PARTY = "political_party";
    public static final String GOVERNMENT = "government";
    public static final String MEMBER_REGION = "member_region";
    public static final String ROLES = "roles";
    public static final String MEMBER_GENDER = "member_gender";
    public static final String SPEECH = "speech";
    private static final String[] HEADER = {MEMBER_NAME, SITTING_DATE, PARLIAMENTARY_PERIOD, PARLIAMENTARY_SESSION, PARLIAMENTARY_SITTING, POLITICAL_PARTY, GOVERNMENT, MEMBER_REGION, ROLES, MEMBER_GENDER, SPEECH};
    private static final String[] HEADER2 = {MEMBER_NAME, POLITICAL_PARTY, SPEECH};

    public static void read() {
        InvertedIndex invertedIndex = new InvertedIndex();
        Set<Government> governments = new HashSet<>();
        Map<String, Integer> members = new HashMap<>();
        Processor parliamentProcessor = new Processor();

        InvertedIndexRepository invertedIndexRepository = new InvertedIndexRepository();
        MemberRepository memberRepository = new MemberRepository();
        SpeechRepository speechRepository = new SpeechRepository();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(Config.BIG))) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader(HEADER).withFirstRecordAsHeader());

            long counter = 0;
            long id = 0;
            for (CSVRecord csvRecord : csvParser) {
                id = csvRecord.getRecordNumber();
                String name = csvRecord.get(MEMBER_NAME);
                if (name != null && !name.isBlank()) {
                    counter++;

                    if (!members.containsKey(name)) {
                        Member member = Member.with().name(name)
                                .politicalParty(csvRecord.get(POLITICAL_PARTY))
                                .region(csvRecord.get(MEMBER_REGION))
                                .role(csvRecord.get(ROLES))
                                .gender(parseGender(csvRecord.get(MEMBER_GENDER)))
                                .create();

                        memberRepository.addToBatch(member);
                        members.put(name, member.getId());
                    }

                    String sessionName = csvRecord.get(PARLIAMENTARY_SESSION);
                    String sittingName = csvRecord.get(PARLIAMENTARY_SITTING);

                    parliamentProcessor.process(csvRecord.get(PARLIAMENTARY_PERIOD),
                            sessionName,
                            sittingName,
                            csvRecord.get(SITTING_DATE));

                    //governments.add(Government.processGovernment(csvRecord.get(GOVERNMENT)));

                    Speech speech = new Speech(members.get(name),
                            csvRecord.get(SPEECH),
                            parliamentProcessor.getSittingId(sessionName, sittingName)
                    );

                    invertedIndex.indexSpeech(speech);
                    speechRepository.addToBatch(speech);

                    if (counter == EXECUTE_BATCH_AFTER) {
                        System.out.println(id);
                        counter = 0;
                        invertedIndexRepository.save(invertedIndex);
                    }
                }
            }

            parliamentProcessor.flush();
            speechRepository.flushBatch();
            invertedIndexRepository.save(invertedIndex);
            memberRepository.flushBatch();

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private static boolean parseGender(String gender) {
        return gender.equalsIgnoreCase("male");
    }

    private static void processInvertedIndex() {
    }

    ;


}
