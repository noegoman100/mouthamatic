package mouthamatic;

import java.util.ArrayList;
import java.util.List;

public class Word {
    private String word_name;
    private List<Integer> orderedPhonemes = new ArrayList<>();

    //Constructor
    public Word(String word){
        setWord_name(word);
    }

    public String getWord_name() {
        return word_name;
    }

    public void setWord_name(String word_name) {
        this.word_name = word_name;
    }

    public List<Integer> getPhonemes() {
        return orderedPhonemes;
    }

    public void setPhonemes(List<Integer> phonemes) {
        this.orderedPhonemes = phonemes;
    }
}

