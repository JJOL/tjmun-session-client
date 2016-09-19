package mx.x10.iowizportal.tjmunapp.windows.protocolwindows;

import mx.x10.iowizportal.tjmunapp.ApplicationCore;
import mx.x10.iowizportal.tjmunapp.utils.DebateInfo;

/**
 * Created by JJOL on 13/11/2015.
 */
public abstract class ProtocolWindow {

    protected final ApplicationCore APP;

    public ProtocolWindow(ApplicationCore app) {
        this.APP = app;
    }

    public abstract void endConstruction(BaseDesign design, DebateInfo info);

    public void onCloseProgramm() {}


}
