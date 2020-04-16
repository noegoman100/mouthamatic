package mouthamatic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//todo redesign this to pull the image map table in from the DB, and do the computation locally, and not on the DB.
public class SymbolMapper {

//    public static List<String> mapSymbolsTo(List<Integer> parcedSymbols, int mouth_pair_id){
//        //SELECT image_url FROM `word-to-phoneme`.image_map WHERE symbol_id_pk2 = 999;
//        ResultSet rs;
//        List<String> imageSequence = new ArrayList<>();
//
//        for (int i = 0; i < parcedSymbols.size(); i++) {
//            rs = Main.db.sendQuery("SELECT image_url FROM `word-to-phoneme`.image_map WHERE symbol_id_pk2 = " + parcedSymbols.get(i) + ";");
//            while (true){
//                try {
//                    if (!rs.next()) break;
//                } catch (SQLException throwables) {
//                    throwables.printStackTrace();
//                }
//                try {
//                    System.out.println(rs.getString(1));
//                } catch (SQLException throwables) {
//                    throwables.printStackTrace();
//                }
//            }
//        }
//
//        return imageSequence;
//    }
    public static SentenceData mapSymbolsTo(SentenceData sentenceData, int mouth_pair_id){
        //SELECT image_url FROM `word-to-phoneme`.image_map WHERE symbol_id_pk2 = 999;
        ResultSet rs;
        List<String> imageSequence = new ArrayList<>();

        for (int i = 0; i < sentenceData.getParcedSymbols().size(); i++) {
            rs = Main.db.sendQuery("SELECT image_url FROM `word-to-phoneme`.image_map WHERE symbol_id_pk2 = " + sentenceData.getParcedSymbols().get(i)
                    + " AND mouth_pair_id_pk1 = " + mouth_pair_id
                    + ";");
            while (true){
                try {
                    if (!rs.next()) break;
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                try {
                    System.out.println(rs.getString(1));
                    imageSequence.add(rs.getString(1));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        sentenceData.setParcedImageSequence(imageSequence);
        return sentenceData;
    }


}
