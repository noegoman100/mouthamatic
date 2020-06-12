package mouthamatic;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WordSearch {
    public void run(TextField dataSearchWordTextField, TableView dataTableView){
        String word = new String(dataSearchWordTextField.getText());
        ObservableList<ObservableList> wordData;
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
            //If maxParts == 0 (No word found), then clear the table and the search field
            if (maxParts == 0) {
                dataSearchWordTextField.setText("");
                dataTableView.getColumns().clear();
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
                queryAllParts = queryAllParts + "INNER JOIN `word-to-phoneme`.word_parts AS set" + (i+1) + " ON (word_id = set" + (i+1) + ".word_id_pk1 AND "
                        + "set" + (i+1) + ".part_segment_pk2=" + (i+1) + ") ";
            }
            queryAllParts = queryAllParts + "WHERE word_name='" + word + "'; ";

            /**** End Build Query based on max parts **/
            //System.out.println(queryAllParts);
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
                        run(dataSearchWordTextField, dataTableView);
                        //mWordSearchButton();//Refresh the table.
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
                        run(dataSearchWordTextField, dataTableView);
                        //mWordSearchButton();//Refresh the table.
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
}

