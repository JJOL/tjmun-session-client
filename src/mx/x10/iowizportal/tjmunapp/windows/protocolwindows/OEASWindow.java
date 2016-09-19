package mx.x10.iowizportal.tjmunapp.windows.protocolwindows;

import javafx.application.Platform;
import javafx.scene.layout.VBox;
import mx.x10.iowizportal.tjmunapp.ApplicationCore;
import mx.x10.iowizportal.tjmunapp.elements.ClockFrame;
import mx.x10.iowizportal.tjmunapp.elements.WarningsPanel;
import mx.x10.iowizportal.tjmunapp.utils.DebateInfo;
import mx.x10.iowizportal.tjmunapp.utils.TimeEvent;
import mx.x10.iowizportal.tjmunapp.utils.TimeResponsive;

/**
 * Created by JJOL on 21/11/2015.
 */
public class OEASWindow extends ProtocolWindow {
    private ClockFrame generalClockFrame;

    public OEASWindow(ApplicationCore app) {
        super(app);
    }

    @Override
    public void endConstruction(BaseDesign design, DebateInfo info) {
        System.out.println("SetUpOEAS OEAS Window");
        VBox col1 = (VBox)design.getRoot().getChildren().get(0);
        col1.getChildren().remove(2);
        final WarningsPanel panel = (WarningsPanel)design.getImportantThing("wpanel");
        generalClockFrame = new ClockFrame(col1, APP, false);
        generalClockFrame.addTimeListener(event -> {
            if(event==TimeEvent.ENDED) {
                new Thread(() -> {
                    int i = 0;
                    while(i < 5) {
                        i++;
                        Platform.runLater(() -> {
                            generalClockFrame.flashWarning(true);
                            panel.flashWarning(true);
                        });
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Platform.runLater(() -> {
                            generalClockFrame.flashWarning(false);
                            panel.flashWarning(false);
                        });
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    @Override
    public void onCloseProgramm() {
        generalClockFrame.close();
    }
}
