package mx.x10.iowizportal.tjmunapp.windows;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mx.x10.iowizportal.tjmunapp.ApplicationCore;
import mx.x10.iowizportal.tjmunapp.utils.AlphabetOrder;
import mx.x10.iowizportal.tjmunapp.utils.WarningRecorder;
import mx.x10.iowizportal.tjmunapp.utils.WarningsRegistry;

import java.util.*;

/**
 * Created by JJOL on 21/08/2015.
 */
public class WarningWindow {

    public WarningWindow(String group, final ApplicationCore app) {

        final Stage window = new Stage();
        window.setTitle("Warnings");
        window.initModality(Modality.APPLICATION_MODAL);
        final Scene scene;
        final VBox dirList = new VBox(20);

        Label groupNameLabel = new Label(group);
        //groupNameLabel
        dirList.getChildren().add(groupNameLabel);
        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                window.close();
            }
        });
        dirList.getChildren().add(closeBtn);
        dirList.getChildren().add(new Label("Please Wait A Moment..."));

        if (app.isDataReadyToUse()) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    WarningRecorder recorder = app.getWarningRecorder();
                    List<String> countries = new ArrayList<>(recorder.getWarnedCountries());
                    Collections.sort(countries, new AlphabetOrder());

                    final ScrollPane pane = new ScrollPane();
                    final VBox countryBox = new VBox(25);
                    countryBox.setMaxWidth(400);

                    for (String country : countries) {
                        WarningsRegistry registry = recorder.getWarningRegistryForCountry(country);
                        //String name = recorder.getWarningRegistryForCountry(country);
                        int count = registry.getWarningCount();
                        //TODO Display Countrys name as TextLabel
                        //TODO Display a small Warnings Amount Indicator
                        final Label name = new Label(country);

                        //name.setPadding(new Insets(0,0,0,50));
                        name.setAlignment(Pos.CENTER);
                        name.setFont(Font.font("Tahoma", FontWeight.BOLD, 24));
                        Label warningsDis = new Label("Warnings ("+count+") : ");
                        warningsDis.setPadding(new Insets(0,0,0,50));
                        warningsDis.setAlignment(Pos.CENTER);
                        warningsDis.setFont(Font.font("Trebuchet MS", FontWeight.BOLD, 24));
                        countryBox.getChildren().add(name);
                        countryBox.getChildren().add(warningsDis);
                        VBox warns = new VBox(10);
                        for (int i = 0; i < count; i++) {
                            //TODO Display Each Warning below the previous one
                            String commText = registry.getWarningComment(i + 1);
                            if (commText != null && !commText.isEmpty()) {
                                Label comment = new Label(commText);
                                comment.setWrapText(true);
                                comment.setFont(Font.font("Arial", FontPosture.REGULAR, 18));
                                warns.getChildren().add(comment);
                            }
                        }
                        warns.setPadding(new Insets(0,0,0,60));
                        countryBox.getChildren().add(warns);

                        List<String> tards = registry.getTards();
                        Label tardMaksDis = new Label("Tardy ("+tards.size()+") :");
                        tardMaksDis.setPadding(new Insets(0,0,0,50));
                        tardMaksDis.setAlignment(Pos.CENTER);
                        tardMaksDis.setFont(Font.font("Trebuchet MS", FontWeight.BOLD, 24));
                        countryBox.getChildren().add(tardMaksDis);
                        VBox tardsList = new VBox(10);
                        for (String tardMark : tards) {
                            Label tardLabel = new Label(tardMark);
                            tardLabel.setWrapText(true);
                            tardLabel.setFont(Font.font("Arial", FontPosture.REGULAR, 18));
                            tardsList.getChildren().add(tardLabel);
                        }
                        tardsList.setPadding(new Insets(0,0,0,60));
                        countryBox.getChildren().add(tardsList);

                    }
                    pane.setContent(countryBox);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            dirList.getChildren().remove(2);
                            dirList.getChildren().add(pane);
                        }
                    });
                }
            }).start();
        } else {
            dirList.getChildren().add(new Label("Database Is not Ready Yet!"));
        }

        scene = new Scene(dirList, 640, 480);
        window.setScene(scene);
        window.setFullScreen(true);
        window.showAndWait();
    }
}