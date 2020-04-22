package mouthamatic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//todo redesign this to pull the image map table in from the DB, and do the computation locally, and not on the DB.
public class SymbolMapper {

    public static SentenceData mapSymbolsTo(SentenceData sentenceData, int mouth_pair_id){
        //SELECT image_url FROM `word-to-phoneme`.image_map WHERE symbol_id_pk2 = 999;
//        ResultSet rs;
//        List<String> imageSequence = new ArrayList<>();
//
//        for (int i = 0; i < sentenceData.getParcedSymbols().size(); i++) {
//            rs = Main.db.sendQuery("SELECT image_url FROM `word-to-phoneme`.image_map WHERE symbol_id_pk2 = " + sentenceData.getParcedSymbols().get(i)
//                    + " AND mouth_pair_id_pk1 = " + mouth_pair_id
//                    + ";");
//            while (true){
//                try {
//                    if (!rs.next()) break;
//                } catch (SQLException throwables) {
//                    throwables.printStackTrace();
//                }
//                try {
//                    System.out.println(rs.getString(1));
//                    imageSequence.add(rs.getString(1));
//                } catch (SQLException throwables) {
//                    throwables.printStackTrace();
//                }
//            }
//        }
//        sentenceData.setParcedImageSequence(imageSequence);

        ///************ New version
        ResultSet rs2;
        ResultSet noPhenomeImageRS;
        List<String> imageSequence2 = new ArrayList<>();
        //noPhonomeImageRS is for adding a no-sound image between words
        noPhenomeImageRS = Main.db.sendQuery("SELECT image_url FROM `word-to-phoneme`.image_map WHERE symbol_id_pk2 = "
                + "999 "
                + " AND mouth_pair_id_pk1 = " + mouth_pair_id
                + ";");
        try {
            noPhenomeImageRS.next();//get past blank first row.
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        for (int i = 0; i < sentenceData.getParcedSentenceWordsList().size(); i++) {
            //**** Add Blank Below - I am loading up the No-Sound  (or Blank) image to paste between each word.

            try {
                imageSequence2.add(noPhenomeImageRS.getString(1));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            //**** End Add Blank
            //Below For Loop - iterate inside of each word, looking at each phonemeId.
            for (int j = 0; j < sentenceData.getParcedSentenceWordsList().get(i).getPhonemes().size(); j++){
                rs2 = Main.db.sendQuery("SELECT image_url FROM `word-to-phoneme`.image_map WHERE symbol_id_pk2 = "
                        + sentenceData.getParcedSentenceWordsList().get(i).getPhonemes().get(j)
                        + " AND mouth_pair_id_pk1 = " + mouth_pair_id
                        + ";");
                while (true) {
                    try {
                        if (!rs2.next()) break;
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    try {
                        System.out.println(rs2.getString(1)); //TODO Temp
                        imageSequence2.add(rs2.getString(1));
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                } //End While Loop
            } //End Inner For Loop
        } //End Outer For Loop

        try { //Try to add a no-sound image to the end of the list
            imageSequence2.add(noPhenomeImageRS.getString(1));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        sentenceData.setParcedImageSequence(imageSequence2);

        ///************ End New version



        return sentenceData;
    }


}
