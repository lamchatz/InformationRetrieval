package entities;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    private static final String BIG = "src/main/resources/Greek_Parliament_Proceedings_1989_2020.csv";
    private static final String NORMAL = "src/main/resources/Greek_Parliament_Proceedings.csv";
    private static final String TEST = "src/main/resources/test.csv";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("dd/MM/yyyy");
    private static final String[] HEADER = {"member_name", "sitting_date", "parliamentary_period", "parliamentary_session", "parliamentary_sitting", "political_party", "government", "member_region", "roles", "member_gender", "speech"};


    public static void main(String[] args) {
        reader2();
    }

    private static void reader2() {
        InvertedIndex invertedIndex = new InvertedIndex();
        Map<Member, List<Speech>> members = new HashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(NORMAL))) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader(HEADER).withFirstRecordAsHeader());

            for (CSVRecord csvRecord : csvParser) {
                long i = csvRecord.getRecordNumber();

                String name = csvRecord.get("member_name");
                if (name != null && !name.isBlank()) {
                    Member member = Member.with().name(name).create();

                    TimePeriod timePeriod = TimePeriod.with()
                            .sittingDate(LocalDate.parse(csvRecord.get("sitting_date"), DATE_TIME_FORMATTER))
                            .parliamentaryPeriod(csvRecord.get("parliamentary_period"))
                            .parliamentarySession(csvRecord.get("parliamentary_session"))
                            .parliamentarySitting(csvRecord.get("parliamentary_sitting"))
                            .government(csvRecord.get("government"))
                            .create();

                    Speech speech = new Speech();
                    speech.setContent(csvRecord.get("speech"));
                    speech.setTimePeriod(timePeriod);

                    members.computeIfAbsent(member, s -> new ArrayList<>()).add(speech);

                    invertedIndex.indexSpeech(member.getName(), speech.getWords(), i);
                }
            }


        } catch (IOException e) {
            System.out.println(e);
        }
    }
}