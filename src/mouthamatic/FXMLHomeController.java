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
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
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
    private ScrollPaneAddImages scrollPaneAddImages
            = new ScrollPaneAddImages();                        //GENERATE TAB
    private GetMouthPairId getMouthPairId
            = new GetMouthPairId();                             //GENERATE TAB
    @FXML private ListView queryListView;                       //REPORTS TAB
    @FXML private TableView reportsTableView;                   //REPORTS TAB
    @FXML private TextField outputDestTextField;                //EXPORT TAB
    @FXML private TextField imagesPerSymbolTextField;           //EXPORT TAB
    @FXML private TableView dataTableView;                      //DATA TAB
    @FXML private TextField dataSearchWordTextField;            //DATA TAB
    private ObservableList<ObservableList> data;                //DATA TAB
    //private ObservableList<ObservableList> wordData;            //DATA TAB
    @FXML private ComboBox dataMouthSetChoiceComboBox;          //DATA TAB
    @FXML private ScrollPane referenceScrollPane;               //DATA TAB
    @FXML private TableView referenceChartTableView;            //DATA TAB
    //private ObservableList<ObservableList> referenceListData;   //DATA TAB
    private WordSearch wordSearch
            = new WordSearch();                                 //DATA TAB
    private LoadReferenceImages loadReferenceImages
            = new LoadReferenceImages();                        //DATA TAB
    private LoadSymbolChart loadSymbolChart
            = new LoadSymbolChart();                            //DATA TAB
    @FXML private TableView multiSearchTableView;               //SEARCH TAB
    @FXML private TextField multiSearchTextField;               //SEARCH TAB


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mInitializeGenerateTab();
        mInitializeReportsView();
        mInitializeDataTab();

    }    

    //GENERATE TAB
    @FXML
    private void generateButtonAction() {

        System.out.println("*********** Start Data Transformation ***********");
        sentenceData = new SentenceData(sentenceTextField.getText().toUpperCase());
        SentenceParser.parseSentence(sentenceData);
        WordParser.parseWords(sentenceData, 999);
        SymbolMapper.mapSymbolsTo(sentenceData, getMouthPairId.run(mouthPairComboBox));
        ImageMapper.mapImages(sentenceData);
        System.out.println("*********** End Data Transformation ***********");

        scrollPaneAddImages.run(sentenceData, imageScrollPane);
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
        mouthPairComboBox.getSelectionModel().select(1); //Set initial choice.
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
        wordSearch.run(dataSearchWordTextField, dataTableView);
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
        mLoadSymbolChart();
    }

    //DATA TAB
    @FXML
    private void mLoadReferenceImages(){
        loadReferenceImages.run(dataMouthSetChoiceComboBox, referenceScrollPane);

    }

    //DATA TAB
    @FXML
    private void mLoadSymbolChart(){
        loadSymbolChart.run(referenceChartTableView);

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
        //Check to see if a number was entered into the Phoneme Count Text Field
        if (result.isPresent()){
            phonemeCount = Integer.parseInt(result.get());
            //TODO get next Available word_id
            ResultSet nextWordIdRS;
            String nextWordIdQuery = new String("SELECT MAX(word_id) FROM `word-to-phoneme`.word;");
            nextWordIdRS = Main.db.sendQuery(nextWordIdQuery);
            int nextAvailableId = 0;
            try {
                nextWordIdRS.next();//Get past blank row
                nextAvailableId = nextWordIdRS.getInt(1);
                nextAvailableId = nextAvailableId + 1;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            //TODO Now, send an Update with a Blank word
            String insertWordQuery = new String("INSERT INTO `word-to-phoneme`.`word` (`word_id`, `word_name`) VALUES ('" + nextAvailableId + "', 'NEWWORD');");
            System.out.println("updateQuery: " + insertWordQuery);
            Main.db.sendUpdate(insertWordQuery);

            //TODO Build an insert Query with the correct number of parts
            for(int i = 0; i < phonemeCount; i++) {
                String insertPartsQuery = new String("INSERT INTO `word-to-phoneme`.`word_parts` (`word_id_pk1`, `part_segment_pk2`, `symbol_id_fk`) VALUES ('" +
                        nextAvailableId + "', '" + (i+1) +
                        "', '999');");
                System.out.println("updateQuery: " + insertPartsQuery);
                Main.db.sendUpdate(insertPartsQuery);
            }

            //TODO then send this new word to the search function

            dataSearchWordTextField.setText("NEWWORD");
            mWordSearchButton();
        }



    }

    //DATA TAB
    @FXML
    private void mDeleteWordButton(){

        String deleteQuery = new String("DELETE FROM `word-to-phoneme`.word WHERE word_name = '" + dataSearchWordTextField.getText() + "';");
        System.out.println(deleteQuery);
        Main.db.sendUpdate(deleteQuery);
    }

    //SEARCH TAB
    @FXML
    private void mMultiSearchButton(){ //TODO convert me to Search Tab
        String word = new String(multiSearchTextField.getText());
        ObservableList<ObservableList> wordData = FXCollections.observableArrayList();
        int maxParts = 0;
        try {
            /**** Find the Max number of parts for the word being searched **/
            String queryMaxParts = new String("SELECT max(part_segment_pk2) FROM `word-to-phoneme`.word_parts " +
                    "INNER JOIN `word-to-phoneme`.word ON (word_id = word_id_pk1) " +
                    "WHERE word_name LIKE '" + word + "%';");
            System.out.println("queryMaxParts: " + queryMaxParts);
            ResultSet maxPartsRS = Main.db.sendQuery(queryMaxParts);
            maxPartsRS.next(); //TODO if the word is not there (or too long) an error occurs
            maxParts = maxPartsRS.getInt(1);
            //If maxParts == 0 (No word found), then clear the table and the search field
            if (maxParts == 0) {
                multiSearchTextField.setText("");
                multiSearchTableView.getColumns().clear();
                return;
            }
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
                queryAllParts = queryAllParts + "LEFT OUTER JOIN `word-to-phoneme`.word_parts AS set" + (i+1) + " ON (word_id = set" + (i+1) + ".word_id_pk1 AND "
                        + "set" + (i+1) + ".part_segment_pk2=" + (i+1) + ") ";
            }
            queryAllParts = queryAllParts + "WHERE word_name LIKE '" + word + "%'; ";
            System.out.println("queryAllParts: " + queryAllParts);
            /**** End Build Query based on max parts **/
            ResultSet rs = Main.db.sendQuery(queryAllParts);
            multiSearchTableView.getColumns().clear();

            /**
             * ********************************
             * TABLE COLUMNS ADDED DYNAMICALLY *
             *********************************
             */
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnLabel(i + 1));
                //col.setCellFactory(TextFieldTableCell.forTableColumn()); //This makes the cells editable.
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        //Add a check for Null Values
                        String valueString = new String();
                        if (param.getValue().get(j) == null) {
                            //System.out.println("value is Empty");
                            valueString = new String("NULL");
                        } else {
                            //System.out.println("Value is not supposed to be Empty");
                            valueString = param.getValue().get(j).toString();
                        }
                        return new SimpleStringProperty(valueString);
                    }
                });

                multiSearchTableView.getColumns().addAll(col);
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
            multiSearchTableView.setItems(wordData);
            //multiSearchTableView.setEditable(true);

        } catch (Exception e) {
            e.printStackTrace();
        }

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

    @FXML
    private File mExportFolderChooser(ActionEvent event){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);
        outputDestTextField.setText(selectedDirectory.toString());

        return new File("E:\\_Ed's Sweet Media\\WGU Classes\\WGU C868 - Capstone\\TestSequence\\");
    }
}
