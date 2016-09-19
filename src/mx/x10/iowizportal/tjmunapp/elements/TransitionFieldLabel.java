package mx.x10.iowizportal.tjmunapp.elements;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import mx.x10.iowizportal.tjmunapp.utils.ResourceLibrary;

import java.io.Serializable;

/**
 * Created by JJOL on 05/11/2015.
 */
public abstract class TransitionFieldLabel implements Serializable {

    protected transient HBox wrapper;
    private boolean open;
    private boolean closeWithClick = true;

    public TransitionFieldLabel(boolean clickCloseable) {
        closeWithClick = clickCloseable;
        wrapper = new HBox();
        open = false;
        wrapper.setOnMouseClicked(event -> {
            if(open && !closeWithClick)
                return;
            trigger();
        });
    }

    public void attach(Pane base) {
        base.getChildren().add(wrapper);
    }

    protected void addToWrapper(Node node) {
        wrapper.getChildren().add(node);
    }
    protected void clearWrapper() {
        wrapper.getChildren().clear();
    }
    protected void addAllToWrapper(Node... nodes) {
        wrapper.getChildren().addAll(nodes);
    }

    protected void trigger() {
        if(open) {
            saveFields();
            constructShowDisplay();
        } else {
            constructFieldDisplay();
        }
        open = !open;
    }

    protected abstract void constructShowDisplay();
    protected abstract void constructFieldDisplay();
    protected abstract void saveFields();

    protected Button getCloseButton() {
        Button closeButton = new Button("", ResourceLibrary.getTinyIcon(ResourceLibrary.CHECK_ICON));
        closeButton.setOnMouseClicked(event -> trigger() );
        return closeButton;
    }
    protected void setReady() {
        open = false;
        trigger();
        trigger();
    }

    public abstract Object save();
    public abstract void load(Object data);

}
