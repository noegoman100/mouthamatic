/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mouthamatic;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Ed
 */
public class FXMLHomeController implements Initializable {

    @FXML
    private Button generateButton;
    @FXML
    private TextField sentenceTextField;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void generateButtonAction(ActionEvent event) {
        //System.out.println(sentenceTextField.getText());
//        String sentence = sentenceTextField.getText().toUpperCase();
//        List<String> parcedSentence = SentenceParcer.parceSentence(sentence);
//        System.out.println(parcedSentence.toString());
//        List<Integer> parcedSymbols = WordParcer.parceWords(parcedSentence);
//        List<String> imageSequence = SymbolMapper.mapSymbolsTo(parcedSymbols, 3);

//        System.out.println("*********** Start ***********");
//        SentenceData sentenceData = new SentenceData(sentenceTextField.getText().toUpperCase());
//        sentenceData = SentenceParcer.parceSentence(sentenceData);
//        sentenceData = WordParcer.parceWords(sentenceData);
//        sentenceData = SymbolMapper.mapSymbolsTo(sentenceData, 3);
//        sentenceData = ImageMapper.mapImages(sentenceData);
//        System.out.println("*********** End ***********");

        System.out.println("*********** Start 2 ***********");
        SentenceData sentenceData = new SentenceData(sentenceTextField.getText().toUpperCase());
        SentenceParcer.parceSentence(sentenceData);
        WordParcer.parceWords(sentenceData);
        SymbolMapper.mapSymbolsTo(sentenceData, 3);
        ImageMapper.mapImages(sentenceData);
        System.out.println("*********** End 2 ***********");


    }
    
}
