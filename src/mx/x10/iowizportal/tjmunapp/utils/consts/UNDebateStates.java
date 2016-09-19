package mx.x10.iowizportal.tjmunapp.utils.consts;

import mx.x10.iowizportal.tjmunapp.ApplicationCore;

/**
 * Created by JJOL on 05/11/2015.
 */
public enum UNDebateStates {

    SPEAKERS_LIST("Speaker's List", "Lista de Oradores"), MODERATED_CAUCUS("Moderated Caucus", "Debate Moderado"),
    UNMODERATED_CAUCUS("Unmoderated Caucus", "Debate No Moderado"), DRAFT_RESOLUTION("Draft Resolution", "Proceso de Resoluci\u00F3n"),
    ROLL_CALLING_VOTING("Roll-Call Voting", "Votaciones de Llamado a Lista");

    public String US_name;
    public String ES_name;

    UNDebateStates(String US_name, String ES_name) {
        this.US_name = US_name;
        this.ES_name = ES_name;
    }

    public String getName() {
        String name;
        switch (ApplicationCore.APP_LAN)
        {
            case SPANISH:
                name = ES_name;
                break;
            default:
                name = US_name;
        }
        return name;
    }




}
