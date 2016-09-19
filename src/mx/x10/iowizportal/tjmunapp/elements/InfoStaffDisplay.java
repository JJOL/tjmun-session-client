package mx.x10.iowizportal.tjmunapp.elements;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import mx.x10.iowizportal.tjmunapp.utils.ResourceLibrary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JJOL on 05/11/2015.
 */
public class InfoStaffDisplay extends TransitionFieldLabel {

    private List<String> chairNames = new ArrayList<>();

    private final String title;
    private final String defaultPromptText;

    public InfoStaffDisplay(boolean clickCloseable, String title, String defaultPromptText) {
        super(clickCloseable);
        this.title = title;
        this.defaultPromptText = defaultPromptText;
        setReady();
    }

    @Override
    protected void constructShowDisplay() {
        clearWrapper();
        VBox chairNamesList = new VBox(5);
        Label titleLabel = new Label(title+":");
        titleLabel.setMinWidth(ResourceLibrary.CENTRAL_COLUMN_WIDTH);
        titleLabel.setFont(Font.font("Trebuchet MS", FontWeight.BOLD, 24));
        titleLabel.setAlignment(Pos.CENTER);
        chairNamesList.getChildren().add(titleLabel);
        for(String chair : chairNames) {
            Label nameLabel = new Label(chair);
            nameLabel.setFont(Font.font(18));
            nameLabel.setMinWidth(ResourceLibrary.CENTRAL_COLUMN_WIDTH);
            nameLabel.setAlignment(Pos.CENTER);
            chairNamesList.getChildren().add(nameLabel);
        }
        addToWrapper(chairNamesList);
    }

    @Override
    protected void constructFieldDisplay() {
        clearWrapper();
        VBox content = new VBox(10);
        Label titleLabel = new Label(title+":");
        titleLabel.setMinWidth(ResourceLibrary.CENTRAL_COLUMN_WIDTH);
        titleLabel.setFont(Font.font("Lucida Sans", FontWeight.BOLD, 24));
        titleLabel.setAlignment(Pos.CENTER);
        VBox chairNamesList = new VBox(5);
        for(String name : chairNames)
            createPair(name, chairNamesList);
        Button addBtn = new Button("", ResourceLibrary.getTinyIcon(ResourceLibrary.PLUS_ICON));
        addBtn.setOnMouseClicked(event ->
            createPair(null, chairNamesList)
        );
        HBox controls = new HBox();
        controls.getChildren().addAll(addBtn, getCloseButton());
        controls.setMinWidth(ResourceLibrary.CENTRAL_COLUMN_WIDTH);
        controls.setAlignment(Pos.CENTER);
        content.getChildren().addAll(titleLabel, chairNamesList, controls);

        wrapper.setOnKeyPressed(event -> {
            if(event.getCode() != KeyCode.ENTER)
                return;
            trigger();
        });

        addToWrapper(content);
    }

    private void createPair(String name, VBox chairList) {
        HBox pair = new HBox();
        final TextField chairField = new TextField(name);
        chairField.setPromptText(defaultPromptText);
        Button deleteBtn = new Button();
        ImageView iconView = new ImageView(ResourceLibrary.CROSS_ICON);
        iconView.setFitWidth(15);
        iconView.setFitHeight(15);
        deleteBtn.setGraphic(iconView);
        if(name == null) {
            deleteBtn.setOnMouseClicked(event -> {
                chairList.getChildren().remove(pair);
            });
        } else {
            deleteBtn.setOnMouseClicked(event -> {
                chairNames.remove(name);
                chairList.getChildren().remove(pair);
            });
        }
        pair.getChildren().addAll(chairField, deleteBtn);
        pair.setMinWidth(ResourceLibrary.CENTRAL_COLUMN_WIDTH);
        pair.setAlignment(Pos.CENTER);
        chairList.getChildren().add(pair);
    }

    @Override
    protected void saveFields() {

        VBox chairList = (VBox)((VBox) wrapper.getChildren().get(0)).getChildren().get(1);
        chairNames.clear();
        for(int i = 0; i < chairList.getChildren().size(); i++) {
            HBox pair = (HBox) chairList.getChildren().get(i);
            String name = ((TextField)pair.getChildren().get(0)).getText();
            if(name != null && !name.isEmpty() && !name.trim().isEmpty()) {
                chairNames.add(name);
            }
        }

    }

    @Override
    public Object save() {
        return chairNames;
    }

    @Override
    public void load(Object data) {
        chairNames = (List<String>) data;
        Platform.runLater(() -> constructShowDisplay());
    }


}
