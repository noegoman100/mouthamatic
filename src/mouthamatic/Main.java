/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mouthamatic;

import java.sql.ResultSet;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Ed
 */
public class Main extends Application {
    public static DB db = new DB(); 
    public static ScreenManager screenManager;
    
    @Override
    public void start(Stage stage) throws Exception {
        db.connect();
        
        ResultSet rs = db.sendQuery("SELECT * FROM user");
        while(rs.next()){
            System.out.println(rs.getString(2));
        }
        Parent root = FXMLLoader.load(getClass().getResource("FXMLLoginView.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        screenManager = new ScreenManager(stage, scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
