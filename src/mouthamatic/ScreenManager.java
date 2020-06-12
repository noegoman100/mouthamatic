package mouthamatic;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
/**
 *
 * @author Ed
 */
public class ScreenManager {
    Stage stage;
    Scene scene;
    
    public ScreenManager() { //no arg constructor
    
    }//End Constructor
    public ScreenManager(Stage stage, Scene scene){ //initializing constructor. This gets and keeps the initial provided stage. 
        this.stage = stage;
        this.scene = scene;
    }//end Initializer Constructor.
    
    
    public void changeStage(ScreenEnum screenEnum) throws Exception {
        //stage.setScene(scene);
        //stage.show();
        Parent loginRoot = FXMLLoader.load(getClass().getResource("FXMLLoginView.fxml"));
        Scene loginScene = new Scene(loginRoot);
        Parent homeRoot = FXMLLoader.load(getClass().getResource("FXMLHomeView.fxml"));
        Scene homeScene = new Scene(homeRoot);  
//        Parent newAppointmentRoot = FXMLLoader.load(getClass().getResource("NewAppointmentView.fxml"));
//        Scene newAppointmentScene = new Scene(newAppointmentRoot);
        
        switch (screenEnum) {
        case LOGIN: 
                stage.setScene(loginScene); 
                stage.show();
                break;
        case HOME: 
                stage.setScene(homeScene);
                stage.show();
                break;
//        case APPOINTMENT:
//                stage.setScene(appointmentScene);
//                stage.show();
//                break;
//        case NEWAPPOINTMENT:
//                stage.setScene(newAppointmentScene); //Correct to New Appt Screen.
//                stage.show();
//                break;
        default:
                stage.setScene(loginScene);
                stage.show();
                break;
            
        }//end Switch
    }    
    
}

