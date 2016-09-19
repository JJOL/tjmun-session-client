package mx.x10.iowizportal.tjmunapp.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by JJOL on 12/10/2015.
 */
public class CommitteeInfo {

    private String topicName;
    private final int _ID;

    private Map<String, String> staffPositionsMap = new TreeMap<>();

    public CommitteeInfo(String name, int id) {
        this._ID = id;

        //Set Positions Name
        staffPositionsMap.put("President", "");
        staffPositionsMap.put("Secretary General", "");
        staffPositionsMap.put("Undersecretary", "");
        staffPositionsMap.put("Chair", "");
        staffPositionsMap.put("Moderator", "");
    }

    public void setName(String topicName) {
        this.topicName = topicName;
    }

    public String getName() {
        return topicName;
    }

    public String getStaff(String position) {
        if(staffPositionsMap.containsKey(position))
            return staffPositionsMap.get(position).isEmpty() ? staffPositionsMap.get(position): "NOT SET";
        return "UNKNOWN POSITION";
    }

    public void setStaff(String position, String name) {
        if(staffPositionsMap.containsKey(position))
            staffPositionsMap.put(position, name);
    }

    public void saveInDB() {
        SQLiteHelper sqlDB = new SQLiteHelper();
        Connection conn = sqlDB.openConnection();

        try {
            PreparedStatement statement = conn.prepareStatement("UPDATE " + SQLiteHelper.STAFF_TABLE + " SET topic_name=?, " +
                    "president_name=?, " +
                    "secretary_name=?, " +
                    "undersecretary_name=?, " +
                    "chair_name=?, " +
                    "moderator_name=?, " +
                    "color=?");
            statement.setString(1, topicName);
            int i = 1;
            for(Map.Entry<String, String> entry : staffPositionsMap.entrySet()) {
                statement.setString(i++, entry.getValue());
            }
        } catch (SQLException ex) {
            System.out.println("Error Saving Topic with Name: " + topicName + ", id: " + _ID);
            ex.printStackTrace();
        }

        sqlDB.closeConnection();
    }

}
