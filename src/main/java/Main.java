import config.Config;
import csv.Reader;
import database.DatabaseManager;
import utility.Functions;

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

        Functions.println("Searching... ");

        searchEngine.search("συνεδριαση", "", "", "", "session 1");

        //searchEngine.search("αποκατάσταση κανεις τραγωδία ΣΗΜΑΝΤΙΚΌ");
    }
}