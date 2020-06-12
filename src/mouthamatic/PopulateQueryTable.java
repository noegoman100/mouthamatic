package mouthamatic;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PopulateQueryTable {
    public void run(String selectionName, TableView reportsTableView) {
        ObservableList<ObservableList> data = FXCollections.observableArrayList();
        ResultSet rsSelection = Main.db.sendQuery("SELECT report_query_string FROM `word-to-phoneme`.report_query "
                + "WHERE report_query_name = '" + selectionName + "' "
                + "ORDER BY report_query_name DESC LIMIT 1; ");
        String query = new String("Error Loading Query");
        try {
            rsSelection.next();
        } catch (
                SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            query = rsSelection.getString(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        //Now!! We can send out the desired Query, then load the results into a dynamic table.
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

}

