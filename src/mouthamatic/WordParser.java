/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mouthamatic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ed
 */
public class WordParser {
    
//    public static List<Integer> parseWords(List<String> incomingWordList){
//        List<Integer> symbolIdList = new ArrayList<Integer>();
//        //For each word, send a query. Pull the resultSet into the word list. Add a closed mouth symbol_id between
//        //each word.
//
//        //Add a closed mouth symbol to the beginning //TODO make reference variable
//        symbolIdList.add(999);
//        for (int i = 0; i < incomingWordList.size(); i++){
//            try {
//                ResultSet rs;
//                String query;
//                // SELECT word_name, word_id_pk1, part_segment_pk2, symbol_id_fk FROM `word-to-phoneme`.word_parts JOIN `word-to-phoneme`.word ON word_id = word_id_pk1 WHERE word_name = "AARON";
//                query = "SELECT word_name, word_id_pk1, part_segment_pk2, symbol_id_fk FROM `word-to-phoneme`.word_parts JOIN `word-to-phoneme`.word ON word_id = word_id_pk1 WHERE word_name = \""
//                        + incomingWordList.get(i) + "\";";
//                rs = Main.db.sendQuery(query);
//                while(rs.next()){
//                    symbolIdList.add(rs.getInt(4));
//                }
//                //Add symbol for a closed mouth
//                symbolIdList.add(999); //TODO Make this a reference.
//            } catch (SQLException ex) {
//                Logger.getLogger(WordParser.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        System.out.println(symbolIdList.toString());
//        return symbolIdList;
//    }
    public static SentenceData parseWords(SentenceData sentenceData, int blankSymbolId){
        List<Integer> symbolIdList = new ArrayList<Integer>();
        List<Integer> symbolIdList2 = new ArrayList<>();
        //For each word, send a query. Pull the resultSet into the word list. Add a closed mouth symbol_id between
        //each word.

        for (int i = 0; i < sentenceData.getParsedSentenceWordsList().size(); i++){
            try {
                ResultSet rs;
                String query;
                // SELECT word_name, word_id_pk1, part_segment_pk2, symbol_id_fk FROM `word-to-phoneme`.word_parts JOIN `word-to-phoneme`.word ON word_id = word_id_pk1 WHERE word_name = "AARON";
                query = "SELECT word_name, word_id_pk1, part_segment_pk2, symbol_id_fk FROM `word-to-phoneme`.word_parts JOIN `word-to-phoneme`.word ON word_id = word_id_pk1 WHERE word_name = \""
                        + sentenceData.getParsedSentenceWordsList().get(i).getWord_name() + "\";";
                rs = Main.db.sendQuery(query);
                sentenceData.getParsedSentenceWordsList().get(i).getPhonemes().add(blankSymbolId); //TODO Make this a reference.
                while(rs.next()){
                    symbolIdList2.add(rs.getInt(4));
                    sentenceData.getParsedSentenceWordsList().get(i).getPhonemes().add(rs.getInt(4));
                }
                //Add symbol for a closed mouth
                sentenceData.getParsedSentenceWordsList().get(i).getPhonemes().add(blankSymbolId); //TODO Make this a reference.
            } catch (SQLException ex) {
                Logger.getLogger(WordParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        /////**** End New Version



        return sentenceData;
    }
    
    
    
}
