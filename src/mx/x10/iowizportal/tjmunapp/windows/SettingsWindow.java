package mx.x10.iowizportal.tjmunapp.windows;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import mx.x10.iowizportal.tjmunapp.elements.ConfigPanel;
import mx.x10.iowizportal.tjmunapp.utils.CommitteeInfo;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by JJOL on 26/10/2015.
 */
public class SettingsWindow {

    public SettingsWindow(CommitteeInfo committeeInfo) {

        final Stage window = new Stage();
        window.setTitle("Committee Configuration");
        VBox column = new VBox();
        ConfigPanel configPanel = new ConfigPanel(Arrays.asList(committeeInfo), column);

        Scene scene = new Scene(column);
        window.setScene(scene);
        window.showAndWait();
    }

}
