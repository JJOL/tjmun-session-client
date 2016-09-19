package mx.x10.iowizportal.tjmunapp.elements;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import mx.x10.iowizportal.tjmunapp.utils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by JJOL on 18/09/2015.
 */
public class CountrySearcher {

    //final CountrySelector selector;
    private final static String SHADOW_BOX = "-fx-effect: dropshadow(three-pass-box, blue, 10, 0, 0, 0);";


    public static Control[] countrySearcher(final CountrySelector selector, final Pane parent, final ChangeListener<String> updater, final WarningRecorder recorder) {
        //this.selector = selector;

        final BooleanMutable selected = new BooleanMutable(false);
        final TextField inputField = new TextField();
        inputField.setPromptText("Select Country");
        final ListView<String> optionSelector = new ListView<>();
        if(recorder != null) {
            optionSelector.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
                @Override
                public ListCell<String> call(ListView<String> param) {
                    return new WarningsPanel.WarningCountryCell(recorder);
                }
            });
        }

        optionSelector.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue && !selected.getValue()) {
                    optionSelector.setItems(FXCollections.observableArrayList(new ArrayList<String>()));
                } else if (!newValue) {
                    selected.setValue(false);
                }
                else{
                    selected.setValue(true);;
                }
            }
        });

        // Respond to Type Search
        inputField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String search = inputField.getText();
                //System.out.println(search);
                List<String> countryOptions = selector.getCountries(search);
                Collections.sort(countryOptions, new AlphabetOrder());
                ObservableList<String> optionsList = FXCollections.observableArrayList(countryOptions);
                optionSelector.setItems(optionsList);
            }
        });

        inputField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue && !selected.getValue()) {
                    optionSelector.setItems(FXCollections.observableArrayList(new ArrayList<String>()));
                } else if (!newValue) {
                    selected.setValue(false);
                }
                else{
                    selected.setValue(true);;
                }
            }
        });

        optionSelector.getStyleClass().add("flashable-off");
        inputField.getStyleClass().add("flashable-off");
        /*optionSelector.setStyle(ResourceLibrary.SHADOW_BOX);
        inputField.setStyle(ResourceLibrary.SHADOW_BOX);*/

        // Respond to Item Selected in Suggestions
        optionSelector.getSelectionModel().selectedItemProperty().addListener(updater);

        parent.getChildren().addAll(inputField, optionSelector);

        return new Control[] {optionSelector, inputField};
    }

}
