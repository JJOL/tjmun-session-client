package mx.x10.iowizportal.tjmunapp.tasks;

import javafx.application.Platform;
import javafx.scene.control.Label;
import mx.x10.iowizportal.tjmunapp.utils.TimeEvent;
import mx.x10.iowizportal.tjmunapp.utils.TimeResponsive;

import java.util.concurrent.TimeUnit;

/**
 * Created by JJOL on 20/08/2015.
 */
public class Timer implements Runnable{

    private Thread coundownThread = null;

    private volatile boolean clockAlive = false;

    private int activeMinutes = 0;
    private int activeSeconds = 0;
    private boolean canStartCountdown = false;
    private volatile boolean counting = false;
    private final Label timeDisplay;
    private TimeResponsive listener;

    public Timer(Label timeDisplay, TimeResponsive listener) {
        this.timeDisplay = timeDisplay;
        this.listener = listener;
    }

    // StartCoundown
    public void startTimer() {
        System.out.println("onStartTimer()");

        if(canStartCountdown) {
            System.out.println("timerCanStart()");
            counting = true;
            listener.onTimeStateChanged(TimeEvent.STARTED);
            if(coundownThread == null) {
                clockAlive = true;
                System.out.println("Thread Created and started!");
                coundownThread = new Thread(this);
                coundownThread.start();
                //SecondCycle.get().subscribe(this);
            }
        }

    }

    private void displayTime() {
        String formatedMinutes = ("0" + activeMinutes);
        formatedMinutes = formatedMinutes.substring(formatedMinutes.length()-2, formatedMinutes.length());
        String formatedSeconds = ("0" + activeSeconds);
        formatedSeconds = formatedSeconds.substring(formatedSeconds.length()-2, formatedSeconds.length());
        final String text = formatedMinutes + ":" + formatedSeconds;
        System.out.println(" - " + text +" - ");
        Platform.runLater(new Runnable() {
                  @Override
                  public void run() {
                      timeDisplay.setText(text);
                  }
              }
        );
    }


    // Resume Countdown

    // Stop
    public void stopTime() {
        System.out.println("onStopTimer();");
        counting = false;
        canStartCountdown = true;
        //SecondCycle.get().unsubscribe(this);
        listener.onTimeStateChanged(TimeEvent.STOPPED);
    }

    public void pause() {
        System.out.println("onPause()!");
        counting = false;
        canStartCountdown = true;
        //SecondCycle.get().unsubscribe(this);
        listener.onTimeStateChanged(TimeEvent.PAUSED);
        //listener.onTimeStateChanged(TimeEvent.PAUSED);
    }

    public void resume() {
        //counting = true;
        //listener.onTimeStateChanged(TimeEvent.RESUMED);
        if(canStartCountdown) {
            counting = true;
            SecondCycle.get().subscribe(this);
            listener.onTimeStateChanged(TimeEvent.RESUMED);
            System.out.println("onResume()!");
            //listener.onTimeStateChanged(TimeEvent.RESUMED);
        }
    }

    // Reset
    public void resetTime() {
        stopTime();
        //SecondCycle.get().unsubscribe(this);
        setActiveTime(0, 0);
    }

    public int[] getActiveTime() {
        return new int[]{activeMinutes, activeSeconds};
    }

    // SetTime
    public void setActiveTime(int minutes, int seconds) {
        System.out.println("onSetActiveTime("+minutes+","+seconds+")");
        activeMinutes = minutes;
        activeSeconds = seconds;
        displayTime();
        listener.onTimeStateChanged(TimeEvent.TIME_SET);
    }

    private void onTimeFinished() {
        System.out.println("onTimerFinished()");
        stopTime();
        listener.onTimeStateChanged(TimeEvent.ENDED);

    }

    @Override
    public void run() {
        // Do Timing
        while(clockAlive) {

            if(counting) {
                displayTime();
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(1));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //System.out.println("Tick!");

                if (--activeSeconds < 0) {
                    if (activeMinutes > 0) {
                        activeSeconds = 59;
                        activeMinutes--;
                    } else {
                        // Timer Hit 00,00
                        onTimeFinished();
                    }
                }
            }
        }




    }

    public void destroy() {
        clockAlive = false;
    }
    public boolean isCounting() {
        return counting;
    }

    public boolean hasAlreadyStarted() {
        return coundownThread != null;
    }

    public void setCanStartCountdown(boolean b) {
            canStartCountdown = b;
    }
}
