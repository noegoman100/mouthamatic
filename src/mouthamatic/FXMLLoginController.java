/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mouthamatic;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


/**
 *
 * @author Ed
 */
public class FXMLLoginController implements Initializable {
    
    @FXML
    private TextField usernameTextField;
    @FXML 
    private PasswordField passwordField;
    private DB db;
    

    @FXML
    private void bypassLogin(ActionEvent event){
        //***** TEMP for Testing.
        try {
            Main.screenManager.changeStage(ScreenEnum.HOME);
        } catch (Exception ex) {
            Logger.getLogger(FXMLLoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
        //***** END TEMP for Testing.
    }
    
    @FXML
    private void loginButtonAction(ActionEvent event) {

        //Todo Validate Username and Password are not blank
        String username = usernameTextField.getText();
        String password = passwordField.getText();
        String query = "SELECT user_name, user_password FROM user WHERE user_name = '"
                + username + "';";
        ResultSet rs = db.sendQuery(query);
        try {
            rs.next();
            String usernameDB = rs.getString(1);
            String passwordDB = rs.getString(2);
            if (password.equals(passwordDB)){
                try {
                    Main.screenManager.changeStage(ScreenEnum.HOME);
                } catch (Exception ex) {
                    Logger.getLogger(FXMLLoginController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        } catch (SQLException ex) {
            Logger.getLogger(FXMLLoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        db = Main.db;
        try {
            db.connect();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Connection Error");
            alert.setHeaderText(null);
            alert.setContentText("The AWS Database may not be running, or some other connection problem");

            alert.showAndWait();
        }

    }    
    
}
