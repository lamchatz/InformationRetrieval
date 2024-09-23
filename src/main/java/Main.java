import config.Config;
import csv.Reader;
import database.DatabaseManager;
import keyword.Extractor;
import similarity.Calculator;

import static utility.Functions.println;

public class Main {

    public static void main(String[] args) {
        if (Config.DROP_AND_RECREATE_TABLES) {
            DatabaseManager.init();
            Reader.read();
            DatabaseManager.createTFIndex();
            DatabaseManager.createIdfTfTable();
        }

        if (Config.SEARCH) {
            println("Searching... ");
            SearchEngine searchEngine = new SearchEngine();
            //searchEngine.search("αποκατάσταση τραγωδία σημαντικό", "", "2000-11-01");
            //searchEngine.search("αποκατάσταση κανεις τραγωδία ΣΗΜΑΝΤΙΚΌ");
        }

        if (Config.EXTRACT_KEY_WORDS) {
            Extractor extractor = new Extractor();
            if (Config.EXTRACT_MEMBER_KEY_WORDS) {
                println("Extracting Member keywords...");
                extractor.extractMemberKeyWords();
            }
            if (Config.EXTRACT_POLITICAL_PARTY_KEY_WORDS) {
                println("Extracting Political Party keywords...");
                extractor.extractKeyWordsForPoliticalParties();
            }
            if (Config.EXTRACT_SPEECH_KEY_WORDS) {
                println("Extracting Speech keywords...");
                extractor.extractKeyWordsForSpeeches();
            }
        }

        if (Config.FIND_SIMILARITIES) {
            Calculator calculator = new Calculator();

            if (Config.FIND_SIMILARITIES_IN_BATCHES) {
                //calculator.calculateInBatches();
            } else {
                calculator.calculate();
            }
        }

        //new ClustersRepository().getSpeechVectors();
    }
}