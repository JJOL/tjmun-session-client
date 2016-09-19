package mx.x10.iowizportal.tjmunapp.windows;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Created by JJOL on 04/11/2015.
 */
public class AlertWindow {

    private Stage window;

    public AlertWindow(String title, String message) {

        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);

        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50,50,50,50));
        Label messageLabel = new Label(message);
        root.getChildren().add(messageLabel);

        Scene scene = new Scene(root);
        window.setScene(scene);
    }

    public void display() {
        window.showAndWait();
    }

}
