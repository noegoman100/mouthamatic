package mouthamatic;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GenericReport {
    public String addHeader(){
        ResultSet rs = Main.db.sendQuery("SELECT CURRENT_TIMESTAMP;");
        String currentTimestamp = new String();
        try {
            rs.next();
            currentTimestamp = rs.getString(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return new String("Amazing Report Header!!\n\n" + "Current Timestamp: " + currentTimestamp + "\n\n");
    }
    public String addFooter(){

        return new String("Capstone Project Report Footer\n");
    }
    public String addGuts(){

        return new String("addGuts\n");
    }
    public String addAll(){

        return addHeader() + addGuts() + addFooter();
    }

}

