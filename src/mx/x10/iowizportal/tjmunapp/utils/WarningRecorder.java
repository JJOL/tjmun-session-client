package mx.x10.iowizportal.tjmunapp.utils;

import mx.x10.iowizportal.tjmunapp.ApplicationCore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by JJOL on 08/10/2015.
 */
public class WarningRecorder {

    private final static String SELECT_WARNINGS = "SELECT pais, cantidad, com1, com2, com3 FROM " + SQLiteHelper.WARNING_TABLE;
    private final static String SAVE_WARNINGS = "UPDATE " + SQLiteHelper.WARNING_TABLE + " SET cantidad=?, com1=?, com2=?, com3=? WHERE pais=?";


    private final static String SELECT_TARDS = "SELECT * FROM " + SQLiteHelper.DELAY_TABLE + " WHERE pais = ?";
    private final static String SAVE_TARDS = "INSERT INTO " + SQLiteHelper.DELAY_TABLE + " VALUES (?, ?)";
    private final static String CLEAR_TARDS_FOR_COUNTRY = "DELETE FROM " + SQLiteHelper.DELAY_TABLE + " WHERE pais = ?";

    private final Map<String, WarningsRegistry> warningsMap = new HashMap<>();
    private final Set<String> updatedCountries = new HashSet<>();

    private boolean warningsLoaded = false;
    private final ApplicationCore APP;

    public WarningRecorder(ApplicationCore APP) {
        this.APP = APP;
    }


    public boolean loadWarningRegistry(Connection conn) {

        try {

            PreparedStatement ps = conn.prepareStatement(SELECT_WARNINGS);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String name = rs.getString("pais");
                WarningsRegistry registry = new WarningsRegistry(name, 0);

                // Agregar los comentarios que estan guardados por cada pais de 0  a la cantidad que tienen
                for (int i = 0; i < WarningsRegistry.MAX_WARNING_COUNT; i++) {
                    String index = "com" + Integer.toString(i + 1);
                    String saveComment = rs.getString(index);
                    if (saveComment==null ||saveComment.isEmpty() || saveComment.trim().equalsIgnoreCase("")) {
                        continue;
                    }
                    else {
                        registry.addComment(i+1, rs.getString("com" + Integer.toString(i + 1)));
                    }
                }
                warningsMap.put(name, registry);

                System.out.println("Pais (" + name + ") con " + registry.getWarningCount() + " counts");
            }
            // Paises ya se cargaron y guardaron en memoria en una tabla hash
            rs.close();
            ps.close();
            System.out.println("Operation done Correctly");
        } catch (SQLException ignored) {
            ignored.printStackTrace();

            return false;
        }

        return true;

    }

    public boolean loadTardRegistry(Connection conn) {
        try {
            PreparedStatement statement = conn.prepareStatement(SELECT_TARDS);

            for (String country : warningsMap.keySet()) {
                statement.setString(1, country);
                ResultSet resultSet = statement.executeQuery();

                WarningsRegistry registry = getWarningRegistryForCountry(country);
                while(resultSet.next()) {
                    registry.addRetard(resultSet.getString("delayDate"));
                }
                resultSet.close();

            }
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public WarningsRegistry getWarningRegistryForCountry(String countryName) {
        return warningsMap.containsKey(countryName) ? warningsMap.get(countryName) : null;
    }

    public void updateWarning(String countryName, int wIndex, String wComment) { //wIndex = Warning Index 0,1,2

        // Dont Save Nulls

        if(!warningsMap.containsKey(countryName))
            return;

        if (wComment == null) {
            warningsMap.get(countryName).removeComment(wIndex);
            updatedCountries.add(countryName);
            return;
        }

        if (wComment.isEmpty() || wComment.trim().equals(""))
            return;

        warningsMap.get(countryName).addComment(wIndex, wComment);
        markCountryRegistry(countryName);
        /*if (!warningsMap.containsKey(countryName)) {
            warningsMap.put(countryName, new WarningsRegistry(countryName, 1)); // If it wasnt registered do it now with a warning count of 1
        }*/
    }

    public void markCountryRegistry(String countryName) {
        updatedCountries.add(countryName);
        APP.close();
    }

    public boolean isEmpty() {
        return warningsMap.isEmpty();
    }

    public boolean isReady() {
        return !isEmpty() && warningsLoaded;
    }

    public Set<String> getUpdatedCountries() {
        return updatedCountries;
    }

    public Set<String> getWarnedCountries() {
        Set<String> result = new HashSet<>();
        for (WarningsRegistry registry : warningsMap.values()) {
            if (registry.getWarningCount() > 0 || registry.getTards().size() > 0)
                result.add(registry.getCountryName());
        }
        //System.out.println(result);
        return result;
    }

    public void updateDatabaseWarnings(Connection conn) {

        //TODO Do proper updating with warning recorder
        for (String countryName : updatedCountries) {

            WarningsRegistry registry = warningsMap.get(countryName);
            try {
                PreparedStatement ps = conn.prepareStatement(SAVE_WARNINGS);
                ps.setInt(1, registry.getWarningCount());
                for(int i = 1; i <= WarningsRegistry.MAX_WARNING_COUNT; i++) {
                    String comment = registry.getWarningComment(i);
                    if(comment == null)
                        comment = "";
                    ps.setString(i+1, comment);
                }
                ps.setString(5, registry.getCountryName());
                ps.execute();
                ps.close();

                ps = conn.prepareStatement(CLEAR_TARDS_FOR_COUNTRY);
                ps.setString(1, countryName);
                ps.execute();
                ps.close();
                ps = conn.prepareStatement(SAVE_TARDS);
                String lastDate = "";
                for(String date : registry.getTards()) {
                    if(Objects.equals(date, lastDate))
                        continue;
                    ps.setString(1, date);
                    ps.setString(2, countryName);
                    ps.addBatch();
                    lastDate = date;
                }
                ps.executeBatch();



            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        }
        updatedCountries.clear();
    }

    public void clearRecords() {
        warningsMap.clear();
        updatedCountries.clear();
    }

}
