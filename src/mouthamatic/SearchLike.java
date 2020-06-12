package mouthamatic;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import java.sql.ResultSet;

public class SearchLike {
    public void run(TextField multiSearchTextField, TableView multiSearchTableView){
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
}

