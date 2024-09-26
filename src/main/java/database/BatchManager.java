package database;

import config.Config;
import entities.Member;
import entities.PoliticalParty;
import entities.Speech;
import entities.TfOfSpeech;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class BatchManager {
    private final Collection<PoliticalParty> politicalPartiesBatch;
    private final Collection<Member> membersBatch;
    private final Collection<Speech> speechesBatch;
    private final Collection<TfOfSpeech> tfOfSpeechBatches;
    //set initial capacity to avoid resizing.

    private final PoliticalPartyRepository politicalPartyRepository;
    private final MemberRepository memberRepository;
    private final SpeechRepository speechRepository;
    private final TfOfSpeechesRepository tfOfSpeechesRepository;


    public BatchManager() {
        this.politicalPartiesBatch = new ArrayList<>(32); //based on the number of political parties in the big dataset
        this.membersBatch = new ArrayList<>(1524); //based on the number of members in the big dataset
        this.speechesBatch = new ArrayList<>(Config.EXECUTE_BATCH_AFTER);
        this.tfOfSpeechBatches = new ArrayList<>(Config.EXECUTE_BATCH_AFTER);

        this.politicalPartyRepository = new PoliticalPartyRepository();
        this.memberRepository = new MemberRepository();
        this.speechRepository = new SpeechRepository();
        this.tfOfSpeechesRepository = new TfOfSpeechesRepository();
    }

    public void addToBatch(PoliticalParty politicalParty) {
        politicalPartiesBatch.add(politicalParty);
    }

    public void addToBatch(Member member) {
        membersBatch.add(member);
    }
    public void addToBatch(Speech speech) {
        speechesBatch.add(speech);
    }
    public void addToBatch(TfOfSpeech tfOfSpeech) {
        tfOfSpeechBatches.add(tfOfSpeech);
    }

    public void flushBatches() {
        try (Connection connection = DatabaseManager.connect()) {
            connection.createStatement().execute("PRAGMA SYNCHRONOUS = OFF;");
            connection.setAutoCommit(false);

            politicalPartyRepository.executeBatch(connection, politicalPartiesBatch);
            memberRepository.executeBatch(connection, membersBatch);
            speechRepository.executeBatch(connection, speechesBatch);
            tfOfSpeechesRepository.executeBatch(connection, tfOfSpeechBatches);

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
