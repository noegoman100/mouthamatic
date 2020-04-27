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

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

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
    @FXML
    private ListView queryListView;
    @FXML
    private TableView reportsTableView;
    private ObservableList<ObservableList> data;
    private SentenceData sentenceData;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mInitializeGenerateView();
        mInitializeReportsView();
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

    private void mInitializeGenerateView(){
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

    private void mInitializeReportsView() {
        ResultSet rs = null;
        try {
            rs = Main.db.sendQuery("SELECT report_query_name FROM `word-to-phoneme`.report_query ORDER BY report_query_name ASC;");
        } catch (Exception e) {
            e.printStackTrace();
        }


        while (true) {
            try {
                if (!rs.next()) break;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                queryListView.getItems().add(rs.getString(1));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        queryListView.setOnMouseClicked(new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent event) {
                System.out.println("Item clicked at location: " + queryListView.getSelectionModel().getSelectedIndex());//TODO temp
                mPopulateReportsTable(queryListView.getSelectionModel().getSelectedItem().toString());
            }

        });
    }

    private void mPopulateReportsTable(String selectionName){
        //TODO implement me.
        //?? Add columns to table
        //?? Add data to columns.
        data = FXCollections.observableArrayList();
        System.out.println(selectionName); //TODO Temp
        ResultSet rsSelection = Main.db.sendQuery("SELECT report_query_string FROM `word-to-phoneme`.report_query "
                + "WHERE report_query_name = '" + selectionName + "' "
                + "ORDER BY report_query_name DESC LIMIT 1; ");
        String selectedQuery = new String("Error Loading Query");
        try {
            rsSelection.next();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            selectedQuery = rsSelection.getString(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println(selectedQuery);//TODO Temp
        //Now!! We can send out the desired Query, then load the results into a dynamic table.
        mSendQueryToTable(selectedQuery);

    }

    private void mSendQueryToTable(String query){ //this method From: https://blog.ngopal.com.np/2011/10/19/dyanmic-tableview-data-from-database/
        try {
            ResultSet rs = Main.db.sendQuery(query);
            reportsTableView.getColumns().clear();
            /**
             * ********************************
             * TABLE COLUMN ADDED DYNAMICALLY *
             *********************************
             */
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });

                reportsTableView.getColumns().addAll(col);
                System.out.println("Column [" + i + "] ");
            }

            /**
             * ******************************
             * Data added to ObservableList *
             *******************************
             */
            while (rs.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                System.out.println("Row [1] added " + row);
                data.add(row);

            }

            //FINALLY ADDED TO TableView
            reportsTableView.setItems(data);

        } catch (Exception e) {
            e.printStackTrace();
        }


//        TableColumn col = new TableColumn("test column");
//
//        reportsTableView.getColumns().add(col);
    }
}
