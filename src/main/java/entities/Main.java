package entities;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static final String BIG = "src/main/resources/Greek_Parliament_Proceedings_1989_2020.csv";
    private static final String NORMAL = "src/main/resources/Greek_Parliament_Proceedings.csv";
    private static final String TEST = "src/main/resources/test.csv";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("dd/MM/yyyy");

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

    public static void main(String[] args) {
        reader2();
    }

    private static void reader2() {
        Map<Member, List<Long>> members = new HashMap<>(); //ΒΑΣΗ
        Map<Long, Speech> speeches = new HashMap<>(); //ΒΑΣΗ

        Config config = new Config();
        InvertedIndex invertedIndex = new InvertedIndex(config.shouldSaveIndexToFile(), config.getSaveAfter());



        try (BufferedReader reader = Files.newBufferedReader(Paths.get(TEST))) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader(HEADER2).withFirstRecordAsHeader());

            for (CSVRecord csvRecord : csvParser) {
                long id = csvRecord.getRecordNumber();

                String name = csvRecord.get(MEMBER_NAME);
                if (name != null && !name.isBlank()) {
                    Member member = Member.with().name(name).create();

                    Speech speech = new Speech(id);
                    speech.setContent(csvRecord.get(SPEECH));
                    speech.setTimePeriod(TimePeriod.with()
//                            .sittingDate(LocalDate.parse(csvRecord.get(SITTING_DATE), DATE_TIME_FORMATTER))
//                            .parliamentaryPeriod(csvRecord.get(PARLIAMENTARY_PERIOD))
//                            .parliamentarySession(csvRecord.get(PARLIAMENTARY_SESSION))
//                            .parliamentarySitting(csvRecord.get(PARLIAMENTARY_SITTING))
//                            .government(csvRecord.get(GOVERNMENT))
                            .create()
                    );

                    speeches.put(id, speech);

                    members.computeIfAbsent(member, s -> new ArrayList<>()).add(id);

                    invertedIndex.indexSpeech(speech);
                }
            }
//            invertedIndex.print();
//            Scanner scanner = new Scanner(System.in);
//            System.out.println("Yo");
//
//            String input = scanner.nextLine();
//
//            Map<Long, Long> map = invertedIndex.search(input);
//
//            Optional<Map.Entry<Long, Long>> max = map.entrySet()
//                    .stream().max(Map.Entry.comparingByValue());
//
//            if (max.isPresent()) {
//                Long idS = max.get().getKey();
//                Speech s = speeches.get(idS);
//                System.out.println(s);
//            }
//
//            scanner.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}