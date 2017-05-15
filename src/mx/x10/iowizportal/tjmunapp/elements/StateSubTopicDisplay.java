package mx.x10.iowizportal.tjmunapp.elements;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import mx.x10.iowizportal.tjmunapp.utils.consts.MUNProtocol;
import mx.x10.iowizportal.tjmunapp.utils.consts.OASDebateStates;
import mx.x10.iowizportal.tjmunapp.utils.consts.UNDebateStates;

import static mx.x10.iowizportal.tjmunapp.utils.consts.StaffPosition.SUB_TOPIC;

/**
 * Created by JJOL on 10/11/2015.
 */
public class StateSubTopicDisplay extends TransitionFieldLabel {
    
    Spinner<String> selectionSpinner;


    public StateSubTopicDisplay(boolean clickCloseable) {
        super(clickCloseable);
        ObservableList<String> listOfStates = FXCollections.observableArrayList(
                UNDebateStates.MODERATED_CAUCUS.getName(),
                UNDebateStates.UNMODERATED_CAUCUS.getName()
        );
        SpinnerValueFactory<String> values = new SpinnerValueFactory.ListSpinnerValueFactory<>(listOfStates);
        selectionSpinner = new Spinner<>(values);
        setReady();
    }

    @Override
    protected void constructShowDisplay() {
        /*
        InfoStaffDisplay topicsDisplay = new InfoStaffDisplay(false, SUB_TOPIC.getName(), SUB_TOPIC.getName());
        InfoStaffDisplay*/
    }

    @Override
    protected void constructFieldDisplay() {
        wrapper.getChildren().remove(0);
    }

    @Override
    protected void saveFields() {

    }

    @Override
    public Object save() {
        return null;
    }

    @Override
    public void load(Object data) {

    }
}
