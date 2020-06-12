package mouthamatic;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ScrollPaneAddImages {
    public void run(SentenceData sentenceData, ScrollPane scrollPane){
        HBox imageHBox = new HBox();
        //Image image = new Image()
        int counter = 0;
        for (int i = 0; i < sentenceData.getParsedSentenceWordsList().size(); i++) { //Iterate into each word

            for (int j = 0; j < sentenceData.getParsedSentenceWordsList().get(i).getPhonemes().size(); j++) { //iterate through each symbol
                VBox contentsVBox = new VBox();
                TextField wordNameText = new TextField(sentenceData.getParsedSentenceWordsList().get(i).getWord_name());
                TextField sequenceNumber = new TextField(Integer.toString(counter + 1));
                TextField symbolId = new TextField(sentenceData.getParsedSentenceWordsList().get(i).getPhonemes().get(j).toString());
                ImageView imageView = new ImageView(sentenceData.getParsedImages().get(counter));
                contentsVBox.getChildren().addAll(sequenceNumber, wordNameText, symbolId, imageView);
                imageHBox.getChildren().addAll(contentsVBox);

                counter++;
            }

        }
        scrollPane.setContent(imageHBox);
    }

}

