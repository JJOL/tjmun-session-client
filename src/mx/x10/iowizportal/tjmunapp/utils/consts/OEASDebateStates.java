package mx.x10.iowizportal.tjmunapp.utils.consts;

import mx.x10.iowizportal.tjmunapp.ApplicationCore;

/**
 * Created by JJOL on 05/11/2015.
 */
public enum OEASDebateStates {

    PRESENTATION("Presentation", "Presentaci\u00F3n"), FREE_CAUCUS("Free Caucus", "Debate Libre"),
    OPEN_CAUCUS("Open Caucus", "Debate Abierto"), PRESENTATION_WITH_CONCLUSION("Presentation and Conclusions", "Presentaci\u00F3n con Conclusiones"),
    COUNTERAGUMENT("CounterArgumentation", "ContraArgumentaci\u00F3n"), DELIBERATION("Deliberation", "Deliberaci\u00F3n"),
    REPLICAS("Replicas", "Replicas"), TEAM_REUNION("Team Reunion", "Deliberacion"),
    INTERROGATIONS("Interrogations", "Interrogatorios"), FACE_TO_FACE("Face to Face", "Cara a Cara"), INTERROGATIONS_OF("Interrogations of", "Interrogatorios de");

    public String US_name;
    public String ES_name;

    OEASDebateStates(String US_name, String ES_name) {
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
