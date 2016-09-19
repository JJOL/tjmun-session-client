package mx.x10.iowizportal.tjmunapp.elements;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import mx.x10.iowizportal.tjmunapp.utils.DebateInfo;
import mx.x10.iowizportal.tjmunapp.utils.ResourceLibrary;
import mx.x10.iowizportal.tjmunapp.utils.consts.MUNProtocol;
import mx.x10.iowizportal.tjmunapp.utils.consts.OEASDebateStates;
import mx.x10.iowizportal.tjmunapp.utils.consts.UNDebateStates;

import javax.crypto.spec.OAEPParameterSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by JJOL on 05/11/2015.
 */
public class DebateStateDisplay extends TransitionFieldLabel {

    private final DebateInfo info;

    private List<String> debateStates = new ArrayList<>();
    /*static {
        for(UNDebateStates state : UNDebateStates.values())
            debateStates.add(state.name);
    }*/
    private String activeState; //= debateStates.get(0);

    private Spinner<String> stateSpinner;

    public DebateStateDisplay(boolean b, List<String> states, DebateInfo info) {
        super(b);
        this.info = info;
        debateStates = states;
        activeState = debateStates.get(0);
        stateSpinner = new Spinner<>();
        SpinnerValueFactory.ListSpinnerValueFactory<String> valueFactory = new SpinnerValueFactory.ListSpinnerValueFactory<>(FXCollections.observableList(debateStates));
        //setSpinnerInValue(valueFactory, activeState);
        stateSpinner.setValueFactory(valueFactory);
        stateSpinner.setMinWidth(ResourceLibrary.CENTRAL_COLUMN_WIDTH);

        setReady();
    }

    @Override
    protected void constructShowDisplay() {

        clearWrapper();
        Label defaultTimeLabel = new Label(activeState);
        defaultTimeLabel.setFont(Font.font("Trebuchet MS", FontWeight.BOLD, 24));
        defaultTimeLabel.setMinWidth(ResourceLibrary.CENTRAL_COLUMN_WIDTH);
        defaultTimeLabel.setAlignment(Pos.CENTER);
        addToWrapper(defaultTimeLabel);
    }

    @Override
    protected void constructFieldDisplay() {
        clearWrapper();
        /*Spinner<String> stateSpinner = new Spinner<>();
        SpinnerValueFactory.ListSpinnerValueFactory<String> valueFactory = new SpinnerValueFactory.ListSpinnerValueFactory<>(FXCollections.observableList(debateStates));
        setSpinnerInValue(valueFactory, activeState);
        stateSpinner.setValueFactory(valueFactory);*/

        addAllToWrapper(stateSpinner, getCloseButton());
    }

    @Override
    protected void saveFields() {
        activeState = ((Spinner<String>)wrapper.getChildren().get(0)).getValue();
    }

    @Override
    public Object save() {
        //return activeState;
        if(info.protocol == MUNProtocol.UN) {
            for (UNDebateStates state : UNDebateStates.values()) {
                if (Objects.equals(state.getName(), activeState))
                    return state;
            }
            return UNDebateStates.SPEAKERS_LIST;
        } else {
            for (OEASDebateStates state : OEASDebateStates.values()) {
                if (Objects.equals(state.getName(), activeState))
                    return state;
            }
            return OEASDebateStates.PRESENTATION;
        }
        //return null;
    }

    @Override
    public void load(Object data) {
        //activeState = (String)data;
        if(info.protocol == MUNProtocol.UN) {
            UNDebateStates state = (UNDebateStates) data;
            activeState = state.getName();
        } else {
            OEASDebateStates state = (OEASDebateStates) data;
            activeState = state.getName();
        }
        Platform.runLater(() -> constructShowDisplay());
    }

    public UNDebateStates getState() {
        for (UNDebateStates state : UNDebateStates.values()) {
            if (Objects.equals(state.getName(), activeState))
                return state;
        }
        return UNDebateStates.SPEAKERS_LIST;
    }

    /*private void setSpinnerInValue(SpinnerValueFactory.ListSpinnerValueFactory<String> options, String value) {
        // Get Absolute Position of the Value in the general List
        int i = 0;
        for(; i < debateStates.size(); i++) {
            if (debateStates.get(i) == value)
                break;
        }
        int j = 0;
        // No point at doing this operation as we know it will always start in 0
        *//*for(; j < debateStates.size(); j++) {
            if (debateStates.get(j) == options.getValue())
                break;
        }*//*

        // Decrement or increment as needed




        if(i < j)
            options.decrement(j - i);
        else if(i > j)
            options.increment(i - j);
    }*/
}