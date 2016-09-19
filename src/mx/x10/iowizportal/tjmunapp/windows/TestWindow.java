package mx.x10.iowizportal.tjmunapp.windows;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import mx.x10.iowizportal.tjmunapp.ApplicationCore;

/**
 * Created by JJOL on 22/11/2015.
 */
public class TestWindow implements AppWindow {



    public TestWindow(Stage stage, ApplicationCore app) {
        Scene scene;
        stage.setMinWidth(500);
        stage.setMinHeight(500 / 2);

        StackPane root = new StackPane();
        Label label = new Label("Perro Caliente");

        root.getChildren().add(label);
        scene = new Scene(root);

        stage.setScene(scene);
        stage.showAndWait();

    }

    @Override
    public void cancelWarning() {

    }

    @Override
    public void showWarning(String message) {

    }
}
