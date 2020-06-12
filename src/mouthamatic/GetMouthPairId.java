package mouthamatic;

import javafx.scene.control.ComboBox;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GetMouthPairId {
    public int run(ComboBox mouthPairComboBox){
        //Get the MouthPairTypeId from the name selected in the ComboBox.
        System.out.println(mouthPairComboBox.getSelectionModel().getSelectedItem().toString()); //TODO temp
        String comboSelectedName = new String(mouthPairComboBox.getSelectionModel().getSelectedItem().toString());
        ResultSet rs;
        String query = new String("SELECT mouth_pair_type_id FROM `word-to-phoneme`.mouth_pair_type WHERE mouth_pair_name = '" + comboSelectedName + "' LIMIT 1;");
        rs = Main.db.sendQuery(query);
        int mouthPairId = 0;
        try {
            rs.next();
            mouthPairId = rs.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return mouthPairId;
    }
}

