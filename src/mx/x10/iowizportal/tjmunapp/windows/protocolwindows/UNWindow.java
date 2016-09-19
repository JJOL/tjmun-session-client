package mx.x10.iowizportal.tjmunapp.windows.protocolwindows;

import mx.x10.iowizportal.tjmunapp.ApplicationCore;
import mx.x10.iowizportal.tjmunapp.utils.DebateInfo;
import mx.x10.iowizportal.tjmunapp.utils.consts.MUNProtocol;
import mx.x10.iowizportal.tjmunapp.utils.consts.UNDebateStates;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JJOL on 13/11/2015.
 */
public class UNWindow extends ProtocolWindow {


    public UNWindow(ApplicationCore app) {
        super(app);
    }

    @Override
    public void endConstruction(BaseDesign design, DebateInfo info) {
        List<String> states = new ArrayList<>();
        for (UNDebateStates state : UNDebateStates.values())
            states.add(state.getName());
    }
}
