package mouthamatic;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoadSymbolChart {
    public void run(TableView referenceChartTableView){
        String query = new String("SELECT symbol AS 'Symbol', symbol_id_fk AS 'Symbol ID', word_name AS 'Example Word' FROM `word-to-phoneme`.word INNER JOIN `word-to-phoneme`.word_parts ON (word_id = word_id_pk1) INNER JOIN `word-to-phoneme`.symbols ON (symbols_id = symbol_id_fk) GROUP BY symbol ORDER BY symbol, part_segment_pk2;");
        ObservableList<ObservableList> referenceListData;
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
}

