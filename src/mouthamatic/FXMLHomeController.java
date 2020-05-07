/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mouthamatic;

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
import java.util.Optional;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
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

    @FXML private TextField sentenceTextField;                  //GENERATE TAB
    @FXML private ScrollPane imageScrollPane;                   //GENERATE TAB
    @FXML private ComboBox mouthPairComboBox;                   //GENERATE TAB
    private SentenceData sentenceData;                          //GENERATE TAB
    @FXML private ListView queryListView;                       //REPORTS TAB
    @FXML private TableView reportsTableView;                   //REPORTS TAB
    @FXML private TextField outputDestTextField;                //EXPORT TAB
    @FXML private TextField imagesPerSymbolTextField;           //EXPORT TAB
    @FXML private TableView dataTableView;                      //DATA TAB
    @FXML private TextField dataSearchWordTextField;            //DATA TAB
    private ObservableList<ObservableList> data;                //DATA TAB
    private ObservableList<ObservableList> wordData;            //DATA TAB
    @FXML private ComboBox dataMouthSetChoiceComboBox;          //DATA TAB
    @FXML private ScrollPane referenceScrollPane;               //DATA TAB
    @FXML private TableView referenceChartTableView;            //DATA TAB
    private ObservableList<ObservableList> referenceListData;   //DATA TAB


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mInitializeGenerateTab();
        mInitializeReportsView();
        mInitializeDataTab();
        mLoadSymbolReferenceChart();
    }    

    //GENERATE TAB
    @FXML
    private void generateButtonAction() {

        System.out.println("*********** Start Data Transformation ***********");
        sentenceData = new SentenceData(sentenceTextField.getText().toUpperCase());
        SentenceParser.parseSentence(sentenceData);
        WordParser.parseWords(sentenceData, 999);
        SymbolMapper.mapSymbolsTo(sentenceData, mGetMouthPairId());
        ImageMapper.mapImages(sentenceData);
        System.out.println("*********** End Data Transformation ***********");

        mAddImagesToScrollPane(sentenceData, imageScrollPane);
    }

    //GENERATE TAB
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

    //GENERATE TAB
    private int mGetMouthPairId(){
        int index = mouthPairComboBox.getSelectionModel().getSelectedIndex() + 1; //The +1 is needed to start the list at 1, not 0.
        return index;
    }

    //GENERATE TAB
    private void mInitializeGenerateTab(){
        List<String> choicesArray = new ArrayList<>();
        ResultSet rs = Main.db.sendQuery("SELECT mouth_pair_name FROM `word-to-phoneme`.mouth_pair_type ORDER BY mouth_pair_type_id ASC;");
        while (true){
            try {
                if (!rs.next()) break;
                choicesArray.add(rs.getString(1));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
        mouthPairComboBox.setItems(FXCollections.observableList(choicesArray));
    }

    //EXPORT TAB
    @FXML
    private void mExportImages(){
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

    //REPORTS TAB
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
                mPopulateReportsTable(queryListView.getSelectionModel().getSelectedItem().toString());
            }

        });
    }

    //REPORTS TAB
    private void mPopulateReportsTable(String selectionName){

        data = FXCollections.observableArrayList();
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
        //Now!! We can send out the desired Query, then load the results into a dynamic table.
        mSendQueryToTable(selectedQuery);

    }

    //REPORTS TAB Essentially an extension of the mPopulateReportsTable method.
    private void mSendQueryToTable(String query){ //this method From: https://blog.ngopal.com.np/2011/10/19/dyanmic-tableview-data-from-database/
        try {
            ResultSet rs = Main.db.sendQuery(query);
            reportsTableView.getColumns().clear();
            reportsTableView.setEditable(true);
            final EventHandler<TableColumn.CellEditEvent<ObservableList, String>> dataEditCommitHandler = new EventHandler<TableColumn.CellEditEvent<ObservableList, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<ObservableList, String> event) {
                    System.out.println("Send query to DB with data currently in the cell");//TODO temp.

                }
            };
            /**
             * ********************************
             * TABLE COLUMN ADDED DYNAMICALLY *
             *********************************
             */
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                //col.setCellFactory(TextFieldTableCell.forTableColumn()); //This makes the cells editable.
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                col.setOnEditCommit(dataEditCommitHandler);
                col.setEditable(true);
                reportsTableView.getColumns().addAll(col);
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
                data.add(row);

            }

            //FINALLY ADDED TO TableView
            reportsTableView.setItems(data);
            reportsTableView.setEditable(true);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //DATA TAB
    @FXML
    private void mWordSearchButton(){
        String word = new String(dataSearchWordTextField.getText());
        wordData = FXCollections.observableArrayList();
        int maxParts = 0;
        try {
            /**** Find the Max number of parts for the word being searched **/
                String queryMaxParts = new String("SELECT max(part_segment_pk2) FROM `word-to-phoneme`.word_parts " +
                        "INNER JOIN `word-to-phoneme`.word ON (word_id = word_id_pk1) " +
                        "WHERE word_name = '" + word + "';");
                ResultSet maxPartsRS = Main.db.sendQuery(queryMaxParts);
                maxPartsRS.next(); //TODO if the word is not there (or too long) an error occurs
                maxParts = maxPartsRS.getInt(1);
                if (maxParts == 0) return;
            /**** End Find Max Parts **/
            /**** Build Query based on maxParts count **/
                String queryAllParts = new String("SELECT word_name AS Word, ");
                for (int i = 0; i < maxParts; i++){
                    queryAllParts = queryAllParts + "set" + (i+1) + ".symbol_id_fk AS Symbol" + (i+1);
                    if (i < maxParts -1){
                        queryAllParts = queryAllParts + ", ";
                    }
                }
                queryAllParts = queryAllParts + " " + "FROM `word-to-phoneme`.word ";
                for (int i = 0; i < maxParts; i++){
                    queryAllParts = queryAllParts + "INNER JOIN `word-to-phoneme`.word_parts AS set" + (i+1) + " ON (word_id = set" + (i+1) + ".word_id_pk1 AND "
                    + "set" + (i+1) + ".part_segment_pk2=" + (i+1) + ") ";
                }
                queryAllParts = queryAllParts + "WHERE word_name='" + word + "'; ";

            /**** End Build Query based on max parts **/
            ResultSet rs = Main.db.sendQuery(queryAllParts);
            dataTableView.getColumns().clear();
            dataTableView.setEditable(true);
            final EventHandler<TableColumn.CellEditEvent<ObservableList, String>> dataEditCommitHandler = new EventHandler<TableColumn.CellEditEvent<ObservableList, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<ObservableList, String> editEvent) {

                    /** Get word_id value from word name in tableView **/
                        int word_id = 0;
                        ResultSet rs = Main.db.sendQuery("SELECT word_id FROM `word-to-phoneme`.word WHERE word_name = '" + editEvent.getRowValue().get(0) + "';");
                        while (true){
                            try {
                                if (!rs.next()) break;
                                word_id = rs.getInt(1);
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                        }
                    /** End Get word_id value from word name in tableView **/
                    /** This section Edits the Word in the first column, if the word matches the search value **/
                    if(editEvent.getOldValue().toUpperCase().equals(dataSearchWordTextField.getText().toUpperCase())) {
                        System.out.println("I get here");
                        String updateQuery = new String("UPDATE " +
                                    "`word-to-phoneme`.word " +
                                    "SET word_name = '" + editEvent.getNewValue() + "' " +
                                    "WHERE word_id = " + word_id + ";"
                                );
                        System.out.println(updateQuery);
                        Main.db.sendUpdate(updateQuery);
                        mWordSearchButton();//Refresh the table.
                    }
                    /** END This section Edits the Word in the first column, if the word matches the search value **/
                    /** With word_id, and part_segment_pk2 (column index), a specific item can be updated**/
                    else if (word_id != 0) {
                        String updateQuery = new String("UPDATE " +
                                "`word-to-phoneme`.word_parts " +
                                "SET symbol_id_fk = " + editEvent.getNewValue() +
                                " WHERE word_id_pk1 = " + word_id +
                                " AND part_segment_pk2 = " + editEvent.getTablePosition().getColumn() + ";");
                        Main.db.sendUpdate(updateQuery);
                        mWordSearchButton();//Refresh the table.
                    }
                    /** End With word_id, and part_segment_pk2 (column index), a specific items can be updated**/
                }
            };
            /**
             * ********************************
             * TABLE COLUMNS ADDED DYNAMICALLY *
             *********************************
             */
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnLabel(i + 1));
                col.setCellFactory(TextFieldTableCell.forTableColumn()); //This makes the cells editable.
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                col.setOnEditCommit(dataEditCommitHandler);
                col.setEditable(true);
                dataTableView.getColumns().addAll(col);
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
                wordData.add(row);

            }

            //FINALLY ADDED TO TableView
            dataTableView.setItems(wordData);
            dataTableView.setEditable(true);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //DATA TAB
    @FXML
    private void mInitializeDataTab(){
        //Populate MouthPairType ComboBox
        List<String> choicesArray = new ArrayList<>();
        ResultSet rs = Main.db.sendQuery("SELECT mouth_pair_name FROM `word-to-phoneme`.mouth_pair_type ORDER BY mouth_pair_type_id ASC;");
        while (true){
            try {
                if (!rs.next()) break;
                choicesArray.add(rs.getString(1));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
        dataMouthSetChoiceComboBox.setItems(FXCollections.observableList(choicesArray));
        //end Populate MouthPairType ComboBox
    }

    //DATA TAB
    @FXML
    private void mLoadReferenceImages(){
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
                TextField symbolIdTextField = new TextField(Integer.toString(symbolId));
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

    //DATA TAB
    @FXML
    private void mLoadSymbolReferenceChart(){
        String query = new String("SELECT symbol AS 'Symbol', symbol_id_fk AS 'Symbol ID', word_name AS 'Example Word' FROM `word-to-phoneme`.word INNER JOIN `word-to-phoneme`.word_parts ON (word_id = word_id_pk1) INNER JOIN `word-to-phoneme`.symbols ON (symbols_id = symbol_id_fk) GROUP BY symbol ORDER BY symbol, part_segment_pk2;");
        referenceListData = FXCollections.observableArrayList();
        ResultSet rsChart = Main.db.sendQuery(query);
        referenceChartTableView.getColumns().clear();

        /**
         * ********************************
         * TABLE COLUMNS ADDED DYNAMICALLY *
         *********************************
         */
        try {
            for (int i = 0; i < rsChart.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rsChart.getMetaData().getColumnLabel(i + 1));
                col.setCellFactory(TextFieldTableCell.forTableColumn()); //This makes the cells editable.
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                referenceChartTableView.getColumns().addAll(col);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        /**
         * ******************************
         * Data added to ObservableList *
         *******************************
         */
        try {
            while (rsChart.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rsChart.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(rsChart.getString(i));
                }
                referenceListData.add(row);

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //FINALLY ADDED TO TableView
        referenceChartTableView.setItems(referenceListData);


    }

    //DATA TAB
    @FXML
    private void mNewWordButton(){
        int phonemeCount = 0;

        TextInputDialog dialog = new TextInputDialog("5");
        dialog.setTitle("New Word");
        dialog.setHeaderText("How many phoneme segments in this new word?");
        dialog.setContentText("Word Phoneme Segments: ");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            phonemeCount = Integer.parseInt(result.get());
        }
        //TODO get next Available word_id
        //TODO Now, send an Update with a Blank word, and the correct number of parts
        //TODO Build a query
        //TODO then send this new word to the search function

        dataSearchWordTextField.setText("testword");
        mWordSearchButton();
    }
}
