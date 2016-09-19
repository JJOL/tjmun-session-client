package mx.x10.iowizportal.tjmunapp.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JJOL on 08/09/2015.
 */
public class WarningsRegistry {


    public static final int MAX_WARNING_COUNT = 3;

    private final String countryName;
    private final String comments[] = new String[MAX_WARNING_COUNT];
    private int count = 0;
    private List<String> tards = new ArrayList<>();

    public WarningsRegistry(String countryName, int count) {
        this.countryName = countryName;
        this.count = count;
    }

    public String getCountryName() {
        return countryName;
    }

    public int getWarningCount() {
        return count;
    }

    // (1,2,3)
    public String getWarningComment(int warningNumber) {
        if(warningNumber <= 0 || warningNumber > MAX_WARNING_COUNT) {
             //return invalidCommentResponse(warningNumber); //TODO Invalid Comment Number
            return null;
        }
        return (comments[warningNumber-1] == null) ? null : comments[warningNumber-1] ;
    }

    // (1,2,3)
    public void addComment(int index, String comment) {
        if(index > 0 && index <= MAX_WARNING_COUNT) {
            if(comments[index - 1] == null)
                count++;
            comments[index - 1] = comment;

        }
    }

    public void removeComment(int index) {
        if(index > 0 && index <= MAX_WARNING_COUNT && comments[index - 1] != null) {
            count--;
            comments[index - 1] = null;
            System.out.println("Removed");
        }

    }

    public List<String> getTards() {
        return tards;
    }

    public void addRetard(String retard) {
        tards.add(retard);
    }

    public void removeLastRetard() {
        if(!tards.isEmpty())
            tards.remove(tards.size()-1);
    }

    // Auto
    public void addComment(String comment) {
        if(count < MAX_WARNING_COUNT)
            comments[count++] = comment;
    }



    public static String invalidCommentResponse(int n) {
        return "No Comment Made Yet for Warning #" + n;
    }
}
