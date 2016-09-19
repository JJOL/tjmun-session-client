package mx.x10.iowizportal.tjmunapp.utils;

import java.util.Comparator;

/**
 * Created by JJOL on 30/08/2015.
 */
public class AlphabetOrder implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
        int l = Math.max(o1.length(), o2.length());
        String s1 = o1.toLowerCase();
        String s2 = o2.toLowerCase();
        for(int i = 0; i < l; i++) {
            if(s1.charAt(i) == s2.charAt(i))
                continue;
            if(s1.charAt(i) >= s2.charAt(i))
                return 1;
            else {
                return -1;
            }
        }
        return 0;
    }
}
