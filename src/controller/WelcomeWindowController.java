/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import jgpx.model.jaxb.GpxType;
import jgpx.model.jaxb.TrackPointExtensionT;

/**
 * FXML Controller class
 *
 * @author Víctor
 */
public class WelcomeWindowController implements Initializable {

    private Stage primaryStage;
    private List<GpxType> gpxfiles;
    
    @FXML
    private Label loadedlabel;
    @FXML
    private Button contBut;
    @FXML
    private Label statuslabel;
    @FXML
    private Button loadButt;


    //Disable the continue button until load a file
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Tooltips
        contBut.setTooltip(new Tooltip("Changes to the main window when you have almost 1 file loaded"));
        loadButt.setTooltip(new Tooltip("Opens a dialogue to introduce a file in the APP"));
        
        Locale.setDefault(Locale.UK);
        contBut.disableProperty().set(true);
        loadedlabel.setVisible(false);
        statuslabel.setVisible(false);
    }    

    //We increase the height of the window to inform about the loading of each file
    //We add loaded files to the list gpxfiles
    //Bad file -> Alert
    @FXML
    private void pressLoad(ActionEvent event){
        statuslabel.setVisible(true);
        statuslabel.setText("Loading");
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("GPX","gpx"));
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.showOpenDialog(null);
        File[] files = fileChooser.getSelectedFiles();
        
        for(int j=0; j<files.length;j++){
            try{
                
            File file= files[j];
        if (file == null) {
            statuslabel.setVisible(false);
            return;
        }
        
        JAXBContext jaxbContext = JAXBContext.newInstance(GpxType.class, TrackPointExtensionT.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        JAXBElement<Object> root = (JAXBElement<Object>) unmarshaller.unmarshal(file);
        GpxType gpx = (GpxType) root.getValue();

        if (gpx != null) {
            boolean igual=false;
            for(int i=0;i<gpxfiles.size() && !igual;i++){
                if(gpxfiles.get(i).getTrk().get(0).getName().equals(gpx.getTrk().get(0).getName())){
                    igual=true;}
            }
            if(igual){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("You already load this file: " + file.getName());
            alert.showAndWait();
            }
            else{
            gpxfiles.add(gpx);
            loadedlabel.setText(loadedlabel.getText().concat('\n' + "  " + file.getName()));
            primaryStage.setHeight(primaryStage.getHeight()+20);
            contBut.disableProperty().set(false);
            loadedlabel.setVisible(true);
            }
        } else {
            statuslabel.setText("Error loading GPX from " + file.getName());
        }
        }catch(JAXBException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("You didn't select an appropiate GPX file");
            alert.showAndWait();
        }
        }
        statuslabel.setVisible(false);
    }

    //Load the main window and pass gpxfiles to init the other stage, also change the Height of the window
    @FXML
    private void pressContinue(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainWindow.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        
        MainWindowController mainController =loader.<MainWindowController>getController();
        mainController.initStage(primaryStage, gpxfiles);
        primaryStage.setHeight(600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //Init gpxfiles to an empty list
    public void initStage(Stage stage) {
        this.primaryStage=stage;
        this.gpxfiles= new ArrayList<GpxType>();
    }
    
}
