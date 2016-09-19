package mx.x10.iowizportal.tjmunapp.windows;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import mx.x10.iowizportal.tjmunapp.ApplicationCore;
import mx.x10.iowizportal.tjmunapp.elements.*;
import mx.x10.iowizportal.tjmunapp.tasks.Timer;
import mx.x10.iowizportal.tjmunapp.utils.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JJOL on 29/08/2015.
 */
public class TimerWindow implements AppWindow, TimeResponsive {

    public final static String FLASH_STYLE_COLOUR = "#FF0000";
    private final List<String> debateStates = new ArrayList<>();
    /*{
        for(UNDebateStates state : UNDebateStates.values())
            debateStates.add(state.name);
    }*/

    private final DebateInfo DEBATE_INFO;
    private final ApplicationCore APP;
    private final CommitteeInfo COMMITTEE_INFO;

    private final Scene scene;

    private Timer clock1, clock2;
    private boolean flag = true;

    private final StackPane panelRoot;
    private final HBox      rootPanel;
    private Stage window;

    public TimerWindow(final Stage window, final ApplicationCore app, DebateInfo debateInfo, CommitteeInfo committeeInfo) {

        this.window = window;
        APP = app;
        APP.setCurrentWindow(this);
        DEBATE_INFO = debateInfo;
        COMMITTEE_INFO = committeeInfo;
        ApplicationCore.APP_LAN = DEBATE_INFO.language;
        panelRoot = new StackPane();
        rootPanel = new HBox(100);
        rootPanel.setId("base-root");

        Label a = new Label();
        Label b = new Label();
        a.setFont(Font.font("Tahoma", FontWeight.BOLD, 36));
        b.setFont(Font.font("Tahoma", FontWeight.BOLD, 36));

        clock1 = new Timer(a, this);
        clock2 = new Timer(b, this);

        clock1.setActiveTime(45, 0);
        clock2.setActiveTime(15, 0);
        clock1.setCanStartCountdown(true);
        clock2.setCanStartCountdown(true);
        clock1.startTimer();

        Label label = new Label("P\u00E9rr\u00F3 Cal\u00EDente");

        label.setFont(Font.font(36));
        label.setFont(Font.font("Trebuchet MS", FontWeight.BOLD, 36));

        rootPanel.getChildren().addAll(a, b, label);
        panelRoot.getChildren().addAll(rootPanel);
        System.out.println(rootPanel.getHeight());
        scene = new Scene(panelRoot);
        scene.getStylesheets().add("main.css");
        window.setScene(scene);
        window.sizeToScene();
        window.setFullScreen(false);
        window.setFullScreen(true);




    }

    private void setupForUN() {

    }

    private void setupForOAES() {
        VBox col1 = (VBox)rootPanel.getChildren().get(0);
        col1.getChildren().remove(2);
        ClockFrame generalClockFrame = new ClockFrame(col1, APP, true);
    }

    private String getImagePathForCountry(String country) {
        String basePath = APP.getImagesPath() + File.separator + country;
        if (new File(basePath + ".jpg").exists()) {
            basePath += ".jpg";
        } else {
            basePath += ".png";
        }
        return "file:/" + basePath;
    }

    @Override
    public void cancelWarning() {

    }

    @Override
    public void showWarning(String message) {
        AlertWindow alert = new AlertWindow("Error", message);
        alert.display();
    }

    @Override
    public void onTimeStateChanged(TimeEvent event) {
        if(event == TimeEvent.ENDED) {
            if(flag) {
                clock2.setActiveTime(15, 0);
                clock2.startTimer();
            } else {
                clock1.setActiveTime(45, 0);
                clock1.startTimer();
            }
            Platform.runLater(() -> window.show());
            flag = !flag;
        }
    }
}
