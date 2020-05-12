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
    private ImageExporter imageExporter
            = new ImageExporter();
    @FXML private TableView dataTableView;                      //DATA TAB
    @FXML private TextField dataSearchWordTextField;            //DATA TAB
    private ObservableList<ObservableList> data;                //DATA TAB
    //private ObservableList<ObservableList> wordData;          //DATA TAB
    @FXML private ComboBox dataMouthSetChoiceComboBox;          //DATA TAB
    @FXML private ScrollPane referenceScrollPane;               //DATA TAB
    @FXML private TableView referenceChartTableView;            //DATA TAB
    //private ObservableList<ObservableList> referenceListData; //DATA TAB
    private WordSearch wordSearch
            = new WordSearch();                                 //DATA TAB
    private LoadReferenceImages loadReferenceImages
            = new LoadReferenceImages();                        //DATA TAB
    private LoadSymbolChart loadSymbolChart
            = new LoadSymbolChart();                            //DATA TAB
    private MakeNewWord makeNewWord
            = new MakeNewWord();                                //DATA TAB
    @FXML private TableView multiSearchTableView;               //SEARCH TAB
    @FXML private TextField multiSearchTextField;               //SEARCH TAB
    private SearchLike searchLike
            = new SearchLike();                                 //SEARCH TAB


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
            makeNewWord.run(dataSearchWordTextField);
            mWordSearchButton();
    }

    //DATA TAB
    @FXML
    private void mDeleteWordButton(){

        String deleteQuery = new String("DELETE FROM `word-to-phoneme`.word WHERE word_name = '" + dataSearchWordTextField.getText() + "';");
        System.out.println(deleteQuery);
        Main.db.sendUpdate(deleteQuery);
        mWordSearchButton();
    }

    //SEARCH TAB
    @FXML
    private void mMultiSearchButton(){ //TODO convert me to Search Tab
        searchLike.run(multiSearchTextField, multiSearchTableView);
    }

    //EXPORT TAB
    @FXML
    private void mExportImages(){
        imageExporter.run(imagesPerSymbolTextField, outputDestTextField, sentenceData);
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
