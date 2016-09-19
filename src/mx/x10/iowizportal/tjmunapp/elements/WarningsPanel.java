package mx.x10.iowizportal.tjmunapp.elements;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import mx.x10.iowizportal.tjmunapp.ApplicationCore;
import mx.x10.iowizportal.tjmunapp.utils.CountrySelector;
import mx.x10.iowizportal.tjmunapp.utils.ResourceLibrary;
import mx.x10.iowizportal.tjmunapp.utils.WarningRecorder;
import mx.x10.iowizportal.tjmunapp.utils.WarningsRegistry;
import mx.x10.iowizportal.tjmunapp.windows.WarningWindow;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by JJOL on 17/10/2015.
 */
public class WarningsPanel extends MainWindowElement{

    private final static String DEFAULT_TAB_STYLE = "";
    //String da = "-fx-"
    private final static String WARNING_TAB_STYLE = "-fx-background-color: #304ea4;";
    private final static String EMPTY_TAB_STYLE   = "-fx-background-color: #5A9674;";

    private String countryViewed;
    private Tab[]   paneTabs = new Tab[WarningsRegistry.MAX_WARNING_COUNT];
    private TabPane warningPane = new TabPane();
    private VBox delaysDisplay;
    private final WarningRecorder RECORDER;
    private final Control[] elementsToFlash;

    public WarningsPanel(Pane parent, final ApplicationCore app, final CountrySelector countrySelector) {
        RECORDER = app.getWarningRecorder();
        
        // Set GUI
        // Bottom Warnging Section
        // Groups
        StackPane root = new StackPane();
        HBox warningContent = new HBox(10);
        VBox warningDisplayColumn = new VBox(10);
        VBox searchColumn = new VBox(20);
        HBox controlsRow = new HBox(10);
        HBox delayControlsRow = new HBox(5);
        VBox delayPanel = new VBox(5);
        delaysDisplay = new VBox(10);
        delaysDisplay.setPrefHeight(100);
        delaysDisplay.setMaxHeight(100);
        delaysDisplay.setMinHeight(100);
        // Control
        Button saveWarningBtn = new Button("", ResourceLibrary.getTinyIcon(ResourceLibrary.CHECK_ICON));
        Button removeWarningBtn = new Button("", ResourceLibrary.getTinyIcon(ResourceLibrary.CROSS_ICON));
        Button warningWindowBtn = new Button("Warning Window");

        Button delayAddBtn = new Button("", ResourceLibrary.getTinyIcon(ResourceLibrary.PLUS_ICON));
        Button delayRmvBtn = new Button("", ResourceLibrary.getTinyIcon(ResourceLibrary.CROSS_ICON));
        // Displays
        Label delaysLabel = new Label("Delays:");

        // Core
        warningPane = new TabPane();
        warningPane.setPrefHeight(50);
        warningPane.setMinHeight(50);
        //warningPane.setMaxHeight(50);
        paneTabs = new Tab[WarningsRegistry.MAX_WARNING_COUNT];
        
        // Create warning default tabs
        for(int i=0; i < paneTabs.length; i++) {
            paneTabs[i] = new Tab("Warning " + (i+1));
            paneTabs[i].setClosable(false);
            paneTabs[i].setContent(new Label(WarningsRegistry.invalidCommentResponse(i+1)));
            paneTabs[i].setStyle(DEFAULT_TAB_STYLE);
            warningPane.getTabs().add(paneTabs[i]);
        }

        elementsToFlash = CountrySearcher.countrySearcher(countrySelector, searchColumn, new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null) {
                    //TODO Prevent Duplicate Keys and Fix Selection Bugs
                    viewCountry(newValue);
                }
            }
        }, RECORDER);

        warningPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
                //Node content = paneTabs[warningPane.getSelectionModel().getSelectedIndex()].getContent();
                //TODO Change Tab Color to a darkish color

                /*String selectedOldStyle = ResourceLibrary.valueFromStyle(oldTab.getStyle());
                int oldTabColor = Integer.parseInt(selectedOldStyle, 16);
                oldTabColor = oldTabColor - 0x101010;
                */
                //oldTab.setStyle("-fx-background-color: #" + String.valueOf(oldTabColor) + ";");

                String normalNewStyle = ResourceLibrary.valueFromStyle(newTab.getStyle());
                System.out.println("Old Style: #" + normalNewStyle);
                int newTabColor = Integer.parseInt(normalNewStyle, 16);
                newTabColor = newTabColor + 0xAAA;
                System.out.println("New Style: #" + String.valueOf(newTabColor));
                //newTab.setStyle("-fx-background-color: #" + String.valueOf(newTabColor) + ";");
                System.out.println(newTab.getStyle());
            }


        });

        ArrayList<String> ad = new ArrayList<>();
        ad.clear();
        saveWarningBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /*int warningIndex = warningPane.getSelectionModel().getSelectedIndex();
                paneTabs[warningIndex].setStyle(WARNING_TAB_STYLE);
                WarningsRegistry registry = RECORDER.getWarningRegistryForCountry(countryViewed);
                registry.addComment(warningIndex+1, getCommentInCurrentTab());*/
                saveCurrentWarning();
            }
        });

        delayAddBtn.setOnMouseClicked(event -> {
            if(countryViewed == null || RECORDER.getWarningRegistryForCountry(countryViewed).getTards().size() > 2)
                return;
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("E-hh:mm a");
            String[] dateFormatted = dateFormat.format(date).split("-");
            Label timeLabel = new Label(dateFormatted[0] + " - " + dateFormatted[1]);
            timeLabel.setFont(Font.font("Lucida Sans", FontWeight.BOLD, 14));
            timeLabel.setTextFill(Color.DARKRED);
            delaysDisplay.getChildren().add(timeLabel);
            RECORDER.getWarningRegistryForCountry(countryViewed).addRetard(timeLabel.getText());
            RECORDER.markCountryRegistry(countryViewed);
        });

        delayRmvBtn.setOnMouseClicked(event -> {
            if(countryViewed == null)
                return;
            if(!delaysDisplay.getChildren().isEmpty()) {
                delaysDisplay.getChildren().remove(delaysDisplay.getChildren().size() - 1);
                RECORDER.getWarningRegistryForCountry(countryViewed).removeLastRetard();
                RECORDER.markCountryRegistry(countryViewed);
            }
        });

        removeWarningBtn.setOnMouseClicked( event -> removeCurrentWarning());

        warningWindowBtn.setOnMouseClicked( event -> new WarningWindow("TJMun Warnings", app));

        delayControlsRow.getChildren().addAll(delayAddBtn, delayRmvBtn);

        controlsRow.getChildren().addAll(saveWarningBtn, removeWarningBtn, warningWindowBtn);
        delayPanel.getChildren().addAll(delaysLabel, delayControlsRow, delaysDisplay);
        searchColumn.setPadding(new Insets(20,0,0,0));
        warningDisplayColumn.getChildren().addAll(warningPane, controlsRow, delayPanel, searchColumn);
        //root.getChildren().addAll(searchColumn,warningDisplayColumn);

        warningContent.getChildren().addAll(warningDisplayColumn);
        parent.getChildren().add(warningContent);
    }

    private String getCommentInCurrentTab() {
        Node content = paneTabs[warningPane.getSelectionModel().getSelectedIndex()].getContent();
        if(content instanceof TextField)
            return ((TextField) content).getText();
        else
            return null;
    }

    public void viewCountry(String countryName) {
        if(Objects.equals(countryName, countryViewed)) // Don't update anything if the country is the same as before
            return;
        countryViewed = countryName;
        loadWarnings();
        loadTardMarks();
    }

    private void loadWarnings() {
        WarningsRegistry registry = RECORDER.getWarningRegistryForCountry(countryViewed);
        warningPane.getSelectionModel().selectFirst();
        //if(warningPane.getSelectionModel().getSelectedIndex() != 0)
        for(int i=0; i < WarningsRegistry.MAX_WARNING_COUNT; i++) {
            String comment = registry.getWarningComment(i+1);
            if (comment == null) {
                comment = WarningsRegistry.invalidCommentResponse(i+1);
                paneTabs[i].setStyle(EMPTY_TAB_STYLE);
            } else {
                paneTabs[i].setStyle(WARNING_TAB_STYLE);
            }
            TextField commentDisplay = new TextField(comment);
            commentDisplay.setMinHeight(10);
            paneTabs[i].setContent(commentDisplay);
        }
    }

    private void loadTardMarks() {
        delaysDisplay.getChildren().clear();
        WarningsRegistry registry = RECORDER.getWarningRegistryForCountry(countryViewed);

        for(String tard : registry.getTards()) {
            Label tardLabel = new Label(tard);
            tardLabel.setFont(Font.font("Lucida Sans", FontWeight.BOLD, 14));
            tardLabel.setTextFill(Color.DARKRED);
            delaysDisplay.getChildren().add(tardLabel);
        }
    }

    private void saveCurrentWarning() {
        int warningIndex = warningPane.getSelectionModel().getSelectedIndex();
        paneTabs[warningIndex].setStyle(WARNING_TAB_STYLE);
        RECORDER.updateWarning(countryViewed, warningIndex+1, getCommentInCurrentTab());
    }

    public void removeCurrentWarning() {
        int warningIndex = warningPane.getSelectionModel().getSelectedIndex();
        paneTabs[warningIndex].setStyle(EMPTY_TAB_STYLE);
        RECORDER.updateWarning(countryViewed, warningIndex+1, null);
    }

    @Override
    public void flashWarning(boolean turnOn) {
        for(Control e : elementsToFlash) {
            if(turnOn) {
                e.getStyleClass().remove("flashable-off");
                e.getStyleClass().add("flashable-on");
            } else {
                e.getStyleClass().remove("flashable-on");
                e.getStyleClass().add("flashable-off");
            }
        }
    }


    public static class WarningCountryCell extends ListCell<String> {

        private final WarningRecorder recorder;

        public WarningCountryCell(WarningRecorder recorder) {
            this.recorder = recorder;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(new Label(item));
            if(recorder.getWarnedCountries().contains(item)) {
                setStyle(WARNING_TAB_STYLE + "-fx-color: white;");
            } else setStyle(DEFAULT_TAB_STYLE);
        }
    }

}
