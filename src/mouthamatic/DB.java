/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mouthamatic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



/**
 *
 * @author edjsa
 */
public class DB {
    
        private static Connection conn = null;
	//private final String driver = "com.mysql.cj.jdbc.Driver";
        //private final String driver = "com.mysql.jdbc.Driver";
	private final String db = "word-to-phoneme";
	private final String url = "jdbc:mysql://phoneme-db.crm7fj6xpwpv.us-west-1.rds.amazonaws.com/" + db;
	private final String user = "admin";
	private final String pass = "Uxh3wFzeZwyawn";
        //private String currentUser = new String("");
        //private int currentUserId = 0;
    public void connect() {
        System.out.println("DatabaseConnection.connect() was called");
        try {
            //Class.forName(driver);  //This was for older version. No longer needed. 
            if (conn == null) {
                conn = DriverManager.getConnection(url,user,pass);
                System.out.println("Connected to database: " + db);
            } else {
                System.out.println("Database may already be connected");
            }
	} catch (SQLException e) {
            System.out.println("SQLException: "+e.getMessage());
            System.out.println("SQLState: "+e.getSQLState());
            System.out.println("VendorError: "+e.getErrorCode());
	} catch (Exception e) {
             System.out.println("Exception from DatabaseConnection.java : connect()");
             e.printStackTrace();
        }//End Try
    }
    

    
    public ResultSet sendQuery(String query){
    //System.out.println("DatabaseConnection.sendQuery called");
    ResultSet resultSet = null;
    Statement statement = null;
    try {            
        statement = conn.createStatement(); 
        resultSet = statement.executeQuery(query); 


    } catch (Exception e) {
        System.out.println("Exception thrown in DatabaseConnection.sendQuery() :Try Block");
    }//End Try+Catch

    return resultSet;
}

    public void sendUpdate(String updateSQL){
    //System.out.println("DatabaseConnection.sendQuery called");
    ResultSet resultSet = null;
    Statement statement = null;
    try {            
        statement = conn.createStatement(); 
        statement.executeUpdate(updateSQL); 


    } catch (Exception e) {
        System.out.println("Exception thrown in DatabaseConnection.sendUpdate() :Try Block");
    }//End Try+Catch


}
}
