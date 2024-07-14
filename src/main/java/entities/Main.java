package entities;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;

public class Main {
    public static void main(String[] args) {
        Collection<Member> members = new ArrayList<>();
        try (FileReader fileReader = new FileReader("src/main/resources/test.csv")) {
            CSVReader csvReader = new CSVReader(fileReader);
            String[] nextRecord;

            csvReader.readNext();
            while((nextRecord = csvReader.readNext()) != null) {
                members.add(Member.with()
                        .name(nextRecord[0])
                        .politicalParty(nextRecord[1])
                        .region(nextRecord[2])
                        .role(nextRecord[3])
                        .create()
                );
            }

            members.forEach(Member::print);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}