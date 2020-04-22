/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mouthamatic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Ed
 */
public class FXMLHomeController implements Initializable {

    @FXML
    private TextField sentenceTextField;
    @FXML
    private TextField outputDestTextField;
    @FXML
    private TextField imagesPerSymbolTextField;
    @FXML
    private ScrollPane imageScrollPane;
    @FXML
    private ComboBox mouthPairComboBox;

    private SentenceData sentenceData;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO load the ComboBox
        List<String> choicesArray = new ArrayList<>();
        ResultSet rs = Main.db.sendQuery("SELECT mouth_pair_name FROM `word-to-phoneme`.mouth_pair_type ORDER BY mouth_pair_type_id ASC;");
        while (true){
            try {
                if (!rs.next()) break;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                choicesArray.add(rs.getString(1));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        mouthPairComboBox.setItems(FXCollections.observableList(choicesArray));
    }    
    
    @FXML
    private void generateButtonAction(ActionEvent event) {

        System.out.println("*********** Start Data Transformation ***********");
        sentenceData = new SentenceData(sentenceTextField.getText().toUpperCase());
        SentenceParser.parseSentence(sentenceData);
        WordParser.parseWords(sentenceData, 999);
        SymbolMapper.mapSymbolsTo(sentenceData, mGetMouthPairId());
        ImageMapper.mapImages(sentenceData);
        System.out.println("*********** End Data Transformation ***********");

        mAddImagesToScrollPane(sentenceData, imageScrollPane);
    }

private void mAddImagesToScrollPane(SentenceData sentenceData, ScrollPane scrollPane) {
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

    private int mGetMouthPairId(){
        int index = mouthPairComboBox.getSelectionModel().getSelectedIndex() + 1; //The +1 is needed to start the list at 1, not 0.
        return index;
    }

    @FXML
    private void mExportImages(ActionEvent event){
        int imagesPerSymbol =  Integer.parseInt(imagesPerSymbolTextField.getText());
        String outputDest = new String(outputDestTextField.getText()); //TODO Validate Input.
        int fileCounter = 1;
        try {
            for (int i = 0; i < sentenceData.getParsedImageSequence().size(); i++){
                for (int j = 0; j < imagesPerSymbol; j++) {
                    Path source = Paths.get("E:\\_Ed's Sweet Media\\WGU Classes\\WGU C868 - Capstone\\Project\\Resources\\Mouth_Image_Sets\\"
                            + sentenceData.getParsedImageSequence().get(i));
                    Path destination = Paths.get("E:\\_Ed's Sweet Media\\WGU Classes\\WGU C868 - Capstone\\TestSequence\\"
                            + fileCounter + "_"
                            + sentenceData.getParsedImageSequence().get(i));

                    Files.copy(source, destination);
                    fileCounter++;

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
