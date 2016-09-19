package mx.x10.iowizportal.tjmunapp.utils;

import java.sql.*;
import java.util.*;
import java.util.prefs.Preferences;

/**
 * Created by JJOL on 17/09/2015.
 */
public class SQLiteHelper {

    public final static String WARNING_TABLE = "WarningsRegistry";
    public final static String DELAY_TABLE = "DelayRecord";
    public final static String DATABASE_NAME = "warningsregistry.db";
    public final static String FILE_PATH_TABLE = "FILE_PATH";
    public final static String STAFF_TABLE = "Committees";
    
   private final static String WARNING_TABLE_DEFINE =  "CREATE TABLE IF NOT EXISTS " + WARNING_TABLE + "(" +
            "ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "pais VARCHAR(60)," +
            "cantidad int," +
            "com1 VARCHAR(140)," +
            "com2 VARCHAR(140)," +
            "com3 VARCHAR(140) )";

    private final static String DELAY_TABLE_DEFINE = "CREATE TABLE IF NOT EXISTS " + DELAY_TABLE + "(" +
            "delayDate VARCHAR(20) NOT NULL," +
            "pais VARCHAR(60) NOT NULL," +
            "PRIMARY KEY (delayDate, pais))";
    
    

    private Connection connection;

    public String getImagesPath() {
        boolean found = false;
        String  path = "";
        try {

            PreparedStatement query = connection.prepareStatement("SELECT * FROM " + FILE_PATH_TABLE);
            ResultSet result = query.executeQuery();

            if(result.next()) {
                    System.out.println();
                    path = result.getString("folder_path");
                    found = path != null && !path.isEmpty();
            }
            result.close();
            query.close();

        } catch (SQLException ex) {
            System.out.println("[ERROR GETTING FOLDER PATH!]");
            ex.printStackTrace();
        }
        System.out.println("[FOLDER PATH (" + path + ") FOUND]");
        return found ? path : null;
    }

    public void setImagesPath(String path) {
        try {
            PreparedStatement query = connection.prepareStatement("DELETE FROM " + FILE_PATH_TABLE);
            System.out.println("[FOLDER PATH CLEARED]");
            query.execute();
            query = connection.prepareStatement("INSERT INTO " + FILE_PATH_TABLE +  " VALUES (?)");
            query.setString(1, path);
            query.execute();
            System.out.println("[FOLDER PATH (" + path + ") INSERTED]");
            query.close();
        } catch (SQLException ex) {
            System.out.println("[ERROR SETTING FOLDER PATH!]");
            ex.printStackTrace();
        }
    }

    public void defineTable() {
        //if(true) return;
        try {
            PreparedStatement createStatemet =
                    connection.prepareStatement(WARNING_TABLE_DEFINE);

            //createStatemet =

            //PreparedStatement createStatemet = connection.prepareStatement("DROP TABLE IF EXISTS " + WARNING_TABLE);
            createStatemet.execute();
            System.out.println("[WARNING TABLE CREATED]");
            createStatemet = connection.prepareStatement(DELAY_TABLE_DEFINE);

            createStatemet.execute();
            System.out.println("[DELAY RECORD TABLE CREATED]");

            createStatemet = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + FILE_PATH_TABLE + " (folder_path VARCHAR(255), PRIMARY KEY(folder_path))");
            createStatemet.execute();
            System.out.println("[FILE_PATH TABLE CREATED]");

            //TODO Change Staff Saving Model To Make it more Flexible
            createStatemet = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + STAFF_TABLE +
                    " ( ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "topic_name VARCHAR(60)," +
                    "chair_name VARCHAR(20)," +
                    "secretary_name VARCHAR(20)," +
                    "president_name VARCHAR(20)," +
                    "undersecretary_name VARCHAR (20)," +
                    "moderator_name VARCHAR(20)," +
                    "color VARCHAR(10) DEFAULT RED)");
            createStatemet.execute();
            System.out.println("[FILE_PATH TABLE CREATED]");

            createStatemet.close();

            System.out.println("[DATABASE MODEL CREATION CORRECTLY DONE]");
        } catch(SQLException exp) {
            exp.printStackTrace();
            System.out.println("Error on Creating Table");
        }
    }

    public void setupDefaults(Set<String> countries) {
        //if(true) return;

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT pais FROM " + WARNING_TABLE);
            ResultSet result = statement.executeQuery();
            Set<String> countriesInDB = new HashSet<>();
            Set<String> newCountries  = new HashSet<>();
            while(result.next()) {
                countriesInDB.add(result.getString("pais"));
                System.out.println(result.getString("pais"));
            }
            result.close();
            statement.close();
            for(String n : countries) {
                if(!countriesInDB.contains(n)) {
                    newCountries.add(n);
                }
            }

            String INSERT_SQL = "INSERT INTO WarningsRegistry (pais, cantidad, com1, com2, com3) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insert = connection.prepareStatement(INSERT_SQL);
            for(String c : newCountries) {
                System.out.println(c);
                insert.setString(1, c);
                insert.setInt(2, 0);
                insert.setString(3, "");
                insert.setString(4, "");
                insert.setString(5, "");
                insert.execute();

            }
            insert.close();


            System.out.println("[COUNTRIES DEFAULT RECORDS ADDED]");
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error on Inserting Data");
        }
    }

    public void cleanTable() {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM " + WARNING_TABLE);
            statement.execute();
            statement.close();
            System.out.println("Table " + WARNING_TABLE + " has been cleaned!");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void resetDB(boolean firstTable, boolean secondTable, boolean third) {

        try {
            PreparedStatement statement;
            if (firstTable) {
                statement = connection.prepareStatement("DROP TABLE IF EXISTS " + WARNING_TABLE);
                statement.execute();
                statement.close();
                System.out.println("Table " + WARNING_TABLE + "has been destroyed!");
            }
            if (secondTable) {
                statement = connection.prepareStatement("DROP TABLE IF EXISTS " + DELAY_TABLE);
                statement.execute();
                statement.close();
                System.out.println("Table " + DELAY_TABLE + "has been destroyed!");
            }

            if (third) {
                statement = connection.prepareStatement("DROP TABLE IF EXISTS " + FILE_PATH_TABLE);
                statement.execute();
                statement.close();
                System.out.println("Table " + FILE_PATH_TABLE + "has been destroyed!");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Connection openConnection() {
        try {
            Class.forName("org.sqlite.JDBC").newInstance();
            connection =  DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
            return connection;
        } catch (Exception e) {
            System.out.println("Error Connection to Database!");
            e.printStackTrace();
            return null;
        }
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
