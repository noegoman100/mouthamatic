package mouthamatic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SymbolMapper {

    public static SentenceData mapSymbolsTo(SentenceData sentenceData, int mouth_pair_id, int blankSymbolId){
        ResultSet rs;
        ResultSet noPhenomeImageRS;
        List<String> imageSequence = new ArrayList<>();
        //noPhonomeImageRS is for adding a no-sound image between words
        noPhenomeImageRS = Main.db.sendQuery("SELECT image_url FROM `word-to-phoneme`.image_map WHERE symbol_id_pk2 = "
                + blankSymbolId + " "
                + " AND mouth_pair_id_pk1 = " + mouth_pair_id
                + ";");
        try {
            noPhenomeImageRS.next();//get past blank first row.
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        for (int i = 0; i < sentenceData.getParsedSentenceWordsList().size(); i++) {

            //Below For Loop - iterate inside of each word, looking at each phonemeId.
            for (int j = 0; j < sentenceData.getParsedSentenceWordsList().get(i).getPhonemes().size(); j++){
                rs = Main.db.sendQuery("SELECT image_url FROM `word-to-phoneme`.image_map WHERE symbol_id_pk2 = "
                        + sentenceData.getParsedSentenceWordsList().get(i).getPhonemes().get(j)
                        + " AND mouth_pair_id_pk1 = " + mouth_pair_id
                        + ";");
                while (true) {
                    try {
                        if (!rs.next()) break;
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    try {
                        imageSequence.add(rs.getString(1));
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                } //End While Loop
            } //End Inner For Loop
        } //End Outer For Loop

        sentenceData.setParsedImageSequence(imageSequence); //

        return sentenceData;
    }

}
