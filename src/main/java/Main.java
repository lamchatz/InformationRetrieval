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
        searchEngine.search("αποκατάσταση τραγωδία σημαντικό");
        //searchEngine.search("αποκατάσταση κανεις τραγωδία ΣΗΜΑΝΤΙΚΌ");

        if (Config.EXTRACT_KEY_WORDS) {
            Extractor extractor = new Extractor();
            extractor.extractMemberKeyWords();
            extractor.extractKeyWordsForPoliticalParties();
            extractor.extractKeyWordsForSpeeches();
        }

        if (Config.FIND_SIMILARITIES) {
            Calculator calculator = new Calculator();

            if (Config.FIND_SIMILARITIES_IN_BATCHES) {
                calculator.calculateInBatches();
            } else {
                calculator.calculate();
            }
        }

        //new ClustersRepository().getSpeechVectors();
    }
}