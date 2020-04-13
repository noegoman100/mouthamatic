package mouthamatic;

import javafx.scene.image.Image;

import java.nio.file.Path;
import java.util.List;

public class SentenceData {

    String rawSentence;
    List<String> parcedSentence;
    List<Integer> parcedSymbols;
    List<String> parcedImageSequence;
    List<Image> parcedImages;

    public List<Image> getParcedImages() {
        return parcedImages;
    }

    public void setParcedImages(List<Image> parcedImages) {
        this.parcedImages = parcedImages;
    }

    public SentenceData(String rawSentence){
        setRawSentence(rawSentence);
    }

    public String getRawSentence() {
        return rawSentence;
    }

    public void setRawSentence(String rawSentence) {
        this.rawSentence = rawSentence;
    }

    public List<String> getParcedSentence() {
        return parcedSentence;
    }

    public void setParcedSentence(List<String> parcedSentence) {
        this.parcedSentence = parcedSentence;
    }

    public List<Integer> getParcedSymbols() {
        return parcedSymbols;
    }

    public void setParcedSymbols(List<Integer> parcedSymbols) {
        this.parcedSymbols = parcedSymbols;
    }

    public List<String> getParcedImageSequence() {
        return parcedImageSequence;
    }

    public void setParcedImageSequence(List<String> parcedImageSequence) {
        this.parcedImageSequence = parcedImageSequence;
    }
}
