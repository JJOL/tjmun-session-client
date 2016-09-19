package mx.x10.iowizportal.tjmunapp.elements;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import mx.x10.iowizportal.tjmunapp.utils.CommitteeInfo;

import java.util.List;

/**
 * Created by JJOL on 12/10/2015.
 */
public class ConfigPanel {

    public ConfigPanel(List<CommitteeInfo> committees, Pane root) {

        TabPane commitePanel = new TabPane();
        Tab tabView = new Tab();
        tabView.setClosable(false);
        tabView.setText("View");

        VBox committeesDisplay = new VBox(10);
        for(CommitteeInfo committee : committees) {
            committeesDisplay.getChildren().add(new Label(committee.getName()));
        }

        tabView.setContent(commitePanel);
        Tab tabCreate = new Tab();
        tabCreate.setClosable(false);
        tabCreate.setText("Set");

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
        tabCreate.setContent(createForm);
        commitePanel.getTabs().addAll(tabView, tabCreate);


    }

}
