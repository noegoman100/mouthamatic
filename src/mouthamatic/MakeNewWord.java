package mouthamatic;

import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class MakeNewWord {
    public void run(TextField dataSearchWordTextField) {
        int phonemeCount = 0;

        TextInputDialog dialog = new TextInputDialog("5");
        dialog.setTitle("New Word");
        dialog.setHeaderText("How many phoneme segments in this new word?");
        dialog.setContentText("Word Phoneme Segments: ");
        Optional<String> result = dialog.showAndWait();
        //Check to see if a number was entered into the Phoneme Count Text Field
        if (result.isPresent()) {
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
            //System.out.println("updateQuery: " + insertWordQuery);
            Main.db.sendUpdate(insertWordQuery);

            //TODO Build an insert Query with the correct number of parts
            for (int i = 0; i < phonemeCount; i++) {
                String insertPartsQuery = new String("INSERT INTO `word-to-phoneme`.`word_parts` (`word_id_pk1`, `part_segment_pk2`, `symbol_id_fk`) VALUES ('" +
                        nextAvailableId + "', '" + (i + 1) +
                        "', '999');");
                //System.out.println("updateQuery: " + insertPartsQuery);
                Main.db.sendUpdate(insertPartsQuery);
            }

            //TODO then send this new word to the search function

            dataSearchWordTextField.setText("NEWWORD");
        }

    }

}

