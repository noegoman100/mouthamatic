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
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 *
 * This is the amazing Mouthamatic application. The primary classes in the application are:
 * SentenceParser, WordParser, SymbolMapper, and ImageMapper. Also, the MySQL database, hosted on AWS,
 * is the real hero in the story (Mouthamatic V2 uses an embedded SQLite database). The other classes are either helpers,
 * or just created to fill out all the requirements for my senior project.
 *
 * This is a Java 8 application using FXML for the GUI. This was developed with the IntelliJ IDEA IDE
 * and the SceneBuilder application (built into IDEA). The IDEA Configuration is simple: "Application" from the
 * templates. The SQL Driver should be in 'lib' folder ("mysql-connector-java-8.0.19").
 *
 * The core process is fairly straight forward. Take a raw input sentence. Parse it into individual words.
 * Parse each word into its constituent symbolIDs (these represent the phonemes). Map each symbol to
 * the chosen image-set image. Map each image URL to its actual file, and copy out to an export folder.
 *
 * Documentation can be found in the Documentation folder
 */
public class Main extends Application {
    public static DB db = new DB(); 
    public static ScreenManager screenManager;
    
    @Override
    public void start(Stage stage) throws Exception {
        try {
            db.connect();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Connection Error");
            alert.setHeaderText(null);
            alert.setContentText("The AWS Database may not be running, or some other connection problem");

            alert.showAndWait();
            return;
        }

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

