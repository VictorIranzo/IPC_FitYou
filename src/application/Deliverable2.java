
package application;

import controller.WelcomeWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author Víctor
 */
public class Deliverable2 extends Application {
    
    @Override
    public void start(Stage stage) throws Exception{
        //Loads FirstWindow
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/WelcomeWindow.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        
        WelcomeWindowController mainController =loader.<WelcomeWindowController>getController();
        mainController.initStage(stage);
        
        stage.setTitle("FitYou!");
        stage.getIcons().add(new Image("images/runner.png"));
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
    
}
