package mouthamatic;

import javafx.scene.image.Image;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SentenceData {

    String rawSentence;
    List<Word> ParsedSentenceWordsList = new ArrayList<>();
    List<String> parsedImageSequence;
    List<Image> parsedImages;

    //Constructor
    public SentenceData(String rawSentence){
        setRawSentence(rawSentence);
    }

    public List<Image> getParsedImages() {
        return parsedImages;
    }

    public void setParsedImages(List<Image> parsedImages) {
        this.parsedImages = parsedImages;
    }

    public String getRawSentence() {
        return rawSentence;
    }

    public void setRawSentence(String rawSentence) {
        this.rawSentence = rawSentence;
    }

    public List<String> getParsedImageSequence() {
        return parsedImageSequence;
    }

    public void setParsedImageSequence(List<String> parsedImageSequence) {
        this.parsedImageSequence = parsedImageSequence;
    }

    public List<Word> getParsedSentenceWordsList() {
        return ParsedSentenceWordsList;
    }

    public void setParsedSentenceWordsList(List<Word> parsedSentenceWords) {
        this.ParsedSentenceWordsList = parsedSentenceWords;
    }
}

