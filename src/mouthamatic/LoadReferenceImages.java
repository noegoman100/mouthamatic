package mouthamatic;


import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoadReferenceImages {
    public void run(ComboBox dataMouthSetChoiceComboBox, ScrollPane referenceScrollPane){
        int selectedItemIndex = dataMouthSetChoiceComboBox.getSelectionModel().getSelectedIndex();
        String selectedName = new String(dataMouthSetChoiceComboBox.getItems().get(selectedItemIndex).toString());
        String mouthPairTypeIdQuery = new String("SELECT mouth_pair_type_id FROM `word-to-phoneme`.mouth_pair_type WHERE mouth_pair_name = '" + selectedName + "' LIMIT 1;");
        ResultSet rs = Main.db.sendQuery(mouthPairTypeIdQuery);
        int mouthPairTypeId = 0;
        try {
            rs.next(); //Get past empty row
            mouthPairTypeId = rs.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println("mouthPairTypeId: " + mouthPairTypeId);
        String referenceQuery = new String("SELECT symbol, symbol_id_pk2, image_url FROM `word-to-phoneme`.symbols INNER JOIN `word-to-phoneme`.image_map ON (symbols_id = symbol_id_pk2) WHERE mouth_pair_id_pk1 = " + mouthPairTypeId + " ORDER BY symbol;");
        ResultSet referenceImagesRs = Main.db.sendQuery(referenceQuery);
        String symbolName =  new String();
        int symbolId = 0;
        String imageFileName = new String();
        VBox vBox = new VBox();
        /** Load up an HBox, then place it inside a VBox. Add the VBox to the ScrollPane **/
        while(true){
            try {
                if (!referenceImagesRs.next()) break;
                HBox tempHbox = new HBox();
                symbolName = referenceImagesRs.getString(1);
                symbolId = referenceImagesRs.getInt(2);
                imageFileName = referenceImagesRs.getString(3);
                TextField symbolNameTextField = new TextField(symbolName);
                symbolNameTextField.setEditable(false);
                //symbolNameTextField.setDisable(true);
                TextField symbolIdTextField = new TextField(Integer.toString(symbolId));
                symbolIdTextField.setEditable(false);
                //symbolIdTextField.setDisable(true);
                String filePath = "resources\\mouth_image_sets\\" + imageFileName;
                FileInputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(filePath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Image image = new Image(inputStream);
                ImageView imageView = new ImageView(image);
                tempHbox.getChildren().addAll(symbolNameTextField, symbolIdTextField, imageView);
                vBox.getChildren().add(tempHbox);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
        referenceScrollPane.setContent(vBox);
    }
}
