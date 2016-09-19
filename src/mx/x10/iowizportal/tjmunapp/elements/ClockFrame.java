package mx.x10.iowizportal.tjmunapp.elements;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import mx.x10.iowizportal.tjmunapp.ApplicationCore;
import mx.x10.iowizportal.tjmunapp.tasks.Timer;
import mx.x10.iowizportal.tjmunapp.utils.TimeEvent;
import mx.x10.iowizportal.tjmunapp.utils.TimeResponsive;
import mx.x10.iowizportal.tjmunapp.windows.MainWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JJOL on 01/10/2015.
 */
public class ClockFrame extends MainWindowElement implements TimeResponsive{

    private final static Color DEFAULT_TIME_DISPLAY_COLOR = Color.BLACK;

    private boolean isAutplayable = true;
    private boolean ORIGINAL_IS_AUTOPLAYABLE;

    private final ApplicationCore APP;
    private Timer clock;
    private int defaultMinutes = 0, defaultSeconds = 0;
    private final List<TimeResponsive> listeners = new ArrayList<>();
    private final Label TIME_DISPLAY;
    private final TimeAdjustable timeAdjustable;

    private final Button controlBtn;

    private boolean paused = true;

    private HBox defaultTimeWrapper;

    public ClockFrame(Pane root, ApplicationCore app, boolean isAutplayable) {
        APP = app;
        VBox frame = new VBox(30);

        HBox controls = new HBox(20);
        defaultTimeWrapper = new HBox();
        defaultTimeWrapper.setPadding(new Insets(0,0,0,15));

        TIME_DISPLAY = new Label("00:00");
        TIME_DISPLAY.setMinWidth(ApplicationCore.SCREEN_WIDTH/3);
        TIME_DISPLAY.setAlignment(Pos.CENTER);

        this.isAutplayable = isAutplayable;
        ORIGINAL_IS_AUTOPLAYABLE = isAutplayable;

        Button setTimeBtn = new Button("Set");
        controlBtn = new Button("Start");

        clock = new Timer(TIME_DISPLAY, this);
        clock.setCanStartCountdown(true);

        timeAdjustable = new TimeAdjustable(false, APP);

        setTimeBtn.setOnMouseClicked(event -> {
            int[] time = timeAdjustable.getTime();
            clock.setActiveTime(time[0], time[1]);
        });

        controlBtn.setOnMouseClicked(event -> {
            if(paused) {
                if(!clock.hasAlreadyStarted()) {
                    clock.startTimer();
                }
                else {
                    clock.resume();
                }
                setAutplayable(ORIGINAL_IS_AUTOPLAYABLE);
                controlBtn.setText("Stop");

            } else {
                clock.pause();
                controlBtn.setText("Start");
            }
            paused = !paused;
        });

        TIME_DISPLAY.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        TIME_DISPLAY.setTextFill(DEFAULT_TIME_DISPLAY_COLOR);

        controls.getChildren().addAll(setTimeBtn, controlBtn);
        controls.setMinWidth(350);
        controls.setAlignment(Pos.CENTER);
        timeAdjustable.attach(defaultTimeWrapper);
        frame.getChildren().addAll(defaultTimeWrapper, TIME_DISPLAY, controls);
        //root.setStyle(ResourceLibrary.SHADOW_BOX);
        root.getChildren().addAll(frame);
    }

    public void addTimeListener(TimeResponsive listener) {
        listeners.add(listener);
    }

    @Override
    public void onTimeStateChanged(TimeEvent event) {
        fireEvent(event);
        if(event == TimeEvent.ENDED) {
            int[] t = timeAdjustable.getTime();
            clock.setActiveTime(t[0], t[1]);
            if(isAutplayable) {
                clock.startTimer();
            } else {
                Platform.runLater(()->{
                    paused = true;
                    clock.pause();

                    controlBtn.setText("Start");
                });

            }
        }
    }


    public void close() {
        if(clock != null) {
            clock.destroy();
        }
    }

    private void fireEvent(TimeEvent event) {
        for(TimeResponsive listener : listeners)
            listener.onTimeStateChanged(event);
    }

    @Override
    public void flashWarning(boolean turnOn) {
        if(turnOn) {
            TIME_DISPLAY.setTextFill(Color.valueOf(MainWindow.FLASH_STYLE_COLOUR));
            for(Node n : defaultTimeWrapper.getChildren()) {
                n.setStyle("-fx-border-color: " + MainWindow.FLASH_STYLE_COLOUR);
            }
        } else {
            TIME_DISPLAY.setTextFill(DEFAULT_TIME_DISPLAY_COLOR);
            for(Node n : defaultTimeWrapper.getChildren()) {
                n.setStyle("");
            }
        }
    }

    public boolean isAutoPlay() {
        return isAutplayable;
    }

    public void setDefaultAutoplayble(boolean b) { ORIGINAL_IS_AUTOPLAYABLE = b;}
    public void setAutplayable(boolean b) {
        isAutplayable = b;
    }


    private void update(int t1, int t2) {
        defaultMinutes = t1;
        defaultSeconds = t2;
    }



    private static class TimeAdjustable extends TransitionFieldLabel {

        private int defaultMinutes = 0, defaultSeconds = 0;
        private final ApplicationCore APP;

        public TimeAdjustable(boolean clickCloseable, ApplicationCore APP) {
            super(clickCloseable);
            this.APP = APP;
            setReady();
        }

        @Override
        protected void constructShowDisplay() {
            clearWrapper();
            String displayDefaultTime = get2DigitFormatText(defaultMinutes) + ":" + get2DigitFormatText(defaultSeconds);
            Label defaultTimeLabel = new Label("Default: " + displayDefaultTime);
            addToWrapper(defaultTimeLabel);
        }

        @Override
        protected void constructFieldDisplay() {
            clearWrapper();
            TextField minutesInput = new TextField(get2DigitFormatText(defaultMinutes));
            TextField secondsInput = new TextField(get2DigitFormatText(defaultSeconds));

            wrapper.setOnKeyPressed(event -> {
                if(event.getCode() != KeyCode.ENTER)
                    return;
                trigger();
            });

            Button setBtn = getCloseButton();
            addAllToWrapper(minutesInput, secondsInput, setBtn);
        }

        @Override
        protected void saveFields() {
            String f1 = ((TextField)wrapper.getChildren().get(0)).getText();
            String f2 = ((TextField)wrapper.getChildren().get(1)).getText();
            getTimeFromText(f1, f2);

        }

        @Override
        public Object save() {
            return null;
        }

        @Override
        public void load(Object data) {

        }

        private void getTimeFromText(String minsText, String secsText) {
            if(minsText==null || minsText.isEmpty() || minsText.trim().isEmpty())
                minsText = "0";
            if(minsText==null || minsText.isEmpty() || minsText.trim().isEmpty())
                secsText = "0";
            try {
                defaultMinutes = Integer.parseInt(minsText);
                defaultSeconds = Integer.parseInt(secsText);
            } catch (Exception ex) {
                APP.showWarning("Invalid Input For Minutes and Seconds ("+minsText+","+secsText+")!");
            }
        }

        private String get2DigitFormatText(int n) {
            String raw = "00" + n;
            int size = raw.length();
            return raw.substring(size-2, size);
        }

        public int[] getTime() {
            return new int[] {defaultMinutes, defaultSeconds};
        }
    }

}
