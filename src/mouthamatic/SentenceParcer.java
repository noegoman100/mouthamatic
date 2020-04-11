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
public class SentenceParcer {
    private String sentence;
    //private static ParcedSentence parcedSentence = new ParcedSentence();
    
    public static ArrayList<String> parceSentence(String sentence){
        //TODO
        ArrayList<String> wordList = new ArrayList<String>();
        String[] words = sentence.split(" ");
        wordList.addAll(Arrays.asList(words));
        
        return wordList;
    }
}
