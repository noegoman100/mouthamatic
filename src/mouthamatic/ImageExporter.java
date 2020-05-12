package mouthamatic;


import javafx.scene.control.TextField;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageExporter {
    public void run(TextField imagesPerSymbolTextField, TextField outputDestTextField, SentenceData sentenceData){
        if (sentenceData == null) return; //sentenceData needs to be Generated first. //TODO popup alert - Generate First
        int imagesPerSymbol =  Integer.parseInt(imagesPerSymbolTextField.getText());
        String outputDest = new String(outputDestTextField.getText()); //TODO Validate Input.
        int fileCounter = 1;
        try {
            for (int i = 0; i < sentenceData.getParsedImageSequence().size(); i++){
                for (int j = 0; j < imagesPerSymbol; j++) {
                    //"E:\\_Ed's Sweet Media\\WGU Classes\\WGU C868 - Capstone\\Project\\Resources\\Mouth_Image_Sets\\"
                    Path source = Paths.get("resources\\mouth_image_sets\\"
                            + sentenceData.getParsedImageSequence().get(i));
//                    Path destination = Paths.get("E:\\_Ed's Sweet Media\\WGU Classes\\WGU C868 - Capstone\\TestSequence\\"
//                            + fileCounter + "_"
//                            + sentenceData.getParsedImageSequence().get(i));
                    Path destination = Paths.get(outputDestTextField.getText()
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
