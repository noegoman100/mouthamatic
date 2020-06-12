package mouthamatic;


import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageExporter {
    public void run(TextField imagesPerSymbolTextField, TextField outputDestTextField, SentenceData sentenceData){
        if (sentenceData == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Sequence Found");
            alert.setHeaderText("Generate a Sequence First");
            alert.setContentText("Please go to the Generate Tab, and Generate a sequence for some dialog first.");

            alert.showAndWait();
            return;
        }
        int imagesPerSymbol =  Integer.parseInt(imagesPerSymbolTextField.getText());
        //String outputDest = new String(outputDestTextField.getText());
        int fileCounter = 1;
        try {
            for (int i = 0; i < sentenceData.getParsedImageSequence().size(); i++){
                for (int j = 0; j < imagesPerSymbol; j++) {
                    //"E:\\_Ed's Sweet Media\\WGU Classes\\WGU C868 - Capstone\\Project\\Resources\\Mouth_Image_Sets\\"
                    Path source = Paths.get("resources\\mouth_image_sets\\"
                            + sentenceData.getParsedImageSequence().get(i));
                    Path destination = Paths.get(outputDestTextField.getText()
                            + fileCounter + "_"
                            + sentenceData.getParsedImageSequence().get(i));
                    Files.copy(source, destination);
                    fileCounter++;
                }
            }
            //Send a success dialog
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("The file sequence has been copied to the destination folder.");
            alert.showAndWait();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("FileNotFound");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO Exception");
            //Send a success dialog
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Uh oh. ");
            alert.setHeaderText(null);
            alert.setContentText("Files may already be present in destination folder.");
            alert.showAndWait();
        }
    }
}

