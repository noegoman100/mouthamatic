/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mouthamatic;

import java.util.ArrayList;

/**
 *
 * @author Ed
 */
public class ParcedSentence {
    private ArrayList<Word> wordList = new ArrayList<Word>();
    
    public void addWord(Word word){
        wordList.add(word);
    }
    public ArrayList<Word> getWordList(){
        return wordList;
    }
}
