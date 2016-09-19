package mx.x10.iowizportal.tjmunapp.listeners;

import mx.x10.iowizportal.tjmunapp.utils.consts.DBState;

/**
 * Created by JJOL on 17/10/2015.
 */
public interface DatabaseStateDependent {

    void onDatabaseStateChange(DBState state);

}
