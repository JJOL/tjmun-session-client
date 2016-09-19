package mx.x10.iowizportal.tjmunapp.utils;

/**
 * Created by JJOL on 23/10/2015.
 */
public class BooleanMutable {
    private boolean value;

    public BooleanMutable(boolean b) {
        value = b;
    }

    public void setValue(boolean b) {
        this.value = b;
    }

    public boolean getValue() {
        return value;
    }
}
