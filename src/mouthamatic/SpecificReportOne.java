package mouthamatic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SpecificReportOne extends GenericReport{
    @Override
    public String addGuts(){
        //Distinct Symbols currently used by all the words in the database.
        String query = new String("SELECT DISTINCT symbol FROM `word-to-phoneme`.`word` JOIN `word-to-phoneme`.`word_parts` ON (word_id = word_id_pk1) JOIN `word-to-phoneme`.`symbols` ON (symbols_id = symbol_id_fk) ORDER BY symbol;");
        ResultSet rs = Main.db.sendQuery(query);
        List<String> symbolList = new ArrayList();
        while (true){
            try {
                if (!rs.next()) break;
                symbolList.add(rs.getString(1));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        System.out.println("Symbol Count: " + symbolList.size());
        String outputString = new String();
        outputString = outputString + "Symbols Currently Used in the Database: \n\n";
        for (int i = 0; i < symbolList.size(); i++) {
            outputString = outputString + symbolList.get(i) + "\t\t";
            if (((i+1) % 5 == 0)) outputString = outputString + "\n";
        }
        outputString = outputString + "\n";

        return new String(outputString);
    }

}

