package mouthamatic;

import javafx.scene.image.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ImageMapper {
    public static SentenceData mapImages(SentenceData sentenceData){
        System.out.println("mapImages called. Image Sequence Size: " + sentenceData.getParcedImageSequence().size());
        List<Image> imageList = new ArrayList<>();
        for (int i = 0; i < sentenceData.getParcedImageSequence().size(); i++) {
            //System.out.println("mapImages inside for loop.");
            try {
                //String filePath = ".//Images//" + sentenceData.getParcedImageSequence().get(i);
                String filePath = "E:\\_Ed's Sweet Media\\WGU Classes\\WGU C868 - Capstone\\Project\\Resources\\Mike-LipSet-Layers-TanBG\\"
                        + sentenceData.getParcedImageSequence().get(i); //TODO make this relative! Or use a string variable or option or something.
                FileInputStream inputStream = new FileInputStream(filePath);
                Image image = new Image(inputStream);
                imageList.add(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        sentenceData.setParcedImages(imageList);
        return sentenceData;
    }
}
