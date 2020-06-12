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
    

    public static SentenceData parseWords(SentenceData sentenceData, int blankSymbolId){
        List<Integer> symbolIdList = new ArrayList<>();
        //For each word, send a query. Pull the resultSet into the word list.
        //Adds a closed mouth symbol_id between each word!!!

        for (int i = 0; i < sentenceData.getParsedSentenceWordsList().size(); i++){
            try {
                ResultSet rs;
                String query;
                // SELECT word_name, word_id_pk1, part_segment_pk2, symbol_id_fk FROM `word-to-phoneme`.word_parts JOIN `word-to-phoneme`.word ON word_id = word_id_pk1 WHERE word_name = "AARON";
                query = "SELECT word_name, word_id_pk1, part_segment_pk2, symbol_id_fk FROM `word-to-phoneme`.word_parts JOIN `word-to-phoneme`.word ON word_id = word_id_pk1 WHERE word_name = \""
                        + sentenceData.getParsedSentenceWordsList().get(i).getWord_name() + "\";";
                rs = Main.db.sendQuery(query);
                sentenceData.getParsedSentenceWordsList().get(i).getPhonemes().add(blankSymbolId); //Adding the Blank Symbol to start the word.
                while(rs.next()){
                    symbolIdList.add(rs.getInt(4));
                    sentenceData.getParsedSentenceWordsList().get(i).getPhonemes().add(rs.getInt(4));
                }
            } catch (SQLException ex) {
                Logger.getLogger(WordParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        int lastWord = sentenceData.getParsedSentenceWordsList().size() - 1;
        sentenceData.getParsedSentenceWordsList().get(lastWord).getPhonemes().add(blankSymbolId); //Add a blank to the end of the last word.
        /////**** End New Version



        return sentenceData;
    }

}
