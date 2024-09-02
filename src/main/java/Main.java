import config.Config;
import csv.Reader;
import database.DatabaseManager;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        if (Config.DROP_AND_RECREATE_TABLES) {
            DatabaseManager.init();
            Reader.read();
            DatabaseManager.createTableIndexes();
            DatabaseManager.createIDF_TF_RelatedTables();
        }

        SearchEngine searchEngine = new SearchEngine();
//
        System.out.println("Searching... ");

        searchEngine.search("αποκατάσταση κανεις τραγωδία ΣΗΜΑΝΤΙΚΌ");

//        searchEngine.search("συνάδελφε", "σακοραφα ηλια");
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