import clusters.KMeans;
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
            extractor.extract();
        }

        if (Config.FIND_SIMILARITIES) {
            Calculator calculator = new Calculator();
            calculator.calculate();
        }

        if (Config.PERFORM_CLUSTERING) {
            KMeans kMeans = new KMeans();
            kMeans.compute();
        }
    }
}