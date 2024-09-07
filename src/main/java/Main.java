import config.Config;
import csv.Reader;
import database.DatabaseManager;
import utility.Functions;

public class Main {

    public static void main(String[] args) {

        if (Config.DROP_AND_RECREATE_TABLES) {
            DatabaseManager.init();
            Reader.read();
            DatabaseManager.createTFIndex();
            DatabaseManager.createIdfTfTable();
        }

        SearchEngine searchEngine = new SearchEngine();

        Functions.println("Searching... ");

        //searchEngine.search("αποκατάσταση");

        //searchEngine.search("αποκατάσταση κανεις τραγωδία ΣΗΜΑΝΤΙΚΌ");
    }
}