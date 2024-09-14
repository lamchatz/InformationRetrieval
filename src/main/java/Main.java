import config.Config;
import csv.Reader;
import database.DatabaseManager;
import keyword.Extractor;
import similarity.Calculator;
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

        if (Config.EXTRACT_KEY_WORDS) {
            Extractor extractor = new Extractor();
            extractor.extractMemberKeyWords();
            extractor.extractKeyWordsForPoliticalParties();
            extractor.extractKeyWordsForSpeeches();
        }

        Calculator calculator = new Calculator();
        calculator.calculate5();
        calculator.calculate();
    }
}