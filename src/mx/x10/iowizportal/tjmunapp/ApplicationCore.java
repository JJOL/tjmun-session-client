package mx.x10.iowizportal.tjmunapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import mx.x10.iowizportal.tjmunapp.listeners.DatabaseStateDependent;
import mx.x10.iowizportal.tjmunapp.utils.*;
import mx.x10.iowizportal.tjmunapp.utils.consts.DBState;
import mx.x10.iowizportal.tjmunapp.utils.consts.Language;
import mx.x10.iowizportal.tjmunapp.utils.consts.MUNProtocol;
import mx.x10.iowizportal.tjmunapp.windows.AppWindow;
import mx.x10.iowizportal.tjmunapp.windows.WelcomeScreen;

import java.io.File;
import java.net.MalformedURLException;
import java.sql.*;
import java.util.*;

/**
 * Created by JJOL on 08/09/2015.
 */
public class ApplicationCore extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private static boolean databaseReady = false;

    private static ApplicationCore INSTANCE;
    public static ApplicationCore get() {
        return INSTANCE;
    }


    // Screen Resolution Properties
    public static final int SCREEN_WIDTH = 1024;
    public static final int SCREEN_HEIGHT = 768;
    public static final int BASE_WIDTH = 300;
    public static final int BASE_HEIGHT = BASE_WIDTH;
    public static final int BASE_SCALE = 3;
    public static final int ASPECT_RATION = 16 / 9;


    private Thread setupDB;
    private Runnable setupDBTask;
    private AppWindow currentWindow;
    private WarningRecorder warningRecorder;
    private SQLiteHelper db;
    private final static String PATH_KEY = "IMAGES_PATH";
    private final static String PATH_UNDEFINED = "UNDEFINED_PATH";
    public static Language APP_LAN = Language.SPANISH;
    public static MUNProtocol PROTOCOL = MUNProtocol.UN;
    public static String getLanguageName() {
        return APP_LAN.toString();
    }
    private String imagesPath = null;

    private final List<DatabaseStateDependent> databaseListeners = new ArrayList<>();
    private final Set<String> countriesInRoom = new HashSet<>();

    public ApplicationCore() {

        INSTANCE = this;

        System.out.println("I the App Was Created!");
        warningRecorder = new WarningRecorder(this);

        prepareDatabase(false, false);
    }
    
    public boolean isDataReadyToUse() {
        //return databaseReady && warningsMap.size() > 0;
        return databaseReady && !warningRecorder.isEmpty();
    }

    public synchronized void setCurrentWindow(AppWindow window) {
        currentWindow = window;
    }

    public void showWarning(final String message) {
        Platform.runLater(() -> {
            currentWindow.showWarning(message);
            System.out.println(currentWindow.getClass().getName());
        });
    }

    public WarningRecorder getWarningRecorder() {
        return warningRecorder;
    }

    public Set<String> getAllCountries() {
        return countriesInRoom;
    }

    public String getImagesPath() {
        return imagesPath;
    }

    private void loadWarnings(Connection conn) {
        warningRecorder.loadWarningRegistry(conn);
    }

    private void saveChanges() {
        warningRecorder.updateDatabaseWarnings(db.openConnection());
        db.closeConnection();
    }

    public void resetApp() {
        showWarning("App Is Being Reset");
        new Thread(()-> {
            {
                File file = new File("countries_list.ser");
                if (file.exists()) {
                    file.delete();
                }
            }
            {
                File file = new File("infostaff.ser");
                if (file.exists()) {
                    file.delete();
                }
            }
            prepareDatabase(true, true);
        }).start();
        
    }


    public void setFlagsFolder(File folder) {
        if(databaseReady) {
            System.out.println("Setup Stopped Because Thread Was already Ready");
            databaseReady = true;
            return;
        }
        clear();
//        Preferences preferences = Preferences.systemNodeForPackage(ApplicationCore.class);
//        preferences.put(PATH_KEY, folder.getAbsolutePath());
//        System.out.println("Preferences Saved!");
        try {
            System.out.println(folder.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        imagesPath = folder.getAbsolutePath();
        //setupDB = new Thread(setupDBTask);
        for(DatabaseStateDependent dependent : databaseListeners)
            dependent.onDatabaseStateChange(DBState.UNCONNECTED);
        System.out.println("Starting Setup Thread");
        prepareDatabase(false, false);
        //setupDB.start();
    }

    public void prepareDatabase(boolean warningsTable, boolean delaysTable) {
        setupDBTask = new Runnable() {
            @Override
            public void run() {

                if(databaseReady && !warningsTable && !delaysTable) {
                    System.out.println("Setup Stopped Because Thread Was already Ready");
                    databaseReady = true;
                    return;
                }

                //Create the Table Definition

                db = new SQLiteHelper();
                Connection conn = db.openConnection();
                db.resetDB(warningsTable, delaysTable, false);                  //TODO <--- Used for Debug Purpose and Clear all data   REMOVE WHEN NOT NEEDED
                db.defineTable();
                //Preferences preferences = Preferences.systemNodeForPackage(ApplicationCore.class);
                //String imagesPath = preferences.get(PATH_KEY, PATH_UNDEFINED);
                if(imagesPath != null) db.setImagesPath(imagesPath);
                else {
                	System.out.println("Records of Image Path Found in the Database!!!!!");
                	imagesPath = db.getImagesPath();
                }


                if(imagesPath == null || imagesPath.equalsIgnoreCase(PATH_UNDEFINED) || imagesPath.isEmpty()) {
                    System.out.println("Setup Stopped Because File Path was Invalid");
                    db.closeConnection();
                    return;
                }
                for(DatabaseStateDependent dependent : databaseListeners)
                    dependent.onDatabaseStateChange(DBState.CONNECTED);

                // Load Countries in the Images Folder
                File folder = new File(imagesPath);
                File[] listedFiles = folder.listFiles();
                for(File file : listedFiles) {
                    String fileName = file.getName();
                    // Select all *.jpg *.png files from the directory
                    if(fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
                        //Save each ones name in the set
                        countriesInRoom.add(fileName.substring(0, fileName.length()-4));
                        System.out.println(fileName.substring(0, fileName.length() - 4));
                    } else System.out.println("File: " + fileName + "Found!");
                }

                if(countriesInRoom.isEmpty()) {
                    db.closeConnection();
                    return;
                }

                //Insert Countries Unregistered Yet with their defaults (0 Warnings, Warning 1 "", ... 3)
                db.setupDefaults(countriesInRoom);
                //Select everything from the Database that has 1 or more warnings and save it in a map
                //loadWarnings(conn);

                boolean loaded = warningRecorder.loadWarningRegistry(conn);
                loaded = warningRecorder.loadTardRegistry(conn);
                db.closeConnection();
                //Database is Ready
                System.out.println("Database has been initialized with success");
                showWarning("Everything Ready To Use!!");

                databaseReady = loaded;

                for(DatabaseStateDependent dependent : databaseListeners)
                    dependent.onDatabaseStateChange(DBState.READY);
            }
        };


        setupDB = new Thread(setupDBTask);
        setupDB.start();
    }

    public void subscribeDatabaseStateListener(DatabaseStateDependent listener) {
        databaseListeners.add(listener);
    }
    public void unsubscribeDatabaseStateListener(DatabaseStateDependent listener) {
        databaseListeners.remove(listener);
    }

    public void clear() {
        //warningsMap.clear();
        warningRecorder.clearRecords();
        //updatedCountries.clear();
        countriesInRoom.clear();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        currentWindow = new WelcomeScreen(primaryStage, this);
    }

    public void close() {
        saveChanges();
    }
}
