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

public class Main {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("dd/MM/yyyy");

    private static final boolean SAVE_INDEX_TO_DB = Config.SAVE_INDEX_TO_DATABASE;
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

    public static void main(String[] args) {
        reader2();
//        InvertedIndex invertedIndex = new InvertedIndex();
//
//        invertedIndex.readMap();
    }

    private static void reader2() {
        //Map<Member, List<Long>> members = new HashMap<>(); //ΒΑΣΗ
        //Map<Long, Speech> speeches = new HashMap<>(); //ΒΑΣΗ
        DatabaseManager databaseManager = new DatabaseManager();

        InvertedIndex invertedIndex = new InvertedIndex();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(Config.BIG))) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader(HEADER).withFirstRecordAsHeader());

            long counter = 0;
            for (CSVRecord csvRecord : csvParser) {
                long id = csvRecord.getRecordNumber();

                String name = csvRecord.get(MEMBER_NAME);
                if (name != null && !name.isBlank()) {
                    //Member member = Member.with().name(name).create();
                    counter++;

                    Speech speech = new Speech(id);
                    speech.setText(csvRecord.get(SPEECH));
                    speech.setTimePeriod(TimePeriod.with()
//                            .sittingDate(LocalDate.parse(csvRecord.get(SITTING_DATE), DATE_TIME_FORMATTER))
//                            .parliamentaryPeriod(csvRecord.get(PARLIAMENTARY_PERIOD))
//                            .parliamentarySession(csvRecord.get(PARLIAMENTARY_SESSION))
//                            .parliamentarySitting(csvRecord.get(PARLIAMENTARY_SITTING))
//                            .government(csvRecord.get(GOVERNMENT))
                                    .create()
                    );

                    invertedIndex.indexSpeech(speech);
                    databaseManager.addSpeechToBatch(speech);
                    if (SAVE_INDEX_TO_DB && counter == EXECUTE_BATCH_AFTER) {
                        System.out.println("Saving " + id);
                        counter = 0;
                        databaseManager.saveInvertedIndex(invertedIndex.getIndex());
                    }
//                    if (counter > SAVE_AFTER) {
//                        System.out.println("shit");
//                        System.exit(1);
//                    }
                }
            }

            databaseManager.flushSpeechesBatch();
            //databaseManager.select();
//            invertedIndex.print();
            // invertedIndex.readMap();
            //databaseManager.selectIndex();
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