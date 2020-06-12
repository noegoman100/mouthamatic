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
                String filePath = "resources\\mouth_image_sets\\"
                        + sentenceData.getParsedImageSequence().get(i);
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

