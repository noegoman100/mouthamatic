/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mouthamatic;

import java.io.File;
import java.io.FileInputStream;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
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
    private Button generateButton;
    @FXML
    private Button exportImagesButton;
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
        SentenceParcer.parceSentence(sentenceData);
        WordParcer.parceWords(sentenceData);
        SymbolMapper.mapSymbolsTo(sentenceData, mGetMouthPairId());
        ImageMapper.mapImages(sentenceData);
        System.out.println("*********** End Data Transformation ***********");

        mAddImagesToScrollPane(sentenceData, imageScrollPane);
    }

    private void mAddImagesToScrollPane(SentenceData sentenceData, ScrollPane scrollPane) {
        //We have the array of images.
        //We have the scroll pane in place
        //We need to make an array of ImageViews
        //We need to make an array of valuable information
        //We need to add these ImageViews and TextViews to a VBox.
        //We need to add this VBox to the ScrollPane.
        HBox imageHBox = new HBox();
        //Image image = new Image()
        for (int i = 0; i < sentenceData.getParcedImages().size(); i++) {
            VBox contentsVBox = new VBox();
            TextField sequenceNumber = new TextField(Integer.toString(i));
            TextField infoText = new TextField(sentenceData.getParcedSymbols().get(i).toString());
            ImageView imageView = new ImageView(sentenceData.getParcedImages().get(i));
            contentsVBox.getChildren().addAll(sequenceNumber, infoText, imageView);
            //imageHBox.getChildren().addAll(imageView); //TODO change imageView to something containing the imageView
            imageHBox.getChildren().addAll(contentsVBox);
        }
        scrollPane.setContent(imageHBox);

    }

    private int mGetMouthPairId(){
        int index = mouthPairComboBox.getSelectionModel().getSelectedIndex() + 1; //The +1 is needed to start the list at 1, not 0.
        System.out.println("The selected index is: " + index);//TODO temp.
        return index;
    }

    @FXML
    private void mExportImages(ActionEvent event){
        int imagesPerSymbol =  Integer.parseInt(imagesPerSymbolTextField.getText());
        String outputDest = new String(outputDestTextField.getText()); //TODO Validate Input.
        int fileCounter = 1;
        try {
            for (int i = 0; i < sentenceData.getParcedImageSequence().size(); i++){
                for (int j = 0; j < imagesPerSymbol; j++) {
                    Path source = Paths.get("E:\\_Ed's Sweet Media\\WGU Classes\\WGU C868 - Capstone\\Project\\Resources\\Mouth_Image_Sets\\"
                            + sentenceData.getParcedImageSequence().get(i));
                    Path destination = Paths.get("E:\\_Ed's Sweet Media\\WGU Classes\\WGU C868 - Capstone\\TestSequence\\"
                            + fileCounter + "_"
                            + sentenceData.getParcedImageSequence().get(i));

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
