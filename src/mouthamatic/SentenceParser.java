/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mouthamatic;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Ed
 */
public class SentenceParser {

    public static void parseSentence(SentenceData sentenceData){
        ArrayList<String> wordList = new ArrayList<String>();
        String[] words = sentenceData.getRawSentence().split("[ .,!?;:@#&]+");
        wordList.addAll(Arrays.asList(words));
        //sentenceData.setParsedSentence(wordList);
        //Below is the new Word object, replacing String object
        for (String currentWord: wordList) {
            sentenceData.getParsedSentenceWordsList().add(new Word(currentWord.toUpperCase()));
        }

        //return sentenceData;
    }
}

