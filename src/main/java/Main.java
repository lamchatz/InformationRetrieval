import clusters.KMeans;
import config.Config;
import csv.Reader;
import database.DatabaseManager;
import keyword.Extractor;
import search.Engine;
import similarity.Calculator;

public class Main {

    public static void main(String[] args) {
        if (Config.DROP_AND_RECREATE_TABLES) {
            DatabaseManager.init();
            Reader.read();
            DatabaseManager.createTFIndex();
            DatabaseManager.createIdfTfTable();
        }

        if (Config.SEARCH) {
            Engine searchEngine = new Engine();
            searchEngine.readQuestions();
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