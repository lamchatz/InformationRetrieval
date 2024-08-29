import config.Config;
import csv.Reader;
import database.DatabaseManager;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        DatabaseManager.init();

        if (Config.DROP_AND_RECREATE_TABLES) {
            Reader.read();
        }

        //new InvertedIndexRepository().ss("δημοκρατια");
        //new PeriodRepository().getByName("entities.parliament 18 review 9");
    }


    private static void read() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Search for:");

        String input = scanner.nextLine();

//        Map<Long, Long> map = invertedIndex.search(input);
//
//        Optional<Map.Entry<Long, Long>> max = map.entrySet()
//                .stream().max(Map.Entry.comparingByValue());
//
//        if (max.isPresent()) {
//            Long idS = max.get().getKey();
//            System.out.println(s);
//        }

        scanner.close();
    }
}