package mx.x10.iowizportal.tjmunapp.windows;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mx.x10.iowizportal.tjmunapp.ApplicationCore;
import mx.x10.iowizportal.tjmunapp.elements.*;
import mx.x10.iowizportal.tjmunapp.utils.*;
import mx.x10.iowizportal.tjmunapp.utils.consts.MUNProtocol;
import mx.x10.iowizportal.tjmunapp.utils.consts.UNDebateStates;
import mx.x10.iowizportal.tjmunapp.windows.protocolwindows.BaseDesign;
import mx.x10.iowizportal.tjmunapp.windows.protocolwindows.OEASWindow;
import mx.x10.iowizportal.tjmunapp.windows.protocolwindows.ProtocolWindow;
import mx.x10.iowizportal.tjmunapp.windows.protocolwindows.UNWindow;

import static mx.x10.iowizportal.tjmunapp.utils.consts.StaffPosition.*;

import java.io.*;
import java.util.*;

/**
 * Created by JJOL on 29/08/2015.
 */
public class MainWindow implements AppWindow {

    public final static String FLASH_STYLE_COLOUR = "#FF0000";
    private final List<String> debateStates = new ArrayList<>();
    /*{
        for(UNDebateStates state : UNDebateStates.values())
            debateStates.add(state.name);
    }*/

    private final DebateInfo DEBATE_INFO;
    private final ApplicationCore APP;
    private final CommitteeInfo COMMITTEE_INFO;
    private int firstPointer = 0;

    private final Scene scene;
    private final ClockFrame clock;
    private final CountrySelector countrySelector;
    private final ImageView flagView;

    private final StackPane panelRoot;
    private final HBox      rootPanel;
    private final DebateStateDisplay debateState;
    private boolean inTimeCounting = false;
    private final WarningsPanel warningsPanel;

    private final List<String> backendList = new ArrayList<>();

    private List<Node> flashyElements = new ArrayList<>();
    private boolean timeShouldEnd;

    public MainWindow(final Stage window, final ApplicationCore app, DebateInfo debateInfo, CommitteeInfo committeeInfo) {

        APP = app;
        APP.setCurrentWindow(this);
        DEBATE_INFO = debateInfo;
        COMMITTEE_INFO = committeeInfo;


        ApplicationCore.APP_LAN = DEBATE_INFO.language;
        panelRoot = new StackPane();
        rootPanel = new HBox(20);
        rootPanel.setId("base-root");

        VBox column1, column2, column3;
        column1 = new VBox(30);
        column1.setStyle("-fx-background-color: rgba(0,0,0,0);");
        column2 = new VBox(10);
        column2.setStyle("-fx-background-color: rgba(0,0,0,0); ");
        column2.widthProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue);
        });
        column2.setPadding(new Insets(60,0,0,0));
        column3 = new VBox(20);
        column3.setStyle("-fx-background-color: rgba(0,0,0,0)");

        final List<String> countries = new ArrayList<>(app.getAllCountries());
        final BaseDesign design = new BaseDesign(rootPanel);
        countrySelector = new CountrySelector(new HashSet<>(countries));
        flagView = new ImageView();
        //TODO Resolution Calcs
        flagView.setFitWidth(ApplicationCore.SCREEN_WIDTH/3);
        flagView.setFitHeight(200);

        // Left Country Roll Column
        VBox column = new VBox(25);
        column.setStyle("-fx-background-color: rgba(0,0,0,0)");

        final ListView<String> countriesQueueList = new ListView<>();
        countriesQueueList.getStyleClass().add("flashable-off");
        flashyElements.add(countriesQueueList);
        //countriesQueueList.setOnKeyPressed();
        countriesQueueList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                ObservableList<String> items = countriesQueueList.getItems();
                items.remove(countriesQueueList.getSelectionModel().getSelectedItem());
            }
        });
        column.getChildren().add(countriesQueueList);
        Node[] es = CountrySearcher.countrySearcher(countrySelector, column,
                new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> ov, String old_val, String countryAdded) {
                        if (countryAdded != null) {
                            // Add it to the roll list
                            if (!countriesQueueList.getItems().contains(countryAdded)) {
                                countriesQueueList.getItems().add(countryAdded);
                                if(debateInfo.protocol == MUNProtocol.UN  && !inTimeCounting) {
                                	Image countryImage = new Image(getImagePathForCountry(countryAdded));
                                	System.out.println(getImagePathForCountry(countryAdded));
                                    flagView.setImage(new Image(getImagePathForCountry(countryAdded)));
                                }
                            }
                        }
                    }
                }, null);
        for(Node n : es) {
            flashyElements.add(n);
        }
        //rootPanel.getChildren().add(column);

        // Right Timer and Warning Section
        VBox rightPanel = new VBox(20);

        // Top Clock and Flag

        StackPane backSection = new StackPane();

        // CLOCK FRAME

        flagView.setImage(ResourceLibrary.DEFAULT_FLAG);
        //backSection.getChildren().addAll(flagView);
        warningsPanel = new WarningsPanel(rightPanel, app, countrySelector);
        design.insertImportantThing("wpanel", warningsPanel);
        clock = new ClockFrame(backSection, app, true);

        clock.addTimeListener(event -> {
            if(event == TimeEvent.ENDED) {
                System.out.print("Time Has Ended!  Main Window!!");
                new Thread(() -> {
                    int i = 0;
                    while(i < 5) {
                        i++;
                        Platform.runLater(() -> {
                            clock.flashWarning(true);
                            warningsPanel.flashWarning(true);
                            flashScreen(true);
                        });
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Platform.runLater(() -> {
                            flashScreen(false);
                            clock.flashWarning(false);
                            warningsPanel.flashWarning(false);
                        });
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                ObservableList<String> items = countriesQueueList.getItems();
                timeShouldEnd = (items.size()==1);
                if(timeShouldEnd) {
                    clock.setAutplayable(false);
                    inTimeCounting = false;
                }

                Platform.runLater(() -> {
                            //ObservableList<String> items = countriesQueueList.getItems();
                            //System.out.println("Going to change to next Country [ "+ items.get(0) + " -> " + items.get(1) + "]");
                            if(!items.isEmpty()) {
                                if(isRollingState()) {
                                    items.remove(0);
                                    firstPointer = 0;
                                } else {
                                    firstPointer++;
                                }
                            }
                            countriesQueueList.getSelectionModel().select(firstPointer);
                            flagView.setImage(new Image(getImagePathForCountry(items.get(firstPointer))));
                        }
                );
            }

            if(event == TimeEvent.STARTED || event == TimeEvent.RESUMED) {
                if (isRollingState()) {
                   firstPointer = 0;
                }
                System.out.print("Time Has Started!  Main Window!!");
                ObservableList<String> items = countriesQueueList.getItems();
                //if(items.size()==1)
                inTimeCounting = true;
                Platform.runLater(() -> {

                            String imagePath;
                            if(countriesQueueList.getItems().isEmpty()) {
                                imagePath = "mun_flag.jpg";
                            } else {
                                imagePath = getImagePathForCountry(countriesQueueList.getItems().get(firstPointer));
                            }
                            flagView.setImage(new Image(imagePath));
                        }
                );
            }

            if(event == TimeEvent.STOPPED || event == TimeEvent.PAUSED) {
                inTimeCounting = false;
            }
        });
        rightPanel.getChildren().add(backSection);

        // Bottom Warning Section

        column1.getChildren().addAll(flagView, backSection, column);

        List<String> states = new ArrayList<>();
        if(DEBATE_INFO.protocol == MUNProtocol.UN) {
            for (UNDebateStates state : UNDebateStates.values())
                states.add(state.getName());
        } else {
            //for(OEASDebateStates state : OEASDebateStates.values())
                states.add(UNDebateStates.MODERATED_CAUCUS.getName());
                states.add(UNDebateStates.UNMODERATED_CAUCUS.getName());
        }

        List<TransitionFieldLabel> staffDisplays = new ArrayList<>();

        staffDisplays.add(new InfoStaffDisplay(false, COMMITEE_NAME.getName(), "Committee Name"));
        staffDisplays.add(new InfoStaffDisplay(false, TOPICS.getName(), "Topic Name"));
        staffDisplays.add(new InfoStaffDisplay(false, CHAIRS.getName(), "Chair Name"));
        staffDisplays.add(new InfoStaffDisplay(false, CO_CHAIRS.getName(), "Co-Chair Name"));
        debateState = new DebateStateDisplay(false, states, DEBATE_INFO);
        staffDisplays.add(debateState);
        //Label l = new Label("�");
        //column2.getChildren().add(l);

        final InfoStaff infoStaff;




        /*committeeDisplay.attach(column2);
        topicsDisplay.attach(column2);
        chairsDisplay.attach(column2);
        coChairsDisplay.attach(column2);
        stateDisplay.attach(column2);*/

        int baseWidth = ResourceLibrary.CENTRAL_COLUMN_WIDTH;
        column2.setMinWidth(baseWidth-baseWidth/6);
        Button configPanel = new Button("Committee Panel");
        configPanel.setOnMouseClicked(event -> new SettingsWindow(committeeInfo));

        column3.getChildren().addAll(rightPanel);

        rootPanel.getChildren().addAll(column1, column2, column3);

        panelRoot.getChildren().addAll(rootPanel);
        System.out.println(rootPanel.getHeight());
        scene = new Scene(panelRoot);
        scene.getStylesheets().add("main.css");
        window.setScene(scene);
        window.sizeToScene();
        window.setFullScreen(false);
        window.setFullScreen(true);

        final ProtocolWindow protocolTemplate;

        if (DEBATE_INFO.protocol == MUNProtocol.UN){
            setupForUN();
            protocolTemplate = new UNWindow(app);
        } else {
            String[] subStates = {
                    UNDebateStates.MODERATED_CAUCUS.getName(),
                    UNDebateStates.UNMODERATED_CAUCUS.getName()
            };
            //staffDisplays.add(new DebateStateDisplay(false, Arrays.asList(subStates), DEBATE_INFO));
            staffDisplays.add(new InfoStaffDisplay(false, SUB_TOPIC.getName(), SUB_TOPIC.getName()));
            protocolTemplate = new OEASWindow(app);
            flashyElements.clear();
            setupForOAES();
        }

        protocolTemplate.endConstruction(design, debateInfo);

        for(TransitionFieldLabel field : staffDisplays) {
            field.attach(column2);
        }
        
        // Logos box in the middle column at the bottom
        HBox logosBox = new HBox();
        
        int imgWidth  = (int) ((column2.getWidth()-10) / 2);
        int imgHeight = 100;
        // 2 images 2 logos
        ImageView vd = new ImageView(ResourceLibrary.ITJ_LOGO);
        vd.setFitWidth(imgWidth);
        vd.setFitHeight(imgHeight);
        ImageView vd2 = new ImageView(ResourceLibrary.TJMUN_LOGO);
        vd2.setFitWidth(imgWidth);
        vd2.setFitHeight(imgHeight);
        logosBox.getChildren().add(vd);
        logosBox.getChildren().add(vd2);
        
        
        column2.getChildren().add(logosBox);
        
        // Calculate position after everything is in place
        Platform.runLater(() -> {
        	Bounds boundsInScene = logosBox.localToScene(logosBox.getBoundsInLocal());
            // Get Desire Y
            int desireY = (int) (window.getHeight() - boundsInScene.getHeight() - 20);
            // Get the offset Needed to Apply
            int offsetY = (int) (desireY  - boundsInScene.getMinY());
            
            // Translate the logos box
            logosBox.setTranslateY(offsetY);
            
        });
        
        
        infoStaff = new InfoStaff(staffDisplays.toArray(new TransitionFieldLabel[staffDisplays.size()]));
        //infoStaff = new InfoStaff(stateDisplay, chairsDisplay, coChairsDisplay, committeeDisplay, topicsDisplay);

        new Thread(() -> {
            infoStaff.loadData();
            File listFile = new File("countries_list.ser");
            if(!listFile.exists()) {
                return;
            }
            try {
                FileInputStream fileIn = new FileInputStream(listFile);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                ArrayList<String> countriesInList = (ArrayList < String >) in.readObject();
                in.close();
                fileIn.close();
                for(String country : countriesInList)
                    countriesQueueList.getItems().add(country);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        window.setOnCloseRequest(event -> {
            event.consume();
            infoStaff.saveData();
            ArrayList<String> items = new ArrayList<>();
            for(String item :countriesQueueList.getItems()) {
                items.add(item);
            }
            try {
                FileOutputStream fileOut = new FileOutputStream("countries_list.ser");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(items);
                out.close();
                fileOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            app.close();
            clock.close();

            protocolTemplate.onCloseProgramm();

            window.close();
        });


    }

    private boolean isRollingState() {
        return debateState.getState() == UNDebateStates.SPEAKERS_LIST;
    }

    private void flashScreen(boolean flash) {
        clock.flashWarning(flash);
        warningsPanel.flashWarning(flash);

        for(Node n : flashyElements) {
            if(flash) {
                n.getStyleClass().remove("flashable-off");
                n.getStyleClass().add("flashable-on");
            } else {
                n.getStyleClass().remove("flashable-on");
                n.getStyleClass().add("flashable-off");
            }
        }

    }

    private void setupForUN() {

    }

    private void setupForOAES() {
        //System.out.println("SetUpOEAS MainWindow");
        //VBox col1 = (VBox)rootPanel.getChildren().get(0);
        //col1.getChildren().remove(2);
        //ClockFrame generalClockFrame = new ClockFrame(col1, APP, false);
        clock.setDefaultAutoplayble(false);
        //generalClockFrame.setAutplayable(false);
    }

    private String getImagePathForCountry(String country) {
        String basePath = APP.getImagesPath() + File.separator + country;
        if (new File(basePath + ".jpg").exists()) {
            basePath += ".jpg";
        } else {
            basePath += ".png";
        }
        //String s = "";
        //s.matches("ac");
        System.out.println(System.getProperty("os.name"));
        //return "file:/" + basePath;
        return "file:" + basePath;
    }

    @Override
    public void cancelWarning() {

    }

    @Override
    public void showWarning(String message) {
        AlertWindow alert = new AlertWindow("Error", message);
        alert.display();
    }
}
