package mouthamatic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SpecificReportTwo extends GenericReport{
    @Override
    public String addGuts(){
        //Number of Words Per Phoneme Count
        String query = new String("SELECT part_segment_pk2, count(*) FROM `word-to-phoneme`.word_parts GROUP BY part_segment_pk2;");
        ResultSet rs = Main.db.sendQuery(query);
        List<Integer> numberOfPhonemes = new ArrayList<>();
        List<Integer> numberOfWords = new ArrayList<>();
        while (true){
            try {
                if (!rs.next()) break;
                numberOfPhonemes.add(rs.getInt(1));
                numberOfWords.add(rs.getInt(2));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        String outputString = new String();
        outputString = outputString + "Number of Words Per Number of Phonemes: \n\n";
        outputString = outputString + "Phoneme Count\t\t# of Words\t\n";
        for (int i = 0; i < numberOfPhonemes.size(); i++) {
            outputString = outputString + numberOfPhonemes.get(i) + "\t\t\t\t\t" + numberOfWords.get(i) + "\n";

        }
        outputString = outputString + "\n";

        return new String(outputString);
    }

}

