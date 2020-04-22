package mouthamatic;

import javafx.scene.image.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ImageMapper {
    public static SentenceData mapImages(SentenceData sentenceData){
        System.out.println("mapImages called. Image Sequence Size: " + sentenceData.getParsedImageSequence().size());
        List<Image> imageList = new ArrayList<>();
        for (int i = 0; i < sentenceData.getParsedImageSequence().size(); i++) {
            try {
                String filePath = "E:\\_Ed's Sweet Media\\WGU Classes\\WGU C868 - Capstone\\Project\\Resources\\Mouth_Image_Sets\\"
                        + sentenceData.getParsedImageSequence().get(i); //TODO make this relative! Or use a string variable or option or something.
                FileInputStream inputStream = new FileInputStream(filePath);
                Image image = new Image(inputStream);
                imageList.add(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        sentenceData.setParsedImages(imageList);
        return sentenceData;
    }
}
