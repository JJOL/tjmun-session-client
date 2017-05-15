package mx.x10.iowizportal.tjmunapp.windows;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import mx.x10.iowizportal.tjmunapp.ApplicationCore;
import mx.x10.iowizportal.tjmunapp.listeners.DatabaseStateDependent;
import mx.x10.iowizportal.tjmunapp.utils.CommitteeInfo;
import mx.x10.iowizportal.tjmunapp.utils.DebateInfo;
import mx.x10.iowizportal.tjmunapp.utils.consts.DBState;
import mx.x10.iowizportal.tjmunapp.utils.consts.Language;
import mx.x10.iowizportal.tjmunapp.utils.consts.MUNProtocol;
import mx.x10.iowizportal.tjmunapp.utils.SQLiteHelper;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by JJOL on 20/08/2015.
 */
public class WelcomeScreen implements AppWindow, DatabaseStateDependent{

    private Label warningSection;
    private GridPane rootPane;

    private final DatabaseStateDependent listener;
    private Map<String, CommitteeInfo> cInfoMap = new HashMap<>();
    
    public WelcomeScreen(final Stage window, final ApplicationCore app) {

        listener = this;
        app.subscribeDatabaseStateListener(listener);
        window.setTitle("TJMun Manager");
        rootPane = new GridPane();
        rootPane.setAlignment(Pos.CENTER);
        rootPane.setVgap(10);
        rootPane.setHgap(10);
        rootPane.setPadding(new Insets(25, 25, 25, 25));

//       HBox commitePanel = new HBox(20);

        TabPane commitePanel = new TabPane();
        Tab tabView = new Tab();
        tabView.setClosable(false);
        tabView.setText("Committee Info");

        tabView.setContent(new Rectangle(200, 200, Color.LIGHTSTEELBLUE));
        /*Tab tabCreate = new Tab();
        tabCreate.setClosable(false);
        tabCreate.setText("Set");*/

        VBox createForm = new VBox();
        HBox inputArea = new HBox(30);

        ComboBox choseField = new ComboBox();
        ObservableList<String> inOptions = FXCollections.observableArrayList("Name", "Color", "President", "Secretary", "UnderSecretary", "Chair", "Moderator");
        choseField.setItems(inOptions);
        choseField.getSelectionModel().select(0);
        TextField nameField = new TextField();
        nameField.setPromptText("Vale");
        inputArea.getChildren().addAll(choseField, nameField);

        HBox controls = new HBox(5);
        Button setBtn = new Button("Set");
        Button saveBtn = new Button("Done");
        controls.getChildren().addAll(setBtn, saveBtn);

        createForm.getChildren().addAll(inputArea, controls);
        //tabCreate.setContent(createForm);
        commitePanel.getTabs().addAll(tabView);





        //committeeCreatorPanel.getChildren().addAll();

        VBox launchWrapperSection = new VBox();
        HBox launchSection = new HBox(6);
        Button startButton = new Button("Start");
        final ComboBox<String> protocolSelect = new ComboBox<>(FXCollections.observableArrayList("UN", "OAS"));
        protocolSelect.getSelectionModel().select(0);

        protocolSelect.setPrefWidth(147);
        protocolSelect.setMaxWidth(147);
        protocolSelect.setMinWidth(147);

        List<String> langs = new ArrayList<>();
        for(Language language : Language.values()) {
            langs.add(language.toString());
        }
        final ComboBox<String> languageSelect = new ComboBox<>(FXCollections.observableArrayList(langs));
        languageSelect.setPromptText(Language.ENGLISH.toString());
        languageSelect.getSelectionModel().selectFirst();

        languageSelect.setPrefWidth(147);
        languageSelect.setMaxWidth(147);
        languageSelect.setMinWidth(147);

        startButton.setPrefWidth(300);
        startButton.setMaxWidth(300);
        startButton.setMinWidth(300);

        launchSection.getChildren().addAll(protocolSelect, languageSelect);
        launchWrapperSection.getChildren().addAll(launchSection, startButton);
        //Button warningsButton = new Button("Warnings");
        warningSection = new Label();

        VBox box = new VBox(20);


        HBox otherOptionsPanel = new HBox();
        Button IMGSConfigButton = new Button("Select Images Folder");
        IMGSConfigButton.setAlignment(Pos.BOTTOM_LEFT);
        Button resetApp = new Button("Reset");
        resetApp.setAlignment(Pos.BOTTOM_CENTER);
        Button warningPanelButton = new Button("Warnings");
        warningPanelButton.setAlignment(Pos.BOTTOM_RIGHT);
        otherOptionsPanel.getChildren().addAll(IMGSConfigButton, resetApp, warningPanelButton);
        box.getChildren().addAll(commitePanel, launchWrapperSection, warningSection,  otherOptionsPanel);


        IMGSConfigButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser folderSelector = new DirectoryChooser();
                folderSelector.setTitle("Select Flags Folder");
                File folder = folderSelector.showDialog(window);
                if(folder == null || !folder.isDirectory() || !folder.exists()) {
                    app.showWarning("No Valid Folder Found!");
                    return;
                }
                app.showWarning("Valid Folder selected...");
                app.setFlagsFolder(folder);
            }
        });

        resetApp.setOnAction((event) -> {
            // Erase Files, Database
            app.resetApp();
        });

        warningPanelButton.setOnAction(event -> {
            new WarningWindow("TJMun Warnings", app);
        });


        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!app.isDataReadyToUse()) {
                    app.showWarning("Database Is Not Ready Yet!");
                    return;
                }
                final MUNProtocol protocol = (protocolSelect.getSelectionModel().getSelectedItem().charAt(0) == 'U') ?
                        MUNProtocol.UN : MUNProtocol.OAS;
                app.unsubscribeDatabaseStateListener(listener);
                CommitteeInfo info = new CommitteeInfo("Topic A", 0);
                DebateInfo debateInfo = new DebateInfo();
                debateInfo.protocol = (protocolSelect.getSelectionModel().getSelectedItem().charAt(0) == 'U') ?
                        MUNProtocol.UN : MUNProtocol.OAS;
                debateInfo.language = (languageSelect.getSelectionModel().getSelectedItem().charAt(0) == 'E') ? Language.ENGLISH : Language.SPANISH;
                System.out.println(debateInfo.language.toString());
                new MainWindow(window, app, debateInfo, info);
            }
        });


        //rootPane.setGridLinesVisible(true);
        Scene scene = new Scene(box, 300, 275);
        /*scene.getStylesheets().add(
                WelcomeScreen.class.getResource("style.css").toExternalForm()
        );*/
        window.setScene(scene);
        //window.setFullScreen(true);
        window.show();
    }








    @Override
    public void cancelWarning() {
        warningSection.setText("");
    }

    @Override
    public void showWarning(final String message) {
        warningSection.setText("Attention: " + message + "!");
    }

    @Override
    public void onDatabaseStateChange(DBState state) {
        if(state != DBState.READY)
            return;

        // Load Committees
        SQLiteHelper sqlHelper = new SQLiteHelper();
        Connection conn = sqlHelper.openConnection();
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM " + SQLiteHelper.STAFF_TABLE); //TODO Change Staff Saving Model To Make it more Flexible
            ResultSet results = statement.executeQuery();
            while(results.next()) {

                CommitteeInfo committeeInfo = new CommitteeInfo(results.getString("topic_name"), results.getInt(0));
                //

                //
                cInfoMap.put(committeeInfo.getName(), committeeInfo);
            }
            results.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        sqlHelper.closeConnection();
    }
}

/*
* LOG RECORD VIEW
* */

/*
CREATE / EDIT COMMITTEES
CREATE VIEW
SEE CHECK VIEW (SEE C PROPERTIES, EDIT and DELETE)
* */

//      |||
//      VVV

/*  START APP NOW
// SET COMMITTEE FROM PREVIOUSLY SAVED ONES
// Select UN or OAS
*/