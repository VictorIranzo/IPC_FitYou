/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import jgpx.model.analysis.Chunk;
import jgpx.model.analysis.TrackData;
import jgpx.model.gpx.Track;
import jgpx.model.jaxb.GpxType;
import jgpx.model.jaxb.TrackPointExtensionT;
import jgpx.util.DateTimeUtils;


public class MainWindowController implements Initializable {

    private Stage primaryStage;
    private List<GpxType> gpxfiles;
    private boolean answeredHR;
    private XYChart.Series serieSpTi, serieSpDis, serieHRTi, serieHRDis, seriePedTi, seriePedDis, serieHeiTi, serieHeiDis,
            serieSpTi2, serieSpDis2, serieHRTi2, serieHRDis2, seriePedTi2, seriePedDis2;
    private boolean initGraphs;
    private XYChart.Series serieAllDistance, serieAllTime, serie3MDist,serie3MTime,serieLMDist,serieLMTime;
    private ToggleGroup group, group2, group3;
    private LocalDate min;
    private LocalDate max;
    private LocalDate[] presents;

    @FXML
    private ComboBox<GpxType> comboBox;
    @FXML
    private Label startlabel;
    @FXML
    private Label durlabel;
    @FXML
    private Label exerciselabel;
    @FXML
    private Label distancelabel;
    @FXML
    private Label slopelabel;
    @FXML
    private Label maxspeedlabel;
    @FXML
    private Label avspeedlabel;
    @FXML
    private Label HRlabel;
    @FXML
    private Label maxpedallabel;
    @FXML
    private Label avpedalinglabel;
    @FXML
    private RadioButton RadioDist;
    @FXML
    private RadioButton RadioTime;
    @FXML
    private AreaChart<Double, Double> HeightGraph;
    @FXML
    private LineChart<Double, Double> SpeedGraph;
    @FXML
    private LineChart<Double, Double> HRGraph;
    @FXML
    private LineChart<Double, Double> PedalingGraph;
    @FXML
    private PieChart elPieChart;
    @FXML
    private Label YourHRlabel;
    @FXML
    private BarChart<String, Double> notebookGraph;
    @FXML
    private Label descendentSlope;
    @FXML
    private TabPane tabPane;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private LineChart<Double, Double> SeriesGraph;
    @FXML
    private CheckBox HRCheckBox;
    @FXML
    private CheckBox SpeedCheckBox;
    @FXML
    private CheckBox PedalingCheckBox;
    @FXML
    private RadioButton RDis;
    @FXML
    private RadioButton Rti;
    @FXML
    private RadioButton allFiles;
    @FXML
    private RadioButton ThreeMonths;
    @FXML
    private RadioButton LastMonth;
    @FXML
    private Button loadButt;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //Changes the Locale
        Locale.setDefault(Locale.UK);

        
        //Hide the progressBar until the loading of the graphs
        progressBar.setVisible(false);
        
        //Tooltips
        comboBox.setTooltip(new Tooltip("Select here one of your GPX files loaded"));
        loadButt.setTooltip(new Tooltip("Load more files to compare all the tracks"));
        
        //Hide the legend of the four main graphs
        SpeedGraph.setLegendVisible(false);
        HRGraph.setLegendVisible(false);
        PedalingGraph.setLegendVisible(false);
        HeightGraph.setLegendVisible(false);

        //Listener: when we select from comboBox-> Update all tabs
        comboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<GpxType>() {
            @Override
            public void changed(ObservableValue<? extends GpxType> observable, GpxType oldValue, GpxType newValue) {
                answeredHR = false;
                initGraphs = false;
                updateAll(newValue);
            }
        });

        //How to display elements in the comboBox
        comboBox.setCellFactory(lv -> new ListCell<GpxType>() {
            @Override
            public void updateItem(GpxType item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getTrk().get(0).getName());
                } else {
                    setText(null);
                }
            }
        });

        //How to display the element selected of the ComboBox
        comboBox.setButtonCell(new ListCell<GpxType>() {
            @Override
            protected void updateItem(GpxType item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getTrk().get(0).getName());
                } else {
                    setText(null);
                }
            }
        });
        
        //Updates YLabels
        SpeedGraph.getYAxis().setLabel("Speed (km/h)");
        HeightGraph.getYAxis().setLabel("Height (km)");
        HRGraph.getYAxis().setLabel("HR (bpm)");
        PedalingGraph.getYAxis().setLabel("Cadence (rpm)");
        SeriesGraph.getYAxis().setLabel("(bpm,km/h,rpm)");
        notebookGraph.getYAxis().setLabel("(Km, min)");
        
        //Graphs titles
        SpeedGraph.setTitle("Speed");
        HeightGraph.setTitle("Height");
        HRGraph.setTitle("Heart Rate");
        PedalingGraph.setTitle("Pedaling Rate");
        
        //Select first item of the comboBox at the beginning 
        //comboBox.getSelectionModel().selectFirst();

        //Radio buttons belong to the same ToggleGroup
        group = new ToggleGroup();
        RadioDist.setToggleGroup(group);
        RadioTime.setToggleGroup(group);
        RadioDist.selectedProperty().set(true);

        //Radio buttons belong to the same ToggleGroup
        group2 = new ToggleGroup();
        RDis.setToggleGroup(group2);
        Rti.setToggleGroup(group2);
        RDis.selectedProperty().set(true);
        
        //ToggleGroup for the NoteBook
        group3= new ToggleGroup();
        allFiles.setToggleGroup(group3);
        LastMonth.setToggleGroup(group3);
        ThreeMonths.setToggleGroup(group3);
        allFiles.selectedProperty().set(true);
        

        //Listener: change type of graph when we change the selection in the ToggleGroup
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (!comboBox.getSelectionModel().isEmpty()) {
                    ObservableList<Chunk> chunks = new TrackData(new Track(comboBox.getSelectionModel().getSelectedItem().getTrk().get(0))).getChunks();
                    Task<Void> tarea = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            if (RadioDist.isSelected()) {
                                updateGraphsDist();
                            } else {
                                updateGraphsTime();
                            }
                            return null;
                        }
                    };
                    Thread thgraf = new Thread(tarea);
                    thgraf.setDaemon(true);
                    thgraf.start();
                }
            }
        });

    }

    //Load another file to the program, it's added to the comboBox and to gpxfiles
    @FXML
    private void loadPress(ActionEvent event) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("GPX", "gpx"));
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.showOpenDialog(null);
        File[] files = fileChooser.getSelectedFiles();

        for (int j = 0; j < files.length; j++) {
            try {
                File file = files[j];
                if (file == null) {
                    return;
                }

                JAXBContext jaxbContext = JAXBContext.newInstance(GpxType.class, TrackPointExtensionT.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                JAXBElement<Object> root = (JAXBElement<Object>) unmarshaller.unmarshal(file);
                GpxType gpx = (GpxType) root.getValue();

                if (gpx != null) {
                    boolean igual = false;
                    for (int i = 0; i < gpxfiles.size() && !igual; i++) {
                        if (gpxfiles.get(i).getTrk().get(0).getName().equals(gpx.getTrk().get(0).getName())) {
                            igual = true;
                        }
                    }
                    if (igual) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("You already load this file: " + file.getName());
                        alert.showAndWait();
                    } else {
                        //Added to the list of gpxfiles and to the list in the comboBox
                        gpxfiles.add(gpx);
                        comboBox.getItems().add(gpx);
                        Task<Void> graficos = new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                initNotebook(gpxfiles);
                                return null;
                            }
                        };
                        Thread thgraf = new Thread(graficos);
                        thgraf.setDaemon(true);
                        thgraf.start();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Not a GPX file: " + file.getName());
                    alert.showAndWait();
                }

            } catch (JAXBException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("You didn't select an appropiate GPX file");
                alert.showAndWait();
            }
        }

    }

    //Ask for the max. HR
    //File not selected: alert and return to the first Tab
    //Don't answered, return to the first Tab
    //Check negative values and update PieChart and label
    @FXML
    private void selectedHRZ(Event event) {
            if (tabPane.getTabs().get(3).isSelected() && !answeredHR && !comboBox.getSelectionModel().isEmpty()) {
            TextInputDialog dialog = new TextInputDialog(); // Default value
            dialog.setTitle("Maximum Heart Rate");
            dialog.setHeaderText(null);
            dialog.setContentText("What is your maximum Heart Rate?");
            Optional<String> result = dialog.showAndWait();
            // Obtain the result
            if (result.isPresent()) {
                try {
                    double maxHR = Double.parseDouble(result.get());
                    if (maxHR <= 0) {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("You input a negative or zero number");
                        alert.showAndWait();
                        tabPane.getSelectionModel().select(0);
                    } else {
                        YourHRlabel.setText("Your maximum Heart Rate is " + maxHR);
                        initPieChart(maxHR);
                        answeredHR = true;
                    }
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("You don't input a valid number");
                    alert.showAndWait();
                    tabPane.getSelectionModel().select(0);
                }
            } else {
                tabPane.getSelectionModel().select(0);
            }
        }
    }

    //Inits the comboBox with the files provided in the first Window
    //With a thread, noteBook is inited
    public void initStage(Stage primaryStage, List<GpxType> gpxfiles) {
        this.primaryStage = primaryStage;
        this.gpxfiles = gpxfiles;
        this.answeredHR = false;
        this.initGraphs = false;

        ObservableList<GpxType> data = FXCollections.observableArrayList(gpxfiles);
        comboBox.setItems(data);
        Task<Void> graficos = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                initNotebook(gpxfiles);
                return null;
            }
        };
        Thread thgraf = new Thread(graficos);
        thgraf.setDaemon(true);
        thgraf.start();
        
                //Confirmation when exiting the APP
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Close Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to close the APP?");
            Optional<ButtonType> contest=alert.showAndWait();
            if (contest.isPresent() && contest.get() == ButtonType.OK){
            primaryStage.close();
            } else {
            alert.close();
            event.consume();
            }
        }});
        
    }

    //Inits the summary tab, and the graphs by means of a thread and indicating
    //progress with the progressBar -> During the process, comboBox is disabled,
    //and the tabs for graphs too
    private void updateAll(GpxType gpxnew) {
        //Puts well all the labels of the Summary Tab
        TrackData newValue = new TrackData(new Track(gpxnew.getTrk().get(0)));
        startlabel.setText(DateTimeUtils.format(newValue.getStartTime()));
        durlabel.setText(DateTimeUtils.format(newValue.getTotalDuration()));
        exerciselabel.setText(DateTimeUtils.format(newValue.getMovingTime()));
        double aKm = newValue.getTotalDistance() / 1000;
        distancelabel.setText(String.format("%.2f Km", aKm));
        slopelabel.setText(String.format("%.2f m", newValue.getTotalAscent()));
        descendentSlope.setText(String.format("%.2f m", newValue.getTotalDescend()));
        maxspeedlabel.setText(String.format("%.2f Km/h", newValue.getMaxSpeed() * 3, 6));
        avspeedlabel.setText(String.format("%.2f Km/h", newValue.getAverageSpeed() * 3, 6));
        HRlabel.setText(newValue.getMinHeartRate() + " , " + newValue.getMaxHeartrate() + " , " + newValue.getAverageHeartrate() + " bpm");
        maxpedallabel.setText(String.format("%d rpm", newValue.getMaxCadence()));
        avpedalinglabel.setText(String.format("%d rpm", newValue.getAverageCadence()));

        tabPane.getSelectionModel().select(0);

        ObservableList<Chunk> chunks = newValue.getChunks();
        if (!initGraphs) {
            Task<Void> graficos = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    comboBox.disableProperty().set(true);
                    tabPane.getTabs().get(1).disableProperty().set(true);
                    tabPane.getTabs().get(2).disableProperty().set(true);
                    SeriesGraph.getData().clear();
                    serieSpTi = new XYChart.Series();
                    serieSpDis = new XYChart.Series();
                    serieHRTi = new XYChart.Series();
                    serieHRDis = new XYChart.Series();
                    seriePedTi = new XYChart.Series();
                    seriePedDis = new XYChart.Series();
                    serieHeiTi = new XYChart.Series();
                    serieHeiDis = new XYChart.Series();
                    serieSpTi2 = new XYChart.Series();
                    serieSpDis2 = new XYChart.Series();
                    serieHRTi2 = new XYChart.Series();
                    serieHRDis2 = new XYChart.Series();
                    seriePedTi2 = new XYChart.Series();
                    seriePedDis2 = new XYChart.Series();
                    serieSpTi2.setName("Speed x Time");
                    serieSpDis2.setName("Speed x Distance");
                    serieHRTi2.setName("Heart Rate x Time");
                    serieHRDis2.setName("Heart Rate x Distance");
                    seriePedTi2.setName("Pedaling Rate x Time");
                    seriePedDis2.setName("Pedaling x Distance");

                    initGraphs = true;
                    double time = 0;
                    double dist = 0;
                    for (int i = 0; i < chunks.size(); i++) {
                        Chunk chun = chunks.get(i);
                        serieSpTi.getData().add(new XYChart.Data<>(time, chun.getSpeed() * 3, 6));
                        serieHeiTi.getData().add(new XYChart.Data<>(time, chun.getAvgHeight()));
                        serieHRTi.getData().add(new XYChart.Data<>(time, chun.getAvgHeartRate()));
                        seriePedTi.getData().add(new XYChart.Data<>(time, chun.getAvgCadence()));
                        serieSpDis.getData().add(new XYChart.Data<>(dist, chun.getSpeed() * 3, 6));
                        serieHeiDis.getData().add(new XYChart.Data<>(dist, chun.getAvgHeight()));
                        serieHRDis.getData().add(new XYChart.Data<>(dist, chun.getAvgHeartRate()));
                        seriePedDis.getData().add(new XYChart.Data<>(dist, chun.getAvgCadence()));
                        dist += (chun.getDistance()/1000.0);
                        time += (chun.getDuration().getSeconds()/60.0);
                        updateProgress(i, chunks.size());
                    }
                    
                    serieSpTi2.getData().addAll(serieSpTi.getData());
                    serieSpDis2.getData().addAll(serieSpDis.getData());
                    seriePedDis2.getData().addAll(seriePedDis.getData());
                    seriePedTi2.getData().addAll(seriePedTi.getData());
                    serieHRDis2.getData().addAll(serieHRDis.getData());
                    serieHRTi2.getData().addAll(serieHRTi.getData());
                    
                    if (RadioDist.isSelected()) {
                        updateGraphsDist();
                    } else {
                        updateGraphsTime();
                    }
                    if (RDis.isSelected()) {
                        pressDis(new ActionEvent());
                    } else {
                        pressTi(new ActionEvent());
                    }

                    comboBox.disableProperty().set(false);
                    tabPane.getTabs().get(1).disableProperty().set(false);
                    tabPane.getTabs().get(2).disableProperty().set(false);
                    return null;
                }
            };
            Thread thgraf = new Thread(graficos);
            thgraf.setDaemon(true);
            thgraf.start();
            progressBar.progressProperty().bind(graficos.progressProperty());
            progressBar.visibleProperty().bind(graficos.runningProperty());
        }

    }

    //Updates xaxis to distance
    private void updateGraphsDist() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                RadioDist.setDisable(true);
                RadioTime.setDisable(true);
                SpeedGraph.getData().setAll(serieSpDis);
                HeightGraph.getData().setAll(serieHeiDis);
                HRGraph.getData().setAll(serieHRDis);
                PedalingGraph.getData().setAll(seriePedDis);
                
                SpeedGraph.getXAxis().setLabel("Distance (Km)");
                HeightGraph.getXAxis().setLabel("Distance (Km)");
                HRGraph.getXAxis().setLabel("Distance (Km)");
                PedalingGraph.getXAxis().setLabel("Distance (Km)");
                
                RadioDist.setDisable(false);
                RadioTime.setDisable(false);
            }
        });
    }

    //Updates xaxis to time
    private void updateGraphsTime() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                RadioDist.setDisable(true);
                RadioTime.setDisable(true);
                SpeedGraph.getData().setAll(serieSpTi);
                HeightGraph.getData().setAll(serieHeiTi);
                HRGraph.getData().setAll(serieHRTi);
                PedalingGraph.getData().setAll(seriePedTi);
                
                SpeedGraph.getXAxis().setLabel("Time (min)");
                HeightGraph.getXAxis().setLabel("Time (min)");
                HRGraph.getXAxis().setLabel("Time (min)");
                PedalingGraph.getXAxis().setLabel("Time (min)");
                
                RadioDist.setDisable(false);
                RadioTime.setDisable(false);
            }
        });
    }

    //Similar to selectedHRZ, but if we don't answer the dialogue, nothing happens
    @FXML
    private void changeHRButton(ActionEvent event) {
        if(!comboBox.getSelectionModel().isEmpty()){
        TextInputDialog dialog = new TextInputDialog(); // Default value
        dialog.setTitle("Maximum Heart Rate");
        dialog.setHeaderText(null);
        dialog.setContentText("What is your maximum Heart Rate?");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                double maxHR = Double.parseDouble(result.get());
                if (maxHR <= 0) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("You input a negative or zero number. Please input a valid one.");
                    alert.showAndWait();
                } else {
                    YourHRlabel.setText("Your maximum Heart Rate is " + maxHR);
                    initPieChart(maxHR);
                    answeredHR = true;
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("You don't input a number. Please input a valid one.");
                alert.showAndWait();
            }
            }
        }
    }

    //Inits notebook
    private void initNotebook(List<GpxType> items) {
        tabPane.getTabs().get(4).disableProperty().set(true);
        
        min = LocalDate.MAX;
        max = LocalDate.MIN;
        presents = new LocalDate[items.size()];

        //We store maximum and minimum dates in 2 variables, and all the dates where we make
        //exercise in an array of localDates
        for (int i = 0; i < items.size(); i++) {
            TrackData trackdata = new TrackData(new Track(items.get(i).getTrk().get(0)));
            LocalDate toLocalDate = trackdata.getEndTime().toLocalDate();
            presents[i] = toLocalDate;
            if (toLocalDate.compareTo(max) > 0) {
                max = toLocalDate;
            }
            if (toLocalDate.compareTo(min) < 0) {
                min = toLocalDate;
            }
        }
        
        allFilesSelected(new ActionEvent());
        tabPane.getTabs().get(4).disableProperty().set(false);
    }

    //We compute the intervals with the maxHR provided, and we compute how many chunks
    //Are in each interval
    private void initPieChart(double maxHR) {
        ObservableList<Chunk> chunks = new TrackData((new Track(comboBox.getSelectionModel().getSelectedItem().getTrk().get(0)))).getChunks();
        double limitZ1 = 0.60 * maxHR;
        double limitZ2 = 0.70 * maxHR;
        double limitZ3 = 0.80 * maxHR;
        double limitZ4 = 0.90 * maxHR;
        int counter1 = 0, counter2 = 0, counter3 = 0, counter4 = 0, counter5 = 0;
        for (int i = 0; i < chunks.size(); i++) {
            double hr = chunks.get(i).getAvgHeartRate();
            if (hr < limitZ1) {
                counter1++;
            }
            if (hr > limitZ1 && hr < limitZ2) {
                counter2++;
            }
            if (hr > limitZ2 && hr < limitZ3) {
                counter3++;
            }
            if (hr > limitZ3 && hr < limitZ4) {
                counter4++;
            }
            if (hr > limitZ4 && hr < maxHR) {
                counter5++;
            }
        }
        ObservableList<PieChart.Data> dat = FXCollections.observableArrayList(
                new PieChart.Data("Z1 Recovery", counter1),
                new PieChart.Data("Z2 Endurance", counter2),
                new PieChart.Data("Z3 Tempo", counter3),
                new PieChart.Data("Z4 Threshold", counter4),
                new PieChart.Data("Z5 Anaerobic", counter5));
        elPieChart.setData(dat);
    }

    //If we mark HRCheckBox -> update Series Graph (checking file selected and radioButtons)
    @FXML
    private void markedHRData(ActionEvent event) {
        if (!comboBox.getSelectionModel().isEmpty()) {
            if (HRCheckBox.isSelected()) {
                if (RDis.isSelected()) {
                    SeriesGraph.getData().add(serieHRDis2);
                } else {
                    SeriesGraph.getData().add(serieHRTi2);
                }
            } else {
                SeriesGraph.getData().remove(serieHRDis2);
                SeriesGraph.getData().remove(serieHRTi2);
            }
        }
    }

    //If we mark SpeedCheckBox -> update Series Graph (checking file selected and radioButtons)
    @FXML
    private void markedSpeedData(ActionEvent event) {
        if (!comboBox.getSelectionModel().isEmpty()) {
            if (SpeedCheckBox.isSelected()) {
                if (RDis.isSelected()) {
                    SeriesGraph.getData().add(serieSpDis2);
                } else {
                    SeriesGraph.getData().add(serieSpTi2);
                }
            } else {
                SeriesGraph.getData().remove(serieSpDis2);
                SeriesGraph.getData().remove(serieSpTi2);
            }
        }
    }

    //If we mark PedalingCheckBox -> update Series Graph (checking file selected and radioButtons)
    @FXML
    private void markedPedalingData(ActionEvent event) {
        if (!comboBox.getSelectionModel().isEmpty()) {
            if (PedalingCheckBox.isSelected()) {
                if (RDis.isSelected()) {
                    SeriesGraph.getData().add(seriePedDis2);
                } else {
                    SeriesGraph.getData().add(seriePedTi2);
                }
            } else {
                SeriesGraph.getData().remove(seriePedDis2);
                SeriesGraph.getData().remove(seriePedTi2);
            }
        }
    }

    //Change in the RadioButtons of SeriesGraph -> Distance
    @FXML
    private void pressDis(ActionEvent event) {
        if (!comboBox.getSelectionModel().isEmpty()) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    SeriesGraph.getXAxis().setLabel("Distance (Km)");
                    SeriesGraph.getData().clear();
                    if (HRCheckBox.isSelected()) {
                        SeriesGraph.getData().add(serieHRDis2);
                    }
                    if (SpeedCheckBox.isSelected()) {
                        SeriesGraph.getData().add(serieSpDis2);
                    }
                    if (PedalingCheckBox.isSelected()) {
                        SeriesGraph.getData().add(seriePedDis2);
                    }
                }
            });
        }
    }

    //Change in the RadioButtons of SeriesGraph -> Time
    @FXML
    private void pressTi(ActionEvent event) {
        if (!comboBox.getSelectionModel().isEmpty()) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    SeriesGraph.getXAxis().setLabel("Time (min)");
                    SeriesGraph.getData().clear();
                    if (HRCheckBox.isSelected()) {
                        SeriesGraph.getData().add(serieHRTi2);
                    }
                    if (SpeedCheckBox.isSelected()) {
                        SeriesGraph.getData().add(serieSpTi2);
                    }
                    if (PedalingCheckBox.isSelected()) {
                        SeriesGraph.getData().add(seriePedTi2);
                    }
                }
            });
        }
    }

    @FXML
    private void allFilesSelected(ActionEvent event) {
        if(allFiles.isSelected()){
            serieAllDistance = new XYChart.Series();
            serieAllTime = new XYChart.Series();
            
        //We go from the minimum to the maximum increasing the min variable
        //If we find that in this date we did exercise, we add its value
        //Else, we create an empty value with this date
        LocalDate aux=min;
        while (aux.compareTo(max) <= 0) {
            boolean pres = false;
            for (int i = 0; i < presents.length; i++) {
                if (presents[i].compareTo(aux) == 0) {
                    pres = true;
                    TrackData trackdata = new TrackData(new Track(gpxfiles.get(i).getTrk().get(0)));
                    serieAllDistance.getData().add(new XYChart.Data(presents[i].toString(), (trackdata.getTotalDistance()/1000)));
                    serieAllTime.getData().add(new XYChart.Data(presents[i].toString(), (trackdata.getTotalDuration().getSeconds()/60)));
                }
            }
            if (!pres) {
                serieAllDistance.getData().add(new XYChart.Data(aux.toString(), 0));
            }
            aux = aux.plusDays(1);
        }
        serieAllDistance.setName("Total Distance");
        serieAllTime.setName("Total Duration");
            
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
            notebookGraph.getData().clear();
            notebookGraph.getData().addAll(serieAllDistance, serieAllTime);
            }});
        }
    }

    @FXML
    private void ThMonthsSelected(ActionEvent event) {
        if(ThreeMonths.isSelected()){
            serie3MDist= new XYChart.Series();
            serie3MTime= new XYChart.Series();
            
            LocalDate hoy = LocalDate.now();
            LocalDate month3before= hoy.minusMonths(3);
            
            while(month3before.compareTo(hoy)<=0){
            boolean pres = false;
            for (int i = 0; i < presents.length; i++) {
                if (presents[i].compareTo(month3before) == 0) {
                    pres = true;
                    TrackData trackdata = new TrackData(new Track(gpxfiles.get(i).getTrk().get(0)));
                    serie3MDist.getData().add(new XYChart.Data(presents[i].toString(), (trackdata.getTotalDistance()/1000)));
                    serie3MTime.getData().add(new XYChart.Data(presents[i].toString(), (trackdata.getTotalDuration().getSeconds()/60)));
                }
            }
            if (!pres) {
                serie3MDist.getData().add(new XYChart.Data(month3before.toString(), 0));
            }
            month3before = month3before.plusDays(1);
        }
        serie3MDist.setName("Total Distance");
        serie3MTime.setName("Total Duration");

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
            notebookGraph.getData().clear();
            notebookGraph.getData().addAll(serie3MDist, serie3MTime);
            }});
        }
    }

    @FXML
    private void lastMonthSelected(ActionEvent event) {
        if(LastMonth.isSelected()){
            serieLMDist= new XYChart.Series();
            serieLMTime= new XYChart.Series();
            
        LocalDate hoy = LocalDate.now();
        LocalDate monthbefore= hoy.minusMonths(1);
        while(monthbefore.compareTo(hoy)<=0){
            boolean pres = false;
            for (int i = 0; i < presents.length; i++) {
                if (presents[i].compareTo(monthbefore) == 0) {
                    pres = true;
                    TrackData trackdata = new TrackData(new Track(gpxfiles.get(i).getTrk().get(0)));
                    serieLMDist.getData().add(new XYChart.Data(presents[i].toString(), (trackdata.getTotalDistance()/1000)));
                    serieLMTime.getData().add(new XYChart.Data(presents[i].toString(), (trackdata.getTotalDuration().getSeconds()/60)));
                    
                }
            }
            if (!pres) {
            serieLMDist.getData().add(new XYChart.Data(monthbefore.toString(),0));
            }
            monthbefore = monthbefore.plusDays(1);
        }
        
        serieLMDist.setName("Total Distance");
        serieLMTime.setName("Total Duration");
            
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
            notebookGraph.getData().clear();
            notebookGraph.getData().addAll(serieLMDist, serieLMTime);
            }});
        }
    }

}
