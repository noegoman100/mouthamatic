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
import javafx.scene.control.cell.TextFieldTableCell;
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

    @FXML private TextField sentenceTextField;          //GENERATE TAB
    @FXML private ScrollPane imageScrollPane;           //GENERATE TAB
    @FXML private ComboBox mouthPairComboBox;           //GENERATE TAB
    private SentenceData sentenceData;                  //GENERATE TAB
    @FXML private ListView queryListView;               //REPORTS TAB
    @FXML private TableView reportsTableView;           //REPORTS TAB
    @FXML private TextField outputDestTextField;        //EXPORT TAB
    @FXML private TextField imagesPerSymbolTextField;   //EXPORT TAB
    @FXML private TableView dataTableView;              //DATA TAB
    @FXML private TextField dataSearchWordTextField;    //DATA TAB
    private ObservableList<ObservableList> data;        //DATA TAB
    private ObservableList<ObservableList> wordData;    //DATA TAB
    @FXML private ComboBox dataMouthSetChoiceComboBox;  //DATA TAB

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mInitializeGenerateView();
        mInitializeReportsView();
        mInitializeDataView();
    }    

    //GENERATE TAB
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
    private void mInitializeGenerateView(){
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
                System.out.println("Item clicked at location: " + queryListView.getSelectionModel().getSelectedIndex());//TODO temp
                mPopulateReportsTable(queryListView.getSelectionModel().getSelectedItem().toString());
            }

        });
    }

    //REPORTS TAB
    private void mPopulateReportsTable(String selectionName){

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
                System.out.println("Column [" + i + "] "); //TODO temp
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
                System.out.println("Row added " + row); //TODO temp
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
    private void mPopulateDataTable(ActionEvent event){
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
                System.out.println("Max Parts: " + maxParts); //TODO temp
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

                System.out.println("queryAllParts: " + queryAllParts); //TODO temp
            /**** End Build Query based on max parts **/
            ResultSet rs = Main.db.sendQuery(queryAllParts);
            dataTableView.getColumns().clear();
            dataTableView.setEditable(true);
            final EventHandler<TableColumn.CellEditEvent<ObservableList, String>> dataEditCommitHandler = new EventHandler<TableColumn.CellEditEvent<ObservableList, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<ObservableList, String> editEvent) {
                    System.out.println("Send query to DB with data currently in the cell");//TODO temp.
                    System.out.println("event.getNewValue(): " + editEvent.getNewValue()); //TODO temp.
                    System.out.println("event.getTablePosition().getColumn(): " + editEvent.getTablePosition().getColumn()); //TODO temp.
                    System.out.println("event.getTableColumn().getText(): " + editEvent.getTableColumn().getText()); //TODO temp.
                    /** Send out update query **/
                    //TODO get word_id from word table using
                    /** Get word_id value from word name in tableView **/
                        System.out.println("event.getRowValue().get(0): " + editEvent.getRowValue().get(0)); //TODO temp
                        int word_id = 0;
                        ResultSet rs = Main.db.sendQuery("SELECT word_id FROM `word-to-phoneme`.word WHERE word_name = '" + editEvent.getRowValue().get(0) + "';");
                        while (true){
                            try {
                                if (!rs.next()) break;
                                word_id = rs.getInt(1);
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                            System.out.println("word_id: " + word_id); //TODO temp
                        }
                    /** End Get word_id value from word name in tableView **/
                    /** With word_id, and part_segment_pk2 (column index), a specific item can be updated**/
                        String updateQuery = new String("UPDATE " +
                                "`word-to-phoneme`.word_parts " +
                                "SET symbol_id_fk = " + editEvent.getNewValue() +
                                " WHERE word_id_pk1 = " + word_id +
                                " AND part_segment_pk2 = " + editEvent.getTablePosition().getColumn() + ";");
                        Main.db.sendUpdate(updateQuery);
                        //TODO refresh data in the table.
                        mPopulateDataTable(event);//Refresh the table.
                    /** End With word_id, and part_segment_pk2 (column index), a specific items can be updated**/
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
                System.out.println("Column [" + i + "] "); //TODO temp
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
                System.out.println("Row added " + row); //TODO temp
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
    private void mInitializeDataView(){
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
    }

    //DATA TAB
    @FXML
    private void mLoadReferenceImages(ActionEvent event){
        System.out.println("Reference Combo Box Action");//TODO Temp
        int selectedItemIndex = dataMouthSetChoiceComboBox.getSelectionModel().getSelectedIndex();
        String selectedName = new String(dataMouthSetChoiceComboBox.getItems().get(selectedItemIndex).toString());
        System.out.println("selectedName: " + selectedName); //TODO Temp
        //TODO get mouth_pair_id from DB based on the selected Name
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
        //TODO Query to get All Symbols and Their Selected Mouth Counterparts

    }
}
