package mx.x10.iowizportal.tjmunapp.tasks;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by JJOL on 04/12/2015.
 */
public class SecondCycle implements Runnable {

    private static SecondCycle instance = null;

    private Thread timeTask;
    private boolean active;

    private Set<Runnable> subscribers = new HashSet<>();

    public static SecondCycle get() {
        if(instance == null) {
            instance = new SecondCycle();
        }
        return instance;
    }

    public static void shutdown() {
        instance.active = false;
        instance = null;
    }


    public SecondCycle() {
        timeTask = new Thread("TimeTask");
        active = true;
        timeTask.start();
    }

    public void subscribe(Runnable task) {
        subscribers.add(task);
    }

    public void unsubscribe(Runnable task) {
        subscribers.remove(task);
    }

    public void run() {
        while(active){
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            // Call all its subcribers

            for(Runnable task : subscribers) {
                task.run();
            }


        }
    }

}
